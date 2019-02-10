define(["require", "exports", "jquery", "w2ui", "socket", "common", "tree", "editor", "variables", "explorer", "profiler", "status", "problem"], function (require, exports, $, w2ui_1, socket_1, common_1, tree_1, editor_1, variables_1, explorer_1, profiler_1, status_1, problem_1) {
    "use strict";
    (function (ThreadStatus) {
        ThreadStatus[ThreadStatus["RUNNING"] = 0] = "RUNNING";
        ThreadStatus[ThreadStatus["SUSPENDED"] = 1] = "SUSPENDED";
        ThreadStatus[ThreadStatus["UNKNOWN"] = 2] = "UNKNOWN";
    })(exports.ThreadStatus || (exports.ThreadStatus = {}));
    var ThreadStatus = exports.ThreadStatus;
    var ThreadScope = (function () {
        function ThreadScope(variables, evaluation, status, instruction, process, resource, source, thread, stack, line, depth, key, change) {
            this._variables = variables;
            this._evaluation = evaluation;
            this._status = status;
            this._instruction = instruction;
            this._process = process;
            this._resource = resource;
            this._source = source;
            this._thread = thread;
            this._stack = stack;
            this._line = line;
            this._depth = depth;
            this._change = change;
            this._key = key;
        }
        ThreadScope.prototype.getVariables = function () {
            return this._variables;
        };
        ThreadScope.prototype.getEvaluation = function () {
            return this._evaluation;
        };
        ThreadScope.prototype.getStatus = function () {
            return this._status;
        };
        ThreadScope.prototype.getInstruction = function () {
            return this._instruction;
        };
        ThreadScope.prototype.getProcess = function () {
            return this._process;
        };
        ThreadScope.prototype.getResource = function () {
            return this._resource;
        };
        ThreadScope.prototype.getSource = function () {
            return this._source;
        };
        ThreadScope.prototype.getThread = function () {
            return this._thread;
        };
        ThreadScope.prototype.getStack = function () {
            return this._stack;
        };
        ThreadScope.prototype.getLine = function () {
            return this._line;
        };
        ThreadScope.prototype.getDepth = function () {
            return this._depth;
        };
        ThreadScope.prototype.getKey = function () {
            return this._key;
        };
        ThreadScope.prototype.getChange = function () {
            return this._change;
        };
        return ThreadScope;
    }());
    exports.ThreadScope = ThreadScope;
    var ThreadVariables = (function () {
        function ThreadVariables(variables) {
            this._variables = variables;
        }
        ThreadVariables.prototype.getVariables = function () {
            return this._variables;
        };
        return ThreadVariables;
    }());
    exports.ThreadVariables = ThreadVariables;
    var ThreadManager;
    (function (ThreadManager) {
        var suspendedThreads = {};
        var threadEditorFocus = null;
        function createThreads() {
            socket_1.EventBus.createRoute("BEGIN", startThreads, clearThreads);
            socket_1.EventBus.createRoute("SCOPE", updateThreads, variables_1.VariableManager.clearVariables);
            socket_1.EventBus.createRoute("TERMINATE", deleteThreads);
            socket_1.EventBus.createRoute("EXIT", deleteThreads);
        }
        ThreadManager.createThreads = createThreads;
        function startThreads(socket, type, text) {
            var message = JSON.parse(text);
            suspendedThreads = {};
            clearFocusThread();
            variables_1.VariableManager.clearVariables();
            profiler_1.Profiler.clearProfiler();
            clearThreads();
            status_1.StatusPanel.showProcessStatus(message.resource, message.process, message.debug);
        }
        function deleteThreads(socket, type, text) {
            var message = JSON.parse(text);
            var process = message.process;
            if (threadEditorFocus != null && threadEditorFocus.getProcess() == process) {
                terminateThreads();
            }
        }
        function terminateThreads() {
            suspendedThreads = {};
            clearFocusThread();
            editor_1.FileEditor.clearEditorHighlights(); // this should be done in editor.js, i.e EventBus.createRoute("EXIT" ... )
            problem_1.ProblemManager.highlightProblems(); // don't hide the errors
            variables_1.VariableManager.clearVariables();
            clearThreads();
        }
        ThreadManager.terminateThreads = terminateThreads;
        function clearThreads() {
            clearFocusThread();
            w2ui_1.w2ui['threads'].records = [];
            w2ui_1.w2ui['threads'].refresh();
            $("#process").html("");
        }
        ThreadManager.clearThreads = clearThreads;
        function updateThreads(socket, type, text) {
            var message = JSON.parse(text);
            var status = message.status;
            var threadVariables = new ThreadVariables(message.variables);
            var threadEvaluation = new ThreadVariables(message.evaluation);
            var threadStatus = ThreadStatus[status];
            if (!status || !ThreadStatus.hasOwnProperty(status)) {
                threadStatus = ThreadStatus.UNKNOWN; // when we have an error
                console.warn("No such thread status " + status + " setting to " + ThreadStatus[threadStatus]);
            }
            var threadScope = new ThreadScope(threadVariables, threadEvaluation, threadStatus, message.instruction, message.process, message.resource, message.source, message.thread, message.stack, message.line, message.depth, message.key, message.change);
            if (isThreadFocusResumed(threadScope)) {
                clearFocusThread(); // clear focus as it is a resume
                updateThreadPanels(threadScope);
                editor_1.FileEditor.clearEditorHighlights(); // the thread has resumed so clear highlights
            }
            else {
                if (threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
                    if (isThreadFocusUpdateNew(threadScope)) {
                        updateFocusedThread(threadScope); // something new happened so focus editor
                        updateThreadPanels(threadScope);
                    }
                }
                else if (!threadEditorFocus || threadEditorFocus.getThread() == null) {
                    focusThread(threadScope);
                    updateThreadPanels(threadScope);
                }
                else {
                    var currentScope = suspendedThreads[threadScope.getThread()];
                    if (isThreadScopeDifferent(currentScope, threadScope)) {
                        updateThreadPanels(threadScope);
                    }
                }
            }
            suspendedThreads[threadScope.getThread()] = threadScope;
            showThreadBreakpointLine(threadScope); // show breakpoint on editor
        }
        function isThreadScopeDifferent(leftScope, rightScope) {
            if (leftScope != null && rightScope != null) {
                if (leftScope.getThread() != rightScope.getThread()) {
                    return true;
                }
                if (leftScope.getStatus() != rightScope.getStatus()) {
                    return true;
                }
                if (leftScope.getResource() != rightScope.getResource()) {
                    return true;
                }
                if (leftScope.getLine() != rightScope.getLine()) {
                    return true;
                }
                return false;
            }
            return leftScope != rightScope;
        }
        function showThreadBreakpointLine(threadScope) {
            var editorState = editor_1.FileEditor.currentEditorState();
            if (threadEditorFocus.getThread() == threadScope.getThread()) {
                if (editorState.getResource().getFilePath() == threadScope.getResource() && threadScope.getStatus() == ThreadStatus.SUSPENDED) {
                    if (editor_1.FileEditor.createEditorHighlight(threadScope.getLine(), "threadHighlight")) {
                        editor_1.FileEditor.showEditorLine(threadScope.getLine());
                    }
                }
            }
        }
        function updateThreadPanels(threadScope) {
            suspendedThreads[threadScope.getThread()] = threadScope; // N.B update suspended threads before rendering
            showThreads();
            variables_1.VariableManager.showVariables();
        }
        function updateFocusedThread(threadScope) {
            if (isThreadFocusLineChange(threadScope)) {
                if (isThreadFocusResourceChange(threadScope)) {
                    openAndShowThreadResource(threadScope);
                }
                else {
                    updateThreadFocus(threadScope);
                    editor_1.FileEditor.showEditorLine(threadScope.getLine());
                }
            }
            else {
                updateThreadFocus(threadScope); // record focus thread
            }
        }
        function focusThread(threadScope) {
            var editorState = editor_1.FileEditor.currentEditorState();
            if (!editorState || !editorState.getResource() || editorState.getResource().getFilePath() != threadScope.getResource()) {
                openAndShowThreadResource(threadScope);
            }
            else {
                updateThreadFocus(threadScope);
                editor_1.FileEditor.showEditorLine(threadScope.getLine());
            }
        }
        function openAndShowThreadResource(threadScope) {
            var resourcePathDetails = tree_1.FileTree.createResourcePath(threadScope.getResource());
            var scopeSource = threadScope.getSource();
            if (scopeSource) {
                explorer_1.FileExplorer.showAsTreeFile(resourcePathDetails.getResourcePath(), scopeSource, function () {
                    updateThreadFocus(threadScope);
                    editor_1.FileEditor.showEditorLine(threadScope.getLine());
                });
            }
            else {
                explorer_1.FileExplorer.openTreeFile(resourcePathDetails.getResourcePath(), function () {
                    updateThreadFocus(threadScope);
                    editor_1.FileEditor.showEditorLine(threadScope.getLine());
                });
            }
        }
        function isThreadFocusResumed(threadScope) {
            if (threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
                return threadScope.getStatus() != ThreadStatus.SUSPENDED; // the thread has resumed
            }
            return false;
        }
        function isThreadFocusUpdateNew(threadScope) {
            if (threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
                if (threadScope.getStatus() == ThreadStatus.SUSPENDED) {
                    if (threadEditorFocus.getKey() != threadScope.getKey()) {
                        return true;
                    }
                    if (threadEditorFocus.getChange() != threadScope.getChange()) {
                        return true;
                    }
                }
            }
            return false;
        }
        function isThreadFocusPositionChange(threadScope) {
            return isThreadFocusLineChange(threadScope) || isThreadFocusResourceChange(threadScope);
        }
        function isThreadFocusLineChange(threadScope) {
            if (threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
                return threadEditorFocus.getLine() != threadScope.getLine(); // hash the thread or focus line changed
            }
            return false;
        }
        function isThreadFocusResourceChange(threadScope) {
            if (threadEditorFocus && threadEditorFocus.getThread() == threadScope.getThread()) {
                var editorState = editor_1.FileEditor.currentEditorState();
                return editorState.getResource().getFilePath() != threadScope.getResource(); // is there a need to update the editor
            }
            return false;
        }
        function focusedThread() {
            if (threadEditorFocus && threadEditorFocus.getThread() != null) {
                return suspendedThreads[threadEditorFocus.getThread()];
            }
            return null;
        }
        ThreadManager.focusedThread = focusedThread;
        function clearFocusThread() {
            variables_1.VariableManager.clearVariables(); // clear the browse tree
            var threadCopy = new ThreadScope(new ThreadVariables({}), new ThreadVariables({}), ThreadStatus.RUNNING, null, null, null, null, null, null, -1, -1, -1, -1);
            threadEditorFocus = threadCopy;
        }
        function updateThreadFocus(threadScope) {
            var threadCopy = new ThreadScope(threadScope.getVariables(), threadScope.getEvaluation(), threadScope.getStatus(), threadScope.getInstruction(), threadScope.getProcess(), threadScope.getResource(), threadScope.getSource(), threadScope.getThread(), threadScope.getStack(), threadScope.getLine(), threadScope.getDepth(), threadScope.getKey(), threadScope.getChange());
            threadEditorFocus = threadCopy;
            updateThreadPanels(threadCopy); // update the thread variables etc..
        }
        function updateThreadFocusByName(threadName) {
            var threadScope = suspendedThreads[threadName];
            updateThreadFocus(threadScope);
        }
        ThreadManager.updateThreadFocusByName = updateThreadFocusByName;
        function focusedThreadVariables() {
            if (threadEditorFocus && threadEditorFocus.getThread() != null) {
                var threadScope = suspendedThreads[threadEditorFocus.getThread()];
                if (threadScope != null) {
                    return threadScope.getVariables();
                }
            }
            return new ThreadVariables({});
        }
        ThreadManager.focusedThreadVariables = focusedThreadVariables;
        function focusedThreadEvaluation() {
            if (threadEditorFocus && threadEditorFocus.getThread() != null) {
                var threadScope = suspendedThreads[threadEditorFocus.getThread()];
                if (threadScope != null) {
                    return threadScope.getEvaluation();
                }
            }
            return new ThreadVariables({});
        }
        ThreadManager.focusedThreadEvaluation = focusedThreadEvaluation;
        function showThreads() {
            var threadRecords = [];
            var threadIndex = 1;
            for (var threadName in suspendedThreads) {
                if (suspendedThreads.hasOwnProperty(threadName)) {
                    var threadScope = suspendedThreads[threadName];
                    var displayStyle = 'threadSuspended';
                    var active = "&nbsp;<input type='radio'>";
                    showThreadBreakpointLine(threadScope);
                    if (threadScope.getStatus() != ThreadStatus.SUSPENDED) {
                        displayStyle = 'threadRunning';
                    }
                    else {
                        if (threadEditorFocus.getThread() == threadScope.getThread()) {
                            active = "&nbsp;<input type='radio' checked>";
                        }
                    }
                    var displayName = "<div title='" + threadScope.getStack() + "' class='" + displayStyle + "'>" + threadName + "</div>";
                    var resourcePathDetails = tree_1.FileTree.createResourcePath(threadScope.getResource());
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
            common_1.Common.updateTableRecords(threadRecords, 'threads'); // update if changed only
        }
        ThreadManager.showThreads = showThreads;
    })(ThreadManager = exports.ThreadManager || (exports.ThreadManager = {}));
});
//ModuleSystem.registerModule("threads", "Thread module: threads.js", null, ThreadManager.createThreads, [ "common", "socket", "explorer" ]); 
