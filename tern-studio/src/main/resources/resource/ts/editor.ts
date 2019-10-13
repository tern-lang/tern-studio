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

   private _editorBreakpointManager: FileEditorBreakpointManager;
   private _editorHighlightManager: FileEditorHighlightManager;
   private _editorResource: FilePath;
   private _editorReadOnly: boolean;
   private _editorHistory: any; // store all editor context
   private _editorMarkers: any;   
   private _editorBreakpoints: any; // spans multiple resources      
   private _editorPanel: any; // this actual editor
   
   constructor(editorPanel: any) {
      this._editorBreakpointManager = new FileEditorBreakpointManager(this);
      this._editorHighlightManager = new FileEditorHighlightManager(this);
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
   
   public getEditorBreakpointManager(): FileEditorBreakpointManager {
      return this._editorBreakpointManager;
   }
   
   public getEditorHighlightManager(): FileEditorHighlightManager {
      return this._editorHighlightManager;
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

export class FileEditorBreakpointManager {
   
   private _editorView: FileEditorView;

   constructor(editorView: FileEditorView) {
      this._editorView = editorView;
   }
   
   private getEditorView(): FileEditorView {
      return this._editorView;
   }
   
   private clearEditorBreakpoint(row) {
      const session = this.getEditorView().getEditorPanel().getSession();
      const breakpoints = session.getBreakpoints();
      const line = parseInt(row);
   
      for(const breakpoint in breakpoints) {
         if(breakpoints.hasOwnProperty(breakpoint)) {
            const breakpointLine = parseInt(breakpoint);
            
            if(breakpointLine == line) {
               session.clearBreakpoint(breakpointLine);
            }
         }
      }
      this.showEditorBreakpoints();
   }
   
   public clearEditorBreakpoints() {
      const session = this.getEditorView().getEditorPanel().getSession();
      const breakpoints = session.getBreakpoints();
   
      for(const breakpoint in breakpoints) {
         if(breakpoints.hasOwnProperty(breakpoint)) {
            session.clearBreakpoint(breakpoint); // XXX is this correct
         }
      }
   }
   
   public showEditorBreakpoints() {
      const allBreakpoints = this.getEditorView().getEditorBreakpoints();
      const breakpointRecords = [];
      var breakpointIndex = 1;
   
      for(const filePath in allBreakpoints) {
         if(allBreakpoints.hasOwnProperty(filePath)) {
            const breakpoints = allBreakpoints[filePath];
   
            for(const lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     const resourcePathDetails: FilePath = FileTree.createResourcePath(filePath);
                     const displayName = "<div class='breakpointEnabled'>"+resourcePathDetails.getProjectPath()+"</div>";
                     
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
   
   public setEditorBreakpoint(row, value) {
      const allBreakpoints = this.getEditorView().getEditorBreakpoints();
      const line = parseInt(row);
      
      if (this.getEditorView().getEditorResource() != null) {
         const session = this.getEditorView().getEditorPanel().getSession();
         var resourceBreakpoints = allBreakpoints[this.getEditorView().getEditorResource().getFilePath()];
   
         if (value) {
            session.setBreakpoint(line);
         } else {
            session.clearBreakpoint(line);
         }
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            allBreakpoints[this.getEditorView().getEditorResource().getFilePath()] = resourceBreakpoints;
         }
         resourceBreakpoints[line + 1] = value;
      }
      this.showEditorBreakpoints();
   }
   
   public toggleEditorBreakpoint(row) {
      const allBreakpoints = this.getEditorView().getEditorBreakpoints();
      
      if (this.getEditorView().getEditorResource() != null) {
         const session = this.getEditorView().getEditorPanel().getSession();
         const breakpoints = session.getBreakpoints();
         var resourceBreakpoints = allBreakpoints[this.getEditorView().getEditorResource().getFilePath()];
         var remove = false;
   
         for(const breakpoint in breakpoints) {
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
         const line = parseInt(row);
   
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            resourceBreakpoints[line + 1] = true;
            allBreakpoints[this.getEditorView().getEditorResource().getFilePath()] = resourceBreakpoints;
         } else {
            if (resourceBreakpoints[line + 1] == true) {
               resourceBreakpoints[line + 1] = false;
            } else {
               resourceBreakpoints[line + 1] = true;
            }
         }
      }
      this.showEditorBreakpoints();
   }

   public restoreResourceBreakpoints() {
      const editorView = this.getEditorView();
      
      if (editorView.getEditorResource()) {
            const filePath: string = editorView.getEditorResource().getFilePath();
            const allBreakpoints = editorView.getEditorBreakpoints();
            const breakpoints = allBreakpoints[filePath];
      
            if (breakpoints != null) {
               for(var lineNumber in breakpoints) {
                  if (breakpoints.hasOwnProperty(lineNumber)) {
                     if (breakpoints[lineNumber] == true) {
                        this.setEditorBreakpoint(parseInt(lineNumber) - 1, true);
                     }
                  }
               }
            }
         }
   }
}

export class FileEditorHighlightManager {
 
   private _editorView: FileEditorView;

   constructor(editorView: FileEditorView) {
      this._editorView = editorView;
   }
   
   private getEditorView(): FileEditorView {
      return this._editorView;
   }
   
   public clearEditorHighlights() {
      const session = this.getEditorView().getEditorPanel().getSession();
      const editorMarkers = this.getEditorView().getEditorMarkers();
      const editorResource: FilePath = this.getEditorView().getEditorResource()
      
//      if(editorResource) {
//         console.log("Clear highlights in " + editorResource.getResourcePath());
//      }
      for (const editorLine in editorMarkers) {
         if (editorMarkers.hasOwnProperty(editorLine)) {
            const marker: FileEditorMarker = editorMarkers[editorLine];
            
            if(marker != null) {
               session.removeMarker(marker.getMarker());
               delete editorMarkers[editorLine];
            }
         }
      }
   }
   
   public showEditorLine(line) {
      const editor = this.getEditorView().getEditorPanel();
      
      this.getEditorView().getEditorPanel().resize(true);
      
      if(line > 1) {
         const requestedLine = line - 1;
         const currentLine = this.getCurrentLineForEditor();
         
         if(currentLine != requestedLine) {
            this.getEditorView().getEditorPanel().scrollToLine(requestedLine, true, true, function () {})
            this.getEditorView().getEditorPanel().gotoLine(line); // move the cursor
            this.getEditorView().getEditorPanel().focus();
         }
      } else {
         this.getEditorView().getEditorPanel().scrollToLine(0, true, true, function () {})
         this.getEditorView().getEditorPanel().focus();
      }
   }
   
   private clearEditorHighlight(line) {
      const session = this.getEditorView().getEditorPanel().getSession();
      const editorMarkers = this.getEditorView().getEditorMarkers();
      const marker: FileEditorMarker = editorMarkers[line];
      
      if(marker != null) {
         session.removeMarker(marker.getMarker());
      }
   }
   
   public createEditorHighlight(line, css): boolean {
      const Range = ace.require('ace/range').Range;
      const session = this.getEditorView().getEditorPanel().getSession();
      const editorMarkers = this.getEditorView().getEditorMarkers();
      const currentMarker: FileEditorMarker = editorMarkers[line];
      
      // clearEditorHighlight(line);
      this.clearEditorHighlights(); // clear all highlights in editor

      const marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
      const editorMarker: FileEditorMarker = new FileEditorMarker(line, css, marker);
      
      editorMarkers[line] = editorMarker;
      
      if(currentMarker) {
         return currentMarker.getStyle() != css;
      }
      return false;
   }
   
   public createMultipleEditorHighlights(lines, css): boolean {
      const Range = ace.require('ace/range').Range;
      const session = this.getEditorView().getEditorPanel().getSession();
      const editorMarkers = this.getEditorView().getEditorMarkers();
      var highlightsChanged = false;
      
      for(var i = 0; i < lines.length; i++) {
         const line = lines[i];
         const currentMarker: FileEditorMarker = editorMarkers[line];
      
         if(currentMarker) {
            if(currentMarker.getStyle() != css) {
               highlightsChanged = true;
            }
         } else {
            highlightsChanged = true;
         }
      }
      
      // clearEditorHighlight(line);
      this.clearEditorHighlights(); // clear all highlights in editor

      for(var i = 0; i < lines.length; i++) {
         const line = lines[i];
         const marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
         const editorMarker: FileEditorMarker = new FileEditorMarker(line, css, marker);

         editorMarkers[line] = editorMarker;
      }
      return highlightsChanged;
   }
   
   private getCurrentLineForEditor() {
      return this.getEditorView().getEditorPanel().getSelectionRange().start.row;
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
      const manager = new ace.UndoManager();
      
      if(text == this._savedText) { // text is the same text
         if(this._undoState) {
            const undoStack = this._undoState.getUndoStack();
            const redoStack = this._undoState.getRedoStack();
            
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
      const source = editorState.getSource();
      const currentText = editorState.getSource();
      
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


class FileEditorContent {
   
   private _editorView: FileEditorView;
   private _resourcePath: FilePath;
   private _editorMode: string;
   private _editorText: string;
   private _isReadOnly: boolean;
   private _lastModified: number;

   constructor(editorView: FileEditorView, resourcePath: FilePath, editorMode: string, editorText: string, isReadOnly: boolean, lastModified: number) {
      this._editorView = editorView;
      this._resourcePath = resourcePath;
      this._editorMode = editorMode;
      this._editorText = editorText;
      this._isReadOnly = isReadOnly;
      this._lastModified = lastModified;
   }
   
   public isReadOnly(): boolean {
      return this._isReadOnly;
   }
   
   public getEditorView(): FileEditorView {
      return this._editorView;
   }
   
   public getResourcePath(): FilePath {
      return this._resourcePath;
   }
   
   public getEditorMode(): string {
      return this._editorMode;
   }
   
   public getEditorText(): string {
      return this._editorText;
   }
   
   public getLastModified(): number {
      return this._lastModified;
   }
}

export module FileEditorBuilder {
   
   export function createFileEditorView(elementName: string): FileEditorView {
      var editor = ace.edit(elementName);
      var editorView: FileEditorView = new FileEditorView(editor);
      var autoComplete = createEditorAutoComplete(editorView);

      editor.completers = [autoComplete];
      // setEditorTheme("eclipse"); // set the default to eclipse
      
      editor.getSession().setMode("ace/mode/tern");
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
      
      //editor.setKeyboardHandler("ace/keyboard/vim");
      editor.setShowPrintMargin(false);
      editor.setOptions({
         enableBasicAutocompletion: true,
         enableLiveAutocompletion: true
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
         editorView.getEditorBreakpointManager().toggleEditorBreakpoint(row);
         e.stop()
      });

      // JavaFX has a very fast scroll speed
      if(typeof java !== 'undefined') {
         editor.setScrollSpeed(0.05); // slow down if its Java FX
      }    
      return editorView;
   }
   
   function createEditorAutoComplete(editorView: FileEditorView) {
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
}

export module FileEditorModeMapper {
   
   export function resolveEditorMode(resource) {
      var token = resource.toLowerCase();
      
      if(Common.stringEndsWith(token, ".tern")) {
         return "ace/mode/tern";
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
}


/**
 * Groups all the editor functions and creates the FileEditorView that
 * contains the state of the editor session. 
 */
export module FileEditor {

   var editorView: FileEditorView = null;
   
   export function createEditor() {
      editorView = FileEditorBuilder.createFileEditorView("editor");
      editorView.activateEditor();
      EventBus.createTermination(function() {
      	editorView.getEditorHighlightManager().clearEditorHighlights();
      }); // create callback
   }
   
   export function clearEditorHighlights() {
      editorView.getEditorHighlightManager().clearEditorHighlights();
   }
   
   export function showEditorLine(line) {
      editorView.getEditorHighlightManager().showEditorLine(line);
   }
   
   export function clearEditorHighlight(line) {
      editorView.getEditorHighlightManager().clearEditorHighlight(line);
   }
   
   export function createEditorHighlight(line, css): boolean {
      return editorView.getEditorHighlightManager().createEditorHighlight(line, css);
   }
   
   export function createMultipleEditorHighlights(lines, css): boolean {
      return editorView.getEditorHighlightManager().createMultipleEditorHighlights(lines, css);
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
   
   export function resizeEditor() {
      const width = document.getElementById('editor').offsetWidth;
      const height = document.getElementById('editor').offsetHeight;
      
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
      const session = editorView.getEditorPanel().getSession();
   
      for(const editorMarker in session.$backMarkers) { // what is this???
         session.removeMarker(editorMarker);
      }
      editorView.getEditorHighlightManager().clearEditorHighlights(); // clear highlighting
      editorView.getEditorBreakpointManager().clearEditorBreakpoints(); // clear highlighting

      $("#currentFile").html("");
   }
   
   export function currentEditorState(): FileEditorState {
      const editorUndoState: FileEditorUndoState = currentEditorUndoState();
      const editorPosition: FileEditorPosition = currentEditorPosition();
      const editorBuffer: FileEditorBuffer = currrentEditorBuffer();
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
      const scrollTop = editorView.getEditorPanel().getSession().getScrollTop();
      const editorCursor = editorView.getEditorPanel().selection.getCursor();

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
      const session = editorView.getEditorPanel().getSession();
      const manager = session.getUndoManager();
      const undoStack = $.extend(true, {}, manager.$undoStack);
      const redoStack = $.extend(true, {}, manager.$redoStack);

      return new FileEditorUndoState(
         undoStack,
         redoStack,
         manager.dirtyCounter
      );
   }
   
   function saveEditorHistory() {
      const editorState: FileEditorState = currentEditorState();
   
      if(!editorState.isReadOnly()) {
         const editorPath: FilePath = editorState.getResource();
         const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorPath);
      
         if(editorState.isStateValid()) {      
            editorHistory.saveHistory(editorState);         
         } else {
            editorHistory.invalidateHistory();
         }
      }
   }

   export function clearSavedEditorBuffer(resource: string) {
      const editorResource: FilePath = FileTree.createResourcePath(resource);
      const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
      
      editorHistory.invalidateHistory();
      updateEditorTabMarkForResource(resource); // remove the *      
   } 
   
   export function currrentEditorBuffer(): FileEditorBuffer {
      if(editorView.getEditorResource()) {
         const editorPath: string = editorView.getEditorResource().getResourcePath();
         return getEditorBufferForResource(editorPath);
      }
      return null;
   }
   
   export function getEditorBufferForResource(resource: string): FileEditorBuffer {
      const editorResource: FilePath = FileTree.createResourcePath(resource);
      const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
      const lastModifiedTime: number = editorHistory.getLastModified(); 
      const resourcePath: string = editorResource.getResourcePath();

      if(isEditorResourcePath(resourcePath)) {
         const editorText: string = currentEditorText();
         
         return new FileEditorBuffer(
            lastModifiedTime, // should this time be now
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
   
   function resolveEditorContentFromHistory(fileResource: FileResource) {
      const newEditorText: string = fileResource.getFileContent();
      const newReadOnly = fileResource.isHistorical() || fileResource.isError();
      
      if(!newReadOnly) {
         const newResourcePath: FilePath = fileResource.getResourcePath();
         const newResource: string = newResourcePath.getResourcePath();  
         const newLastModified: number = fileResource.getLastModified();
         const currentEditorBuffer: FileEditorBuffer = getEditorBufferForResource(newResource); // load saved buffer
         const currentEditorText: string = currentEditorBuffer.getSource();
         const currentLastModified: number = currentEditorBuffer.getLastModified();
         
         if(currentEditorText && currentLastModified >= newLastModified) {
            return currentEditorText;
         }
      } 
      return newEditorText;
   }
   
   function createEditorContent(fileResource: FileResource): FileEditorContent {
      const newEditorText: string = resolveEditorContentFromHistory(fileResource); // this may be from history
      const newResourcePath: FilePath = fileResource.getResourcePath();
      const newResource: string = newResourcePath.getResourcePath();
      const newReadOnly = fileResource.isHistorical() || fileResource.isError();
      const newEditorMode = FileEditorModeMapper.resolveEditorMode(newResource);
      const newLastModified: number = fileResource.getLastModified();
      
      return new FileEditorContent(editorView, newResourcePath, newEditorMode, newEditorText, newReadOnly, newLastModified);
   }
   
   function updateEditorContent(newEditorContent: FileEditorContent, fileResource: FileResource) {
      const session = editorView.getEditorPanel().getSession();
      const currentMode = session.getMode();
      const resourceText = fileResource.getFileContent();
      const newMode = newEditorContent.getEditorMode();
      const newEditorText = newEditorContent.getEditorText();
      const newReadOnly = newEditorContent.isReadOnly();
      const newResourcePath: FilePath = newEditorContent.getResourcePath();
      
      if(newMode != currentMode) {
         session.setMode({path: newMode, v: Date.now()})
      }
      if(!newReadOnly) {
         const editorHistory: FileEditorHistory = editorView.getHistoryForResource(newResourcePath);         
         
         editorView.getEditorPanel().setReadOnly(false);
         editorView.getEditorPanel().setValue(newEditorText, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
         editorHistory.updateHistory(newEditorText, resourceText);
         editorHistory.restoreUndoManager(session, newEditorText);     
      } else {
         editorView.getEditorPanel().setReadOnly(false);
         editorView.getEditorPanel().setValue(newEditorText, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
      }
      setReadOnly(newReadOnly);
      clearEditor();
      editorView.updateResourcePath(newResourcePath, newReadOnly);
   }
   
   export function updateEditor(fileResource: FileResource) { // why would you ever ignore an update here?
      const newEditorContent: FileEditorContent = createEditorContent(fileResource);
   
      saveEditorHistory(); // save any existing history
      updateEditorContent(newEditorContent, fileResource);
      ProblemManager.highlightProblems(); // higlight problems on this resource
      editorView.getEditorBreakpointManager().restoreResourceBreakpoints();
      Project.createEditorTab(); // update the tab name
      History.showFileHistory(); // update the history
      StatusPanel.showActiveFile(editorView.getEditorResource().getProjectPath());  
      FileEditor.showEditorFileInTree();
      scrollEditorToPosition();
      updateEditorTabMark(); // add a * to the name if its not in sync
   }
   
   export function sneakyUpdateEditor(text: string) {
      editorView.getEditorPanel().setReadOnly(false);
      editorView.getEditorPanel().setValue(text, 1);
   }
   
   export function focusEditor() {
      editorView.getEditorPanel().focus();
      editorView.getEditorPanel().setReadOnly(false);
   }
   
   export function setReadOnly(isReadOnly) {
      editorView.getEditorPanel().setReadOnly(isReadOnly);
   }
   
   export function showEditorFileInTree() {
      const editorState: FileEditorState = currentEditorState();
      const resourcePath: FilePath = editorState.getResource();
      
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
         const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
         const currentText = editorView.getEditorPanel().getValue(); 
         const originalText = editorHistory.getOriginalText();
         
         return currentText != originalText;
      }
      return false;
   }
   
   export function isEditorChangedForPath(resource: string) {      
      if(isEditorResourcePath(resource)) {
         return isEditorChanged();
      }
      const resourcePath: FilePath = FileTree.createResourcePath(resource);
      const editorHistory: FileEditorHistory = editorView.getHistoryForResource(resourcePath);
      const originalText: string = editorHistory.getOriginalText();
      const lastSavedText: string = editorHistory.getSavedText();
      
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
      const session = editorView.getEditorPanel().getSession();
      const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
      
      editorHistory.restoreScrollPosition(session, editorView.getEditorPanel());
   }
   
   export function updateProjectTabOnChange() {
      editorView.getEditorPanel().on("input", function() {
         const editorResource: FilePath = editorView.getEditorResource();
         const editorHistory: FileEditorHistory = editorView.getHistoryForResource(editorResource);
         const editorText = currentEditorText();
         
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
   
   export function formatEditorSource() {
      Command.formatEditorSource();
   }
   
   export function setEditorTheme(theme) {
      if(theme != null){
         if(editorView.getEditorPanel() != null) {
            editorView.getEditorPanel().setTheme(theme);
         }
         //editorView.editorTheme = theme;
      }
   }
  
   export function showEditorBreakpoints() {
      editorView.getEditorBreakpointManager().showEditorBreakpoints();
   }
   
   export function updateEditorFont(fontFamily, fontSize) {
      const actualFont = formatFont(fontFamily);
     
      forceFontLoad(actualFont);
      editorView.getEditorPanel().setOptions({
         enableBasicAutocompletion: true,
         enableLiveAutocompletion: true,
         fontFamily: actualFont,
         fontSize: fontSize
      });
   }
   
   function forceFontLoad(fontFamily) {
      var toolbarSeparator = document.getElementById("toolbarSeparator");
      var statusPanelSeparator = document.getElementById("statusPanelSeparator");
      
      if(toolbarSeparator != null) {
         toolbarSeparator.style.fontFamily = fontFamily; // works on all browsers
      }       
      if(statusPanelSeparator != null) {
         statusPanelSeparator.style.fontFamily = fontFamily; // works on all browsers
      }
   }

   function formatFont(fontFamily) {
     const fontList = fontFamily.split(",");
     var actualFont = "";

     for(var i = 0; i < fontList.length; i++) {
       var fontEntry = fontList[i];

       fontEntry = Common.stringReplaceText(fontEntry, "'", "");
       fontEntry = Common.stringReplaceText(fontEntry, "\"", "");
       fontEntry = "'" + fontEntry.trim() + "'";
       actualFont += fontEntry + ",";
     }
     return actualFont +"monospace";
   }
}

//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]);