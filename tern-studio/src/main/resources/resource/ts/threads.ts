import * as $ from "jquery"
import {w2ui} from "w2ui"
import {EventBus} from "socket"
import {Common} from "common"
import {FileTree, FilePath} from "tree"
import {FileEditor, FileEditorState} from "editor"
import {VariableManager} from "variables"
import {FileExplorer} from "explorer"
import {Profiler} from "profiler"
import {StatusPanel} from "status"
import {ProblemManager} from "problem"

export enum ThreadStatus {
   RUNNING,
   SUSPENDED,
   UNKNOWN
}

export class ThreadScope {
   
   private _variables: ThreadVariables;
   private _evaluation: ThreadVariables;
   private _status: ThreadStatus;
   private _instruction: string;
   private _process: string;
   private _resource: string;
   private _source: string;
   private _thread: string;
   private _stack: string;
   private _line: number;
   private _depth: number;
   private _key: number;
   private _change: number;
   
   constructor(variables: ThreadVariables,
               evaluation: ThreadVariables,
               status: ThreadStatus,
               instruction: string,
               process: string,
               resource: string,
               source: string,
               thread: string,
               stack: string,
               line: number,
               depth: number,
               key: number,
               change: number) 
   {
      
      this._variables = variables;
      this._evaluation = evaluation;
      this._status = status;
      this._instruction = instruction;
      this._process = process;
      this._resource = resource;
      this._source = source;
      this._thread = thread;
      this._stack =stack;
      this._line = line;
      this._depth = depth;
      this._change = change;
      this._key= key;
   }
   
   public getVariables(): ThreadVariables {
      return this._variables;
   }
   
   public getEvaluation(): ThreadVariables {
      return this._evaluation;
   }
   
   public getStatus(): ThreadStatus{
      return this._status;
   }
   
   public getInstruction(): string{
      return this._instruction;
   }
   
   public getProcess(): string{
      return this._process;
   }
   
   public getResource(): string{
      return this._resource;
   }
   
   public getSource(): string{
      return this._source;
   }
   
   public getThread(): string{
      return this._thread;
   }
   
   public getStack(): string{
      return this._stack;
   }
   
   public getLine(): number{
      return this._line;
   }
   
   public getDepth(): number{
      return this._depth;
   }
   
   public getKey(): number{
      return this._key;
   }
   
   public getChange(): number{
      return this._change; 
   }
   
}

export class ThreadVariables {
   
   private _variables;
   
   constructor(variables) {
      this._variables = variables;
   }
   
   public getVariables(){
      return this._variables;
   }
   
}

export module ThreadManager {   
   
   var suspendedThreads: any = {};
   var threadEditorFocus: ThreadScope = null;
   
   export function createThreads() {
      EventBus.createRoute("BEGIN", startThreads, clearThreads);
      EventBus.createRoute("SCOPE", updateThreads, VariableManager.clearVariables);
      EventBus.createRoute("TERMINATE", deleteThreads);
      EventBus.createRoute("EXIT", deleteThreads);
   }
   
   function startThreads(socket, type, text) {
      var message = JSON.parse(text);
      
      suspendedThreads = {};
      clearFocusThread();
      VariableManager.clearVariables();
      Profiler.clearProfiler();
      clearThreads();
      
      StatusPanel.showProcessStatus(message.resource, message.process, message.debug);
   }
   
   function deleteThreads(socket, type, text) {
      var message = JSON.parse(text);
      var process: string = message.process;
      
      if(threadEditorFocus != null && threadEditorFocus.getProcess() == process) { // clear if it dies
         terminateThreads();
      }
   }
   
   export function terminateThreads() {
      suspendedThreads = {};
      clearFocusThread();
      FileEditor.clearEditorHighlights(); // this should be done in editor.js, i.e EventBus.createRoute("EXIT" ... )
      ProblemManager.highlightProblems(); // don't hide the errors
      VariableManager.clearVariables();
      clearThreads();
   }
   
   export function clearThreads() {
      clearFocusThread();
      w2ui['threads'].records = [];
      w2ui['threads'].refresh();
      $("#process").html("");
   }
   
   function updateThreads(socket, type, text) {
      var message = JSON.parse(text);
      var status: string = message.status;
      var threadVariables: ThreadVariables = new ThreadVariables(message.variables),
      var threadEvaluation: ThreadVariables = new ThreadVariables(message.evaluation),
      var threadStatus: ThreadStatus = ThreadStatus[status];
      
      if(!status || !ThreadStatus.hasOwnProperty(status)) {
         threadStatus = ThreadStatus.UNKNOWN; // when we have an error
         console.warn("No such thread status " + status + " setting to " + ThreadStatus[threadStatus]);      
      }   
      var threadScope: ThreadScope = new ThreadScope(
            threadVariables,
            threadEvaluation,
            threadStatus,         
            message.instruction,
            message.process,
            message.resource,
            message.source,
            message.thread,
            message.stack,
            message.line,
            message.depth,
            message.key,
            message.change
      );
      if(isThreadFocusResumed(threadScope)) {
         clearFocusThread(); // clear focus as it is a resume
         updateThreadPanels(threadScope);
         FileEditor.clearEditorHighlights(); // the thread has resumed so clear highlights
      } else {
         if(threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) { // has the thread been suspended
            if(isThreadFocusUpdateNew(threadScope)) {
               updateFocusedThread(threadScope); // something new happened so focus editor
               updateThreadPanels(threadScope);
            }
         } else if(!threadEditorFocus || threadEditorFocus.getThread() == null) {  // we have to focus the thread
            focusThread(threadScope);            
            updateThreadPanels(threadScope);
         } else {
            var currentScope = suspendedThreads[threadScope.getThread()];
            
            if(isThreadScopeDifferent(currentScope, threadScope)) {
               updateThreadPanels(threadScope);
            }
         }
      }
      suspendedThreads[threadScope.getThread()] = threadScope;
      showThreadBreakpointLine(threadScope); // show breakpoint on editor
   } 
   
   function isThreadScopeDifferent(leftScope: ThreadScope, rightScope: ThreadScope) {
      if(leftScope != null && rightScope != null) {
         if(leftScope.getThread() != rightScope.getThread()) {
            return true;
         }
         if(leftScope.getStatus() != rightScope.getStatus()) {
            return true;
         }
         if(leftScope.getResource() != rightScope.getResource()) {
            return true;
         }
         if(leftScope.getLine() != rightScope.getLine()) {
            return true;
         }
         return false;
      }
      return leftScope != rightScope;
   }
   
   function showThreadBreakpointLine(threadScope: ThreadScope) { // show breakpoint if focused
      var editorState: FileEditorState = FileEditor.currentEditorState();
      
      if(threadEditorFocus.getThread() == threadScope.getThread()) {
         if(editorState.getResource().getFilePath() == threadScope.getResource() && threadScope.getStatus() == ThreadStatus.SUSPENDED) {
            if(FileEditor.createEditorHighlight(threadScope.getLine(), "threadHighlight")) {
               FileEditor.showEditorLine(threadScope.getLine());
            }
         }
      }
   }
   
   function updateThreadPanels(threadScope: ThreadScope){
      suspendedThreads[threadScope.getThread()] = threadScope; // N.B update suspended threads before rendering
      showThreads();
      VariableManager.showVariables();
   }
   
   function updateFocusedThread(threadScope: ThreadScope) {
      if(isThreadFocusLineChange(threadScope)) { // has the update resulted in a new line or resource
         if(isThreadFocusResourceChange(threadScope)) { // do we need to update the editor with a new resource
            openAndShowThreadResource(threadScope);
         } else {
            updateThreadFocus(threadScope);
            FileEditor.showEditorLine(threadScope.getLine());
         }
      } else {
         updateThreadFocus(threadScope); // record focus thread
      }
   }
   
   function focusThread(threadScope: ThreadScope) {
      var editorState: FileEditorState = FileEditor.currentEditorState();      
      
      if(!editorState ||  !editorState.getResource() || editorState.getResource().getFilePath() != threadScope.getResource()) { // do we need to change resource on hit of breakpoint
         openAndShowThreadResource(threadScope);
      } else {
         updateThreadFocus(threadScope);
         FileEditor.showEditorLine(threadScope.getLine());
      }
   }
   
   function openAndShowThreadResource(threadScope: ThreadScope) {
      var resourcePathDetails: FilePath = FileTree.createResourcePath(threadScope.getResource());
      var scopeSource: string = threadScope.getSource();
   
      if(scopeSource) {
         FileExplorer.showAsTreeFile(resourcePathDetails.getResourcePath(), scopeSource, function(){
            updateThreadFocus(threadScope);
            FileEditor.showEditorLine(threadScope.getLine());
         });
      } else {
         FileExplorer.openTreeFile(resourcePathDetails.getResourcePath(), function(){
            updateThreadFocus(threadScope);
            FileEditor.showEditorLine(threadScope.getLine());
         });
      }
   }
   
   function isThreadFocusResumed(threadScope: ThreadScope) {
      if(threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
         return threadScope.getStatus() != ThreadStatus.SUSPENDED; // the thread has resumed
      }
      return false;
   }
   
   function isThreadFocusUpdateNew(threadScope: ThreadScope) { // have we got a new update
      if(threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) { // is this a new update
         if(threadScope.getStatus() == ThreadStatus.SUSPENDED) {
            if(threadEditorFocus.getKey() != threadScope.getKey()) { // thread position change
               return true;
            }
            if(threadEditorFocus.getChange() != threadScope.getChange()) { // thread variables change, e.g browse
               return true;
            }
         }
      }
      return false;
   }
   
   function isThreadFocusPositionChange(threadScope: ThreadScope) {
      return isThreadFocusLineChange(threadScope) || isThreadFocusResourceChange(threadScope);
   }
   
   function isThreadFocusLineChange(threadScope: ThreadScope) {
      if(threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
         return threadEditorFocus.getLine() != threadScope.getLine(); // hash the thread or focus line changed
      }
      return false;
   }
   
   function isThreadFocusResourceChange(threadScope: ThreadScope) {
      if(threadEditorFocus && threadEditorFocus.getThread()  == threadScope.getThread() ) {
         var editorState: FileEditorState = FileEditor.currentEditorState();
         return editorState.getResource().getFilePath() != threadScope.getResource(); // is there a need to update the editor
      }
      return false;
   }
   
   export function focusedThread(): ThreadScope {
      if(threadEditorFocus && threadEditorFocus.getThread() != null) {
         return suspendedThreads[threadEditorFocus.getThread()];
      }
      return null;
   }
   
   function clearFocusThread() {
      VariableManager.clearVariables(); // clear the browse tree
      var threadCopy: ThreadScope = new ThreadScope(
            new ThreadVariables({}),
            new ThreadVariables({}),            
            ThreadStatus.RUNNING,         
            null,
            null,
            null,
            null,
            null,
            null,
            -1,
            -1,
            -1,
            -1
      );
      threadEditorFocus = threadCopy; 
   }
   
   function updateThreadFocus(threadScope: ThreadScope) {
      var threadCopy: ThreadScope = new ThreadScope(
            threadScope.getVariables(),
            threadScope.getEvaluation(),
            threadScope.getStatus(),   
            threadScope.getInstruction(),
            threadScope.getProcess(),
            threadScope.getResource(),
            threadScope.getSource(),
            threadScope.getThread(),
            threadScope.getStack(),
            threadScope.getLine(),
            threadScope.getDepth(),
            threadScope.getKey(),
            threadScope.getChange()
      );
      threadEditorFocus = threadCopy; 
      updateThreadPanels(threadCopy); // update the thread variables etc..
   } 
   
   export function updateThreadFocusByName(threadName: string) {
      var threadScope: ThreadScope = suspendedThreads[threadName];
      updateThreadFocus(threadScope);
   } 
   
   export function focusedThreadVariables(): ThreadVariables {
      if(threadEditorFocus && threadEditorFocus.getThread() != null) {
         var threadScope: ThreadScope = suspendedThreads[threadEditorFocus.getThread()];
         
         if(threadScope != null) {
            return threadScope.getVariables();
         }
      }
      return new ThreadVariables({});
   }
   
   export function focusedThreadEvaluation(): ThreadVariables {
      if(threadEditorFocus && threadEditorFocus.getThread() != null) {
         var threadScope = suspendedThreads[threadEditorFocus.getThread()];
         
         if(threadScope != null) {
            return threadScope.getEvaluation();
         }
      }
      return new ThreadVariables({});
   }
   
   export function showThreads() {
      var threadRecords = [];
      var threadIndex = 1;
      
      for (var threadName in suspendedThreads) {
         if (suspendedThreads.hasOwnProperty(threadName)) {
            var threadScope: ThreadScope = suspendedThreads[threadName];
            var displayStyle = 'threadSuspended';
            var active = "&nbsp;<input type='radio'>";
            
            showThreadBreakpointLine(threadScope);
            
            if(threadScope.getStatus() != ThreadStatus.SUSPENDED) {
               displayStyle = 'threadRunning';
            } else {
               if(threadEditorFocus.getThread() == threadScope.getThread()) {
                  active = "&nbsp;<input type='radio' checked>";
               }
            }
            var displayName = "<div title='"+threadScope.getStack()+"' class='"+displayStyle+"'>"+threadName+"</div>";
            var resourcePathDetails: FilePath = FileTree.createResourcePath(threadScope.getResource());
            
            threadRecords.push({
               recid: threadIndex++,
               name: displayName,
               thread: threadName,
               status: ThreadStatus[threadScope.getStatus()],
               active: active,
               instruction: threadScope.getInstruction(),
               variables: threadScope.getVariables(),
               resource: threadScope.getResource(),
               key: threadScope.getKey(),
               line: threadScope.getLine(),
               script: resourcePathDetails.getResourcePath()
            });
         }
      }
      Common.updateTableRecords(threadRecords, 'threads'); // update if changed only
   }
}

//ModuleSystem.registerModule("threads", "Thread module: threads.js", null, ThreadManager.createThreads, [ "common", "socket", "explorer" ]);