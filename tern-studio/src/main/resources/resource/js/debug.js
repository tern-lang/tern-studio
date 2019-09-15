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
            function ProcessInfo(process, project, resource, system, pid, status, time, running, debug, focus, memory, expiry, remaining, threads) {
                this._process = process;
                this._resource = resource;
                this._system = system;
                this._pid = pid;
                this._time = time;
                this._running = running;
                this._focus = focus;
                this._project = project;
                this._memory = memory;
                this._expiry = expiry;
                this._remaining = remaining;
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
            ProcessInfo.prototype.getExpiry = function () {
                return this._expiry;
            };
            ProcessInfo.prototype.getRemaining = function () {
                return this._remaining;
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
        function calculateRemainingTimePercent(totalTime, usedTime) {
            if (totalTime <= 0) {
                return 100;
            }
            var remainingTime = totalTime - usedTime;
            return Math.round((remainingTime / totalTime) * 100);
        }
        function calculateUsedMemoryPercent(totalMemory, usedMemory) {
            if (totalMemory <= 0) {
                return 0;
            }
            return Math.round((usedMemory / totalMemory) * 100);
        }
        function createStatusProcess(socket, type, text) {
            var message = JSON.parse(text);
            var process = message.process;
            var status = message.status;
            var processExpiry = calculateRemainingTimePercent(message.totalTime, message.usedTime);
            var processMemory = calculateUsedMemoryPercent(message.totalMemory, message.usedMemory);
            var processStatus = ProcessStatus[status];
            if (!status || !ProcessStatus.hasOwnProperty(status)) {
                processStatus = ProcessStatus.UNKNOWN; // when we have an error
                console.warn("No such status " + status + " setting to " + ProcessStatus[processStatus]);
            }
            var processInfo = statusProcesses[process] = new ProcessInfo(process, message.project, message.resource, message.system, message.pid, processStatus, message.time, message.running, // is anything running
            message.debug, message.focus, processMemory, // how much memory used as a %
            processExpiry, // how much time remains as a %
            message.totalTime == 0 ? null : common_1.Common.formatDuration(message.totalTime - message.usedTime), message.threads);
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
                        var statusPlatform = statusProcessInfo.getSystem();
                        var statusId = "process_" + common_1.Common.escapeHtml(statusProcess);
                        if (statusProject == common_1.Common.getProjectName() || statusProject == null) {
                            var displayName = "<div id='" + statusId + "' class='debugIdleRecord' title='" + statusPlatform + "'>" + statusProcess + "</div>";
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
                                    displayName = "<div id='" + statusId + "' class='debugFocusRecord' title='" + statusPlatform + "'>" + statusProcess + "</div>";
                                }
                                else {
                                    displayName = "<div id='" + statusId + "' class='debugRecord' title='" + statusPlatform + "'>" + statusProcess + "</div>";
                                }
                                resourcePath = resourcePathDetails.getResourcePath();
                                running = true;
                            }
                            var expiryPercentage = Math.round(statusProcessInfo.getExpiry());
                            var percentageTitle = statusProcessInfo.getRemaining() ? ('title="' + statusProcessInfo.getRemaining() + '"') : "";
                            var percentageColor = "#b5bbbe";
                            var percentageBar = "";
                            if (statusProcessInfo.getResource() != null) {
                                percentageColor = expiryPercentage < 20 ? "#ed6761" : "#8adb62";
                            }
                            percentageBar += "<!-- " + statusProcessInfo.getExpiry() + " -->";
                            percentageBar += "<div " + percentageTitle + " style='padding-top: 2px; padding-bottom; 2px; padding-left: 5px; padding-right: 5px;'>";
                            percentageBar += "<div style='height: 10px; background: " + percentageColor + "; width: " + expiryPercentage + "%;'></div>";
                            percentageBar += "</div>";
                            statusRecords.push({
                                recid: statusIndex++,
                                name: displayName,
                                active: active,
                                process: statusProcess,
                                status: ProcessStatus[status],
                                running: running,
                                expiry: percentageBar,
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
