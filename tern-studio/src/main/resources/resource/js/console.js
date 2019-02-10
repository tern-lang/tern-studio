define(["require", "exports", "socket"], function (require, exports, socket_1) {
    "use strict";
    var ProcessConsole;
    (function (ProcessConsole) {
        var consoleTable = {};
        var consoleCapacity = 5000;
        var consoleProcess = null;
        var consoleFont = null;
        var consoleFontSize = null;
        function registerConsole() {
            socket_1.EventBus.createRoute("BEGIN", createConsole, null);
            socket_1.EventBus.createRoute('PRINT_ERROR', updateConsole, null);
            socket_1.EventBus.createRoute('PRINT_OUTPUT', updateConsole, null);
            socket_1.EventBus.createRoute('TERMINATE', terminateConsole, null); // clear focus
            socket_1.EventBus.createRoute('EXIT', terminateConsole, null);
            setInterval(showConsole, 200); // prevents reflow overload when console is busy
        }
        ProcessConsole.registerConsole = registerConsole;
        function updateConsoleFont(fontFamily, fontSize) {
            var consoleElement = document.getElementById("console");
            if (consoleElement != null) {
                consoleElement.style.fontFamily = fontFamily;
                consoleElement.style.fontSize = fontSize;
            }
            consoleFont = fontFamily;
            consoleFontSize = fontSize;
        }
        ProcessConsole.updateConsoleFont = updateConsoleFont;
        function updateConsoleCapacity(maxCapacity) {
            consoleCapacity = maxCapacity;
        }
        ProcessConsole.updateConsoleCapacity = updateConsoleCapacity;
        function terminateConsole(socket, type, text) {
            var message = JSON.parse(text);
            var process = message.process;
            if (consoleProcess == process) {
                showConsole();
            }
            var consoleData = consoleTable[process];
            if (consoleData != null) {
                consoleData.valid = false; // means it should be terminated when unfocused
            }
        }
        function clearConsole() {
            var consoleElement = document.getElementById("console");
            if (consoleElement != null) {
                document.getElementById("console").innerHTML = "";
            }
            consoleProcess = null;
        }
        ProcessConsole.clearConsole = clearConsole;
        /**
         * This method can be very slow, we need to improve the merging of nodes
         * so that concatenation reduces the overhead.
         */
        function showConsole() {
            var consoleElement = document.getElementById("console");
            var consoleText = null;
            var previous = null;
            if (consoleElement != null && consoleProcess != null) {
                var currentText = consoleElement.innerHTML;
                var consoleData = consoleTable[consoleProcess]; // is ther an update?
                if (consoleData != null && (currentText == "" || consoleData.update == true)) {
                    consoleData.update = false; // clear the update
                    for (var i = 0; i < consoleData.list.length; i++) {
                        var next = consoleData.list[i];
                        if (previous == null) {
                            if (next.error) {
                                consoleText = "<span class='consoleError'>" + next.text;
                            }
                            else {
                                consoleText = "<span class='consoleNormal'>" + next.text;
                            }
                            previous = next.error;
                        }
                        else if (next.error != previous) {
                            consoleText += "</span>";
                            if (next.error) {
                                consoleText += "<span class='consoleError'>" + next.text;
                            }
                            else {
                                consoleText += "<span class='consoleNormal'>" + next.text;
                            }
                            previous = next.error;
                        }
                        else {
                            consoleText += next.text;
                        }
                    }
                    if (consoleText != null) {
                        consoleText += "</span>";
                        consoleElement.innerHTML = consoleText;
                        consoleElement.scrollTop = consoleElement.scrollHeight;
                    }
                }
                if (consoleFont && consoleFontSize) {
                    updateConsoleFont(consoleFont, consoleFontSize);
                }
            }
        }
        ProcessConsole.showConsole = showConsole;
        function updateConsoleFocus(processToFocus) {
            if (consoleProcess != processToFocus) {
                deleteAllInvalidConsoles(processToFocus); // delete only on a change of focus
                clearConsole();
                consoleProcess = processToFocus;
                showConsole();
            }
        }
        ProcessConsole.updateConsoleFocus = updateConsoleFocus;
        function deleteAllInvalidConsoles(processToKeep) {
            var validConsoles = {};
            for (var processName in consoleTable) {
                if (consoleTable.hasOwnProperty(processName)) {
                    var consoleData = consoleTable[processName];
                    if (consoleData.valid || processName == processToKeep) {
                        validConsoles[processName] = consoleData;
                    }
                }
            }
            consoleTable = validConsoles; // make sure expired consoles are removed
        }
        function createConsole(socket, type, value) {
            var message = JSON.parse(value);
            var newProcess = message.process;
            var consoleData = consoleTable[newProcess];
            consoleTable[newProcess] = {
                list: [],
                size: 0,
                update: true,
                valid: true
            };
            updateConsoleFocus(newProcess);
        }
        /**
         * This function should probably merge the nodes to some extent, it will improve
         * the performance of the console rendering.
         */
        function updateConsole(socket, type, value) {
            var offset = value.indexOf(':');
            var updateProcess = value.substring(0, offset);
            var updateText = value.substring(offset + 1);
            var node = {
                error: type == 'PRINT_ERROR',
                text: updateText
            };
            var consoleData = consoleTable[updateProcess];
            if (consoleData == null) {
                consoleData = {
                    list: [],
                    size: 0,
                    update: true,
                    valid: true
                };
                consoleTable[updateProcess] = consoleData;
            }
            consoleData.list.push(node); // put at the end, i.e index consoleTable.length - 1
            consoleData.size += updateText.length; // update the size of the console
            while (consoleData.list.length > 3 && consoleData.size > consoleCapacity) {
                var removeNode = consoleData.list.shift(); // remove from the start, i.e index 0
                if (removeNode != null) {
                    consoleData.size -= removeNode.text.length;
                }
            }
            consoleData.update = true;
        }
    })(ProcessConsole = exports.ProcessConsole || (exports.ProcessConsole = {}));
});
//ModuleSystem.registerModule("console", "Console module: console.js", null, ProcessConsole.registerConsole, ["common", "socket"]); 
