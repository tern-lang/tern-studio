import * as $ from "jquery"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {ProcessConsole} from "console"
import {ProblemManager} from "problem"
import {FileEditor, FileEditorState, FileEditorBuffer} from "editor"
import {LoadSpinner} from "spinner"
import {FileTree, FilePath} from "tree"
import {ThreadManager} from "threads"
import {History} from "history"
import {VariableManager} from "variables"
import {FileExplorer} from "explorer"
import {Command} from "commands" 
import {DebugManager} from "debug"
import {KeyBinder} from "keys"
import {Alerts} from "alert"

export module Project {
   
   var currentDisplayInfo: any = {};
   var doubleClickTimes: any = {};

   export function createMainLayout(setupFunction, startFunction) {
      let perspective = determineProjectLayout();
      
      if (perspective == "debug") {
         createDebugLayout(function() {   
            console.log("Performing setup for debug layout");
            setupFunction(); // setup stuff
            activateDebugLayout();
            startResizePoller(); // dynamically resize the editor
            attachClickEvents();
            startFunction(); // start everything
         });
      } else if(perspective == "dialog") {
         createDialogLayout(function() {
            console.log("Performing setup for dialog layout");
            setupFunction(); // setup stuff
            activateDialogLayout();
            startResizePoller(); // dynamically resize the editor
            attachClickEvents();
            startFunction(); // start everything
         });
      } else {
         createExploreLayout(function() {
            console.log("Performing setup for explore layout");
            setupFunction(); // setup stuff
            activateExploreLayout();
            startResizePoller(); // dynamically resize the editor
            attachClickEvents();
            startFunction(); // start everything
         });
      }
      $(window).trigger('resize'); // force a redraw after w2ui

   }

   export function openDialogWindow(name, tabs) {
      var address = "/project/" + Common.getProjectName() + ";dialog?visible=" + name;
      var dialog = Command.openChildWindow(address, name);
      var tabList = w2ui[tabs].panels[0].tabs.tabs;
      var title = document.title;

     if(tabList.length <= 2) {
        for(var i = 0; i < tabList.length; i++) {
            var tab = tabList[i];

            if(tab) {
                tab.closable = false;
            }
        }
     }
     for(var i = 0; i < tabList.length; i++) {
        var tab = tabList[i];

        if(!tab.hidden && !Common.stringStartsWith(tab.id, name)) {
          var perspective = determineProjectLayout();

          if (perspective != "dialog") {
              if (perspective == "debug") {
                 w2ui['debugBottomTabLayout_main_tabs'].click(tab.id);
              } else {
                 w2ui['exploreBottomTabLayout_main_tabs'].click(tab.id);
              }
          }
          break;
        }
     }
   }

   export function showProblemsTab() {
      var perspective = determineProjectLayout();
      
      if (perspective == "debug") {
         w2ui['debugBottomTabLayout_main_tabs'].click('problemsTab');
      } else if (perspective == "dialog") {
         w2ui['dialogTabLayout_main_tabs'].click('problemsTab');
      } else {
         w2ui['exploreBottomTabLayout_main_tabs'].click('problemsTab');
      }
   }
   
   function attachClickEvents() {
      $('#toolbarResize').on('click', function(e) {
         toggleFullScreen();
         e.preventDefault();
      });
      $('#toolbarSwitchLayout').on('click', function(e) {
         Command.switchLayout();
         e.preventDefault();
      });
      $('#toolbarSwitchProject').on('click', function(e) {
         Command.switchProject();
         e.preventDefault();
      });
      $('#toolbarNavigateBack').on('click', function(e) {
         History.navigateBackward();
         e.preventDefault();
      });
      $('#toolbarNavigateForward').on('click', function(e) {
         History.navigateForward();
         e.preventDefault();
      });
      $('#editorTheme').on('change', function(e) {
         changeEditorTheme();
         e.preventDefault();
      });
      $('#fontFamily').on('change', function(e) {
         changeProjectFont();
         e.preventDefault();
      });
      $('#fontSize').on('change', function(e) {
         changeProjectFont();
         e.preventDefault();
      });
      $('#newFile').on('click', function(e) {
         Command.newFile(null);
         e.preventDefault();
      });
      $('#saveFile').on('click', function(e) {
         Command.saveFile(null);
         e.preventDefault();
      });
      $('#deleteFile').on('click', function(e) {
         Command.deleteFile(null);
         e.preventDefault();
      });
      $('#searchTypes').on('click', function(e) {
         Command.searchTypes();
         e.preventDefault();
      });
      $('#runScript').on('click', function(e) {
         Command.runScript();
         e.preventDefault();
      });
      $('#debugScript').on('click', function(e) {
         Command.debugScript();
         e.preventDefault();
      });
      $('#stopScript').on('click', function(e) {
         Command.stopScript();
         e.preventDefault();
      });
      $('#resumeScript').on('click', function(e) {
         Command.resumeScript();
         e.preventDefault();
      });
      $('#stepInScript').on('click', function(e) {
         Command.stepInScript();
         e.preventDefault();
      });
      $('#stepOutScript').on('click', function(e) {
         Command.stepOutScript();
         e.preventDefault();
      });
      $('#stepOverScript').on('click', function(e) {
         Command.stepOverScript();
         e.preventDefault();
      });
      $('#evaluateExpression').on('click', function(e) {
         Command.evaluateExpression();
         e.preventDefault();
      });  
      $('#navigateToTreeArrow').on('click', function(e) {
         FileEditor.showEditorFileInTree();
         e.preventDefault();
      });
   }
   
   function determineProjectLayout() {
      var debugToggle = ";debug";
      var dialogToggle = ";dialog";
      var locationPath = window.document.location.pathname;
      var locationHash = window.document.location.hash;
      var isDebug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
      var isDialog = locationPath.indexOf(dialogToggle, locationPath.length - dialogToggle.length) !== -1;

      if(isDebug) {
         return "debug";
      }
      if(isDialog) {
        return "dialog";
      }
      return "explore";
   }
   
   function startResizePoller() { // because w2ui onResize not working
      var editorWidth = 0;
      var editorHeight = 0;
      
      setInterval(function() {
         var editorElement = document.getElementById("editor");
         
         if(editorElement != null) {
            var currentWidth = editorElement.offsetWidth;
            var currentHeight = editorElement.offsetHeight;
            
            if(editorWidth != currentWidth || editorHeight != currentHeight) {
               editorWidth = currentWidth;
               editorHeight = currentHeight;
               FileEditor.resizeEditor();
            }
         }
      }, 100);
   }
   
   export function changeProjectFont(){
      var fontFamily: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontFamily");
      var fontSize: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontSize");
      
      if(fontSize != null && fontFamily != null) {
         var fontSizeOption: HTMLOptionElement = <HTMLOptionElement>fontSize.options[fontSize.selectedIndex];
         var fontFamilyOption: HTMLOptionElement = <HTMLOptionElement>fontFamily.options[fontFamily.selectedIndex];
         var fontSizeValue = fontSizeOption.value;
         var fontFamilyValue = fontFamilyOption.value;
         
         FileEditor.updateEditorFont(fontFamilyValue, fontSizeValue);
         ProcessConsole.updateConsoleFont(fontFamilyValue, fontSizeValue);
         
         var displayInfo = currentProjectDisplay();
         Command.updateDisplay(displayInfo);
      }
   }
   
   export function changeEditorTheme(){
      var editorTheme: HTMLSelectElement = <HTMLSelectElement>document.getElementById("editorTheme");
      
      if(editorTheme != null) {
         var themeOption: HTMLOptionElement = <HTMLOptionElement>editorTheme.options[editorTheme.selectedIndex];
         var themeName = themeOption.value.toLowerCase();
         FileEditor.setEditorTheme("ace/theme/" + themeName);
         
         var displayInfo = currentProjectDisplay();
         Command.updateDisplay(displayInfo);
         
         if(isProjectThemeChange(displayInfo.themeName)) { // do we need to refresh
            Command.refreshScreen(); // refresh the whole screen
         }
      }
   }
    
   export function toggleFullScreen() {
      var perspective = determineProjectLayout();
   
      if (perspective == "debug") {
         var topPanel = w2ui['debugEditorLayout'].get("top");
         var bottomPanel = w2ui['debugEditorLayout'].get("bottom");
         
         if(topPanel.hidden && bottomPanel.hidden) {
            w2ui['debugEditorLayout'].show("top", true);
            w2ui['debugEditorLayout'].show("bottom", true); 
         } else if(topPanel.hidden && !bottomPanel.hidden) {
            w2ui['debugEditorLayout'].hide("top", true);
            w2ui['debugEditorLayout'].hide("bottom", true); 
         } else {
            w2ui['debugEditorLayout'].hide("top", true);
            w2ui['debugEditorLayout'].show("bottom", true);
         }
      } else {
         var leftPanel = w2ui['exploreMainLayout'].get("left");
         var bottomPanel = w2ui['exploreEditorLayout'].get("bottom");
         
         if(leftPanel.hidden && bottomPanel.hidden) {
            w2ui['exploreMainLayout'].show("left", true);
            w2ui['exploreEditorLayout'].show("bottom", true);
         } else if(leftPanel.hidden && !bottomPanel.hidden) {
            w2ui['exploreMainLayout'].hide("left", true);
            w2ui['exploreEditorLayout'].hide("bottom", true);
         } else {
            w2ui['exploreMainLayout'].hide("left", true);
            w2ui['exploreEditorLayout'].show("bottom", true);
         }
      }
   }
   
   function isProjectThemeChange(name) {
      if(currentDisplayInfo) {
         return currentDisplayInfo.themeName != name.toLowerCase(); // if they are not the same
      }
      return false;
   }
   
   function currentProjectDisplay(){
      var fontFamily: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontFamily");
      var fontSize: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontSize");
      var editorTheme: HTMLSelectElement = <HTMLSelectElement>document.getElementById("editorTheme");
      var availableFonts = {};

      for(var i = 0; i < fontFamily.options.length; i++) {
         var value = Common.clearHtml(fontFamily.options[i].text).trim(); // clear up values
         var key = fontFamily.options[i].value;

         availableFonts[key] = value;
      }
      return {
         consoleCapacity: 50000,
         themeName: editorTheme.value.toLowerCase().trim(),
         fontSize: fontSize.value.toLowerCase().replace("px", "").trim(), // get font size
         fontName: fontFamily.value,
         availableFonts: availableFonts
      };
   }
   
   function applyProjectTheme() {
      $.get("/display/" + Common.getProjectName(), function(displayInfo) {
         currentDisplayInfo = displayInfo; // save display info
         
         if(displayInfo.fontName != null && displayInfo.fontSize != null) {
            var fontFamily: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontFamily");
            var fontSize: HTMLSelectElement = <HTMLSelectElement>document.getElementById("fontSize");
            var editorTheme: HTMLSelectElement = <HTMLSelectElement>document.getElementById("editorTheme");
            
            if(fontSize != null) {
               fontSize.value = displayInfo.fontSize + "px";
            }
            if(fontFamily != null) {
               var orderedKeys = Object.keys(displayInfo.availableFonts).sort();

               for (var i = 0; i < orderedKeys.length; i++) {
                  var name = orderedKeys[i];
                  var text = " " + displayInfo.availableFonts[name];

                  fontFamily.options[i] = new Option(text, name);
               }
               fontFamily.value = displayInfo.fontName;
            }   
            if(editorTheme != null && displayInfo.themeName != null) {
               editorTheme.value = displayInfo.themeName;
            }
            if(displayInfo.consoleCapacity != null) {
               ProcessConsole.updateConsoleCapacity(Math.max(displayInfo.consoleCapacity, 5000)); // don't allow stupidly small size
            }
            if(displayInfo.logoImage != null) {
               var toolbarRow: HTMLTableRowElement = <HTMLTableRowElement>document.getElementById("toolbarRow"); // this is pretty rubbish, but it works!
               
               toolbarRow.insertCell(0).innerHTML = "<div class='toolbarSeparator'></div>";
               toolbarRow.insertCell(0).innerHTML = "&nbsp;";
               toolbarRow.insertCell(0).innerHTML = "&nbsp;";
               toolbarRow.insertCell(0).innerHTML = "<div><img style='height: 25px; margin-top: -1px;' src='" + displayInfo.logoImage + "'></div>"; // /img/logo_grey_shade.png
            }
            ProcessConsole.updateConsoleFont(displayInfo.fontName, displayInfo.fontSize + "px");
         }
         changeProjectFont();// update the fonts
         changeEditorTheme(); // change editor theme
      });
   }
   
   function showBrowseTreeContent(containsBrowse) { // hack to render tree
      if(containsBrowse) {
         // move the explorer
         var newParent = document.getElementById('browseParent');
         var oldParent = document.getElementById('browseParentHidden');
      
         if(oldParent != null && newParent != null){
            while (oldParent.childNodes.length > 0) {
                newParent.appendChild(oldParent.childNodes[0]);
            }
         }
      }
   }
   
   function hideBrowseTreeContent(containsBrowse) { // hack to render tree
      if(containsBrowse) {
         // move the explorer
         var newParent = document.getElementById('browseParentHidden');
         var oldParent = document.getElementById('browseParent');
      
         if(oldParent != null && newParent != null){
            while (oldParent.childNodes.length > 0) {
                newParent.appendChild(oldParent.childNodes[0]);
            }
         }
      }
   }
   
   function showEditorContent(containsEditor) { // hack to render editor
      if(containsEditor) {
         var location = window.location.hash;
         var hashIndex = location.indexOf('#');
         
         if(hashIndex == -1) { // no path specified
            showEditorHelpContent(containsEditor);
         } else {
            showEditorFileContent(containsEditor);
         }
      
      }
   }
   
   function showEditorFileContent(containsEditor) {
      var newParent = document.getElementById('editParent');
      var oldParent = document.getElementById('editParentHidden');
   
      if(oldParent != null && newParent != null){
         $("#help").remove();
         
         while (oldParent.childNodes.length > 0) {
             newParent.appendChild(oldParent.childNodes[0]);
         }
      }
      updateEditorTabName();
   }
   
   function showEditorHelpContent(containsEditor) { // hack to render editor
      var newParent = document.getElementById('editParent');
      var editorFileName = document.getElementById("editFileName");
      
      
      if(newParent != null) {
         var keyBindings = KeyBinder.getKeyBindings();
         var content = "";
         
         content += "<div id='help'>"
         content += "<div id='keyBindings'>";
         content += "<table border='0'>";
         
         for(var keyBinding in keyBindings) {
            if(keyBindings.hasOwnProperty(keyBinding)) {
               var description = keyBindings[keyBinding];
               
               content += "<tr>";
               content += "<td><div class='helpBullet'></div></td>";
               content += "<td align='left'>&nbsp;&nbsp;" + keyBinding + "</td>";
               content += "<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;" + description + "</td>";
               content += "</td>";
            }
         }
         content += "</table>";
         content += "</div>";
         content += "</div>";
         
         $("#editParent").html(content);
      }
      updateEditorTabName();
   }

   export function clickOnTab(name, doubleClickFunction) {
      var currentTime = new Date().getTime();
      var previousTime = doubleClickTimes[name];

      if(previousTime) {
         if((currentTime - previousTime) < 200) {
            doubleClickFunction();
         }
      }
      doubleClickTimes[name] = currentTime;
   }
   
   function updateEditorTabName() {
      var editorFileName = document.getElementById("editFileName");
      
      if(editorFileName != null){
         var editorState: FileEditorState = FileEditor.currentEditorState();
         
         if(editorState != null && editorState.getResource() != null) {
            editorFileName.innerHTML = "<span title='" + editorState.getResource().getResourcePath() +"'>&nbsp;" + editorState.getResource().getFileName() + "&nbsp;</span>";
         }
      }
   }
   
   function findActiveEditorLayout() {
      var tabs = w2ui['exploreEditorTabLayout'];
   
      if(tabs == null) {
         return w2ui['debugEditorTabLayout'];
      }
      return tabs;
   }
   
   function findActiveEditorTabLayout() {
      var tabs = findActiveEditorLayout();
      
      if(tabs != null) {
         return tabs.panels[0].tabs;
      }
      return null;
   }
   
   export function closeEditorTab() {
      var data: FileEditorState = FileEditor.currentEditorState();
      
      if(data.getResource()) {      
         var tabs = findActiveEditorTabLayout();
         
         if(tabs.tabs.length > 1) {
            closeEditorTabForPath(data.getResource().getResourcePath());
         }
      }
   }
   
   export function deleteEditorTab(resource) {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();
      
      if(tabs != null && resource != null) {
         var removeTab = tabs.get(resource);
         
         if(removeTab && removeTab.closable) {
            tabs.remove(resource); // remove the tab
            
            if(removeTab.active && tabs.tabs.length > 0) {
               activateAnyEditorTab(); // if it was active then activate another 
            }  
         }
      }
      if(tabs.tabs.length == 0) {
        createWelcomeTab();
      } 
   }
   
   export function renameEditorTab(fromPath: FilePath, toPath: FilePath) {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();
      
      if(tabs != null && fromPath != null && toPath != null) {
         var originalId = fromPath.getResourcePath(); 
         var tabList = tabs.tabs;
         var count = 0;
         
         for(var i = 0; i < tabList.length; i++) {
            var nextTab = tabList[i];
            
            if(nextTab != null && nextTab.id == originalId) {
               var newTab = JSON.parse(JSON.stringify(nextTab)); // clone the tab
   
               tabs.remove(nextTab.id); // remove the tab
   
               if(nextTab.active) {
                  FileExplorer.openTreeFile(toPath.getResourcePath(), function(){}); // browse style makes no difference here
               } else {
                  var fileNameReplace = new RegExp(fromPath.getFileName(), "g");
                  var filePathReplace = new RegExp(fromPath.getResourcePath(), "g");
                  
                  newTab.caption = newTab.caption.replace(fileNameReplace, toPath.getFileName()).replace(filePathReplace, toPath.getResourcePath()); // rename the tab
                  newTab.text = newTab.text.replace(fileNameReplace, toPath.getFileName()).replace(filePathReplace, toPath.getResourcePath()); // rename the tab
                  newTab.id = toPath.getResourcePath();
                  tabs.add(newTab);
               }
               break;
            }
         }
      }
   }
   
   export function markEditorTab(name, isModified) {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();
      
      if(tabs != null && name != null) {
         var tabList = tabs.tabs;
         var count = 0;
         
         for(var i = 0; i < tabList.length; i++) {
            var nextTab = tabList[i];
            
            if(nextTab != null && nextTab.id == name) {
               var tabPath: FilePath = FileTree.createResourcePath(name);
               var tabFromName = (isModified ? "" : "*") + tabPath.getFileName(); 
               var tabToName =  (isModified ? "*" : "") + tabPath.getFileName(); 
               var isAlreadyModified = Common.stringContains(nextTab.text, "*" + tabPath.getFileName());
               
               if(isModified != isAlreadyModified) {
                  nextTab.caption = Common.stringReplaceText(nextTab.caption, tabFromName, tabToName);
                  nextTab.text = Common.stringReplaceText(nextTab.text, tabFromName, tabToName);
                  tabs.refresh();
               }
               break;
            }
         }
      }
   }

   function createWelcomeTab() {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();

      if(tabs != null) {
        var tabList = tabs.tabs;
      
        if(tabList.length === 0){
            tabs.add({ 
                id : 'editTab',
                caption : "<div class='helpTab' id='editFileName'><span title='Welcome'>&nbsp;Welcome&nbsp;</span></div>",
                content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                closable: false,
                active: true
             });
             tabs.select('editTab');
             window.location.hash = '';
             activateTab('editTab', layout.name, false, true, ""); // browse style makes no difference here
             addTabClickHandler('editTab');
        }
     }
   }
   
   export function createEditorTab() {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();
      var editorState: FileEditorState = FileEditor.currentEditorState();
      
      if(tabs != null && editorState != null && editorState.getResource() != null) {
         var tabList = tabs.tabs;
         var tabResources = {};
         
         for(var i = 0; i < tabList.length; i++) {
            var nextTab = tabList[i];
            
            if(nextTab != null && nextTab.id != 'editTab') {
               tabResources[nextTab.id] = {
                  id : nextTab.id,
                  caption : nextTab.caption.replace('id="editFileName"', "").replace("id='editFileName'", ""),
                  content : "",
                  closable: true,
                  active: false
               }
            }
         }
         tabResources[editorState.getResource().getResourcePath()] = { 
            id : editorState.getResource().getResourcePath(),
            caption : "<div class='editTab' id='editFileName'><span title='" + editorState.getResource().getResourcePath() +"'>&nbsp;" + editorState.getResource().getFileName() + "&nbsp;</span></div>",
            content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
            closable: true,
            active: true
         };
         var sortedNames = [];
         var sortedTabs = [];
         
         for (var tabResource in tabResources) {
            if (tabResources.hasOwnProperty(tabResource)) {
               sortedNames.push(tabResource); // add a '.' to ensure dot notation sorts e.g x.y.z
            }
         }
         sortedNames.sort();
         
         for(var i = 0; i < sortedNames.length; i++) {
            var tabResource: string = sortedNames[i];
            var nextTab = tabResources[tabResource];
            
            //nextTab.closable = sortedNames.length > 1; // if only one tab make sure it cannot be closed
            sortedTabs[i] = nextTab;
         }
         tabs.tabs = sortedTabs;
         tabs.active = editorState.getResource().getResourcePath();
         activateTab(editorState.getResource().getResourcePath(), layout.name, false, true, ""); // browse style makes no difference here
         addTabClickHandler(editorState.getResource().getResourcePath());

      }
   }

   function addTabClickHandler(id) {
         // this is pretty rubbish, it would be good if there was a promise after redraw/repaint
         setTimeout(function() { // wait for the paint to finish
            $('#editFileName').on('click', function(e) {
               clickOnTab(id, toggleFullScreen);
               e.preventDefault();
            });  
         }, 100);
   }
   
   function closeEditorTabForPath(resourcePathToClose) {
      deleteEditorTab(resourcePathToClose); // activate some other tab
      
       if(FileEditor.isEditorChangedForPath(resourcePathToClose)) {
          var currentBuffer: FileEditorBuffer = FileEditor.getEditorBufferForResource(resourcePathToClose);
          var editorResource: FilePath = FileTree.createResourcePath(resourcePathToClose);

          Command.saveEditorOnClose(currentBuffer.getSource(), editorResource);
          console.log("CLOSE: " + resourcePathToClose);
       } else {
          FileEditor.clearSavedEditorBuffer(resourcePathToClose); // remove history anyway as its been closed 
          console.log("CLOSE: " + resourcePathToClose);
       }
   }
   
   
   function activateAnyEditorTab() {
      var layout = findActiveEditorLayout();
      var tabs = findActiveEditorTabLayout();
      
      if(tabs != null) {
         var tabList = tabs.tabs;

         for(var i = 0; i < tabList.length; i++) {
            var nextTab = tabList[i];
            
            if(nextTab != null && nextTab.id != 'editTab') {
               tabs.active = nextTab.id;
               //tabs.closable = false;
               FileExplorer.openTreeFile(nextTab.id, function(){}); // browse style makes no difference here
               break;
            }
         }
      }
   }
   
   function hideEditorContent(containsEditor) { // hack to render editor
      if(containsEditor) {
         // move the editor
         var newParent = document.getElementById('editParentHidden');
         var oldParent = document.getElementById('editParent');         
      
         if(oldParent != null && newParent != null){
            while (oldParent.childNodes.length > 0) {
                newParent.appendChild(oldParent.childNodes[0]);
            }
         }
      }
   }

  function createDialogLayout(startFunction) {
        var layoutEvents = ["createDialogLayout"];
        var layoutEventListener = Common.createSimpleStateMachineFunction("createDialogLayout", function() {
           console.log("Dialog layout fully rendered");
           startFunction();
        }, layoutEvents, 200);

        var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
        var leftStyle = pstyle + " margin-top: 32px; border-top: 1px solid ${PROJECT_BORDER_COLOR};";

        createDialogMainLayout(pstyle, layoutEventListener, layoutEvents);
        createDialogTabLayout(pstyle, layoutEventListener, layoutEvents);

        validateLayout(layoutEvents, ["createDialogLayout"]);

        createProblemsTab();
        createVariablesTab();
        createProfilerTab();
        createBreakpointsTab();
        createDebugTab();
        createThreadsTab();
        createHistoryTab();

        w2ui['dialogMainLayout'].content('main', w2ui['dialogTabLayout']);
        w2ui['dialogMainLayout'].refresh();
        w2ui['dialogTabLayout'].refresh();

        layoutEventListener("createDialogLayout"); // this allows the whole thing to initiate
     }

   function createDialogMainLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("dialogMainLayout");

      $('#mainLayout').w2layout({
         name : 'dialogMainLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '75%',
            resizable : true,
            style : layoutStyle
         }],
         onRender: function(event) {
            layoutEventListener("dialogMainLayout");
         }
      });
   }

    function createDialogTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
       var selectedTab = Common.extractParameter('visible');
       var tabTable = {
            console: {
                id: 'consoleTab',
                caption: '<div class="consoleTab">Console</div>',
                closable: false,
                hide: true
            },
            problems: {
                id: 'problemsTab',
                caption: '<div class="problemsTab">Problems</div>',
                closable: false
            },
            breakpoints: {
                id: 'breakpointsTab',
                caption: '<div class="breakpointsTab">Breakpoints</div>',
                closable: false
            },
            threads: {
                id: 'threadsTab',
                caption: '<div class="threadTab">Threads</div>',
                closable: false
            },
            variables: {
                id: 'variablesTab',
                caption: '<div class="variableTab">Variables</div>',
                closable: false
            },
            profiler: {
                id: 'profilerTab',
                caption: '<div class="profilerTab">Profiler</div>',
                closable: false
            },
            debug: {
                id: 'debugTab',
                caption: '<div class="debugTab">Debug  </div>',
                closable: false
            },
            history: {
                id: 'historyTab',
                caption: '<div class="historyTab">History  </div>',
                closable: false
            },
            browse: {
                id : 'browseTab',
                caption : '<div class="browseTab">Project</div>',
                content : "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'></div></div>",
                closable: false
            }
          };


       layoutEvents.push("dialogTabLayout");
       layoutEvents.push("dialogTabLayout#tabs");

       $('').w2layout({
          name : 'dialogTabLayout',
          padding : 0,
          panels : [ {
             type : 'main',
             size : '100%',
             style : layoutStyle + 'border-top: 0px;',
             resizable : false,
             name : 'tabs',
             tabs : {
                active : 'consoleTab',
                tabs : [
                    tabTable[selectedTab]
                ],
                onClose: function(event) {
                   console.log(event);
                },
                onClick : function(event) {
                   activateTab(event.target, "dialogTabLayout", true, false, "style='right: 0px;'");
                },
                onRender: function(event) {
                   layoutEventListener("dialogTabLayout#tabs");
                }
             }
          } ],
          onRender: function(event) {
             layoutEventListener("dialogTabLayout");
          }
       });
    }

   function activateDialogLayout() {
      var selectedTab = Common.extractParameter('visible');
      var statusFocus = null;

      applyProjectTheme();
      watchParentStatusFocus();
      activateTab(selectedTab + "Tab", "dialogTabLayout", false, false, "style='right: 0px;'");
      w2ui['dialogTabLayout_main_tabs'].click(selectedTab + "Tab");
   }

   function watchParentStatusFocus() {
       var statusFocus = null;

       setInterval(function () {
           if (window.opener) {
               if(statusFocus != window.opener.statusFocus) {
                  console.log("Attaching to " + statusFocus);
                  statusFocus = window.opener.statusFocus;
                  Command.attachProcess(statusFocus);
               }
           }
       }, 500);
   }

   function createExploreLayout(startFunction) {      
      var layoutEvents = ["createExploreLayout"];       
      var layoutEventListener = Common.createSimpleStateMachineFunction("createExploreLayout", function() {
         console.log("Explore layout fully rendered");
         startFunction();
      }, layoutEvents, 200);
   
      var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
      var leftStyle = pstyle + " margin-top: 32px; border-top: 1px solid ${PROJECT_BORDER_COLOR};";
      
      createExploreMainLayout(pstyle, layoutEventListener, layoutEvents);
      createExploreEditorLayout(pstyle, layoutEventListener, layoutEvents);
      createExploreEditorTabLayout(pstyle, layoutEventListener, layoutEvents);
      createExploreLeftTabLayout(pstyle, layoutEventListener, layoutEvents);
      createExploreBottomTabLayout(pstyle, layoutEventListener, layoutEvents);
      
      validateLayout(layoutEvents, ["createExploreLayout"]);
      
      createTopMenuBar(); // menu bar at top
      createProblemsTab();
      createVariablesTab();
      createProfilerTab();
      createBreakpointsTab();
      createDebugTab();
      createThreadsTab();
      createHistoryTab();
      
      w2ui['exploreMainLayout'].content('top', w2ui['topLayout']);
      //w2ui['exploreMainLayout'].content('left', '<table cellpadding="2"><tr><td><span id="leftProjectRoot"></span></td><tr><tr><td><span id="leftDirectory"></span></td><tr><tr><td></td><tr></table>');
      //w2ui['exploreMainLayout'].content('left', '<div style="border: dotted 1px ${PROJECT_BORDER_COLOR}; padding: 1px; margin-top: 10px; margin-left: 5px;"><table cellpadding="2"><tr><td>&nbsp;</td><tr><tr><td><!--span id="leftProjectRoot"></span--></td></tr></table></div>');      
      w2ui['exploreMainLayout'].content('left', w2ui['exploreLeftTabLayout']);
      w2ui['exploreMainLayout'].content('main', w2ui['exploreEditorLayout']);
      w2ui['exploreEditorLayout'].content('main', w2ui['exploreEditorTabLayout']);
      w2ui['exploreEditorLayout'].content('bottom', w2ui['exploreBottomTabLayout']);
      w2ui['exploreEditorTabLayout'].refresh();
      w2ui['exploreBottomTabLayout'].refresh();
      w2ui['exploreLeftTabLayout'].refresh();
      
      layoutEventListener("createExploreLayout"); // this allows the whole thing to initiate
   }
   
   function createExploreMainLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("exploreMainLayout");
      
      $('#mainLayout').w2layout({
         name : 'exploreMainLayout',
         padding : 0,
         panels : [ {
            type : 'top',
            size : '40px',
            resizable : false,
            style : layoutStyle
         }, {
            type : 'left',
            size : '25%',
            resizable : true,
            style : layoutStyle      
         },{
            type : 'right',
            size : '0%',
            resizable : true,
            hidden: true,
            style : layoutStyle
         },{
            type : 'main',
            size : '75%',
            resizable : true,
            style : layoutStyle
         } , {
            type : 'bottom',
            size : '25px',
            resizable : false,
            style : layoutStyle,
            content : createBottomStatusContent()
         } ],
         onRender: function(event) {
            layoutEventListener("exploreMainLayout");
         }         
      });
   }
   
   function createExploreEditorLayout(layoutStyle, layoutEventListener, layoutEvents){ 
      layoutEvents.push("exploreEditorLayout");
      
      $('').w2layout({
         name : 'exploreEditorLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '60%',
            resizable : true,
            overflow: 'auto',
            style : layoutStyle + 'border-bottom: 0px;'
         }, {
            type : 'bottom',
            size : '40%',
            overflow: 'auto',         
            resizable : true,
            style : layoutStyle + 'border-top: 0px;'
         } ],
         onRender: function(event) {
            layoutEventListener("exploreEditorLayout");
         }
      });
   }
   
   function createExploreEditorTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("exploreEditorTabLayout");
      layoutEvents.push("exploreEditorTabLayout#tabs");
      
      $('').w2layout({
         name : 'exploreEditorTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'editTabs',
            tabs : {
               active : 'editTab',
               tabs : [ {
                  id : 'editTab',
                  caption : '<div class="helpTab" id="editFileName">Welcome</div>',
                  content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                  closable: false 
               } ],
               onClick : function(event) {
                  if(event.target != 'editTab') {
                     FileExplorer.openTreeFile(event.target, function(){
                        FileEditor.showEditorFileInTree();
                     });
                  }
               },
               onClose : function(event) {
                  closeEditorTabForPath(event.target);
               },
               onRender: function(event) {
                  layoutEventListener("exploreEditorTabLayout#tabs");              
               }
            }
         } ],
         onRender: function(event) {
            layoutEventListener("exploreEditorTabLayout");
         }
      });
   }
   
   function createExploreLeftTabLayout(layoutStyle, layoutEventListener, layoutEvents) {     
      layoutEvents.push("exploreLeftTabLayout");
      layoutEvents.push("exploreLeftTabLayout#tabs");
      
      $('').w2layout({
         name : 'exploreLeftTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'tabs',
            tabs : {
               active : 'browseTab',
               right: '<div id="navigateToTreeArrow"></div>',
               tabs : [ {
                  id : 'browseTab',
                  caption : '<div class="browseTab">Project&nbsp;</div>',
                  content : "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'><div id='explorer'></div></div></div>",
                  closable: false 
               } ],
               onClick : function(event) {
                  activateTab(event.target, "exploreLeftTabLayout", true, false, "style='right: 0px;'");
               },
               onRender: function(event) {
                  layoutEventListener("exploreLeftTabLayout#tabs");
               }                
            }
         } ],
         onRender: function(event) {
            layoutEventListener("exploreLeftTabLayout");
         }         
      });
   }
   
   function createExploreBottomTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("exploreBottomTabLayout");
      layoutEvents.push("exploreBottomTabLayout#tabs");
      
      $('').w2layout({
         name : 'exploreBottomTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'tabs',
            tabs : {
               active : 'consoleTab',
               tabs : [ {
                  id : 'consoleTab',
                  caption : '<div class="consoleTab" id="consoleTabTitle">Console</div>',
                  closable: false
               }, {
                  id : 'problemsTab',
                  caption : '<div class="problemsTab" id="problemsTabTitle">Problems</div>',
                  closable: false
               }, {
                  id : 'breakpointsTab',
                  caption : '<div class="breakpointsTab" id="breakpointsTabTitle">Breakpoints</div>',
                  closable: true
               }, {
                  id : 'threadsTab',
                  caption : '<div class="threadTab" id="threadTabTitle">Threads</div>',
                  closable: false
               }, {
                  id : 'variablesTab',
                  caption : '<div class="variableTab" id="variableTabTitle">Variables</div>',
                  closable: false
               }, {
                  id : 'profilerTab',
                  caption : '<div class="profilerTab" id="profilerTabTitle">Profiler</div>',
                  closable: false
               }, {
                  id : 'debugTab',
                  caption : '<div class="debugTab" id="debugTabTitle">Debug&nbsp;&nbsp;</div>',
                  closable: false
               }, {
                  id : 'historyTab',
                  caption : '<div class="historyTab" id="historyTabTitle">History&nbsp;&nbsp;</div>',
                  closable: false
               } ],
               onClick : function(event) {
                  var tabName = event.target.replace("Tab", "");

                  clickOnTab(tabName, function() {
                     openDialogWindow(tabName, "exploreBottomTabLayout");
                  });
                  activateTab(event.target, "exploreBottomTabLayout", false, false, "style='right: 0px;'");
               },
               onRender: function(event) {
                  layoutEventListener("exploreBottomTabLayout#tabs");
               }
            }
         } ],
         onRender: function(event) {
            layoutEventListener("exploreBottomTabLayout");
         }
      });
   }
   
   function activateExploreLayout() {
      applyProjectTheme();
      activateTab("consoleTab", "exploreBottomTabLayout", false, false, "style='right: 0px;'"); 
      activateTab("browseTab", "exploreLeftTabLayout", true, false, "style='right: 0px;'"); 
      activateTab("editTab", "exploreEditorTabLayout", false, true, "style='right: 0px;'"); 
   }

   function createDebugLayout(startFunction) {
      var layoutEvents = ["createDebugLayout"];      
      var layoutEventListener = Common.createSimpleStateMachineFunction("createDebugLayout", function() {
         console.log("Debug layout fully rendered");
         startFunction();
      }, layoutEvents, 200);
      
      var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';      
      
      createDebugMainLayout(pstyle, layoutEventListener, layoutEvents);
      createEebugEditorLayout(pstyle, layoutEventListener, layoutEvents);
      createDebugEditorTabLayout(pstyle, layoutEventListener, layoutEvents);
      createDebugTopTabSplit(pstyle, layoutEventListener, layoutEvents);
      createDebugLeftTabLayout(pstyle, layoutEventListener, layoutEvents);
      createDebugRightTabLayout(pstyle, layoutEventListener, layoutEvents);
      createDebugBottomTabLayout(pstyle, layoutEventListener, layoutEvents);
      
      validateLayout(layoutEvents, ["createDebugLayout"]);
            
      createTopMenuBar(); // menu bar at top
      createProblemsTab();
      createVariablesTab();
      createProfilerTab();
      createBreakpointsTab();
      createDebugTab();
      createThreadsTab();
      createHistoryTab();
      
      w2ui['debugMainLayout'].content('top', w2ui['topLayout']);
      w2ui['debugMainLayout'].content('main', w2ui['debugEditorLayout']);
      w2ui['debugEditorLayout'].content('top', w2ui['debugTopTabSplit']);
      w2ui['debugTopTabSplit'].content('left', w2ui['debugLeftTabLayout']);
      w2ui['debugTopTabSplit'].content('main', w2ui['debugRightTabLayout']);
      w2ui['debugEditorLayout'].content('bottom', w2ui['debugBottomTabLayout']);  
      w2ui['debugEditorLayout'].content('main', w2ui['debugEditorTabLayout']);
      w2ui['debugEditorTabLayout'].refresh();
      w2ui['debugTopTabSplit'].refresh();
      w2ui['debugLeftTabLayout'].refresh();
      w2ui['debugRightTabLayout'].refresh();   
      w2ui['debugBottomTabLayout'].refresh();
      
      layoutEventListener("createDebugLayout"); // this allows the whole thing to initiate
   }
   
   function createDebugMainLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugMainLayout");
      
      $('#mainLayout').w2layout({
         name : 'debugMainLayout',
         padding : 0,
         panels : [ {
            type : 'top',
            size : '40px',
            resizable : false,
            style : layoutStyle
         }, {
            type : 'main',
            size : '80%',
            resizable : true,
            style : layoutStyle
         } , {
            type : 'bottom',
            size : '25px',
            resizable : false,
            style : layoutStyle,
            content : createBottomStatusContent()
         } ],
         onRender: function(event) {
            layoutEventListener("debugMainLayout");
         } 
      });
   }
   
   function createEebugEditorLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugEditorLayout");
      
      $('').w2layout({
         name : 'debugEditorLayout',
         padding : 0,
         panels : [ {
            type : 'top',  
            size : '25%',
            overflow: 'auto',         
            resizable : true,
            style : layoutStyle + 'border-top: 0px;'
         }, {
            type : 'main',
            size : '50%',
            resizable : true,
            overflow: 'auto',
            style : layoutStyle + 'border-bottom: 0px;'      
         }, {
            type : 'bottom',
            size : '25%',
            overflow: 'auto',         
            resizable : true,
            style : layoutStyle + 'border-top: 0px;'
         } ],
         onRender: function(event) {
            layoutEventListener("debugEditorLayout");
         } 
      });
   }
   
   function createDebugEditorTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugEditorTabLayout");
      layoutEvents.push("debugEditorTabLayout#tabs");
      
      $('').w2layout({
         name : 'debugEditorTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'editTabs',
            tabs : {
               active : 'editTab',
               tabs : [ {
                  id : 'editTab',
                  caption : '<div class="helpTab" id="editFileName">Welcome</div>',
                  content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                  closable: false 
               } ],
               onClick : function(event) {
                  FileExplorer.openTreeFile(event.target, function(){
                     FileEditor.showEditorFileInTree();
                  });
               },
               onClose : function(event) {
                  closeEditorTabForPath(event.target);
               },
               onRender: function(event) {
                  layoutEventListener("debugEditorTabLayout#tabs");                 
               }                             
            }
         } ],
         onRender: function(event) {
            layoutEventListener("debugEditorTabLayout"); 
         } 
      });
   }
   
   function createDebugTopTabSplit(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugTopTabSplit");
      
      $('').w2layout({
         name : 'debugTopTabSplit',
         padding : 0,
         panels : [ {
            type : 'left',  
            size : '50%',
            overflow: 'auto',         
            resizable : true,
            style : layoutStyle + 'border-top: 0px;'
         }, {
            type : 'main',
            size : '50%',
            resizable : true,
            overflow: 'auto',
            style : layoutStyle + 'border-bottom: 0px;'
         } ],
         onRender: function(event) {
            layoutEventListener("debugTopTabSplit"); 
         } 
      });
   }
   
   function createDebugLeftTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugLeftTabLayout");
      layoutEvents.push("debugLeftTabLayout#tabs");
      
      $('').w2layout({
         name : 'debugLeftTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'tabs',
            tabs : {
               active : 'debugTab',
               tabs : [ {
                  id : 'debugTab',
                  caption : '<div class="debugTab">Debug&nbsp;&nbsp;</div>',
                  closable: false
               }, {
                  id : 'threadsTab',
                  caption : '<div class="threadTab">Threads</div>',
                  closable: false
               },  {
                  id : 'browseTab',
                  caption : '<div class="browseTab">Project</div>',
                  content : "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'></div></div>",
                  closable: false 
               } ],
               onClick : function(event) {
                  var tabName = event.target.replace("Tab", "");

                  clickOnTab(tabName, function() {
                     openDialogWindow(tabName, "debugLeftTabLayout");
                  });
                  activateTab(event.target, "debugLeftTabLayout", true, false, "");
               },
               onRender: function(event) {
                  layoutEventListener("debugLeftTabLayout#tabs"); 
               } 
            }
         } ],
         onRender: function(event) {
            layoutEventListener("debugLeftTabLayout"); 
         } 
      });
   }
   
   function createDebugRightTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugRightTabLayout");
      layoutEvents.push("debugRightTabLayout#tabs");
      
      $('').w2layout({
         name : 'debugRightTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'tabs',
            tabs : {
               active : 'variablesTab',
               tabs : [ {
                  id : 'variablesTab',
                  caption : '<div class="variableTab">Variables</div>',
                  closable: false
               }, {
                  id : 'breakpointsTab',
                  caption : '<div class="breakpointsTab">Breakpoints</div>',
                  closable: false
               } ],
               onClick : function(event) {
                  var tabName = event.target.replace("Tab", "");

                  clickOnTab(tabName, function() {
                     openDialogWindow(tabName, "debugRightTabLayout");
                  });
                  activateTab(event.target, "debugRightTabLayout", false, false, "");
               },
               onRender: function(event) {
                  layoutEventListener("debugRightTabLayout#tabs"); 
               }
            }
         } ],
         onRender: function(event) {
            layoutEventListener("debugRightTabLayout"); 
         }
      });
   }
   
   function createDebugBottomTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
      layoutEvents.push("debugBottomTabLayout");
      layoutEvents.push("debugBottomTabLayout#tabs");
      
      $('').w2layout({
         name : 'debugBottomTabLayout',
         padding : 0,
         panels : [ {
            type : 'main',
            size : '100%',
            style : layoutStyle + 'border-top: 0px;',
            resizable : false,
            name : 'tabs',
            tabs : {
               active : 'consoleTab',
               tabs : [ {
                  id : 'consoleTab',
                  caption : '<div class="consoleTab">Console</div>'
               }, {
                  id : 'problemsTab',
                  caption : '<div class="problemsTab">Problems</div>'
               }, {
                  id : 'profilerTab',
                  caption : '<div class="profilerTab">Profiler</div>'
               }, {
                  id : 'historyTab',
                  caption : '<div class="historyTab">History&nbsp;&nbsp;</div>',
                  closable: false
               } ],
               onClick : function(event) {
                  var tabName = event.target.replace("Tab", "");

                  clickOnTab(tabName, function() {
                     openDialogWindow(tabName, "debugBottomTabLayout");
                  });
                  activateTab(event.target, "debugBottomTabLayout", false, false, "");
               },
               onRender: function(event) {
                  layoutEventListener("debugBottomTabLayout#tabs"); 
               }
            }
         } ],
         onRender: function(event) {
            layoutEventListener("debugBottomTabLayout"); 
         }
      });
   }    
   
   function activateDebugLayout() {
      applyProjectTheme();
      activateTab("debugTab", "debugLeftTabLayout", true, false, "");
      activateTab("variablesTab", "debugRightTabLayout", false, false, "");   
      activateTab("consoleTab", "debugBottomTabLayout", false, false, "");  
      activateTab("editTab", "debugEditorTabLayout", false, true, "");  
   }
   
   function createBottomStatusContent() {
      return "<div id='status'>"+
         "  <table width='100%' height='100%'>"+
         "  <tr>"+
         "    <td width='50%' align='left'><div id='process'></div></td>"+
         "    <td width='50%' align='right'><div id='currentFile'></div></td>"+
         "  </tr>"+
         "  </table>"+
         "</div>"
   }   
   
   function validateLayout(layoutEvents, ignoreEvents) {
      for(var i = 0; i < layoutEvents.length; i++) {
         var layoutEvent = layoutEvents[i];
         var ignoreIndex = ignoreEvents.indexOf(layoutEvent);
         
         if(ignoreIndex == -1) {         
            var tabIndex = layoutEvent.indexOf("#tab");
            
            if(tabIndex == -1) {
               var layout = w2ui[layoutEvent];
               
               if(!layout || !layout.panels) {
                  console.warn("Layout '" + layoutEvent + "' was not registered");
               }
            } else {
               var parentLayoutEvent = layoutEvent.substring(0, tabIndex);
               var layout = w2ui[parentLayoutEvent];
               
               if(!layout || !layout.panels) {
                  console.warn("Layout '" + parentLayoutEvent + "' was not registered");
               } else {
                  var layoutTabName = parentLayoutEvent + "_main_tabs";
                  var mainPanel = layout.panels.filter(function(layoutPanel) {
                     return layoutPanel && layoutPanel.type == 'main'; // its always on main
                  });
               
                  if(mainPanel.length == 0 || mainPanel[0].tabs.name != layoutTabName) {
                     console.warn("Layout '" + parentLayoutEvent + "' does not have any tabs");
                  }
               }            
            }         
         }
      }
   }
   
   function createTopMenuBar(){
      var pstyle = 'background-color: ${PROJECT_MENU_COLOR}; overflow: hidden;';
      $('#topLayout').w2layout(
            {
               name : 'topLayout',
               padding : 0,
               panels : [
                  {
                     type : 'left',
                     size : '40%',
                     style : pstyle,
                     content : "<div class='toolbarTop'>"
                           + "<table border='0'>"
                           + "<tr id='toolbarRow'>"                         
                           + "   <td>"
                           + "      <table id='toolbarNormal'>"
                           + "      <tr>"
                           + "         <td><div id='newFile' title='New File&nbsp;&nbsp;&nbsp;Ctrl+N'></div></td>"                           
                           + "         <td><div id='saveFile' title='Save File&nbsp;&nbsp;&nbsp;Ctrl+S'></div></td>" 
                           + "         <td><div id='deleteFile' title='Delete File'></div></td>"   
                           + "         <td><div id='searchTypes' title='Search Types&nbsp;&nbsp;&nbsp;Ctrl+Shift+S'></div></td>"                             
                           + "         <td><div id='runScript' title='Run Script&nbsp;&nbsp;&nbsp;Ctrl+R'></div></td>" 
                           + "         <td><div id='debugScript' title='Debug Script&nbsp;&nbsp;&nbsp;Ctrl+B'></div></td>"                            
                           + "      </tr>"
                           + "      </table>"
                           + "   </td>" 
                           + "   <td><div class='toolbarSeparator'></div></td>"
                           + "   <td>"
                           + "      <table id='toolbarDebug'>"
                           + "      <tr>"
                           + "         <td><div id='stopScript' title='Stop Script'></div></td>" 
                           + "         <td><div id='resumeScript' title='Resume Script&nbsp;&nbsp;&nbsp;F8'></div></td>" 
                           + "         <td><div id='stepInScript' title='Step In&nbsp;&nbsp;&nbsp;F5'></div></td>" 
                           + "         <td><div id='stepOutScript' title='Step Out&nbsp;&nbsp;&nbsp;F7'></div></td>" 
                           + "         <td><div id='stepOverScript' title='Step Over&nbsp;&nbsp;&nbsp;F6'></div></td>" 
                           + "         <td><div id='evaluateExpression' title='Evaluate Expression&nbsp;&nbsp;&nbsp;Ctrl+Shift+E'></div></td>"                         
                           + "      </tr>"
                           + "      </table>"
                           + "   </td>"
                           + "</tr>"
                           + "</table>" 
                           + "</div>"
                  }, {
                     type : 'main',
                     size : '10%',
                     style : pstyle,
                     content : "<div class='toolbarTop'></div>"
                  }, {
                     type : 'right',
                     size : '50%',
                     style : pstyle,
                     content : "<div class='toolbarTop'>"+
                               "<table border='0' width='100%' cellpadding='0'>"+
                               "<tr>"+
                               "   <td  width='100%'></td>"+
                               "   <td><div id='toolbarNavigateBack' title='Navigate Back'></div></td>"+                                
                               "   <td><div id='toolbarNavigateForward' title='Navigate Forward'></div></td>"+     
                               "   <td>&nbsp;&nbsp;</td>"+   
                               "   <td>"+
                               "        <select class='styledSelect' id='editorTheme' size='1'>\n"+
                               "          <option value='chrome'>&nbsp;Chrome</option>\n"+
                               "          <option value='eclipse' selected='selected'>&nbsp;Eclipse</option>\n"+
                               "          <option value='github'>&nbsp;GitHub</option>\n"+
                               "          <option value='monokai'>&nbsp;Monokai</option>\n"+
                               "          <option value='terminal'>&nbsp;Terminal</option>\n"+
                               "          <option value='textmate'>&nbsp;TextMate</option>\n"+
                               "          <option value='twilight'>&nbsp;Twilight</option>\n"+
                               "          <option value='vibrant_ink'>&nbsp;Vibrant Ink</option>\n"+  
                               "          <option value='xcode'>&nbsp;XCode</option>\n"+                            
                               "        </select>\n"+
                               "   </td>"+  
                               "   <td>&nbsp;&nbsp;</td>"+                              
                               "   <td>"+
                               "        <select class='styledSelect' id='fontFamily' size='1'>\n"+
                               "          <option value='Consolas' selected='selected'>&nbsp;Consolas</option>\n"+
                               "        </select>\n"+
                               "   </td>"+  
                               "   <td>&nbsp;&nbsp;</td>"+
                               "   <td>"+
                               "        <select class='styledSelect' id='fontSize' size='1'>\n"+
                               "          <option value='10px'>&nbsp;10px</option>\n"+
                               "          <option value='11px'>&nbsp;11px</option>\n"+
                               "          <option value='12px'>&nbsp;12px</option>\n"+
                               "          <option value='13px'>&nbsp;13px</option>\n"+
                               "          <option value='14px' selected='selected'>&nbsp;14px</option>\n"+
                               "          <option value='16px'>&nbsp;16px</option>\n"+
                               "          <option value='18px'>&nbsp;18px</option>\n"+
                               "          <option value='20px'>&nbsp;20px</option>\n"+
                               "          <option value='24px'>&nbsp;24px</option>\n"+
                               "        </select>\n"+
                               "   </td>"+
                               "   <td>&nbsp;&nbsp;</td>"+  
                               "   <td><div id='toolbarResize' title='Full Screen&nbsp;&nbsp;&nbsp;Ctrl+M'></div></td>"+                               
                               "   <td><div id='toolbarSwitchLayout' title='Switch Layout&nbsp;&nbsp;&nbsp;Ctrl+L'></div></td>"+                                
                               "   <td><div id='toolbarSwitchProject' title='Switch Project&nbsp;&nbsp;&nbsp;Ctrl+P'></div></td>"+     
                               "   <td>&nbsp;&nbsp;</td>"+                                 
                               "</tr>"+
                               "</table>"+
                               "</div>"
                  } ]
            });
   }
   
   function createProblemsTab(){
      $().w2grid({
         name : 'problems',
         columns : [ {
            field : 'description',
            caption : 'Description',
            size : '45%',
            sortable : true,
            resizable : true
         },{
            field : 'location',
            caption : 'Location',
            size : '10%',
            sortable : true,
            resizable : true
         }, {
            field : 'resource',
            caption : 'Resource',
            size : '45%',
            sortable : true,
            resizable : true
         }],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  FileExplorer.openTreeFile(record.script, function() {
                     FileEditor.showEditorLine(record.line);  
                  });
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createHistoryTab(){
      $().w2grid({
         name : 'history',
         columns : [ {
            field : 'resource',
            caption : 'Resource',
            size : '50%',
            sortable : false,
            resizable : true
         },{
            field : 'date',
            caption : 'Date',
            size : '50%',
            sortable : true,
            resizable : true
         }],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  FileExplorer.openTreeHistoryFile(record.script, record.time, function() {
                     FileEditor.showEditorLine(record.line);  
                  });
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createVariablesTab(){
      $().w2grid({
         recordTitles: false, // show tooltips
         name : 'variables',
         columns : [ {
            field : 'name',
            caption : 'Name',
            size : '30%',
            sortable : false
         }, {
            field : 'value',
            caption : 'Value',
            size : '40%',
            sortable : false
         }, {
            field : 'type',
            caption : 'Type',
            size : '30%'
         } ],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  VariableManager.toggleExpandVariable(record.path);
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createProfilerTab(){
      $().w2grid({
         name : 'profiler',
         columns : [ {
            field : 'resource',
            caption : 'Resource',
            size : '40%',
            sortable : true
         }, {
            field : 'percentage',
            caption : 'Percentage',
            size : '15%',
            sortable : true            
         },{
            field : 'line',
            caption : 'Line',
            size : '15%'
         }, {
            field : 'count',
            caption : 'Count',
            size : '10%',
            sortable : true
         }, {
            field : 'duration',
            caption : 'Duration',
            size : '10%',
            sortable : true
         },{
            field : 'average',
            caption : 'Average',
            size : '10%',
            sortable : true
         }],
         sortData: [
           { field: 'percentage', direction: 'dsc' }
         ],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  FileExplorer.openTreeFile(record.script, function() {
                     FileEditor.showEditorLine(record.line);  
                  }); 
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createBreakpointsTab(){
      $().w2grid({
         name : 'breakpoints',
         columns : [ 
          {
            field : 'name',
            caption : 'Resource',
            size : '60%',
            sortable : true,
            resizable : true
         },{
            field : 'location',
            caption : 'Location',
            size : '40%',
            sortable : true,
            resizable : true
         } ],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  FileExplorer.openTreeFile(record.script, function() {
                     FileEditor.showEditorLine(record.line);  
                  }); 
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createThreadsTab(){
      $().w2grid({
         name : 'threads',
         columns : [ {
            field : 'name',
            caption : 'Thread',
            size : '25%',
            sortable : true,
            resizable : true
         }, {
            field : 'status',
            caption : 'Status',
            size : '10%',
            sortable : true,
            resizable : true
         }, {
            field : 'instruction',
            caption : 'Instruction',
            size : '15%',
            sortable : true,
            resizable : true
         },{
            field : 'resource',
            caption : 'Resource',
            size : '30%',
            sortable : true,
            resizable : true
         },{
            field : 'line',
            caption : 'Line',
            size : '10%',
            sortable : true,
            resizable : true
         },{
            field : 'active',
            caption : 'Active',
            size : '10%',
            sortable : false,
            resizable : true
         }],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  FileExplorer.openTreeFile(record.script, function(){
                     ThreadManager.updateThreadFocusByName(record.thread);
                     FileEditor.showEditorLine(record.line);  
                     ThreadManager.showThreads();
                  });
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }
   
   function createDebugTab(){
      $().w2grid({
         name : 'debug',
         columns : [ 
          {
            field : 'name',
            caption : 'Process',
            size : '20%',
            sortable : true,
            resizable : true
         }, {
            field : 'system',
            caption : 'System',
            size : '20%',
            sortable : true,
            resizable : true
         }, {
            field : 'pid',
            caption : 'PID',
            size : '10%',
            sortable : true,
            resizable : true
         },{
            field : 'status',
            caption : 'Status',
            size : '10%',
            sortable : true,
            resizable : true
         },{
            field : 'resource',
            caption : 'Resource',
            size : '30%',
            sortable : true,
            resizable : true
         },{
            field : 'active',
            caption : 'Focus',
            size : '10%',
            sortable : false,
            resizable : true
         } ],
         onClick : function(event) {
            var grid = this;
            event.onComplete = function() {
               var sel = grid.getSelection();
               if (sel.length == 1) {
                  var record = grid.get(sel[0]);
                  
                  if(record.running) {
                     FileExplorer.openTreeFile(record.script, function() {
                        Command.attachProcess(record.process);
                     });
                  } else {
                     Command.attachProcess(record.process);
                  }
               }
               grid.selectNone();
               grid.refresh();
            }
         }
      });
   }

   function activateTab(tabName, layoutName, containsBrowse, containsEditor, browseStyle) {
      hideBrowseTreeContent(containsBrowse); // hide tree
      hideEditorContent(containsEditor); // hide tree
      
      if (tabName == 'consoleTab') {
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='console'></div>");
         w2ui[layoutName].refresh();
         ProcessConsole.showConsole();
      } else if (tabName == 'problemsTab') {
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='problems'></div>");
         w2ui[layoutName].refresh();
         $('#problems').w2render('problems');
         ProblemManager.showProblems();
      } else if (tabName == 'breakpointsTab') {
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='breakpoints'></div>");
         w2ui[layoutName].refresh();
         $('#breakpoints').w2render('breakpoints');
         FileEditor.showEditorBreakpoints();
      } else if(tabName == 'threadsTab'){
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='threads'></div>");
         w2ui[layoutName].refresh();
         $('#threads').w2render('threads');
         ThreadManager.showThreads();
      } else if(tabName == 'variablesTab'){
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='variables'></div>");
         w2ui[layoutName].refresh();
         $('#variables').w2render('variables');
         VariableManager.showVariables();
      } else if(tabName == 'profilerTab'){
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='profiler'></div>");
         w2ui[layoutName].refresh();
         $('#profiler').w2render('profiler');
         VariableManager.showVariables();
      } else if(tabName == 'browseTab'){
         w2ui[layoutName].content('main', "<div style='overflow: hidden; font-family: monospace;' id='browse'><div id='browseParent' "+browseStyle+"></div></div>");
         w2ui[layoutName].refresh();
         $('#browse').w2render('browse');
         showBrowseTreeContent(containsBrowse); // hack to move tree
      } else if(tabName == 'debugTab'){
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='debug'></div>");
         w2ui[layoutName].refresh();
         $('#debug').w2render('debug');
         DebugManager.showStatus();
      } else if(tabName == 'historyTab'){
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='history'></div>");
         w2ui[layoutName].refresh();
         $('#history').w2render('history');
         History.showFileHistory();
      } else { // editor is always the default as it contains file names
         w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>");
         w2ui[layoutName].refresh();
         $('#edit').w2render('edit');
         showEditorContent(containsEditor);
      }
   }
}

//ModuleSystem.registerModule("project", "Project module: project.js", Project.createMainLayout, Project.startMainLayout, [ "common", "socket", "console", "problem", "editor", "spinner", "tree", "threads" ]);

