package org.ternlang.studio.service.command;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.common.encode.Base64Encoder;
import org.simpleframework.http.Path;
import org.simpleframework.http.parse.AddressParser;
import org.simpleframework.http.socket.FrameChannel;
import org.ternlang.common.command.CommandBuilder;
import org.ternlang.common.command.Console;
import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.common.Problem;
import org.ternlang.studio.common.ProblemFinder;
import org.ternlang.studio.common.display.DisplayDefinition;
import org.ternlang.studio.common.display.DisplayPersister;
import org.ternlang.studio.project.BackupManager;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.config.DependencyFile;
import org.ternlang.studio.project.config.OperatingSystem;
import org.ternlang.studio.project.config.ProjectConfiguration;
import org.ternlang.studio.service.ProblemCollector;
import org.ternlang.studio.service.ProcessManager;
import org.ternlang.studio.service.StudioClientLauncher;
import org.ternlang.studio.service.agent.local.LocalProcessClient;
import org.ternlang.studio.service.tree.TreeContext;
import org.ternlang.studio.service.tree.TreeContextManager;

@Slf4j
public class CommandListener {

   private final StudioClientLauncher clientLauncher;
   private final DisplayPersister displayPersister;
   private final CommandEventForwarder forwarder;
   private final ProblemCollector problemFinder;
   private final LocalProcessClient debugService;
   private final TreeContextManager treeManager;
   private final CommandFilter commandFilter;
   private final CommandClient commandClient;
   private final CommandSession commandSession;
   private final ProcessManager processManager;
   private final BackupManager backupManager;
   private final ProblemFinder finder;
   private final AtomicLong lastModified;
   private final ThreadFactory factory;
   private final Project project;
   private final String projectName;
   private final String cookie;
   private final Path path;
   private final File root;
   
   public CommandListener(
         StudioClientLauncher clientLauncher,
         ProcessManager processManager, 
         ProblemCollector problemFinder, 
         DisplayPersister displayPersister,
         LocalProcessClient debugService,
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         CommandSession commandSession,
         CommandClient commandClient,
         Project project,
         Path path, 
         String cookie) 
   {
      this.commandFilter = commandSession.getFilter();
      this.forwarder = commandClient.getForwarder();
      this.lastModified = new AtomicLong(project.getModificationTime());
      this.factory = new ThreadBuilder(true);
      this.finder = new ProblemFinder();
      this.projectName = project.getName();
      this.root = project.getBasePath();
      this.displayPersister = displayPersister;
      this.clientLauncher = clientLauncher;
      this.treeManager = treeManager;
      this.problemFinder = problemFinder;
      this.backupManager = backupManager;
      this.processManager = processManager;
      this.debugService = debugService;
      this.commandSession = commandSession;
      this.commandClient = commandClient;
      this.project = project;
      this.cookie = cookie;
      this.path = path;
   }

   public void onOpen(OpenCommand command) {
      try {
         // notify all sessions of open
         commandSession.sendOpenAlert(command);
      } catch(Exception e) {
         log.info("Could not send open alert", e);
      }
   }

   public void onLaunch(LaunchCommand command) {
      String address = command.getAddress();
      String session = command.getSession();

      try {
         if(session != null) {
            URI target = URI.create(address);
            String host = target.getHost();
            String path = target.getPath();
            String query = target.getRawQuery();
            int port = target.getPort();

            // this is a hack job for session
            if(port != -1 && port != 0 && port != 80) {
               String redirect = String.format("http://%s:%s/session/%s%s?%s", host, port, session, path, query);
               clientLauncher.launch(redirect);
            } else {
               String redirect = String.format("http://%s/session/%s%s?%s", host, session, path, query);
               clientLauncher.launch(redirect);
            }
         } else {
            clientLauncher.launch(address);
         }
      } catch(Exception e) {
         log.info("Error launching window " + address, e);
      }
   }

   public void onExplore(ExploreCommand command) {
      String resource = command.getResource();
      
      try {
         if(resource != null) {
            File file = new File(root, "/" + resource);
            CommandBuilder builder = new CommandBuilder();
            
            if(!file.isDirectory()) {
               file = file.getParentFile();
            }
            String path = file.getCanonicalPath();
            boolean exists = file.exists();
            boolean directory = file.isDirectory();
            OperatingSystem os = OperatingSystem.resolveSystem();
            
            if(exists && directory) {
               if(!command.isTerminal()) {
                  String expression = os.createExploreCommand(path);
                  Callable<Console> task = builder.create(expression);
                  
                  log.info("Executing: " + expression);
                  task.call();
               } else {
                  String expression = os.createTerminalCommand(path);
                  Callable<Console> task = builder.create(expression);
                  
                  log.info("Executing: " + expression);
                  task.call();
               }
            }
         }
      } catch(Exception e) {
         log.info("Error exploring directory " + resource, e);
      }
   }
   
   public void onUpload(UploadCommand command) {
      Boolean dragAndDrop = command.getDragAndDrop();
      String to = command.getTo();
      String name = command.getName();
      String project = command.getProject();
      
      try {
         if(Boolean.TRUE.equals(dragAndDrop)) {
            log.info("Drag and drop file: " + name + " to: " + to);
         }
         File file = new File(root, to);
         boolean exists = file.exists();
         
         if(exists) {
            backupManager.backupFile(file, project);
         }
         String text = command.getData();
         char[] source = text.toCharArray();
         byte[] data = Base64Encoder.decode(source);
               
         backupManager.saveFile(file, data);
            
         if(!exists) {
            onReload();
         }
      } catch(Exception e) {
         log.info("Error saving " + to, e);
      }
   }
   
   public void onSave(SaveCommand command) {
      String resource = command.getResource();
      String source = command.getSource();
      
      try {
         if(!command.isDirectory()) {
            File file = new File(root, "/" + resource);            
            String name = file.getName();            
            
            if(!name.contains(".")) {
               commandClient.sendAlert(resource, "Resource " + resource + " has no file extension");
            } else {
               Problem problem = finder.parse(projectName, resource, source);
               boolean exists = file.exists();
               
               if(exists) {
                  backupManager.backupFile(file, projectName);
               }
               if(command.isCreate() && exists) {
                  commandClient.sendAlert(resource, "Resource " + resource + " already exists");
               } else {
                  backupManager.saveFile(file, source);
                  
                  if(problem == null) {
                     commandClient.sendScriptError(resource, "", 0, -1); // clear problem
                  } else {
                     String description = problem.getDescription();
                     int line = problem.getLine();
                     long time = System.currentTimeMillis();
                     
                     commandClient.sendScriptError(resource, description, time, line);
                  }
                  if(!exists) {
                     onReload();
                  }
               } 
            }
         } else {
            File file = new File(root, "/"+resource);
            
            if(!file.exists()) {
               file.mkdirs();
               onReload();
            }
         }
      } catch(Exception e) {
         log.info("Error saving " + resource, e);
      }
   }
   
   public void onRename(RenameCommand command) {
      Boolean dragAndDrop = command.getDragAndDrop();
      String from = command.getFrom();
      String to = command.getTo();
      
      try {
         if(Boolean.TRUE.equals(dragAndDrop)) {
            log.info("Drag and drop from: " + from + " to: " + to);
         }
         File fromFile = new File(root, "/" + from);
         File toFile = new File(root, "/" + to); 
         
         if(!fromFile.equals(root) && toFile.getParentFile().isDirectory()) { // don't rename root
            boolean fromExists = fromFile.exists();
            boolean toExists = toFile.exists();
            
            if(!fromExists) {
               //commandClient.sendAlert(from, "Resource " + from + " does not exist");
            } else {
               if(toExists) {
                  commandClient.sendAlert(to, "Resource " + to + " already exists");
               } else {
                  if(fromFile.renameTo(toFile)){
                     onReload();
                  } else {
                     commandClient.sendAlert(from, "Could not rename " + from + " to " + to);
                  }
               }
            }
         } 
      } catch(Exception e) {
         log.info("Error renaming " + from, e);
      }
   }   
   
   public void onExecute(ExecuteCommand command) {
      String resource = command.getResource();
      String source = command.getSource();
      
      try {
         Problem problem = finder.parse(projectName, resource, source);
         
         if(problem == null) {
            File file = new File(root, "/" + resource);
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, projectName);
            }
            backupManager.saveFile(file, source);
            commandClient.sendScriptError(resource, "", 0, -1); // clear problem
            processManager.register(forwarder); // make sure we are registered
            processManager.execute(command, commandFilter); 
         } else {
            String description = problem.getDescription();
            int line = problem.getLine();
            long time = System.currentTimeMillis();
            
            commandClient.sendScriptError(resource, description, time, line);
         }
      } catch(Exception e) {
         log.info("Error executing " + resource, e);
      }
   }

   public void onCreateArchive(CreateArchiveCommand command) {
      final String resource = command.getResource();
      final String archive = command.getArchive();
      
      try {
         if(archive.endsWith(".jar")) {
            final String name = command.getProject();
            final String scriptPath = project.getScriptPath(resource);
            final File rootPath = project.getBasePath();
            final File savePath = new File(rootPath, archive);
            final Runnable exportTask = new Runnable() {
               
               @Override
               public void run() {
                  try {
                     log.info("Exporting archive for {} with {}", name, scriptPath);
                     File archivePath = project.getExportedArchive(scriptPath);
                     
                     if(savePath.exists()) {
                        savePath.delete();
                     }
                     archivePath.renameTo(savePath);
                  } catch(Exception e) {
                     try {
                        String message = e.getMessage();
                        commandClient.sendAlert(archive, message);
                     } catch(Exception ex) {
                        log.info("Error creating archive " + archive, e);
                     }
                  }
               }
            };
            Thread thread = factory.newThread(exportTask);
            thread.start();
         } else {
            commandClient.sendAlert(archive, "Archive " + archive + " should end with .jar");
         }
      } catch(Exception e) {
         log.info("Error creating archive " + archive, e);
      }
   }
   
   public void onRemoteDebug(RemoteDebugCommand command) {
      String project = command.getProject();
      String address = command.getAddress();
      
      try {
         String[] addressParts = address.trim().split(":");
         
         if(addressParts.length > 1) {
            String remoteHost = addressParts[0];
            int remotePort = Integer.parseInt(addressParts[1]);
            AttachResponse response = debugService.attach(project, remoteHost, remotePort);
            String process = response.getProcess();
            
            if(processManager.debug(command, process)) {
               log.info("Successfully attached to " + process + "@" + address);
            } else {
               log.info("Could not find process " + process);
            }
         } else {
            log.info("Invalid remote debug address " + address);
         }
      } catch(Exception e) {
         log.info("Error attaching to remote debugger " + address, e);
      }
   }
   
   public void onAttach(AttachCommand command) {
      String process = command.getProcess();
      
      try {
         String focus = commandFilter.getFocus();
         
         if(focus == null) { // not focused
            if(command.isFocus()) {
               commandFilter.setFocus(process);
            }
         } else if(process.equals(focus)) { // focused
            if(command.isFocus()) {
               commandFilter.setFocus(process); // accept messages from this process
            } else {
               commandFilter.clearFocus(); // clear the focus
            }
         } else {
            if(command.isFocus()) {
               commandFilter.setFocus(process);
            }
         }
         processManager.attach(command, process);
         processManager.register(forwarder); // make sure we are registered
      } catch(Exception e) {
         log.info("Error attaching to process " + process, e);
      }
   }
   
   public void onStep(StepCommand command) {
      String thread = command.getThread();
      String focus = commandFilter.getFocus();
            
      try {
         if(focus != null) {
            processManager.step(command, focus);
         }
      } catch(Exception e) {
         log.info("Error stepping through " + thread +" in process " + focus, e);
      }
   }
   
   public void onDelete(DeleteCommand command) {
      String resource = command.getResource();
      
      try {
         File file = new File(root, "/" + resource);
         
         if(!file.equals(root)) { // don't delete root
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, projectName);
               
               if(file.isDirectory()) {
                  
               }
               file.delete();
               onReload();
            }
         }
      } catch(Exception e) {
         log.info("Error deleting " + resource, e);
      }
   }
   
   public void onBreakpoints(BreakpointsCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.breakpoints(command, focus);
         }
      } catch(Exception e){
         log.info("Error setting breakpoints for process " + focus, e);
      }
   }
   
   public void onBrowse(BrowseCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.browse(command, focus);
         }
      } catch(Exception e) {
         log.info("Error browsing variables for process " + focus, e);
      }
   }
   
   public void onEvaluate(EvaluateCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.evaluate(command, focus);
         }
      } catch(Exception e) {
         log.info("Error browsing variables for process " + focus, e);
      }
   }
   
   public void onFolderExpand(FolderExpandCommand command) {
      String focus = commandFilter.getFocus();
      String folder = command.getFolder();
      String project = command.getProject();
      
      try {
         TreeContext context = treeManager.getContext(root, project, cookie, true);
         
         if(context != null) {
            log.info("Expand folder: " + folder);
            context.folderExpand(folder);
            // notify all sessions of expand
            commandSession.sendFolderExpandAlert(command);
         }
      } catch(Exception e) {
         log.info("Error stopping process " + focus, e);
      }
   }
   
   public void onFolderCollapse(FolderCollapseCommand command) {
      String focus = commandFilter.getFocus();
      String folder = command.getFolder();
      String project = command.getProject();
      
      try {
         TreeContext context = treeManager.getContext(root, project, cookie, true);
         
         if(context != null) {
            log.info("Collapse folder: " + folder);
            context.folderCollapse(folder);
            // notify all sessions of expand
            commandSession.sendFolderCollapseAlert(command);
         }
      } catch(Exception e) {
         log.info("Error stopping process " + focus, e);
      }
   }
   
   public void onDisplayUpdate(DisplayUpdateCommand command) {
      int fontSize = command.getFontSize();
      String fontName = command.getFontName();
      String themeName = command.getThemeName();
      Map<String, String> availableFonts = command.getAvailableFonts();
      
      try {
         DisplayDefinition definition = displayPersister.readDefinition();
         
         if(definition != null) {
            if(fontName != null) {
               definition.setFontName(fontName);
            }
            if(fontSize > 0) {
               definition.setFontSize(fontSize);
            }
            if(themeName != null) {
               definition.setThemeName(themeName);
            }
            if(availableFonts != null) {
               definition.setAvailableFonts(availableFonts);
            }
            displayPersister.saveDefinition(definition);
         }
      } catch(Exception e) {
         log.info("Error saving definition", e);
      }
   }
   
   public void onStop(StopCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            try {
               processManager.stop(focus);
            } catch(Exception e) {
               log.info("Could not stop process", e);
            }
            commandClient.sendProcessTerminate(focus);
            commandFilter.clearFocus();
         }
      } catch(Exception e) {
         log.info("Error stopping process " + focus, e);
      }
   }
   
   public void onPing(PingCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            long time = System.currentTimeMillis();

            if(!processManager.ping(focus, time)) {
               commandClient.sendProcessTerminate(focus);
               commandFilter.clearFocus();
            }
         }
         long projectModification = project.getModificationTime();
         long previousModification = lastModified.get();
         
         if(previousModification < projectModification) {
            onReload();
         }
         try {
            List<DependencyFile> files = project.getDependencies();
            
            for(DependencyFile file : files) {
               String message = file.getMessage();
               
               if(message != null){
                  long time = System.currentTimeMillis();
                  commandClient.sendDependencyError(ProjectConfiguration.PROJECT_FILE, message, time, 1);
               }
            }
         } catch(Exception e) {
            String message = e.getMessage();
            long time = System.currentTimeMillis();
            
            commandClient.sendDependencyError(ProjectConfiguration.PROJECT_FILE, message, time, 1);
         }
         processManager.register(forwarder); // make sure we are registered
      } catch(Exception e) {
         log.info("Error pinging process " + focus, e);
      }
   }
   
   public void onPing() {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            long time = System.currentTimeMillis();
            
            if(!processManager.ping(focus, time)) {
               commandClient.sendProcessTerminate(focus);
               commandFilter.clearFocus();
            }
         }
         processManager.register(forwarder); // make sure we are registered
         Set<Problem> problems = problemFinder.collectProblems(path);
//         Map<String, TypeNode> nodes = loader.compileProject(path);
//         Set<String> names = nodes.keySet();
//         int index = 0;
//         
//         for(String name : names) {
//            String[] pair = name.split(":");
//            TypeNode node = nodes.get(name);
//            index++;
//            
//            if(node.isModule()) {
//               logger.log(index + " module " + pair[0] + " --> '" + pair[1] + "'");
//            } else {
//               logger.log(index + " class " + pair[0] + " --> '" + pair[1] + "'");
//            }
//         }
         for(Problem problem : problems) {
            String description = problem.getDescription();
            String path = problem.getResource();
            int line = problem.getLine();
            long time = System.currentTimeMillis();
            
            commandClient.sendScriptError(path,description,  time, line);
         }
      } catch(Exception e) {
         log.info("Error pinging process " + focus, e);
      }
   }
   
   public void onReload() {
      try {
         lastModified.set(project.getModificationTime());
         commandClient.sendReloadTree();
      } catch(Exception e) {
         log.info("Error reloading tree", e);
      }
   }
   
   public void onClose() {
      try {
         //client.sendProcessTerminate();
         processManager.remove(forwarder);
      } catch(Exception e) {
         log.info("Error removing listener", e);
      }
   }
}