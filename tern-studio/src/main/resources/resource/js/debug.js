define(["require", "exports", "jquery", "w2ui", "./common", "./socket", "./editor", "./profiler", "./variables", "./console", "./threads", "status", "commands", "tree"], function (require, exports, $, w2ui_1, common_1, socket_1, editor_1, profiler_1, variables_1, console_1, threads_1, status_1, commands_1, tree_1) {
    "use strict";
    var DebugManager;
    (function (DebugManager) {
        var ProcessStatus;
        (function (ProcessStatus) {
            ProcessStatus[ProcessStatus["REGISTERING"] = 0] = "REGISTERING";
            ProcessStatus[ProcessStatus["WAITING"] = 1] = "WAITING";
            ProcessStatus[ProcessStatus["STARTING"] = 2] = "STARTING";
            ProcessStatus[ProcessStatus["COMPILING"] = 3] = "COMPILING";
            ProcessStatus[ProcessStatus["DEBUGGING"] = 4] = "DEBUGGING";
            ProcessStatus[ProcessStatus["RUNNING"] = 5] = "RUNNING";
            ProcessStatus[ProcessStatus["TERMINATING"] = 6] = "TERMINATING";
            ProcessStatus[ProcessStatus["FINISHED"] = 7] = "FINISHED";
            ProcessStatus[ProcessStatus["UNKNOWN"] = 8] = "UNKNOWN";
        })(ProcessStatus || (ProcessStatus = {}));
        var ProcessInfo = (function () {
            function ProcessInfo(process, project, resource, system, pid, status, time, running, debug, focus, memory, threads) {
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
            ProcessInfo.prototype.getDescription = function () {
                if (this._resource) {
                    return this._process + " is " + ProcessStatus[this._status] + " with " + this._resource + " focus is " + this.isFocus();
                }
                return this._process + " is " + ProcessStatus[this._status];
            };
            ProcessInfo.prototype.getStatus = function () {
                return this._status;
            };
            ProcessInfo.prototype.getProcess = function () {
                return this._process;
            };
            ProcessInfo.prototype.getProject = function () {
                return this._project;
            };
            ProcessInfo.prototype.getResource = function () {
                return this._resource;
            };
            ProcessInfo.prototype.getSystem = function () {
                return this._system;
            };
            ProcessInfo.prototype.getPid = function () {
                return this._pid;
            };
            ProcessInfo.prototype.getMemory = function () {
                return this._memory;
            };
            ProcessInfo.prototype.getThreads = function () {
                return this._threads;
            };
            ProcessInfo.prototype.getTime = function () {
                return this._time;
            };
            ProcessInfo.prototype.isFocus = function () {
                return "" + this._focus == "true";
            };
            ProcessInfo.prototype.isRunning = function () {
                return "" + this._running == "true";
            };
            ProcessInfo.prototype.isDebug = function () {
                return "" + this._debug == "true";
            };
            return ProcessInfo;
        }());
        var statusProcesses = {};
        var statusFocus = null;
        function createStatus() {
            socket_1.EventBus.createRoute("STATUS", createStatusProcess, clearStatus); // status of processes
            socket_1.EventBus.createRoute("TERMINATE", terminateStatusProcess, null); // clear focus
            socket_1.EventBus.createRoute("EXIT", terminateStatusProcess, null);
            setInterval(refreshStatusProcesses, 1000); // refresh the status systems every 1 second
        }
        DebugManager.createStatus = createStatus;
        function refreshStatusProcesses() {
            var timeMillis = common_1.Common.currentTime();
            var activeProcesses = {};
            var expiryCount = 0;
            for (var statusProcess in statusProcesses) {
                if (statusProcesses.hasOwnProperty(statusProcess)) {
                    var statusProcessInfo = statusProcesses[statusProcess];
                    if (statusProcessInfo != null) {
                        var statusTime = statusProcessInfo.getTime();
                        if (statusTime + 10000 > timeMillis) {
                            activeProcesses[statusProcess] = statusProcessInfo;
                        }
                        else {
                            expiryCount++;
                        }
                    }
                }
            }
            statusProcesses = activeProcesses; // reset refreshed statuses
            if (expiryCount > 0) {
                showStatus(); // something expired!
            }
            commands_1.Command.pingProcess(); // this will force a STATUS event
        }
        function terminateStatusProcess(socket, type, text) {
            var message = JSON.parse(text);
            var process = message.process;
            if (process) {
                var processInfo = statusProcesses[process];
                var duration = message.duration;
                statusProcesses[process] = null;
                console.log(process + " is TERMINATED");
                if (duration && processInfo) {
                    console.log("Process took " + duration + " ms");
                }
            }
            if (statusFocus == process) {
                //suspendedThreads = {};
                //currentFocusThread = null;
                threads_1.ThreadManager.terminateThreads();
                clearStatusFocus();
            }
            showStatus();
        }
        function createStatusProcess(socket, type, text) {
            var message = JSON.parse(text);
            var process = message.process;
            var status = message.status;
            var processMemory = Math.round((message.usedMemory / message.totalMemory) * 100);
            var processStatus = ProcessStatus[status];
            if (!status || !ProcessStatus.hasOwnProperty(status)) {
                processStatus = ProcessStatus.UNKNOWN; // when we have an error
                console.warn("No such status " + status + " setting to " + ProcessStatus[processStatus]);
            }
            var processInfo = statusProcesses[process] = new ProcessInfo(process, message.project, message.resource, message.system, message.pid, processStatus, message.time, message.running, // is anything running
            message.debug, message.focus, processMemory, message.threads);
            var description = processInfo.getDescription();
            var resource = processInfo.getResource();
            if (resource != null) {
            }
            if (processInfo.isFocus()) {
                updateStatusFocus(process);
            }
            else {
                if (statusFocus == process) {
                    clearStatusFocus(); // we are focus = false
                }
            }
            showStatus();
        }
        function isCurrentStatusFocusRunning() {
            if (statusFocus) {
                var statusProcessInfo = statusProcesses[statusFocus];
                if (statusProcessInfo) {
                    return statusProcessInfo.getResource() != null;
                }
            }
            return false;
        }
        DebugManager.isCurrentStatusFocusRunning = isCurrentStatusFocusRunning;
        function currentStatusFocus() {
            return statusFocus;
        }
        DebugManager.currentStatusFocus = currentStatusFocus;
        function updateStatusFocus(process) {
            var statusInfo = statusProcesses[process];
            if (statusInfo != null && statusInfo.getResource() != null) {
                var statusResourcePath = tree_1.FileTree.createResourcePath(statusInfo.getResource());
                $("#toolbarDebug").css('opacity', '1.0');
                $("#toolbarDebug").css('filter', 'alpha(opacity=100)'); // msie
                // debug the status info
                //console.log(statusInfo);
                status_1.StatusPanel.showProcessStatus(statusInfo.getResource(), process, statusInfo.isDebug());
            }
            else {
                $("#toolbarDebug").css('opacity', '0.4');
                $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
                $("#process").html("");
                editor_1.FileEditor.clearEditorHighlights(); // focus lost so clear breakpoints
            }
            if (statusFocus != process) {
                profiler_1.Profiler.clearProfiler(); // profiler does not apply
                threads_1.ThreadManager.clearThreads(); // race condition here
                variables_1.VariableManager.clearVariables();
            }
            console_1.ProcessConsole.updateConsoleFocus(process); // clear console if needed
            statusFocus = process;
            window.statusFocus = process;
        }
        function clearStatusFocus() {
            statusFocus = null;
            window.statusFocus = null;
            threads_1.ThreadManager.clearThreads(); // race condition here
            variables_1.VariableManager.clearVariables();
            //   clearProfiler();
            //   clearConsole();
            $("#toolbarDebug").css('opacity', '0.4');
            $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
            $("#process").html("");
        }
        function clearStatus() {
            statusProcesses = {};
            statusFocus = null;
            window.statusFocus = process;
            w2ui_1.w2ui['debug'].records = [];
            w2ui_1.w2ui['debug'].refresh();
        }
        function showStatus() {
            var statusRecords = [];
            var statusIndex = 1;
            for (var statusProcess in statusProcesses) {
                if (statusProcesses.hasOwnProperty(statusProcess)) {
                    var statusProcessInfo = statusProcesses[statusProcess];
                    if (statusProcessInfo != null) {
                        var statusProject = statusProcessInfo.getProject();
                        if (statusProject == common_1.Common.getProjectName() || statusProject == null) {
                            var displayName = "<div class='debugIdleRecord'>" + statusProcess + "</div>";
                            var active = "&nbsp;<input type='radio'><label></label>";
                            var resourcePath = "";
                            var debugging = statusProcessInfo.isDebug();
                            var status = statusProcessInfo.getStatus();
                            var running = false;
                            if (statusFocus == statusProcess) {
                                active = "&nbsp;<input type='radio' checked><label></label>";
                            }
                            if (statusProcessInfo.getResource() != null) {
                                var resourcePathDetails = tree_1.FileTree.createResourcePath(statusProcessInfo.getResource());
                                if (statusFocus == statusProcess && debugging) {
                                    displayName = "<div class='debugFocusRecord'>" + statusProcess + "</div>";
                                }
                                else {
                                    displayName = "<div class='debugRecord'>" + statusProcess + "</div>";
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
                        }
                        else {
                            console.log("Ignoring process " + statusProcess + " as it belongs to " + statusProject);
                        }
                    }
                }
            }
            common_1.Common.updateTableRecords(statusRecords, 'debug'); // update if changed only
        }
        DebugManager.showStatus = showStatus;
    })(DebugManager = exports.DebugManager || (exports.DebugManager = {}));
});
//ModuleSystem.registerModule("debug", "Debug module: debug.js", null, DebugManager.createStatus, [ "common", "socket", "tree", "threads" ]); 
