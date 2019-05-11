define(["require", "exports", "jquery", "mousetrap", "common", "editor", "commands", "project", "history"], function (require, exports, $, Mousetrap, common_1, editor_1, commands_1, project_1, history_1) {
    "use strict";
    var KeyBinder;
    (function (KeyBinder) {
        var MAX_PRESS_REPEAT = 250; // 250 milliseconds
        var pressTimes = {};
        var controlPressed = false;
        var keyBindings = {};
        var disable = false;
        function getKeyBindings() {
            return keyBindings;
        }
        KeyBinder.getKeyBindings = getKeyBindings;
        function disableKeys() {
            disable = true;
        }
        KeyBinder.disableKeys = disableKeys;
        function isKeysDisabled() {
            return disable;
        }
        function bindKeys() {
            disableBrowserKeys();
            createKeyBinding("alt left", "Navigate Back", true, function () {
                history_1.History.navigateBackward();
            });
            createKeyBinding("alt right", "Navigate Forward", true, function () {
                history_1.History.navigateForward();
            });
            createKeyBinding("ctrl n", "New File", true, function () {
                commands_1.Command.newFile(null);
            });
            createKeyBinding("ctrl s", "Save File", true, function () {
                commands_1.Command.saveFile(null);
            });
            createKeyBinding("ctrl q", "Close File", true, function () {
                project_1.Project.closeEditorTab();
            });
            createKeyBinding("ctrl shift s", "Search Types", true, function () {
                commands_1.Command.searchTypes();
            });
            createKeyBinding("ctrl shift o", "Search Outline", true, function () {
                commands_1.Command.searchOutline();
            });
            createKeyBinding("ctrl tab", "Format Source", true, function () {
                editor_1.FileEditor.formatEditorSource();
            });
            createKeyBinding("ctrl shift e", "Evaluate Expression", true, function () {
                commands_1.Command.evaluateExpression();
            });
            createKeyBinding("ctrl shift m", "Toggle Full Screen", true, function () {
                project_1.Project.toggleFullScreen();
            });
            createKeyBinding("ctrl shift l", "Switch Layout", true, function () {
                commands_1.Command.switchLayout();
            });
            createKeyBinding("ctrl shift p", "Switch Project", true, function () {
                commands_1.Command.switchProject();
            });
            createKeyBinding("ctrl shift g", "Find Files", true, function () {
                commands_1.Command.findFileNames();
            });
            createKeyBinding("ctrl shift h", "Global Search & Replace", true, function () {
                commands_1.Command.searchAndReplaceFiles();
            });
            createKeyBinding("ctrl shift f", "Global Search", true, function () {
                commands_1.Command.searchFiles();
            });
            createKeyBinding("ctrl h", "Search & Replace", true, function () {
                editor_1.FileEditor.findAndReplaceTextInEditor();
            });
            createKeyBinding("ctrl f", "Search", true, function () {
                editor_1.FileEditor.findTextInEditor();
            });
            //      createKeyBinding("ctrl c", true, function() {
            //         console.log("COPY BUFFER");
            //      });
            //      createKeyBinding("ctrl v", true, function() {
            //         console.log("PASTE BUFFER");
            //      });
            //      createKeyBinding("ctrl x", true, function() {
            //         console.log("CUT BUFFER");
            //      });
            createKeyDownBinding("ctrl", false, function () {
                controlPressed = true;
            });
            createKeyUpBinding("ctrl", false, function () {
                controlPressed = false;
            });
            //      createKeyBinding("up", false, function() {
            //         FileEditor.moveCursorUp();
            //      });
            //      createKeyBinding("down", false, function() {
            //         FileEditor.moveCursorDown();
            //      });
            //      createKeyBinding("left", false, function() {
            //         FileEditor.moveCursorLeft();
            //      });
            //      createKeyBinding("right", false, function() {
            //         FileEditor.moveCursorRight();
            //      });
            //      createKeyBinding("tab", true, function() {
            //         FileEditor.indentCurrentLine();
            //      });
            //      createKeyBinding("ctrl /", true, function() {
            //         FileEditor.commentSelection();
            //      });
            //      createKeyBinding("ctrl z", true, function() {
            //         FileEditor.undoEditorChange();
            //      });
            //      createKeyBinding("ctrl y", true, function() {
            //         FileEditor.redoEditorChange();
            //      });
            createKeyBinding("ctrl r", "Run Script", true, function () {
                commands_1.Command.runScript();
            });
            createKeyBinding("ctrl shift r", "Run Script With Arguments", true, function () {
                commands_1.Command.runScriptWithArguments();
            });
            createKeyBinding("ctrl b", "Debug Script", true, function () {
                commands_1.Command.debugScript();
            });
            createKeyBinding("ctrl shift b", "Debug Script With Arguments", true, function () {
                commands_1.Command.debugScriptWithArguments();
            });
            createKeyBinding("ctrl k", "Debug Remote Script", true, function () {
                commands_1.Command.attachRemoteDebugger();
            });
            createKeyBinding("f8", "Resume Script", true, function () {
                console.log("F8");
                commands_1.Command.resumeScript();
            });
            createKeyBinding("f5", "Step In", true, function () {
                console.log("F5");
                commands_1.Command.stepInScript();
            });
            createKeyBinding("f7", "Step Out", true, function () {
                console.log("F7");
                commands_1.Command.stepOutScript();
            });
            createKeyBinding("f6", "Step Over", true, function () {
                console.log("F6");
                commands_1.Command.stepOverScript();
            });
        }
        KeyBinder.bindKeys = bindKeys;
        function isControlPressed() {
            return controlPressed;
        }
        KeyBinder.isControlPressed = isControlPressed;
        function disableBrowserKeys() {
            $(window).keydown(function (event) {
                if (event.ctrlKey) {
                    // do not prevent default for copy/cut/paste
                    if (!isCopyChar(event) && !isCutChar(event) && !isPasteChar(event)) {
                        event.preventDefault();
                    }
                }
            });
        }
        function isCopyChar(e) {
            var evt = e || window.event;
            return evt.keyCode === 67;
        }
        function isCutChar(e) {
            var evt = e || window.event;
            return evt.keyCode === 88;
        }
        function isPasteChar(e) {
            var evt = e || window.event;
            return evt.keyCode === 86;
        }
        function parseKeyBinding(name) {
            var keyParts = name.split(/\s+/);
            var keyBindingParts = [];
            for (var i = 0; i < keyParts.length; i++) {
                var keyPart = keyParts[i];
                if (common_1.Common.isMacintosh() && keyPart == 'ctrl') {
                    keyPart = 'command';
                }
                keyBindingParts[i] = keyPart.charAt(0).toUpperCase() + keyPart.slice(1);
            }
            var editorKeyBinding = keyBindingParts.join("-");
            var globalKeyBinding = keyBindingParts.join("+").toLowerCase();
            return {
                editor: editorKeyBinding,
                global: globalKeyBinding
            };
        }
        function createKeyBinding(name, description, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            //      var editor = ace.edit("editor");
            //       
            //      console.log(keyBinding.editor);
            //      editor.commands.addCommand({
            //           name : name,
            //           bindKey : {
            //               win : keyBinding.editor,
            //               mac : keyBinding.editor
            //           },
            //           exec : function(editor) {
            //              if(pressAction) { 
            //                 pressAction();
            //              }
            //           }
            //      });
            keyBindings[keyBinding.editor] = common_1.Common.escapeHtml(description);
            editor_1.FileEditor.addEditorKeyBinding(keyBinding, pressAction);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    if (!isKeysDisabled()) {
                        pressAction();
                    }
                }
                return !preventDefault;
            });
        }
        function createKeyDownBinding(name, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    if (!isKeysDisabled()) {
                        pressAction();
                    }
                }
                return !preventDefault;
            }, 'keydown');
        }
        function createKeyUpBinding(name, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    if (!isKeysDisabled()) {
                        pressAction();
                    }
                }
                return !preventDefault;
            }, 'keyup');
        }
    })(KeyBinder = exports.KeyBinder || (exports.KeyBinder = {}));
});
//ModuleSystem.registerModule("keys", "Key binder: key.js", null, KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]); 
