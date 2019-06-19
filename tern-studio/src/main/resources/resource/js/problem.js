define(["require", "exports", "w2ui", "common", "socket", "tree", "editor", "project"], function (require, exports, w2ui_1, common_1, socket_1, tree_1, editor_1, project_1) {
    "use strict";
    var ProblemManager;
    (function (ProblemManager) {
        var ProblemItem = (function () {
            function ProblemItem(resource, line, description, message, project, time) {
                this._description = description;
                this._resource = resource;
                this._line = line;
                this._message = message;
                this._project = project;
                this._time = time;
            }
            ProblemItem.prototype.isExpired = function () {
                return this._time + 120000 < common_1.Common.currentTime(); // expire after 2 minutes
            };
            ProblemItem.prototype.getKey = function () {
                return this._resource.getResourcePath() + ":" + this._line;
            };
            ProblemItem.prototype.getResourcePath = function () {
                return this._resource;
            };
            ProblemItem.prototype.getLine = function () {
                return this._line;
            };
            ProblemItem.prototype.getDescription = function () {
                return this._description;
            };
            ProblemItem.prototype.getMessage = function () {
                return this._message;
            };
            ProblemItem.prototype.getProject = function () {
                return this._project;
            };
            ProblemItem.prototype.getTime = function () {
                return this._time;
            };
            return ProblemItem;
        }());
        var currentProblems = {};
        function registerProblems() {
            socket_1.EventBus.createRoute('PROBLEM', updateProblems);
            setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
        }
        ProblemManager.registerProblems = registerProblems;
        function refreshProblems() {
            var activeProblems = {};
            for (var problemKey in currentProblems) {
                if (currentProblems.hasOwnProperty(problemKey)) {
                    var problemItem = currentProblems[problemKey];
                    if (problemItem != null) {
                        var problemResource = problemItem.getResourcePath();
                        var editorBuffer = editor_1.FileEditor.getEditorBufferForResource(problemResource.getResourcePath());
                        if (!isProblemInactive(editorBuffer, problemItem)) {
                            activeProblems[problemKey] = problemItem;
                        }
                    }
                }
            }
            updateActiveProblems(activeProblems);
        }
        function updateActiveProblems(activeProblems) {
            var missingProblems = 0;
            for (var problemKey in currentProblems) {
                if (!activeProblems.hasOwnProperty(problemKey)) {
                    missingProblems++; // something changed
                }
            }
            if (missingProblems > 0) {
                currentProblems = activeProblems;
                showProblems();
            }
        }
        function isProblemInactive(editorBuffer, problemItem) {
            var editorResource = editorBuffer.getResource();
            var problemTime = problemItem.getTime();
            var lastEditTime = editorBuffer.getLastModified();
            if (problemTime < lastEditTime) {
                return true;
            }
            if (!problemItem.isExpired()) {
                return false;
            }
            if (editorBuffer.isBufferCurrent()) {
                return false;
            }
            return true; // not current or problem newer than edit
        }
        function showProblems() {
            var problemRecords = [];
            var problemIndex = 1;
            for (var problemKey in currentProblems) {
                if (currentProblems.hasOwnProperty(problemKey)) {
                    var problemItem = currentProblems[problemKey];
                    if (problemItem != null) {
                        problemRecords.push({
                            recid: problemIndex++,
                            line: problemItem.getLine(),
                            location: "Line " + problemItem.getLine(),
                            resource: problemItem.getResourcePath().getFilePath(),
                            description: problemItem.getMessage(),
                            project: problemItem.getProject(),
                            script: problemItem.getResourcePath().getResourcePath() // /resource/<project>/blah/file.tern
                        });
                    }
                }
            }
            if (common_1.Common.updateTableRecords(problemRecords, 'problems')) {
                highlightProblems(); // highlight them also      
                project_1.Project.showProblemsTab(problemRecords.length); // focus the problems tab
                return true;
            }
            return false;
        }
        ProblemManager.showProblems = showProblems;
        function highlightProblems() {
            var editorState = editor_1.FileEditor.currentEditorState();
            var editorResource = editorState.getResource();
            if (editorResource != null) {
                var highlightUpdates = [];
                //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
                for (var problemKey in currentProblems) {
                    if (currentProblems.hasOwnProperty(problemKey)) {
                        if (common_1.Common.stringStartsWith(problemKey, editorResource.getResourcePath())) {
                            var problemItem = currentProblems[problemKey];
                            if (problemItem != null) {
                                highlightUpdates.push(problemItem.getLine());
                            }
                        }
                    }
                }
                editor_1.FileEditor.clearEditorHighlights();
                if (highlightUpdates.length > 0) {
                    editor_1.FileEditor.createMultipleEditorHighlights(highlightUpdates, "problemHighlight");
                }
            }
        }
        ProblemManager.highlightProblems = highlightProblems;
        function updateProblems(socket, type, text) {
            var problems = w2ui_1.w2ui['problems'];
            var message = JSON.parse(text);
            var resourcePath = tree_1.FileTree.createResourcePath(message.resource);
            var problemItem = new ProblemItem(resourcePath, message.line, message.description, "<div class='errorDescription'>" + message.description + "</div>", message.project, message.time);
            if (message.line >= 0) {
                console.log("Add problem '" + problemItem.getDescription() + "' at line '" + problemItem.getLine() + "'");
            }
            else {
                console.log("Clear all problems for " + problemItem.getResourcePath() + "");
            }
            if (problemItem.getLine() >= 0) {
                currentProblems[problemItem.getKey()] = problemItem;
            }
            else {
                for (var problemKey in currentProblems) {
                    if (currentProblems.hasOwnProperty(problemKey)) {
                        if (common_1.Common.stringStartsWith(problemKey, resourcePath.getResourcePath())) {
                            currentProblems[problemKey] = null;
                        }
                    }
                }
            }
            showProblems(); // if it has changed then highlight
        }
    })(ProblemManager = exports.ProblemManager || (exports.ProblemManager = {}));
});
//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]); 
