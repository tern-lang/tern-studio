package org.ternlang.studio.service.command;

import static org.ternlang.studio.service.command.CommandOrigin.CLIENT;
import static org.ternlang.studio.service.command.CommandOrigin.ENGINE;
import static org.ternlang.studio.service.command.CommandOrigin.PROCESS;

public enum CommandType {
   PRINT_OUTPUT(PrintOutputCommandMarshaller.class, PrintOutputCommand.class, PROCESS),
   PRINT_ERROR(PrintErrorCommandMarshaller.class, PrintErrorCommand.class, PROCESS),
   EXECUTE(ExecuteCommandMarshaller.class, ExecuteCommand.class, CLIENT),
   SAVE(SaveCommandMarshaller.class, SaveCommand.class, CLIENT),
   RENAME(RenameCommandMarshaller.class, RenameCommand.class, CLIENT),   
   EXPLORE(ExploreCommandMarshaller.class, ExploreCommand.class, CLIENT),
   DELETE(DeleteCommandMarshaller.class, DeleteCommand.class, CLIENT),
   RELOAD_TREE(ReloadTreeCommandMarshaller.class, ReloadTreeCommand.class, ENGINE),
   BREAKPOINTS(BreakpointsCommandMarshaller.class, BreakpointsCommand.class, CLIENT),
   TERMINATE(TerminateCommandMarshaller.class, TerminateCommand.class, ENGINE),
   EXIT(ExitCommandMarshaller.class, ExitCommand.class, PROCESS),
   STOP(StopCommandMarshaller.class, StopCommand.class, CLIENT),
   PROBLEM(ProblemCommandMarshaller.class, ProblemCommand.class, ENGINE),
   SCOPE(ScopeCommandMarshaller.class, ScopeCommand.class, PROCESS),
   STEP(StepCommandMarshaller.class, StepCommand.class, CLIENT),
   BROWSE(BrowseCommandMarshaller.class, BrowseCommand.class, CLIENT),
   BEGIN(BeginCommandMarshaller.class, BeginCommand.class, PROCESS),
   PROFILE(ProfileCommandMarshaller.class, ProfileCommand.class, PROCESS),
   STATUS(StatusCommandMarshaller.class, StatusCommand.class, PROCESS),
   ATTACH(AttachCommandMarshaller.class, AttachCommand.class, CLIENT),
   ALERT(AlertCommandMarshaller.class, AlertCommand.class, ENGINE),
   EVALUATE(EvaluateCommandMarshaller.class, EvaluateCommand.class, CLIENT),
   PING(PingCommandMarshaller.class, PingCommand.class, CLIENT),
   FOLDER_EXPAND(FolderExpandCommandMarshaller.class, FolderExpandCommand.class, CLIENT),
   FOLDER_COLLAPSE(FolderCollapseCommandMarshaller.class, FolderCollapseCommand.class, CLIENT),
   DISPLAY_UPDATE(DisplayUpdateCommandMarshaller.class, DisplayUpdateCommand.class, CLIENT),
   UPLOAD(UploadCommandMarshaller.class, UploadCommand.class, CLIENT),
   REMOTE_DEBUG(RemoteDebugCommandMarshaller.class, RemoteDebugCommand.class, CLIENT),
   CREATE_ARCHIVE(CreateArchiveCommandMarshaller.class, CreateArchiveCommand.class, CLIENT),
   LAUNCH(LaunchCommandMarshaller.class, LaunchCommand.class, CLIENT),
   OPEN(OpenCommandMarshaller.class, OpenCommand.class, CLIENT);
   
   public final Class<? extends CommandMarshaller> marshaller;
   public final Class<? extends Command> command;
   public final CommandOrigin origin;
   
   private CommandType(Class<? extends CommandMarshaller> marshaller, Class<? extends Command> command, CommandOrigin origin) {
      this.marshaller = marshaller;
      this.command = command;
      this.origin = origin;
   }
}