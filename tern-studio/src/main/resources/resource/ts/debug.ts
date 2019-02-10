import * as $ from "jquery"
import {w2ui} from "w2ui"
import {Common} from "./common"
import {EventBus} from "./socket"
import {FileEditor} from "./editor"
import {Profiler} from "./profiler"
import {VariableManager} from "./variables"
import {ProcessConsole} from "./console"
import {ThreadManager} from "./threads"
import {StatusPanel} from "status"
import {Command} from "commands"
import {FileTree, FilePath} from "tree"

export module DebugManager {
   
   enum ProcessStatus {
      REGISTERING,
      WAITING,
      STARTING,
      COMPILING,
      DEBUGGING,
      RUNNING,
      TERMINATING,
      FINISHED,
      UNKNOWN
   }
   
   class ProcessInfo {
      
      private _status : ProcessStatus;
      private _process: string;
      private _resource: string;
      private _project: string;
      private _system: string;
      private _pid: string;
      private _time: number;
      private _running: boolean; // is anything running
      private _debug: boolean;
      private _focus: boolean;
      private _memory: number;
      private _threads: number;
      
      constructor(process: string, project: string, resource: string, system: string, pid: string, status: ProcessStatus, time: number, running: boolean, debug: boolean, focus: boolean, memory: number, threads: number) {
         this._process = process;
         this._resource = resource;
         this._system = system;
         this._pid = pid;
         this._time = time;
         this._running = running;
         this._focus = focus;
         this._project = project;
         this._memory = memory;
         this._threads = threads;
         this._debug = debug;
         this._status = status;
      }
      
      public getDescription(): string {
         if(this._resource) {
            return this._process + " is " + ProcessStatus[this._status] + " with " + this._resource + " focus is " + this.isFocus();
         }
         return this._process + " is " + ProcessStatus[this._status];
      }      
      
      public getStatus(): ProcessStatus {
         return this._status;
      }  
      
      public getProcess(): string {
         return this._process;
      }
   
      public getProject(): string {
         return this._project;
      }
      
      public getResource(): string{
         return this._resource;
      }    
      
      public getSystem(): string {
         return this._system;
      }

      public getPid(): string {
         return this._pid;
      }
      
      public getMemory(): number {
         return this._memory;
      }
      
      public getThreads(): number {
         return this._threads;
      }
      
      public getTime(): number {
         return this._time;
      }
      
      public isFocus(): boolean {
         return "" + this._focus == "true";
      }
      
      public isRunning(): boolean {
         return "" + this._running == "true";
      }
      
      public isDebug(): boolean {
         return "" + this._debug == "true";
      }
   }   
   
   var statusProcesses = {};
   var statusFocus: string = null;
   
   export function createStatus() {
      EventBus.createRoute("STATUS", createStatusProcess, clearStatus); // status of processes
      EventBus.createRoute("TERMINATE", terminateStatusProcess, null); // clear focus
      EventBus.createRoute("EXIT", terminateStatusProcess, null);
      setInterval(refreshStatusProcesses, 1000); // refresh the status systems every 1 second
   }
   
   function refreshStatusProcesses() {
      var timeMillis = Common.currentTime();
      var activeProcesses = {};
      var expiryCount = 0;
      
      for (var statusProcess in statusProcesses) {
         if (statusProcesses.hasOwnProperty(statusProcess)) {
            var statusProcessInfo: ProcessInfo = statusProcesses[statusProcess];
            
            if(statusProcessInfo != null) {
               var statusTime = statusProcessInfo.getTime();
               
               if(statusTime + 10000 > timeMillis) {
                  activeProcesses[statusProcess] = statusProcessInfo;
               } else {
                  expiryCount++;
               }
            }
         }
      }
      statusProcesses = activeProcesses; // reset refreshed statuses
      
      if(expiryCount > 0) {
         showStatus(); // something expired!
      }
      Command.pingProcess(); // this will force a STATUS event
   }
   
   function terminateStatusProcess(socket, type, text) {
      var message = JSON.parse(text);
      var process: string = message.process;
      
      if(process) {
         var processInfo: ProcessInfo = statusProcesses[process];
         var duration: number = message.duration;
      
         statusProcesses[process] = null;
         console.log(process + " is TERMINATED");
         
         if(duration && processInfo) {
            console.log("Process took " + duration + " ms");
         }
      }
      if(statusFocus == process) {
         //suspendedThreads = {};
         //currentFocusThread = null;
         ThreadManager.terminateThreads();
         clearStatusFocus();
      }
      showStatus();
   }
   
   function createStatusProcess(socket, type, text) { // process is running
      var message = JSON.parse(text);
      var process: string = message.process;
      var status: string = message.status;
      var processMemory: number = Math.round((message.usedMemory / message.totalMemory) * 100);
      var processStatus: ProcessStatus = ProcessStatus[status];
      
      if(!status || !ProcessStatus.hasOwnProperty(status)) {
         processStatus = ProcessStatus.UNKNOWN; // when we have an error
         console.warn("No such status " + status + " setting to " + ProcessStatus[processStatus]);      
      }
      var processInfo: ProcessInfo = statusProcesses[process] = new ProcessInfo(
         process,    
         message.project,         
         message.resource,         
         message.system,
         message.pid,
         processStatus,
         message.time,                  
         message.running, // is anything running
         message.debug,
         message.focus,
         processMemory,
         message.threads
      );
      var description: string = processInfo.getDescription();
      var resource: string = processInfo.getResource();
      
      if(resource != null) {
      // console.log(message);
      //   console.log(description);
      }      
      if(processInfo.isFocus()) {
         updateStatusFocus(process);
      } else {
         if(statusFocus == process) {
            clearStatusFocus(); // we are focus = false
         }
      }
      showStatus();
   }
   
   export function isCurrentStatusFocusRunning() {
      if(statusFocus) {
         var statusProcessInfo: ProcessInfo = statusProcesses[statusFocus];
      
         if(statusProcessInfo) {
            return statusProcessInfo.getResource() != null;
         }
      }
      return false;
   }
   
   export function currentStatusFocus(): string {
      return statusFocus;
   }
   
   function updateStatusFocus(process) {
      var statusInfo: ProcessInfo = statusProcesses[process];
      
      if(statusInfo != null && statusInfo.getResource() != null){
         var statusResourcePath: FilePath = FileTree.createResourcePath(statusInfo.getResource());
         
         $("#toolbarDebug").css('opacity', '1.0');
         $("#toolbarDebug").css('filter', 'alpha(opacity=100)'); // msie
         
         // debug the status info
         //console.log(statusInfo);
         
         StatusPanel.showProcessStatus(statusInfo.getResource(), process, statusInfo.isDebug());
         //$("#process").html("<i>&nbsp;RUNNING: " + statusInfo.resource + " ("+process+")</i>");
      } else {
         $("#toolbarDebug").css('opacity', '0.4');
         $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
         $("#process").html("");
         FileEditor.clearEditorHighlights(); // focus lost so clear breakpoints
      }
      if(statusFocus != process) {
         Profiler.clearProfiler(); // profiler does not apply
         ThreadManager.clearThreads(); // race condition here
         VariableManager.clearVariables();
      }
      ProcessConsole.updateConsoleFocus(process); // clear console if needed
      statusFocus = process;
   }
   
   function clearStatusFocus(){ // clear up stuff
      statusFocus = null;
      ThreadManager.clearThreads(); // race condition here
      VariableManager.clearVariables();
   //   clearProfiler();
   //   clearConsole();
      $("#toolbarDebug").css('opacity', '0.4');
      $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
      $("#process").html("");
   }
   
   function clearStatus() {
      statusProcesses = {};
      statusFocus = null;
      w2ui['debug'].records = [];
      w2ui['debug'].refresh();
   }
   
   export function showStatus() {
      var statusRecords = [];
      var statusIndex = 1;
      
      for (var statusProcess in statusProcesses) {
         if (statusProcesses.hasOwnProperty(statusProcess)) {
            var statusProcessInfo: ProcessInfo = statusProcesses[statusProcess];
            
            if(statusProcessInfo != null) {
               var statusProject: string = statusProcessInfo.getProject();
               
               if(statusProject == Common.getProjectName() || statusProject == null) {
                  var displayName = "<div class='debugIdleRecord'>"+statusProcess+"</div>";
                  var active = "&nbsp;<input type='radio'><label></label>";
                  var resourcePath = "";
                  var debugging: boolean = statusProcessInfo.isDebug();
                  var status: ProcessStatus = statusProcessInfo.getStatus();
                  var running: boolean = false;
                  
                  if(statusFocus == statusProcess) {
                     active = "&nbsp;<input type='radio' checked><label></label>";
                  }
                  if(statusProcessInfo.getResource() != null) {
                     var resourcePathDetails: FilePath = FileTree.createResourcePath(statusProcessInfo.getResource());
                     
                     if(statusFocus == statusProcess && debugging) {
                        displayName = "<div class='debugFocusRecord'>"+statusProcess+"</div>";
                     } else {
                        displayName = "<div class='debugRecord'>"+statusProcess+"</div>";               
                     }
                     resourcePath = resourcePathDetails.getResourcePath();
                     running = true;
                  } 
                  statusRecords.push({
                     recid: statusIndex++,
                     name: displayName,
                     active: active,
                     process: statusProcess,
                     status: ProcessStatus[status],
                     running: running,
                     system: statusProcessInfo.getSystem(),
                     pid: statusProcessInfo.getPid(),
                     resource: statusProcessInfo.getResource(),
                     focus: statusFocus == statusProcess,
                     script: resourcePath
                  });
               } else {
                  console.log("Ignoring process " + statusProcess + " as it belongs to " + statusProject);
               }
            }
         }
      }
      Common.updateTableRecords(statusRecords, 'debug'); // update if changed only
   }
}

//ModuleSystem.registerModule("debug", "Debug module: debug.js", null, DebugManager.createStatus, [ "common", "socket", "tree", "threads" ]);