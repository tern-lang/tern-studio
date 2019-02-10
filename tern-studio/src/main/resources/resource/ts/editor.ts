import * as $ from "jquery"
import {md5} from "md5"
import {ace} from "ace"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {ProcessConsole} from "console"
import {ProblemManager} from "problem"
import {LoadSpinner} from "spinner"
import {FileTree, FilePath} from "tree"
import {ThreadManager} from "threads"
import {History} from "history"
import {VariableManager} from "variables"
import {Project} from "project"
import {StatusPanel} from "status"
import {KeyBinder} from "keys"
import {Command} from "commands"
import {FileResource} from "explorer"

/**
 * Contains the state for the Ace editor and is a singleton instance
 * that exists as soon as the editor is created.
 */
export class FileEditorView {   

   private _editorResource: FilePath;
   private _editorReadOnly: boolean;
   private _editorHistory: any; // store all editor context
   private _editorMarkers: any;   
   private _editorBreakpoints: any; // spans multiple resources      
   private _editorPanel: any; // this actual editor
   
   constructor(editorPanel: any) {
      this._editorPanel = editorPanel;     
      this._editorHistory = {}; // store all editor context
      this._editorMarkers = {};   
      this._editorBreakpoints = {}; // spans multiple resources   
   } 
   
   public activateEditor() {
      KeyBinder.bindKeys(); // register key bindings
      Project.changeProjectFont(); // project.js update font
      FileEditor.scrollEditorToPosition();
      FileEditor.updateProjectTabOnChange(); // listen to change
   }
   
   public updateResourcePath(resourcePath: FilePath, isReadOnly: boolean) {
      window.location.hash = resourcePath.getProjectPath(); // update # anchor
      this._editorResource = resourcePath;
      this._editorReadOnly = isReadOnly;
   }  
   
   public getHistoryForResource(resource: FilePath): FileEditorHistory {
      if(resource) {
         var editorPath = resource.getResourcePath();
         var editorHistory: FileEditorHistory = this._editorHistory[editorPath];
      
         if(!editorHistory) {
            editorHistory = this._editorHistory[editorPath] = new FileEditorHistory(
                  -1,
                  null,
                  null,
                  null,
                  null // let the original be the same
            );                    
         }
         return editorHistory;
      }
      return new FileEditorHistory(
            -1,
            null,
            null,
            null,
            null // let the original be the same
         );
   }
   
   public getEditorResource(): FilePath {
      return this._editorResource;
   }
   
   public isEditorReadOnly(): boolean {
      return this._editorReadOnly;
   }
   
   public getEditorPanel() {
      return this._editorPanel;
   }
   
   public getEditorMarkers() {
      return this._editorMarkers;
   } 
   
   public getEditorBreakpoints() {
      return this._editorBreakpoints;
   }
}

export class FileEditorMarker {
   
   private _style: string;
   private _line: number;
   private _marker: number;
   
   constructor(line: number, style: string, marker: number) {
      this._style = style;
      this._line = line;
      this._marker = marker;
   }
   
   public getLine(): number {
      return this._line;
   }
   
   public getMarker(): number {
      return this._marker;
   }
   
   public getStyle(): string {
      return this._style;
   }
}

export class FileEditorHistory {

   private _undoState: FileEditorUndoState;
   private _position: FileEditorPosition;
   private _lastModified: number;
   private _savedText: string; // save the buffer if it has changed
   private _originalText: string; // this is the original file contents
   private _changeMade: boolean; 
   
   constructor(lastModified: number, undoState: FileEditorUndoState, position: FileEditorPosition, savedText: string, originalText: string) {
      this._lastModified = lastModified;
      this._undoState = undoState;
      this._position = position;
      this._originalText = originalText;
      this._savedText = savedText;
      this._changeMade = false;
   }
   
   public restoreUndoManager(session: any, text: string) {
      var manager = new ace.UndoManager();
      
      if(text == this._savedText) { // text is the same text
         if(this._undoState) {
            var undoStack = this._undoState.getUndoStack();
            var redoStack = this._undoState.getRedoStack();
            
            for (var undoEntry in undoStack) {
               if (undoStack.hasOwnProperty(undoEntry)) {
                  manager.$undoStack[undoEntry] = undoStack[undoEntry];
               }
            } 
            for (var redoEntry in redoStack) {
               if (redoStack.hasOwnProperty(redoEntry)) {
                  manager.$redoStack[redoEntry] = undoStack[redoEntry];
               }
            }
            manager.$doc = session;
            manager.dirtyCounter = this._undoState.getDirtyCounter();
         }
      }
      session.setUndoManager(manager);
   }
   
   public restoreScrollPosition(session: any, panel: any) {
      if(this._position) {
         const scroll: number = this._position.getScroll();
         const row: number = this._position.getRow();
         const column: number = this._position.getColumn();
         
         if(row >= 0 && column >= 0) {
            panel.selection.moveTo(row, column);
         } else {
            panel.gotoLine(1);
         }
         session.setScrollTop(scroll); 
      } else {
         panel.gotoLine(1); 
         session.setScrollTop(0);
      }
      panel.focus();
   }
   
   public saveHistory(editorState: FileEditorState){ 
      var source = editorState.getSource();
      var currentText = editorState.getSource();
      
      this._savedText = currentText;
      this._lastModified = editorState.getLastModified();
      this._undoState = editorState.getUndoState();
      this._position = editorState.getPosition();
   }   
   
   public updateHistory(currentText: string, originalText: string) {
      if(currentText != this._savedText) { 
         this._undoState = null;
         this._position = null;
      }
      this._savedText = currentText;
      this._originalText = originalText;
      this._changeMade = false;
   }
   
   public invalidateHistory() {
      this._changeMade = false;
      this._undoState = null;
      this._lastModified = -1;
      this._originalText = null;
      this._savedText = null; // clear the buffer
   }
   
   public touchHistory(currentText: string) {
      if(currentText != this._savedText) {
         this._lastModified = Common.currentTime();
      }
      this._savedText = currentText;
   }

   public getLastModified(): number {
      return this._lastModified;
   }
   
   public getOriginalText(): string {
      return this._originalText;
   }
   
   public getSavedText(): string {
      return this._savedText;
   }
}

export class FileEditorUndoState {

   private _undoStack: any;
   private _redoStack: any;
   private _dirtyCounter: any;

   constructor(undoStack: any, redoStack: any, dirtyCounter: any) {
      this._undoStack = undoStack;
      this._redoStack = redoStack;
      this._dirtyCounter = dirtyCounter;
   }
   
   public getUndoStack(): any {
      return this._undoStack;
   }
   
   public getRedoStack(): any {
      return this._redoStack;
   }
   
   public getDirtyCounter(): any {
      return this._dirtyCounter;
   }
}

export class FileEditorState {
   
   private _undoState : FileEditorUndoState;
   private _position: FileEditorPosition;
   private _lastModified: number;
   private _breakpoints: any;
   private _resource : FilePath;
   private _source : string;
   private _isReadOnly: boolean;
   
   constructor(lastModified: number, breakpoints: any, resource: FilePath, undoState: FileEditorUndoState, position: FileEditorPosition, source: string, isReadOnly: boolean) {
      this._lastModified = lastModified;
      this._breakpoints = breakpoints;
      this._resource = resource;
      this._undoState = undoState;
      this._isReadOnly = isReadOnly;
      this._position = position;
      this._source = source;
   }
   
   public isStateValid(): boolean {
      return this._resource && (this._source != null && this._source != "");      
   }
   
   public isReadOnly(): boolean {
      return this._isReadOnly;
   }
   
   public getResource(): FilePath {
      return this._resource;
   }
   
   public getPosition(): FileEditorPosition {
      return this._position;
   }
   
   public getUndoState(): FileEditorUndoState {
      return this._undoState;
   }
   
   public getLastModified(): number {
      return this._lastModified;
   }
   
   public getBreakpoints(): any {
      return this._breakpoints;
   }   
   
   public getSource(): string {
      return this._source;
   }
  
}

export class FileEditorPosition {
   
   private _row: number;
   private _column: number;
   private _scroll: number;

   constructor(row: number, column: number, scroll: number) {
      this._row = row;
      this._column = column;
      this._scroll = scroll;
   }
   
   public getRow(): number {
      return this._row;
   }
   
   public getColumn(): number {
      return this._column;
   }
   
   public getScroll(): number {
      return this._scroll;
   }
}

export class FileEditorBuffer {
   
   private _resource : FilePath;
   private _lastModified: number;
   private _source : string;
   private _isCurrent: boolean;

   constructor(lastModified: number, resource: FilePath, source: string, isCurrent: boolean) {
      this._lastModified = lastModified;
      this._isCurrent = isCurrent;
      this._resource = resource;
      this._source = source;
   }
   
   public isBufferValid(): boolean {
      return this._resource && this._source != null;   
   }
   
   public isBufferCurrent(): boolean {
      return this._isCurrent;
   }
   
   public getResource(): FilePath {
      return this._resource;
   }
   
   public getLastModified(): number {
      return this._lastModified;
   } 
   
   public getSource(): string {
      return this._source;
   }
}


/**
 * Groups all the editor functions and creates the FileEditorView that
 * contains the state of the editor session. 
 */
export module FileEditor {

   var editorView: FileEditorView = null;
   
   export function createEditor() {
      editorView = showEditor();
      editorView.activateEditor();
      EventBus.createTermination(clearEditorHighlights); // create callback
   }
   
   export function clearEditorHighlights() {
      var session = editorView.getEditorPanel().getSession();
      var editorMarkers = editorView.getEditorMarkers();
      var editorResource: FilePath = editorView.getEditorResource()
      
//      if(editorResource) {
//         console.log("Clear highlights in " + editorResource.getResourcePath());
//      }
      for (var editorLine in editorMarkers) {
         if (editorMarkers.hasOwnProperty(editorLine)) {
            var marker: FileEditorMarker = editorMarkers[editorLine];
            
            if(marker != null) {
               session.removeMarker(marker.getMarker());
               delete editorMarkers[editorLine];
            }
         }
      }
   }
   
   export function showEditorLine(line) {
      var editor = editorView.getEditorPanel();
      
      editorView.getEditorPanel().resize(true);
      
      if(line > 1) {
         var requestedLine = line - 1;
         var currentLine = getCurrentLineForEditor();
         
         if(currentLine != requestedLine) {
            editorView.getEditorPanel().scrollToLine(requestedLine, true, true, function () {})
            editorView.getEditorPanel().gotoLine(line); // move the cursor
            editorView.getEditorPanel().focus();
         }
      } else {
         editorView.getEditorPanel().scrollToLine(0, true, true, function () {})
         editorView.getEditorPanel().focus();
      }
   }
   
   function clearEditorHighlight(line) {
      var session = editorView.getEditorPanel().getSession();
      var editorMarkers = editorView.getEditorMarkers();
      var marker: FileEditorMarker = editorMarkers[line];
      
      if(marker != null) {
         session.removeMarker(marker.getMarker());
      }
   }
   
   export function createEditorHighlight(line, css): boolean {
      var Range = ace.require('ace/range').Range;
      var session = editorView.getEditorPanel().getSession();
      var editorMarkers = editorView.getEditorMarkers();
      var currentMarker: FileEditorMarker = editorMarkers[line];
      
      // clearEditorHighlight(line);
      clearEditorHighlights(); // clear all highlights in editor

      var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
      var editorMarker: FileEditorMarker = new FileEditorMarker(line, css, marker);
      
      editorMarkers[line] = editorMarker;
      
      if(currentMarker) {
         return currentMarker.getStyle() != css;
      }
      return false;
   }
   
   export function createMultipleEditorHighlights(lines, css): boolean {
      var Range = ace.require('ace/range').Range;
      var session = editorView.getEditorPanel().getSession();
      var editorMarkers = editorView.getEditorMarkers();
      var highlightsChanged = false;
      
      for(var i = 0; i < lines.length; i++) {
         var line = lines[i];
         var currentMarker: FileEditorMarker = editorMarkers[line];
      
         if(currentMarker) {
            if(currentMarker.getStyle() != css) {
               highlightsChanged = true;
            }
         } else {
            highlightsChanged = true;
         }
      }
      
      // clearEditorHighlight(line);
      clearEditorHighlights(); // clear all highlights in editor

      for(var i = 0; i < lines.length; i++) {
         var line = lines[i];
         var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
         var editorMarker: FileEditorMarker = new FileEditorMarker(line, css, marker);

         editorMarkers[line] = editorMarker;
      }
      return highlightsChanged;
   }
   
   export function findAndReplaceTextInEditor(){
      const state: FileEditorState = currentEditorState();
      const resource: FilePath = state.getResource();
   
      Command.searchAndReplaceFiles(resource.getProjectPath());
   }
   
   export function findTextInEditor() {
      const state: FileEditorState = currentEditorState();
      const resource: FilePath = state.getResource();
   
      Command.searchFiles(resource.getProjectPath());
   }
   
   export function addEditorKeyBinding(keyBinding, actionFunction) {
      editorView.getEditorPanel().commands.addCommand({
           name : keyBinding.editor,
           bindKey : {
              win : keyBinding.editor,
              mac : keyBinding.editor
           },
           exec : function(editor) {
              if(actionFunction) { 
                 actionFunction();
              }
           }
      });
   }
   
   function clearEditorBreakpoint(row) {
      var session = editorView.getEditorPanel().getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(row);
      }
      showEditorBreakpoints();
   }
   
   function clearEditorBreakpoints() {
      var session = editorView.getEditorPanel().getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(breakpoint); // XXX is this correct
      }
   }
   
   export function showEditorBreakpoints() {
      var allBreakpoints = editorView.getEditorBreakpoints();
      var breakpointRecords = [];
      var breakpointIndex = 1;
   
      for(var filePath in allBreakpoints) {
         if(allBreakpoints.hasOwnProperty(filePath)) {
            var breakpoints = allBreakpoints[filePath];
   
            for(var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     var resourcePathDetails: FilePath = FileTree.createResourcePath(filePath);
                     var displayName = "<div class='breakpointEnabled'>"+resourcePathDetails.getProjectPath()+"</div>";
                     
                     breakpointRecords.push({
                        recid: breakpointIndex++,
                        name: displayName,
                        location : "Line " + lineNumber,
                        resource : resourcePathDetails.getProjectPath(),
                        line: parseInt(lineNumber),
                        script : resourcePathDetails.getResourcePath()
                     });
                  }
               }
            }
         }
      }
      w2ui['breakpoints'].records = breakpointRecords;
      w2ui['breakpoints'].refresh();
      Command.updateScriptBreakpoints(); // update the breakpoints
   }
   
   function setEditorBreakpoint(row, value) {
      var allBreakpoints = editorView.getEditorBreakpoints();
      
      if (editorView.getEditorResource() != null) {
         var session = editorView.getEditorPanel().getSession();
         var resourceBreakpoints = allBreakpoints[editorView.getEditorResource().getFilePath()];
         var line = parseInt(row);
   
         if (value) {
            session.setBreakpoint(line);
         } else {
            session.clearBreakpoint(line);
         }
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            allBreakpoints[editorView.getEditorResource().getFilePath()] = resourceBreakpoints;
         }
         resourceBreakpoints[line + 1] = value;
      }
      showEditorBreakpoints();
   }
   
   function toggleEditorBreakpoint(row) {
      var allBreakpoints = editorView.getEditorBreakpoints();
      
      if (editorView.getEditorResource() != null) {
         var session = editorView.getEditorPanel().getSession();
         var resourceBreakpoints = allBreakpoints[editorView.getEditorResource().getFilePath()];
         var breakpoints = session.getBreakpoints();
         var remove = false;
   
         for(var breakpoint in breakpoints) {
            if (breakpoint == row) {
               remove = true;
               break;
            }
         }
         if (remove) {
            session.clearBreakpoint(row);
         } else {
            session.setBreakpoint(row);
         }
         var line = parseInt(row);
   
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            resourceBreakpoints[line + 1] = true;
            allBreakpoints[editorView.getEditorResource().getFilePath()] = resourceBreakpoints;
         } else {
            if (resourceBreakpoints[line + 1] == true) {
               resourceBreakpoints[line + 1] = false;
            } else {
               resourceBreakpoints[line + 1] = true;
            }
         }
      }
      showEditorBreakpoints();
   }
   
   export function resizeEditor() {
      var width = document.getElementById('editor').offsetWidth;
      var height = document.getElementById('editor').offsetHeight;
      
      console.log("Resize editor " + width + "x" + height);
      editorView.getEditorPanel().setAutoScrollEditorIntoView(true);
      editorView.getEditorPanel().resize(true);
      // editor.focus();
   }
   
//   export function resetEditor() {
//      var session = editorView.getEditorPanel().getSession();
//      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorView.getEditorResource());      
//      var originalText: string =  editorHistory.getOriginalText();
//         
//      clearEditorHighlights();
//      //editorView.getEditorResource() = null;
//      editorView.getEditorPanel().setReadOnly(false);
//
//      if(originalText) {
//         session.setValue(originalText, 1);
//      } else {
//         session.setValue("", 1);
//      }
//      $("#currentFile").html("");
//   }
   
   function clearEditor() {
      var session = editorView.getEditorPanel().getSession();
   
      for(var editorMarker in session.$backMarkers) { // what is this???
         session.removeMarker(editorMarker);
      }
      clearEditorHighlights(); // clear highlighting
      
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(breakpoint);
      }
      $("#currentFile").html("");
   }
   
   export function currentEditorState(): FileEditorState {
      var editorUndoState: FileEditorUndoState = currentEditorUndoState();
      var editorPosition: FileEditorPosition = currentEditorPosition();
      var editorBuffer: FileEditorBuffer = currrentEditorBuffer();
      var editorLastModified = -1;
      var editorText = null;      
      
      if(editorBuffer) {
         editorText = editorBuffer.getSource();
         editorLastModified = editorBuffer.getLastModified();
      }
      return new FileEditorState(
         editorLastModified,
         editorView.getEditorBreakpoints(),
         editorView.getEditorResource(),
         editorUndoState,
         editorPosition,
         editorText,
         editorView.isEditorReadOnly()
      );
   }
   
   function currentEditorText(): string{
      return editorView.getEditorPanel().getValue();
   }
   
   function currentEditorPosition(): FileEditorPosition {
      var scrollTop = editorView.getEditorPanel().getSession().getScrollTop();
      var editorCursor = editorView.getEditorPanel().selection.getCursor();

      if(editorCursor) {
         return new FileEditorPosition(
               editorCursor.row,
               editorCursor.column,
               scrollTop
         );
      }
      return new FileEditorPosition( 
         null,
         null,
         scrollTop
      );
   }
   
   function currentEditorUndoState(): FileEditorUndoState {
      var session = editorView.getEditorPanel().getSession();
      var manager = session.getUndoManager();
      var undoStack = $.extend(true, {}, manager.$undoStack);
      var redoStack = $.extend(true, {}, manager.$redoStack);

      return new FileEditorUndoState(
         undoStack,
         redoStack,
         manager.dirtyCounter
      );
   }
   
   export function resolveEditorMode(resource) {
      var token = resource.toLowerCase();
      
      if(Common.stringEndsWith(token, ".snap")) {
         return "ace/mode/snapscript";
      }
      if(Common.stringEndsWith(token, ".policy")) {
         return "ace/mode/policy";
      }
      if(Common.stringEndsWith(token, ".xml")) {
         return "ace/mode/xml";
      }
      if(Common.stringEndsWith(token, ".json")) {
         return "ace/mode/json";
      }
      if(Common.stringEndsWith(token, ".sql")) {
         return "ace/mode/sql";
      }
      if(Common.stringEndsWith(token, ".pl")) {
         return "ace/mode/perl";
      }
      if(Common.stringEndsWith(token, ".kt")) {
         return "ace/mode/kotlin";
      }
      if(Common.stringEndsWith(token, ".js")) {
         return "ace/mode/javascript";
      }
      if(Common.stringEndsWith(token, ".ts")) {
         return "ace/mode/typescript";
      }
      if(Common.stringEndsWith(token, ".java")) {
         return "ace/mode/java";
      }  
      if(Common.stringEndsWith(token, ".groovy")) {
         return "ace/mode/groovy";
      }  
      if(Common.stringEndsWith(token, ".py")) {
         return "ace/mode/python";
      } 
      if(Common.stringEndsWith(token, ".html")) {
         return "ace/mode/html";
      }
      if(Common.stringEndsWith(token, ".htm")) {
         return "ace/mode/html";
      }
      if(Common.stringEndsWith(token, ".txt")) {
         return "ace/mode/text";
      }
      if(Common.stringEndsWith(token, ".properties")) {
         return "ace/mode/properties";
      }
      if(Common.stringEndsWith(token, ".gitignore")) {
         return "ace/mode/text";
      }
      if(Common.stringEndsWith(token, ".project")) {
         return "ace/mode/xml";
      }
      if(Common.stringEndsWith(token, ".classpath")) {
         return "ace/mode/text";
      }
      return "ace/mode/text";
   }
   
   function saveEditorHistory() {
      var editorState: FileEditorState = currentEditorState();
   
      if(!editorState.isReadOnly()) {
         var editorPath: FilePath = editorState.getResource();
         var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorPath);
      
         if(editorState.isStateValid()) {      
            editorHistory.saveHistory(editorState);         
         } else {
            editorHistory.invalidateHistory();
         }
      }
   }
   
   function createEditorUndoManager(session: any, textToDisplay: string, originalText: string, resource: FilePath) {
      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(resource);         
   
      editorView.getEditorPanel().setReadOnly(false);
      editorView.getEditorPanel().setValue(textToDisplay, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
      editorHistory.updateHistory(textToDisplay, originalText);
      editorHistory.restoreUndoManager(session, textToDisplay);     
   } 
   
   function createEditorWithoutUndoManager(textToDisplay: string) {
      editorView.getEditorPanel().setReadOnly(false);
      editorView.getEditorPanel().setValue(textToDisplay, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
   }
   
   export function clearSavedEditorBuffer(resource: string) {
      var editorResource: FilePath = FileTree.createResourcePath(resource);
      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
      
      editorHistory.invalidateHistory();
      updateEditorTabMarkForResource(resource); // remove the *      
   } 
   
   export function currrentEditorBuffer(): FileEditorBuffer {
      if(editorView.getEditorResource()) {
         var editorPath: string = editorView.getEditorResource().getResourcePath();
         return getEditorBufferForResource(editorPath);
      }
      return null;
   }
   
   export function getEditorBufferForResource(resource: string): FileEditorBuffer {
      var editorResource: FilePath = FileTree.createResourcePath(resource);
      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
      var lastModifiedTime: number = editorHistory.getLastModified();
      
      if(isEditorResourcePath(editorResource.getResourcePath())) {
         var editorText: string = currentEditorText();
         
         return new FileEditorBuffer(
            lastModifiedTime,
            editorResource,
            editorText, // if its the current buffer then return it
            true
         );
      }      
      return new FileEditorBuffer(
            lastModifiedTime,
            editorResource,
            editorHistory.getSavedText(), // if its the current buffer then return it
            false
         );             
   }
   
   function resolveEditorTextToUse(fileResource: FileResource) {
      var encodedText: string = fileResource.getFileContent();
      var isReadOnly = fileResource.isHistorical() || fileResource.isError();
      
      console.log("resource=[" + fileResource.getResourcePath().getResourcePath() + 
            "] modified=[" + fileResource.getTimeStamp() + "] length=[" + fileResource.getFileLength() + "] readonly=[" + isReadOnly +"]");
      
      if(!isReadOnly) {
         var savedHistoryBuffer: FileEditorBuffer = getEditorBufferForResource(fileResource.getResourcePath().getResourcePath()); // load saved buffer
   
         if(savedHistoryBuffer.getSource() && savedHistoryBuffer.getLastModified() > fileResource.getLastModified()) {
            console.log("LOAD FROM HISTORY diff=[" + (savedHistoryBuffer.getLastModified() - fileResource.getLastModified()) + "]");
            return savedHistoryBuffer.getSource();
         }
         console.log("IGNORE HISTORY: ", savedHistoryBuffer);
      } else {
         console.log("IGNORE HISTORY WHEN READ ONLY");
      }
      return encodedText;
   }
   
   export function updateEditor(fileResource: FileResource) { // why would you ever ignore an update here?
      var resourcePath: FilePath = fileResource.getResourcePath();
      var isReadOnly = fileResource.isHistorical() || fileResource.isError();
      var realText: string = fileResource.getFileContent();
      var textToDisplay = resolveEditorTextToUse(fileResource);
      var session = editorView.getEditorPanel().getSession();
      var currentMode = session.getMode();
      var actualMode = resolveEditorMode(resourcePath.getResourcePath());

      saveEditorHistory(); // save any existing history
      
      if(actualMode != currentMode) {
         session.setMode({
            path: actualMode,
            v: Date.now() 
         })
      }
      if(!isReadOnly) {
         createEditorUndoManager(session, textToDisplay, realText, resourcePath); // restore any existing history      
      } else {
         createEditorWithoutUndoManager(textToDisplay);
      }
      clearEditor();
      setReadOnly(isReadOnly);
      
      editorView.updateResourcePath(resourcePath, isReadOnly);
      ProblemManager.highlightProblems(); // higlight problems on this resource
      
      if (resourcePath != null && editorView.getEditorResource()) {
         var filePath: string = editorView.getEditorResource().getFilePath();
         var allBreakpoints = editorView.getEditorBreakpoints();
         var breakpoints = allBreakpoints[filePath];
   
         if (breakpoints != null) {
            for(var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     setEditorBreakpoint(parseInt(lineNumber) - 1, true);
                  }
               }
            }
         }
      }
      Project.createEditorTab(); // update the tab name
      History.showFileHistory(); // update the history
      StatusPanel.showActiveFile(editorView.getEditorResource().getProjectPath());  
      FileEditor.showEditorFileInTree();
      scrollEditorToPosition();
      updateEditorTabMark(); // add a * to the name if its not in sync
   }
   
   export function focusEditor() {
      editorView.getEditorPanel().focus();
   }
   
   export function setReadOnly(isReadOnly) {
      editorView.getEditorPanel().setReadOnly(isReadOnly);
   }
   
   export function showEditorFileInTree() {
      var editorState: FileEditorState = currentEditorState();
      var resourcePath: FilePath = editorState.getResource();
      
      FileTree.showTreeNode('explorerTree', resourcePath);
   }
   
   export function getCurrentLineForEditor() {
      return editorView.getEditorPanel().getSelectionRange().start.row;
   }
   
   export function getSelectedText() {
      return editorView.getEditorPanel().getSelectedText();
   }
   
   export function isEditorChanged() {
      if(editorView.getEditorResource() != null) {
         var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
         var currentText = editorView.getEditorPanel().getValue(); 
         var originalText = editorHistory.getOriginalText();
         
         return currentText != originalText;
      }
      return false;
   }
   
   export function isEditorChangedForPath(resource: string) {      
      if(isEditorResourcePath(resource)) {
         return isEditorChanged();
      }
      var resourcePath: FilePath = FileTree.createResourcePath(resource);
      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(resourcePath);
      var originalText: string = editorHistory.getOriginalText();
      var lastSavedText: string = editorHistory.getSavedText();
      
      return originalText != lastSavedText;
   }
   
   function isEditorResourcePath(resource: string) {
      if(editorView.getEditorResource() != null) {
         if(editorView.getEditorResource().getResourcePath() == resource) {
            return true;
         }
      }
      return false;
   }
   
   export function scrollEditorToPosition() {
      var session = editorView.getEditorPanel().getSession();
      var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
      
      editorHistory.restoreScrollPosition(session, editorView.getEditorPanel());
   }
   
   export function updateProjectTabOnChange() {
      editorView.getEditorPanel().on("input", function() {
         var editorResource: FilePath = editorView.getEditorResource();
         var editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
         var editorText = currentEditorText();
         
         editorHistory.touchHistory(editorText);         
         updateEditorTabMark(); // on input then you update star
     });
   }
   
   function updateEditorTabMark() {
      updateEditorTabMarkForResource(editorView.getEditorResource().getResourcePath());
   }
   
   function updateEditorTabMarkForResource(resource: string) {
      Project.markEditorTab(resource, isEditorChangedForPath(resource));
   }
   
   function createEditorAutoComplete() {
      return {
         getCompletions: function createAutoComplete(editor, session, pos, prefix, callback) {
//             if (prefix.length === 0) { 
//                callback(null, []); 
//                return; 
//             }
             var text = editor.getValue();
             var line = editor.session.getLine(pos.row);
             var resource = editorView.getEditorResource().getProjectPath();
             var complete = line.substring(0, pos.column);
             var message = JSON.stringify({
                resource: resource,
                line: pos.row + 1,
                complete: complete,
                source: text,
                prefix: prefix
             });
             $.ajax({
                contentType: 'application/json',
                data: message,
                dataType: 'json',
                success: function(response){
                   var expression = response.expression;

                   if(expression) {
                       var dotIndex = Math.max(0, expression.lastIndexOf('.') + 1);
                       var tokens = response.tokens;
                       var length = tokens.length;
                       var suggestions = [];

                       for(var token in tokens) {
                          if (tokens.hasOwnProperty(token)) {
                             var type = tokens[token];

                             if(Common.stringStartsWith(token, expression)) {
                                token = token.substring(dotIndex);
                             }
                             suggestions.push({className: 'autocomplete_' + type, token: token, value: token, score: 300, meta: type });
                          }
                       }
                       callback(null, suggestions);
                   }
                },
                error: function(){
                    console.log("Completion control failed");
                },
                processData: false,
                type: 'POST',
                url: '/complete/' + Common.getProjectName()
            });
         }
      }
   }
   
   // XXX this should be in commands
   export function formatEditorSource() {
      var text: string = editorView.getEditorPanel().getValue();
      var path: string = editorView.getEditorResource().getFilePath();
      
      $.ajax({
         contentType: 'text/plain',
         data: text,
         success: function(result){
            editorView.getEditorPanel().setReadOnly(false);
            editorView.getEditorPanel().setValue(result, 1);
         },
         error: function(){
             console.log("Format failed");
         },
         processData: false,
         type: 'POST',
         url: '/format/' + Common.getProjectName() + path
     });
   }
   
   export function setEditorTheme(theme) {
      if(theme != null){
         if(editorView.getEditorPanel() != null) {
            editorView.getEditorPanel().setTheme(theme);
         }
         //editorView.editorTheme = theme;
      }
   }
   
   function showEditor(): FileEditorView {
      var editor = ace.edit("editor");
      var autoComplete = createEditorAutoComplete();

      editor.completers = [autoComplete];
      // setEditorTheme("eclipse"); // set the default to eclipse
      
      editor.getSession().setMode("ace/mode/snapscript");
      editor.getSession().setTabSize(3);
      
      editor.setReadOnly(false);
      editor.setAutoScrollEditorIntoView(true);
      editor.getSession().setUseSoftTabs(true);
      //editor.setKeyboardHandler("ace/keyboard/vim");
      
      editor.commands.removeCommand("replace"); // Ctrl-H
      editor.commands.removeCommand("find");    // Ctrl-F
      editor.commands.removeCommand("expandToMatching"); // Ctrl-Shift-M
      editor.commands.removeCommand("expandtoline"); // Ctrl-Shift-L
      
      // ################# DISABLE KEY BINDINGS ######################
      // editor.keyBinding.setDefaultHandler(null); // disable all keybindings
      // and allow Mousetrap to do it
      // #############################################################
      
      editor.setShowPrintMargin(false);
      editor.setOptions({
         enableBasicAutocompletion: true
      });
      editor.on("guttermousedown", function(e) {
         var target = e.domEvent.target;
         if (target.className.indexOf("ace_gutter-cell") == -1) {
            return;
         }
         if (!editor.isFocused()) {
            return;
         }
         if (e.clientX > 25 + target.getBoundingClientRect().left) {
            return;
         }
         var row = e.getDocumentPosition().row;
         // should be a getBreakpoints but does not seem to be there!!
         toggleEditorBreakpoint(row);
         e.stop()
      });
      
      //
      // THIS IS THE LINKS
      //
      
      // JavaFX has a very fast scroll speed
      if(typeof java !== 'undefined') {
         editor.setScrollSpeed(0.05); // slow down if its Java FX
      }
      return new FileEditorView(editor);
   }
   
//   function validEditorLink(string, col) { // see link.js (http://jsbin.com/jehopaja/4/edit?html,output)
//      if(KeyBinder.isControlPressed()) {
//         var tokenPatterns = [
//            "\\.[A-Z][a-zA-Z0-9]*;", // import type
//            "\\sas\\s+[A-Z][a-zA-Z0-9]*;", // import alias
//            "[a-zA-Z][a-zA-Z0-9]*\\s*\\.", // variable or type reference
//            "[a-z][a-zA-Z0-9]*\\s*[=|<|>|!|\-|\+|\*|\\/|%]", // variable
//                                                               // operation
//            "new\\s+[A-Z][a-zA-Z0-9]*\\s*\\(", // constructor call
//            "[a-zA-Z][a-zA-Z0-9]*\\s*\\(", // function or constructor call
//            "[A-Z][a-zA-Z0-9]*\\s*\\[", // type array reference
//            ":\\s*[A-Z][a-zA-Z0-9]*", // type constraint
//            "extends\\s+[A-Z][a-zA-Z0-9]*", // super class
//            "with\\s+[A-Z][a-zA-Z0-9]*" // implements trait
//         ];
//         for(var i = 0; i < tokenPatterns.length; i++) { 
//            var regExp = new RegExp(tokenPatterns[i], 'g'); // WE SHOULD CACHE
//                                                            // THE REGEX FOR
//                                                            // PERFORMANCE
//            var matchFound = null;
//            regExp.lastIndex = 0; // you have to reset regex to its start
//                                    // position
//            
//            string.replace(regExp, function(str) {
//                var offset = arguments[arguments.length - 2];
//                var length = str.length;
//                if (offset <= col && offset + length >= col) {
//                   var indexToken = editorView.editorCurrentTokens[str];
//                   
//                   if(indexToken != null) {
//                      matchFound = {
//                         start: offset,
//                         value: str
//                      };
//                   }
//                }
//            });
//            if(matchFound != null) {
//               return matchFound;
//            }
//         }
//      }
//      return null;
//   }
   
//   function openEditorLink(event) {
//      if(KeyBinder.isControlPressed()) {
//         var indexToken = editorView.editorCurrentTokens[event.value];
//         
//         if(indexToken != null) {
//            if(indexToken.resource != null) {
//               editorView.editorFocusToken = event.value;
//               window.location.hash = indexToken.resource;
//            }else {
//               showEditorLine(indexToken.line); 
//            }
//            // alert("Editor open ["+event.value+"] @ "+line);
//         }
//      }
//   }
   
   export function updateEditorFont(fontFamily, fontSize) {
      var autoComplete = createEditorAutoComplete();
      
      editorView.getEditorPanel().completers = [autoComplete];
      editorView.getEditorPanel().setOptions({
         enableBasicAutocompletion: true,
         fontFamily: "'"+fontFamily+"',monospace",
         fontSize: fontSize
      });
   }
}

//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]);