package tern.studio.project.generate;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import tern.studio.index.classpath.ClassFile;
import tern.studio.index.classpath.ClassFileMarshaller;
import tern.studio.index.scan.ClassPathScanner;
import tern.studio.project.HomeDirectory;
import tern.studio.project.Project;
import tern.studio.project.config.ProjectConfiguration;
import tern.studio.project.config.WorkspaceConfiguration;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Slf4j
@Component
public class SearchIndexFileGenerator implements ConfigFileGenerator {
   
   private static final String SEARCH_INDEX_FILE = ProjectConfiguration.INDEX_FILE;
   private static final String SEARCH_INDEX_DIRECTORY = WorkspaceConfiguration.INDEX_PATH;
   
   private final ClassFileMarshaller marshaller;
   private final Gson gson;
   
   public SearchIndexFileGenerator() {
      this.marshaller = new ClassFileMarshaller();
      this.gson = new GsonBuilder().setPrettyPrinting().create();
   }

   @Override
   public SearchIndexConfigFile generateConfig(Project project) {
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
         files = ClassPathScanner.scanAllClasses(loader);
         
         for(ClassFile file : files) {
            try {
               Map<String, String> data = marshaller.toAttributes(file);
               int modifiers = file.getModifiers();
               
               if(Modifier.isPublic(modifiers)) { // reduce the size of the file
                  dataList.add(data);
               }
            }catch(Throwable e) {
               log.info("Could not load file", e);
            }
         }
         String text = gson.toJson(dataList);
         builder.append(text);
      } catch(Exception e) {
         return null;
      }
      String text = builder.toString();
      return new SearchIndexConfigFile(loader, files, text);
   }

   @Override
   public SearchIndexConfigFile parseConfig(Project project, String content) {
      StringBuilder builder = new StringBuilder();
      List<ClassFile> files = new ArrayList<ClassFile>();
      ClassLoader loader = project.getClassLoader();
      
      try {
         List<Map<String, String>> types = gson.fromJson(content, List.class);
         
         for(Map<String, String> type : types) {
            ClassFile file = marshaller.fromAttributes(type, loader);
            files.add(file);
         }
         builder.append(content);
      } catch(Exception e) {
         log.info("Could not parse search index file " + SEARCH_INDEX_FILE, e);
         return generateConfig(project);
      }
      String text = builder.toString();
      return new SearchIndexConfigFile(loader, files, text);
   }

   @Override
   public File getConfigFilePath(Project project) {
      try {
         String projectName = project.getName();
         File file = HomeDirectory.getPath(SEARCH_INDEX_DIRECTORY, projectName, SEARCH_INDEX_FILE);
         
         return file.getCanonicalFile();
      } catch(Exception e) {
         log.info("Could not create config path " + SEARCH_INDEX_FILE, e);
         throw new IllegalStateException("Could not create config path " + SEARCH_INDEX_FILE, e);
      }
   }
   
   @Override
   public String getConfigName(Project project) {
      return SEARCH_INDEX_FILE;
   }

}
