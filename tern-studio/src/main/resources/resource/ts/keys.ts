import * as $ from "jquery"
import * as Mousetrap from "mousetrap"
import {w2ui} from "w2ui"
import {Common} from "common"
import {FileEditor} from "editor"
import {Command} from "commands"
import {Project} from "project"
import {History} from "history"

export module KeyBinder {

   const MAX_PRESS_REPEAT = 250; // 250 milliseconds
   const pressTimes = {};
   var controlPressed = false;
   var keyBindings = {};  
   var disable = false;

   export function getKeyBindings() {
      return keyBindings;
   }

   export function disableKeys() {
      disable = true;
   }

   function isKeysDisabled() {
      return disable;
   }
   
   function isKeysEnabled() {
      return !isKeysDisabled();
   }

   export function bindKeys() {
      disableBrowserKeys();
      
      createKeyBinding("alt left", "Navigate Back", true, function() {
         return isKeysEnabled() && History.navigateBackward();
      });
      createKeyBinding("alt right", "Navigate Forward", true, function() {
         return isKeysEnabled() && History.navigateForward();
      });
      createKeyBinding("ctrl n", "New File", true, function() {
         return isKeysEnabled() && Command.newFile(null);
      });
      createKeyBinding("ctrl s", "Save File", true, function() {
         return isKeysEnabled() && Command.saveFile(null);
      });
      createKeyBinding("ctrl q", "Close File", true, function() {
         return isKeysEnabled() && Project.closeEditorTab();
      });      
      createKeyBinding("ctrl shift s", "Search Types", true, function() {
         return isKeysEnabled() && Command.searchTypes();
      });
      createKeyBinding("ctrl shift o", "Search Outline", true, function() {
         return isKeysEnabled() && Command.searchOutline();
      });
      createKeyBinding("ctrl tab", "Format Source", true, function() {
         return isKeysEnabled() && FileEditor.formatEditorSource();
      });
      createKeyBinding("ctrl shift e", "Evaluate Expression", true, function() {
         return isKeysEnabled() && Command.evaluateExpression();
      });
      createKeyBinding("ctrl shift m", "Toggle Full Screen", true, function() {
         return isKeysEnabled() && Project.toggleFullScreen();
      });
      createKeyBinding("ctrl shift l", "Switch Layout", true, function() {
         return isKeysEnabled() && Command.switchLayout()
      });
      createKeyBinding("ctrl shift p", "Switch Project", true, function() {
         return isKeysEnabled() && Command.switchProject();
      });
      createKeyBinding("ctrl shift g", "Find Files", true, function() {
         return isKeysEnabled() && Command.findFileNames();
      });
      createKeyBinding("ctrl shift h", "Global Search & Replace", true, function() {
         return isKeysEnabled() && Command.searchAndReplaceFiles();
      });
      createKeyBinding("ctrl shift f", "Global Search", true, function() {
         return isKeysEnabled() && Command.searchFiles();
      });
      createKeyBinding("ctrl h", "Search & Replace", true, function() {
         return isKeysEnabled() && FileEditor.findAndReplaceTextInEditor();
      });
      createKeyBinding("ctrl f", "Search", true, function() {
         return isKeysEnabled() && FileEditor.findTextInEditor();
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
      
      createKeyDownBinding("ctrl", false, function() {
         controlPressed = true;
      });
      createKeyUpBinding("ctrl", false, function() {
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
      createKeyBinding("ctrl r", "Run Script", true, function() {
         return isKeysEnabled() && Command.runScript();
      });
      createKeyBinding("ctrl shift r", "Run Script With Arguments", true, function() {
         return isKeysEnabled() && Command.runScriptWithArguments();
      });
      createKeyBinding("ctrl b", "Debug Script", true, function() {
         return isKeysEnabled() && Command.debugScript();
      });
      createKeyBinding("ctrl shift b", "Debug Script With Arguments", true, function() {
         return isKeysEnabled() && Command.debugScriptWithArguments();
      });     
      createKeyBinding("ctrl k", "Debug Remote Script", true, function() {
         return isKeysEnabled() && Command.attachRemoteDebugger();
      });
      createKeyBinding("f8", "Resume Script", true, function() {
         console.log("F8");
         return isKeysEnabled() && Command.resumeScript();
      });
      createKeyBinding("f5", "Step In", true, function() {
         console.log("F5");
         return isKeysEnabled() && Command.stepInScript();
      });
      createKeyBinding("f7", "Step Out", true, function() {
         console.log("F7");
         return isKeysEnabled() && Command.stepOutScript();
      });
      createKeyBinding("f6", "Step Over", true, function() {
         console.log("F6");
         return isKeysEnabled() && Command.stepOverScript();
      });
   }
   
   export function isControlPressed() {
      return controlPressed;
   }
   
   function disableBrowserKeys() {
      $(window).keydown(function(event) {
         if(event.ctrlKey) { 
            // do not prevent default for copy/cut/paste
            if(!isCopyChar(event) && !isCutChar(event) && !isPasteChar(event)) {
               event.preventDefault(); 
            }
         }
       });
   }
   
   function isCopyChar(e) {
      var evt = e || window.event
      return evt.keyCode === 67;
   }
   
   function isCutChar(e) {
      var evt = e || window.event
      return evt.keyCode === 88;
   }
   
   function isPasteChar(e) {
      var evt = e || window.event
      return evt.keyCode === 86;
   }
   
   function parseKeyBinding(name) {
      var keyParts = name.split(/\s+/);
      var keyBindingParts = [];
      
      for(var i = 0; i < keyParts.length; i++) {
         var keyPart = keyParts[i];
         
         if(Common.isMacintosh() && keyPart == 'ctrl') {
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
      keyBindings[keyBinding.editor] = Common.escapeHtml(description);
      FileEditor.addEditorKeyBinding(keyBinding, pressAction);
      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            if(!isKeysDisabled()) {
                pressAction();
            }
         }
         return !preventDefault;
      });
   }
   
   function createKeyDownBinding(name, preventDefault, pressAction) {
      var keyBinding = parseKeyBinding(name);      

      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            if(!isKeysDisabled()) {
                pressAction();
            }
         }
         return !preventDefault;
      }, 'keydown');
   }
   
   function createKeyUpBinding(name, preventDefault, pressAction) {
      var keyBinding = parseKeyBinding(name);      

      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            if(!isKeysDisabled()) {
               pressAction();
            }
         }
         return !preventDefault;
      }, 'keyup');
   }
}

//ModuleSystem.registerModule("keys", "Key binder: key.js", null, KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]);