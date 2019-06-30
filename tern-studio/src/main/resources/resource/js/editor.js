define(["require", "exports", "jquery", "ace", "w2ui", "common", "socket", "problem", "tree", "history", "project", "status", "keys", "commands"], function (require, exports, $, ace_1, w2ui_1, common_1, socket_1, problem_1, tree_1, history_1, project_1, status_1, keys_1, commands_1) {
    "use strict";
    /**
     * Contains the state for the Ace editor and is a singleton instance
     * that exists as soon as the editor is created.
     */
    var FileEditorView = (function () {
        function FileEditorView(editorPanel) {
            this._editorBreakpointManager = new FileEditorBreakpointManager(this);
            this._editorHighlightManager = new FileEditorHighlightManager(this);
            this._editorPanel = editorPanel;
            this._editorHistory = {}; // store all editor context
            this._editorMarkers = {};
            this._editorBreakpoints = {}; // spans multiple resources   
        }
        FileEditorView.prototype.activateEditor = function () {
            keys_1.KeyBinder.bindKeys(); // register key bindings
            project_1.Project.changeProjectFont(); // project.js update font
            FileEditor.scrollEditorToPosition();
            FileEditor.updateProjectTabOnChange(); // listen to change
        };
        FileEditorView.prototype.updateResourcePath = function (resourcePath, isReadOnly) {
            window.location.hash = resourcePath.getProjectPath(); // update # anchor
            this._editorResource = resourcePath;
            this._editorReadOnly = isReadOnly;
        };
        FileEditorView.prototype.getHistoryForResource = function (resource) {
            if (resource) {
                var editorPath = resource.getResourcePath();
                var editorHistory = this._editorHistory[editorPath];
                if (!editorHistory) {
                    editorHistory = this._editorHistory[editorPath] = new FileEditorHistory(-1, null, null, null, null // let the original be the same
                    );
                }
                return editorHistory;
            }
            return new FileEditorHistory(-1, null, null, null, null // let the original be the same
            );
        };
        FileEditorView.prototype.getEditorBreakpointManager = function () {
            return this._editorBreakpointManager;
        };
        FileEditorView.prototype.getEditorHighlightManager = function () {
            return this._editorHighlightManager;
        };
        FileEditorView.prototype.getEditorResource = function () {
            return this._editorResource;
        };
        FileEditorView.prototype.isEditorReadOnly = function () {
            return this._editorReadOnly;
        };
        FileEditorView.prototype.getEditorPanel = function () {
            return this._editorPanel;
        };
        FileEditorView.prototype.getEditorMarkers = function () {
            return this._editorMarkers;
        };
        FileEditorView.prototype.getEditorBreakpoints = function () {
            return this._editorBreakpoints;
        };
        return FileEditorView;
    }());
    exports.FileEditorView = FileEditorView;
    var FileEditorBreakpointManager = (function () {
        function FileEditorBreakpointManager(editorView) {
            this._editorView = editorView;
        }
        FileEditorBreakpointManager.prototype.getEditorView = function () {
            return this._editorView;
        };
        FileEditorBreakpointManager.prototype.clearEditorBreakpoint = function (row) {
            var session = this.getEditorView().getEditorPanel().getSession();
            var breakpoints = session.getBreakpoints();
            var line = parseInt(row);
            for (var breakpoint in breakpoints) {
                if (breakpoints.hasOwnProperty(breakpoint)) {
                    var breakpointLine = parseInt(breakpoint);
                    if (breakpointLine == line) {
                        session.clearBreakpoint(breakpointLine);
                    }
                }
            }
            this.showEditorBreakpoints();
        };
        FileEditorBreakpointManager.prototype.clearEditorBreakpoints = function () {
            var session = this.getEditorView().getEditorPanel().getSession();
            var breakpoints = session.getBreakpoints();
            for (var breakpoint in breakpoints) {
                if (breakpoints.hasOwnProperty(breakpoint)) {
                    session.clearBreakpoint(breakpoint); // XXX is this correct
                }
            }
        };
        FileEditorBreakpointManager.prototype.showEditorBreakpoints = function () {
            var allBreakpoints = this.getEditorView().getEditorBreakpoints();
            var breakpointRecords = [];
            var breakpointIndex = 1;
            for (var filePath in allBreakpoints) {
                if (allBreakpoints.hasOwnProperty(filePath)) {
                    var breakpoints = allBreakpoints[filePath];
                    for (var lineNumber in breakpoints) {
                        if (breakpoints.hasOwnProperty(lineNumber)) {
                            if (breakpoints[lineNumber] == true) {
                                var resourcePathDetails = tree_1.FileTree.createResourcePath(filePath);
                                var displayName = "<div class='breakpointEnabled'>" + resourcePathDetails.getProjectPath() + "</div>";
                                breakpointRecords.push({
                                    recid: breakpointIndex++,
                                    name: displayName,
                                    location: "Line " + lineNumber,
                                    resource: resourcePathDetails.getProjectPath(),
                                    line: parseInt(lineNumber),
                                    script: resourcePathDetails.getResourcePath()
                                });
                            }
                        }
                    }
                }
            }
            w2ui_1.w2ui['breakpoints'].records = breakpointRecords;
            w2ui_1.w2ui['breakpoints'].refresh();
            commands_1.Command.updateScriptBreakpoints(); // update the breakpoints
        };
        FileEditorBreakpointManager.prototype.setEditorBreakpoint = function (row, value) {
            var allBreakpoints = this.getEditorView().getEditorBreakpoints();
            var line = parseInt(row);
            if (this.getEditorView().getEditorResource() != null) {
                var session = this.getEditorView().getEditorPanel().getSession();
                var resourceBreakpoints = allBreakpoints[this.getEditorView().getEditorResource().getFilePath()];
                if (value) {
                    session.setBreakpoint(line);
                }
                else {
                    session.clearBreakpoint(line);
                }
                if (resourceBreakpoints == null) {
                    resourceBreakpoints = {};
                    allBreakpoints[this.getEditorView().getEditorResource().getFilePath()] = resourceBreakpoints;
                }
                resourceBreakpoints[line + 1] = value;
            }
            this.showEditorBreakpoints();
        };
        FileEditorBreakpointManager.prototype.toggleEditorBreakpoint = function (row) {
            var allBreakpoints = this.getEditorView().getEditorBreakpoints();
            if (this.getEditorView().getEditorResource() != null) {
                var session = this.getEditorView().getEditorPanel().getSession();
                var breakpoints = session.getBreakpoints();
                var resourceBreakpoints = allBreakpoints[this.getEditorView().getEditorResource().getFilePath()];
                var remove = false;
                for (var breakpoint in breakpoints) {
                    if (breakpoint == row) {
                        remove = true;
                        break;
                    }
                }
                if (remove) {
                    session.clearBreakpoint(row);
                }
                else {
                    session.setBreakpoint(row);
                }
                var line = parseInt(row);
                if (resourceBreakpoints == null) {
                    resourceBreakpoints = {};
                    resourceBreakpoints[line + 1] = true;
                    allBreakpoints[this.getEditorView().getEditorResource().getFilePath()] = resourceBreakpoints;
                }
                else {
                    if (resourceBreakpoints[line + 1] == true) {
                        resourceBreakpoints[line + 1] = false;
                    }
                    else {
                        resourceBreakpoints[line + 1] = true;
                    }
                }
            }
            this.showEditorBreakpoints();
        };
        return FileEditorBreakpointManager;
    }());
    exports.FileEditorBreakpointManager = FileEditorBreakpointManager;
    var FileEditorHighlightManager = (function () {
        function FileEditorHighlightManager(editorView) {
            this._editorView = editorView;
        }
        FileEditorHighlightManager.prototype.getEditorView = function () {
            return this._editorView;
        };
        FileEditorHighlightManager.prototype.clearEditorHighlights = function () {
            var session = this.getEditorView().getEditorPanel().getSession();
            var editorMarkers = this.getEditorView().getEditorMarkers();
            var editorResource = this.getEditorView().getEditorResource();
            //      if(editorResource) {
            //         console.log("Clear highlights in " + editorResource.getResourcePath());
            //      }
            for (var editorLine in editorMarkers) {
                if (editorMarkers.hasOwnProperty(editorLine)) {
                    var marker = editorMarkers[editorLine];
                    if (marker != null) {
                        session.removeMarker(marker.getMarker());
                        delete editorMarkers[editorLine];
                    }
                }
            }
        };
        FileEditorHighlightManager.prototype.showEditorLine = function (line) {
            var editor = this.getEditorView().getEditorPanel();
            this.getEditorView().getEditorPanel().resize(true);
            if (line > 1) {
                var requestedLine = line - 1;
                var currentLine = this.getCurrentLineForEditor();
                if (currentLine != requestedLine) {
                    this.getEditorView().getEditorPanel().scrollToLine(requestedLine, true, true, function () { });
                    this.getEditorView().getEditorPanel().gotoLine(line); // move the cursor
                    this.getEditorView().getEditorPanel().focus();
                }
            }
            else {
                this.getEditorView().getEditorPanel().scrollToLine(0, true, true, function () { });
                this.getEditorView().getEditorPanel().focus();
            }
        };
        FileEditorHighlightManager.prototype.clearEditorHighlight = function (line) {
            var session = this.getEditorView().getEditorPanel().getSession();
            var editorMarkers = this.getEditorView().getEditorMarkers();
            var marker = editorMarkers[line];
            if (marker != null) {
                session.removeMarker(marker.getMarker());
            }
        };
        FileEditorHighlightManager.prototype.createEditorHighlight = function (line, css) {
            var Range = ace_1.ace.require('ace/range').Range;
            var session = this.getEditorView().getEditorPanel().getSession();
            var editorMarkers = this.getEditorView().getEditorMarkers();
            var currentMarker = editorMarkers[line];
            // clearEditorHighlight(line);
            this.clearEditorHighlights(); // clear all highlights in editor
            var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
            var editorMarker = new FileEditorMarker(line, css, marker);
            editorMarkers[line] = editorMarker;
            if (currentMarker) {
                return currentMarker.getStyle() != css;
            }
            return false;
        };
        FileEditorHighlightManager.prototype.createMultipleEditorHighlights = function (lines, css) {
            var Range = ace_1.ace.require('ace/range').Range;
            var session = this.getEditorView().getEditorPanel().getSession();
            var editorMarkers = this.getEditorView().getEditorMarkers();
            var highlightsChanged = false;
            for (var i = 0; i < lines.length; i++) {
                var line = lines[i];
                var currentMarker = editorMarkers[line];
                if (currentMarker) {
                    if (currentMarker.getStyle() != css) {
                        highlightsChanged = true;
                    }
                }
                else {
                    highlightsChanged = true;
                }
            }
            // clearEditorHighlight(line);
            this.clearEditorHighlights(); // clear all highlights in editor
            for (var i = 0; i < lines.length; i++) {
                var line = lines[i];
                var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
                var editorMarker = new FileEditorMarker(line, css, marker);
                editorMarkers[line] = editorMarker;
            }
            return highlightsChanged;
        };
        FileEditorHighlightManager.prototype.getCurrentLineForEditor = function () {
            return this.getEditorView().getEditorPanel().getSelectionRange().start.row;
        };
        return FileEditorHighlightManager;
    }());
    exports.FileEditorHighlightManager = FileEditorHighlightManager;
    var FileEditorMarker = (function () {
        function FileEditorMarker(line, style, marker) {
            this._style = style;
            this._line = line;
            this._marker = marker;
        }
        FileEditorMarker.prototype.getLine = function () {
            return this._line;
        };
        FileEditorMarker.prototype.getMarker = function () {
            return this._marker;
        };
        FileEditorMarker.prototype.getStyle = function () {
            return this._style;
        };
        return FileEditorMarker;
    }());
    exports.FileEditorMarker = FileEditorMarker;
    var FileEditorHistory = (function () {
        function FileEditorHistory(lastModified, undoState, position, savedText, originalText) {
            this._lastModified = lastModified;
            this._undoState = undoState;
            this._position = position;
            this._originalText = originalText;
            this._savedText = savedText;
            this._changeMade = false;
        }
        FileEditorHistory.prototype.restoreUndoManager = function (session, text) {
            var manager = new ace_1.ace.UndoManager();
            if (text == this._savedText) {
                if (this._undoState) {
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
        };
        FileEditorHistory.prototype.restoreScrollPosition = function (session, panel) {
            if (this._position) {
                var scroll_1 = this._position.getScroll();
                var row = this._position.getRow();
                var column = this._position.getColumn();
                if (row >= 0 && column >= 0) {
                    panel.selection.moveTo(row, column);
                }
                else {
                    panel.gotoLine(1);
                }
                session.setScrollTop(scroll_1);
            }
            else {
                panel.gotoLine(1);
                session.setScrollTop(0);
            }
            panel.focus();
        };
        FileEditorHistory.prototype.saveHistory = function (editorState) {
            var source = editorState.getSource();
            var currentText = editorState.getSource();
            this._savedText = currentText;
            this._lastModified = editorState.getLastModified();
            this._undoState = editorState.getUndoState();
            this._position = editorState.getPosition();
        };
        FileEditorHistory.prototype.updateHistory = function (currentText, originalText) {
            if (currentText != this._savedText) {
                this._undoState = null;
                this._position = null;
            }
            this._savedText = currentText;
            this._originalText = originalText;
            this._changeMade = false;
        };
        FileEditorHistory.prototype.invalidateHistory = function () {
            this._changeMade = false;
            this._undoState = null;
            this._lastModified = -1;
            this._originalText = null;
            this._savedText = null; // clear the buffer
        };
        FileEditorHistory.prototype.touchHistory = function (currentText) {
            if (currentText != this._savedText) {
                this._lastModified = common_1.Common.currentTime();
            }
            this._savedText = currentText;
        };
        FileEditorHistory.prototype.getLastModified = function () {
            return this._lastModified;
        };
        FileEditorHistory.prototype.getOriginalText = function () {
            return this._originalText;
        };
        FileEditorHistory.prototype.getSavedText = function () {
            return this._savedText;
        };
        return FileEditorHistory;
    }());
    exports.FileEditorHistory = FileEditorHistory;
    var FileEditorUndoState = (function () {
        function FileEditorUndoState(undoStack, redoStack, dirtyCounter) {
            this._undoStack = undoStack;
            this._redoStack = redoStack;
            this._dirtyCounter = dirtyCounter;
        }
        FileEditorUndoState.prototype.getUndoStack = function () {
            return this._undoStack;
        };
        FileEditorUndoState.prototype.getRedoStack = function () {
            return this._redoStack;
        };
        FileEditorUndoState.prototype.getDirtyCounter = function () {
            return this._dirtyCounter;
        };
        return FileEditorUndoState;
    }());
    exports.FileEditorUndoState = FileEditorUndoState;
    var FileEditorState = (function () {
        function FileEditorState(lastModified, breakpoints, resource, undoState, position, source, isReadOnly) {
            this._lastModified = lastModified;
            this._breakpoints = breakpoints;
            this._resource = resource;
            this._undoState = undoState;
            this._isReadOnly = isReadOnly;
            this._position = position;
            this._source = source;
        }
        FileEditorState.prototype.isStateValid = function () {
            return this._resource && (this._source != null && this._source != "");
        };
        FileEditorState.prototype.isReadOnly = function () {
            return this._isReadOnly;
        };
        FileEditorState.prototype.getResource = function () {
            return this._resource;
        };
        FileEditorState.prototype.getPosition = function () {
            return this._position;
        };
        FileEditorState.prototype.getUndoState = function () {
            return this._undoState;
        };
        FileEditorState.prototype.getLastModified = function () {
            return this._lastModified;
        };
        FileEditorState.prototype.getBreakpoints = function () {
            return this._breakpoints;
        };
        FileEditorState.prototype.getSource = function () {
            return this._source;
        };
        return FileEditorState;
    }());
    exports.FileEditorState = FileEditorState;
    var FileEditorPosition = (function () {
        function FileEditorPosition(row, column, scroll) {
            this._row = row;
            this._column = column;
            this._scroll = scroll;
        }
        FileEditorPosition.prototype.getRow = function () {
            return this._row;
        };
        FileEditorPosition.prototype.getColumn = function () {
            return this._column;
        };
        FileEditorPosition.prototype.getScroll = function () {
            return this._scroll;
        };
        return FileEditorPosition;
    }());
    exports.FileEditorPosition = FileEditorPosition;
    var FileEditorBuffer = (function () {
        function FileEditorBuffer(lastModified, resource, source, isCurrent) {
            this._lastModified = lastModified;
            this._isCurrent = isCurrent;
            this._resource = resource;
            this._source = source;
        }
        FileEditorBuffer.prototype.isBufferValid = function () {
            return this._resource && this._source != null;
        };
        FileEditorBuffer.prototype.isBufferCurrent = function () {
            return this._isCurrent;
        };
        FileEditorBuffer.prototype.getResource = function () {
            return this._resource;
        };
        FileEditorBuffer.prototype.getLastModified = function () {
            return this._lastModified;
        };
        FileEditorBuffer.prototype.getSource = function () {
            return this._source;
        };
        return FileEditorBuffer;
    }());
    exports.FileEditorBuffer = FileEditorBuffer;
    var FileEditorBuilder;
    (function (FileEditorBuilder) {
        function createFileEditorView(elementName) {
            var editor = ace_1.ace.edit(elementName);
            var editorView = new FileEditorView(editor);
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
            editor.commands.removeCommand("find"); // Ctrl-F
            editor.commands.removeCommand("expandToMatching"); // Ctrl-Shift-M
            editor.commands.removeCommand("expandtoline"); // Ctrl-Shift-L
            // ################# DISABLE KEY BINDINGS ######################
            // editor.keyBinding.setDefaultHandler(null); // disable all keybindings
            // and allow Mousetrap to do it
            // #############################################################
            editor.setShowPrintMargin(false);
            editor.setOptions({
                enableBasicAutocompletion: true,
                enableLiveAutocompletion: true
            });
            editor.on("click", function (e) {
                var options = editor.getOptions();
                var type = typeof options;
                var optionsUpdate = {};
                console.log("Focus editor on click: options=[" + type + "] -> ", options);
                editor.focus();
                for (var key in options) {
                    if (options.hasOwnProperty(key)) {
                        optionsUpdate[key] = options[key];
                    }
                }
                optionsUpdate['readOnly'] = false;
                console.log("Update after click: ", optionsUpdate);
                editor.setOptions(optionsUpdate);
                editor.setReadOnly(false);
            });
            editor.on("guttermousedown", function (e) {
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
                e.stop();
            });
            // JavaFX has a very fast scroll speed
            if (typeof java !== 'undefined') {
                editor.setScrollSpeed(0.05); // slow down if its Java FX
            }
            return editorView;
        }
        FileEditorBuilder.createFileEditorView = createFileEditorView;
        function createEditorAutoComplete(editorView) {
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
                        success: function (response) {
                            var expression = response.expression;
                            if (expression) {
                                var dotIndex = Math.max(0, expression.lastIndexOf('.') + 1);
                                var tokens = response.tokens;
                                var length = tokens.length;
                                var suggestions = [];
                                for (var token in tokens) {
                                    if (tokens.hasOwnProperty(token)) {
                                        var type = tokens[token];
                                        if (common_1.Common.stringStartsWith(token, expression)) {
                                            token = token.substring(dotIndex);
                                        }
                                        suggestions.push({ className: 'autocomplete_' + type, token: token, value: token, score: 300, meta: type });
                                    }
                                }
                                callback(null, suggestions);
                            }
                        },
                        error: function () {
                            console.log("Completion control failed");
                        },
                        processData: false,
                        type: 'POST',
                        url: '/complete/' + common_1.Common.getProjectName()
                    });
                }
            };
        }
    })(FileEditorBuilder = exports.FileEditorBuilder || (exports.FileEditorBuilder = {}));
    var FileEditorModeMapper;
    (function (FileEditorModeMapper) {
        function resolveEditorMode(resource) {
            var token = resource.toLowerCase();
            if (common_1.Common.stringEndsWith(token, ".tern")) {
                return "ace/mode/tern";
            }
            if (common_1.Common.stringEndsWith(token, ".policy")) {
                return "ace/mode/policy";
            }
            if (common_1.Common.stringEndsWith(token, ".xml")) {
                return "ace/mode/xml";
            }
            if (common_1.Common.stringEndsWith(token, ".json")) {
                return "ace/mode/json";
            }
            if (common_1.Common.stringEndsWith(token, ".sql")) {
                return "ace/mode/sql";
            }
            if (common_1.Common.stringEndsWith(token, ".pl")) {
                return "ace/mode/perl";
            }
            if (common_1.Common.stringEndsWith(token, ".kt")) {
                return "ace/mode/kotlin";
            }
            if (common_1.Common.stringEndsWith(token, ".js")) {
                return "ace/mode/javascript";
            }
            if (common_1.Common.stringEndsWith(token, ".ts")) {
                return "ace/mode/typescript";
            }
            if (common_1.Common.stringEndsWith(token, ".java")) {
                return "ace/mode/java";
            }
            if (common_1.Common.stringEndsWith(token, ".groovy")) {
                return "ace/mode/groovy";
            }
            if (common_1.Common.stringEndsWith(token, ".py")) {
                return "ace/mode/python";
            }
            if (common_1.Common.stringEndsWith(token, ".html")) {
                return "ace/mode/html";
            }
            if (common_1.Common.stringEndsWith(token, ".htm")) {
                return "ace/mode/html";
            }
            if (common_1.Common.stringEndsWith(token, ".txt")) {
                return "ace/mode/text";
            }
            if (common_1.Common.stringEndsWith(token, ".properties")) {
                return "ace/mode/properties";
            }
            if (common_1.Common.stringEndsWith(token, ".gitignore")) {
                return "ace/mode/text";
            }
            if (common_1.Common.stringEndsWith(token, ".project")) {
                return "ace/mode/xml";
            }
            if (common_1.Common.stringEndsWith(token, ".classpath")) {
                return "ace/mode/text";
            }
            return "ace/mode/text";
        }
        FileEditorModeMapper.resolveEditorMode = resolveEditorMode;
    })(FileEditorModeMapper = exports.FileEditorModeMapper || (exports.FileEditorModeMapper = {}));
    /**
     * Groups all the editor functions and creates the FileEditorView that
     * contains the state of the editor session.
     */
    var FileEditor;
    (function (FileEditor) {
        var editorView = null;
        function createEditor() {
            editorView = FileEditorBuilder.createFileEditorView("editor");
            editorView.activateEditor();
            socket_1.EventBus.createTermination(function () {
                editorView.getEditorHighlightManager().clearEditorHighlights();
            }); // create callback
        }
        FileEditor.createEditor = createEditor;
        function clearEditorHighlights() {
            editorView.getEditorHighlightManager().clearEditorHighlights();
        }
        FileEditor.clearEditorHighlights = clearEditorHighlights;
        function showEditorLine(line) {
            editorView.getEditorHighlightManager().showEditorLine(line);
        }
        FileEditor.showEditorLine = showEditorLine;
        function clearEditorHighlight(line) {
            editorView.getEditorHighlightManager().clearEditorHighlight(line);
        }
        FileEditor.clearEditorHighlight = clearEditorHighlight;
        function createEditorHighlight(line, css) {
            return editorView.getEditorHighlightManager().createEditorHighlight(line, css);
        }
        FileEditor.createEditorHighlight = createEditorHighlight;
        function createMultipleEditorHighlights(lines, css) {
            return editorView.getEditorHighlightManager().createMultipleEditorHighlights(lines, css);
        }
        FileEditor.createMultipleEditorHighlights = createMultipleEditorHighlights;
        function findAndReplaceTextInEditor() {
            var state = currentEditorState();
            var resource = state.getResource();
            commands_1.Command.searchAndReplaceFiles(resource.getProjectPath());
        }
        FileEditor.findAndReplaceTextInEditor = findAndReplaceTextInEditor;
        function findTextInEditor() {
            var state = currentEditorState();
            var resource = state.getResource();
            commands_1.Command.searchFiles(resource.getProjectPath());
        }
        FileEditor.findTextInEditor = findTextInEditor;
        function addEditorKeyBinding(keyBinding, actionFunction) {
            editorView.getEditorPanel().commands.addCommand({
                name: keyBinding.editor,
                bindKey: {
                    win: keyBinding.editor,
                    mac: keyBinding.editor
                },
                exec: function (editor) {
                    if (actionFunction) {
                        actionFunction();
                    }
                }
            });
        }
        FileEditor.addEditorKeyBinding = addEditorKeyBinding;
        function resizeEditor() {
            var width = document.getElementById('editor').offsetWidth;
            var height = document.getElementById('editor').offsetHeight;
            console.log("Resize editor " + width + "x" + height);
            editorView.getEditorPanel().setAutoScrollEditorIntoView(true);
            editorView.getEditorPanel().resize(true);
            // editor.focus();
        }
        FileEditor.resizeEditor = resizeEditor;
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
            for (var editorMarker in session.$backMarkers) {
                session.removeMarker(editorMarker);
            }
            editorView.getEditorHighlightManager().clearEditorHighlights(); // clear highlighting
            editorView.getEditorBreakpointManager().clearEditorBreakpoints(); // clear highlighting
            $("#currentFile").html("");
        }
        function currentEditorState() {
            var editorUndoState = currentEditorUndoState();
            var editorPosition = currentEditorPosition();
            var editorBuffer = currrentEditorBuffer();
            var editorLastModified = -1;
            var editorText = null;
            if (editorBuffer) {
                editorText = editorBuffer.getSource();
                editorLastModified = editorBuffer.getLastModified();
            }
            return new FileEditorState(editorLastModified, editorView.getEditorBreakpoints(), editorView.getEditorResource(), editorUndoState, editorPosition, editorText, editorView.isEditorReadOnly());
        }
        FileEditor.currentEditorState = currentEditorState;
        function currentEditorText() {
            return editorView.getEditorPanel().getValue();
        }
        function currentEditorPosition() {
            var scrollTop = editorView.getEditorPanel().getSession().getScrollTop();
            var editorCursor = editorView.getEditorPanel().selection.getCursor();
            if (editorCursor) {
                return new FileEditorPosition(editorCursor.row, editorCursor.column, scrollTop);
            }
            return new FileEditorPosition(null, null, scrollTop);
        }
        function currentEditorUndoState() {
            var session = editorView.getEditorPanel().getSession();
            var manager = session.getUndoManager();
            var undoStack = $.extend(true, {}, manager.$undoStack);
            var redoStack = $.extend(true, {}, manager.$redoStack);
            return new FileEditorUndoState(undoStack, redoStack, manager.dirtyCounter);
        }
        function saveEditorHistory() {
            var editorState = currentEditorState();
            if (!editorState.isReadOnly()) {
                var editorPath = editorState.getResource();
                var editorHistory = editorView.getHistoryForResource(editorPath);
                if (editorState.isStateValid()) {
                    editorHistory.saveHistory(editorState);
                }
                else {
                    editorHistory.invalidateHistory();
                }
            }
        }
        function createEditorUndoManager(session, textToDisplay, originalText, resource) {
            var editorHistory = editorView.getHistoryForResource(resource);
            editorView.getEditorPanel().setReadOnly(false);
            editorView.getEditorPanel().setValue(textToDisplay, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
            editorHistory.updateHistory(textToDisplay, originalText);
            editorHistory.restoreUndoManager(session, textToDisplay);
        }
        function createEditorWithoutUndoManager(textToDisplay) {
            editorView.getEditorPanel().setReadOnly(false);
            editorView.getEditorPanel().setValue(textToDisplay, 1); // this causes a callback resulting in FileEditorHistory.touchHistory
        }
        function clearSavedEditorBuffer(resource) {
            var editorResource = tree_1.FileTree.createResourcePath(resource);
            var editorHistory = editorView.getHistoryForResource(editorResource);
            editorHistory.invalidateHistory();
            updateEditorTabMarkForResource(resource); // remove the *      
        }
        FileEditor.clearSavedEditorBuffer = clearSavedEditorBuffer;
        function currrentEditorBuffer() {
            if (editorView.getEditorResource()) {
                var editorPath = editorView.getEditorResource().getResourcePath();
                return getEditorBufferForResource(editorPath);
            }
            return null;
        }
        FileEditor.currrentEditorBuffer = currrentEditorBuffer;
        function getEditorBufferForResource(resource) {
            var editorResource = tree_1.FileTree.createResourcePath(resource);
            var editorHistory = editorView.getHistoryForResource(editorResource);
            var lastModifiedTime = editorHistory.getLastModified();
            if (isEditorResourcePath(editorResource.getResourcePath())) {
                var editorText = currentEditorText();
                return new FileEditorBuffer(lastModifiedTime, editorResource, editorText, // if its the current buffer then return it
                true);
            }
            return new FileEditorBuffer(lastModifiedTime, editorResource, editorHistory.getSavedText(), // if its the current buffer then return it
            false);
        }
        FileEditor.getEditorBufferForResource = getEditorBufferForResource;
        function resolveEditorTextToUse(fileResource) {
            var encodedText = fileResource.getFileContent();
            var isReadOnly = fileResource.isHistorical() || fileResource.isError();
            console.log("resource=[" + fileResource.getResourcePath().getResourcePath() +
                "] modified=[" + fileResource.getTimeStamp() + "] length=[" + fileResource.getFileLength() + "] readonly=[" + isReadOnly + "]");
            if (!isReadOnly) {
                var savedHistoryBuffer = getEditorBufferForResource(fileResource.getResourcePath().getResourcePath()); // load saved buffer
                if (savedHistoryBuffer.getSource() && savedHistoryBuffer.getLastModified() > fileResource.getLastModified()) {
                    console.log("LOAD FROM HISTORY diff=[" + (savedHistoryBuffer.getLastModified() - fileResource.getLastModified()) + "]");
                    return savedHistoryBuffer.getSource();
                }
                console.log("IGNORE HISTORY: ", savedHistoryBuffer);
            }
            else {
                console.log("IGNORE HISTORY WHEN READ ONLY");
            }
            return encodedText;
        }
        function updateEditor(fileResource) {
            var resourcePath = fileResource.getResourcePath();
            var isReadOnly = fileResource.isHistorical() || fileResource.isError();
            var realText = fileResource.getFileContent();
            var textToDisplay = resolveEditorTextToUse(fileResource);
            var session = editorView.getEditorPanel().getSession();
            var currentMode = session.getMode();
            var actualMode = FileEditorModeMapper.resolveEditorMode(resourcePath.getResourcePath());
            saveEditorHistory(); // save any existing history
            if (actualMode != currentMode) {
                session.setMode({
                    path: actualMode,
                    v: Date.now()
                });
            }
            if (!isReadOnly) {
                createEditorUndoManager(session, textToDisplay, realText, resourcePath); // restore any existing history      
            }
            else {
                createEditorWithoutUndoManager(textToDisplay);
            }
            clearEditor();
            setReadOnly(isReadOnly);
            editorView.updateResourcePath(resourcePath, isReadOnly);
            problem_1.ProblemManager.highlightProblems(); // higlight problems on this resource
            if (resourcePath != null && editorView.getEditorResource()) {
                var filePath = editorView.getEditorResource().getFilePath();
                var allBreakpoints = editorView.getEditorBreakpoints();
                var breakpoints = allBreakpoints[filePath];
                if (breakpoints != null) {
                    for (var lineNumber in breakpoints) {
                        if (breakpoints.hasOwnProperty(lineNumber)) {
                            if (breakpoints[lineNumber] == true) {
                                editorView.getEditorBreakpointManager().setEditorBreakpoint(parseInt(lineNumber) - 1, true);
                            }
                        }
                    }
                }
            }
            project_1.Project.createEditorTab(); // update the tab name
            history_1.History.showFileHistory(); // update the history
            status_1.StatusPanel.showActiveFile(editorView.getEditorResource().getProjectPath());
            FileEditor.showEditorFileInTree();
            scrollEditorToPosition();
            updateEditorTabMark(); // add a * to the name if its not in sync
        }
        FileEditor.updateEditor = updateEditor;
        function focusEditor() {
            editorView.getEditorPanel().focus();
            editorView.getEditorPanel().setReadOnly(false);
        }
        FileEditor.focusEditor = focusEditor;
        function setReadOnly(isReadOnly) {
            editorView.getEditorPanel().setReadOnly(isReadOnly);
        }
        FileEditor.setReadOnly = setReadOnly;
        function showEditorFileInTree() {
            var editorState = currentEditorState();
            var resourcePath = editorState.getResource();
            tree_1.FileTree.showTreeNode('explorerTree', resourcePath);
        }
        FileEditor.showEditorFileInTree = showEditorFileInTree;
        function getCurrentLineForEditor() {
            return editorView.getEditorPanel().getSelectionRange().start.row;
        }
        FileEditor.getCurrentLineForEditor = getCurrentLineForEditor;
        function getSelectedText() {
            return editorView.getEditorPanel().getSelectedText();
        }
        FileEditor.getSelectedText = getSelectedText;
        function isEditorChanged() {
            if (editorView.getEditorResource() != null) {
                var editorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
                var currentText = editorView.getEditorPanel().getValue();
                var originalText = editorHistory.getOriginalText();
                return currentText != originalText;
            }
            return false;
        }
        FileEditor.isEditorChanged = isEditorChanged;
        function isEditorChangedForPath(resource) {
            if (isEditorResourcePath(resource)) {
                return isEditorChanged();
            }
            var resourcePath = tree_1.FileTree.createResourcePath(resource);
            var editorHistory = editorView.getHistoryForResource(resourcePath);
            var originalText = editorHistory.getOriginalText();
            var lastSavedText = editorHistory.getSavedText();
            return originalText != lastSavedText;
        }
        FileEditor.isEditorChangedForPath = isEditorChangedForPath;
        function isEditorResourcePath(resource) {
            if (editorView.getEditorResource() != null) {
                if (editorView.getEditorResource().getResourcePath() == resource) {
                    return true;
                }
            }
            return false;
        }
        function scrollEditorToPosition() {
            var session = editorView.getEditorPanel().getSession();
            var editorHistory = editorView.getHistoryForResource(editorView.getEditorResource());
            editorHistory.restoreScrollPosition(session, editorView.getEditorPanel());
        }
        FileEditor.scrollEditorToPosition = scrollEditorToPosition;
        function updateProjectTabOnChange() {
            editorView.getEditorPanel().on("input", function () {
                var editorResource = editorView.getEditorResource();
                var editorHistory = editorView.getHistoryForResource(editorResource);
                var editorText = currentEditorText();
                editorHistory.touchHistory(editorText);
                updateEditorTabMark(); // on input then you update star
            });
        }
        FileEditor.updateProjectTabOnChange = updateProjectTabOnChange;
        function updateEditorTabMark() {
            updateEditorTabMarkForResource(editorView.getEditorResource().getResourcePath());
        }
        function updateEditorTabMarkForResource(resource) {
            project_1.Project.markEditorTab(resource, isEditorChangedForPath(resource));
        }
        // XXX this should be in commands
        function formatEditorSource() {
            var text = editorView.getEditorPanel().getValue();
            var path = editorView.getEditorResource().getFilePath();
            $.ajax({
                contentType: 'text/plain',
                data: text,
                success: function (result) {
                    editorView.getEditorPanel().setReadOnly(false);
                    editorView.getEditorPanel().setValue(result, 1);
                },
                error: function () {
                    console.log("Format failed");
                },
                processData: false,
                type: 'POST',
                url: '/format/' + common_1.Common.getProjectName() + path
            });
        }
        FileEditor.formatEditorSource = formatEditorSource;
        function setEditorTheme(theme) {
            if (theme != null) {
                if (editorView.getEditorPanel() != null) {
                    editorView.getEditorPanel().setTheme(theme);
                }
            }
        }
        FileEditor.setEditorTheme = setEditorTheme;
        function showEditorBreakpoints() {
            editorView.getEditorBreakpointManager().showEditorBreakpoints();
        }
        FileEditor.showEditorBreakpoints = showEditorBreakpoints;
        function updateEditorFont(fontFamily, fontSize) {
            var actualFont = formatFont(fontFamily);
            editorView.getEditorPanel().setOptions({
                enableBasicAutocompletion: true,
                enableLiveAutocompletion: true,
                fontFamily: actualFont,
                fontSize: fontSize
            });
        }
        FileEditor.updateEditorFont = updateEditorFont;
        function formatFont(fontFamily) {
            var fontList = fontFamily.split(",");
            var actualFont = "";
            for (var i = 0; i < fontList.length; i++) {
                var fontEntry = fontList[i];
                fontEntry = common_1.Common.stringReplaceText(fontEntry, "'", "");
                fontEntry = common_1.Common.stringReplaceText(fontEntry, "\"", "");
                fontEntry = "'" + fontEntry.trim() + "'";
                actualFont += fontEntry + ",";
            }
            return actualFont + "monospace";
        }
    })(FileEditor = exports.FileEditor || (exports.FileEditor = {}));
});
//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]); 
