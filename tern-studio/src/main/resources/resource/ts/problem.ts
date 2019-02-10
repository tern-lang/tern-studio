import * as $ from "jquery"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree, FilePath} from "tree"
import {FileEditor, FileEditorBuffer, FileEditorState} from "editor"
import {Project} from "project"

export module ProblemManager {
   
   class ProblemItem {
   
      private _resource: FilePath;
      private _description: string;
      private _line: number;
      private _message: string;
      private _project: string;
      private _time: number;
   
      constructor(resource: FilePath, line: number, description: string, message: string, project: string, time: number) {
         this._description = description;
         this._resource = resource;
         this._line = line;
         this._message = message;
         this._project = project;
         this._time = time;
      }
      
      public isExpired(): boolean {
         return this._time + 120000 < Common.currentTime(); // expire after 2 minutes
      }
      
      public getKey(): string {
         return this._resource.getResourcePath() + ":" + this._line;
      }
      
      public getResourcePath(): FilePath {
         return this._resource;
      }
      
      public getLine(): number {
         return this._line;
      }
      
      public getDescription(): string {
         return this._description;
      }
      
      public getMessage(): string {
         return this._message;
      }
      
      public getProject(): string {
         return this._project;
      }
      
      public getTime(): number {
         return this._time;
      }
   }
   
   
   var currentProblems = {};
   
   export function registerProblems() {
   	EventBus.createRoute('PROBLEM', updateProblems);
      setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
   }
   
   function refreshProblems() {
      var activeProblems = {};
      
      for (var problemKey in currentProblems) {
         if (currentProblems.hasOwnProperty(problemKey)) {
            var problemItem: ProblemItem = currentProblems[problemKey];
            
            if(problemItem != null) {
               var problemResource: FilePath = problemItem.getResourcePath();
               var editorBuffer: FileEditorBuffer = FileEditor.getEditorBufferForResource(problemResource.getResourcePath());
            
               if(!isProblemInactive(editorBuffer, problemItem)) { // if its not inactive keep it
                  activeProblems[problemKey] = problemItem;
               }
            }
         }
      }
      updateActiveProblems(activeProblems);
   }
   
   function updateActiveProblems(activeProblems) {
      var missingProblems: number = 0
      
      for (var problemKey in currentProblems) {
         if (!activeProblems.hasOwnProperty(problemKey)) {
            missingProblems++; // something changed
         }
      }
      if(missingProblems > 0) {
         currentProblems = activeProblems;
         showProblems();
      }
   }
   
   function isProblemInactive(editorBuffer: FileEditorBuffer, problemItem: ProblemItem) {
      var editorResource: FilePath = editorBuffer.getResource();
      var problemTime = problemItem.getTime();
      var lastEditTime = editorBuffer.getLastModified();
      
      if(problemTime < lastEditTime) { // buffer was edited after problem
         return true;
      }
      if(!problemItem.isExpired()) {
         return false;
      }
      if(editorBuffer.isBufferCurrent()) { // never expires if focused                     
         return false;
      } 
      return true; // not current or problem newer than edit
   }
   
   export function showProblems(): boolean {
      var problemRecords = [];
      var problemIndex = 1;
      
      for (var problemKey in currentProblems) {
         if (currentProblems.hasOwnProperty(problemKey)) {
            var problemItem: ProblemItem = currentProblems[problemKey];
            
         	if(problemItem != null) {
         	   problemRecords.push({ 
         	      recid: problemIndex++,
         		   line: problemItem.getLine(),
         		   location: "Line " + problemItem.getLine(), 
                  resource: problemItem.getResourcePath().getFilePath(), // /blah/file.snap 
                  description: problemItem.getMessage(), 
                  project: problemItem.getProject(), 
                  script: problemItem.getResourcePath().getResourcePath() // /resource/<project>/blah/file.snap
               });
         	}
         }
      }
      if(Common.updateTableRecords(problemRecords, 'problems')) {
         highlightProblems(); // highlight them also      
         Project.showProblemsTab(); // focus the problems tab
         return true;
      }
      return false;
   }
   
   export function highlightProblems(){
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var editorResource: FilePath = editorState.getResource();
      
      if(editorResource != null) {
         var highlightUpdates = [];
         
         //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
         for (var problemKey in currentProblems) {
            if (currentProblems.hasOwnProperty(problemKey)) {
               if(Common.stringStartsWith(problemKey, editorResource.getResourcePath())) {
                  var problemItem: ProblemItem = currentProblems[problemKey];
                  
                  if(problemItem != null) {                     
                     highlightUpdates.push(problemItem.getLine()); 
                  }
               } 
            }
         }
         FileEditor.clearEditorHighlights(); 
         
         if(highlightUpdates.length > 0) {
            FileEditor.createMultipleEditorHighlights(highlightUpdates, "problemHighlight");
         }
      }
   }
   
   function updateProblems(socket, type, text) {
   	var problems = w2ui['problems'];
   	var message = JSON.parse(text);
   	var resourcePath: FilePath = FileTree.createResourcePath(message.resource);   	
   	var problemItem: ProblemItem = new ProblemItem(
            resourcePath,
   	      message.line,
   	      message.description,
   	      "<div class='errorDescription'>"+message.description+"</div>",
   	      message.project,
   	      message.time
   	);
   	if(message.line >= 0) {
   	   console.log("Add problem '" + problemItem.getDescription() + "' at line '" + problemItem.getLine() + "'");
   	} else {
   	   console.log("Clear all problems for " + problemItem.getResourcePath() + "");
   	}  	
   	
   	if(problemItem.getLine() >= 0) {
   	   currentProblems[problemItem.getKey()] = problemItem;
   	} else {
         for (var problemKey in currentProblems) {
            if (currentProblems.hasOwnProperty(problemKey)) {
               if(Common.stringStartsWith(problemKey, resourcePath.getResourcePath())) {
                  currentProblems[problemKey] = null;
               }
            }
         }
   	}
   	showProblems(); // if it has changed then highlight
   }
}

//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]);