import * as $ from "jquery"
import {w2ui} from "w2ui"
import {Common} from "common"
import {Project} from "project"
import {Alerts} from "alert"
import {EventBus} from "socket"
import {ProcessConsole} from "console"
import {ProblemManager} from "problem"
import {FileEditor, FileEditorState} from "editor"
import {LoadSpinner} from "spinner"
import {FileTree, FilePath, FileNode} from "tree"
import {ThreadManager, ThreadScope, ThreadStatus} from "threads"
import {History} from "history"
import {VariableManager} from "variables"
import {DialogBuilder} from "dialog"
import {FileExplorer, FileResource} from "explorer"
import {DebugManager} from "debug" 
  
export module Command {

   var windowHandles = {};

   export function openTreeFile(path) {
      EventBus.sendEvent("OPEN", {
         project: Common.getProjectName(),
         resource: path
      });
   }

   export function openChildWindow(path, name) {
      var host = window.document.location.hostname;
      var port = window.document.location.port;
      var scheme = window.document.location.protocol;
      var address = scheme + "//" + host;
      var session = Common.extractCookie("SESSID"); // hardcoded :(

      if((port - parseFloat(port) + 1) >= 0) {
         address += ":";
         address += port;
      }
      address += path;

      EventBus.sendEvent("LAUNCH", {
         address: address,
         session: session
      });
   }
   
      
   export function openTerminal(resourcePath: FilePath) {
      if(FileTree.isResourceFolder(resourcePath.getFilePath())) {
	      var host = window.document.location.hostname;
	      var port = window.document.location.port;
	      var scheme = window.document.location.protocol;
	      var address = scheme + "//" + host;
	      var session = Common.extractCookie("SESSID"); // hardcoded :(
	
	      if((port - parseFloat(port) + 1) >= 0) {
	         address += ":";
	         address += port;
	      }
	      address += "/terminal?path=" + resourcePath.getFilePath();
	
	      EventBus.sendEvent("LAUNCH", {
	         address: address,
	         session: session
	      });
      }
   }
   
   export function exploreDirectory(resourcePath: FilePath) {
      if(FileTree.isResourceFolder(resourcePath.getFilePath())) {
         var message = {
            project : Common.getProjectName(),
            resource : resourcePath.getFilePath(),
            terminal: false
         };
         EventBus.sendEvent("EXPLORE", message);
      }
   }
   
   export function searchTypes() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete){
         findTypesMatching(text, function(typesFound, originalExpression) {
            var typeRows = [];
           
            for(var i = 0; i < typesFound.length; i++) {
               var debugToggle = ";debug";
               var locationPath = window.document.location.pathname;
               var locationHash = window.document.location.hash;
               var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
               var resourceLink = "/project/" + typesFound[i].project;
               var typePackage = "<i style='opacity: 0.5'>" + typesFound[i].module + "<i>";
               var absolutePath = ""
               var decompile = false;
                  
               if(typesFound[i].extra){
                  absolutePath = "<i style='opacity: 0.5'>" + typesFound[i].extra + "<i>";
               }
               if(debug) {
                  resourceLink += debugToggle;
               }
               if(isJavaResource(typesFound[i].extra)) { // java class in a JAR file
                  var packageName = typesFound[i].module;
                  var className = typesFound[i].name;
                  
                  resourceLink += '#' + createLinkForJavaResource(typesFound[i].extra, packageName + "." + className);
               }
               else if(isJavaModule(typesFound[i].extra, typesFound[i].resource)) { // java internal module
                  var packageName = typesFound[i].module;
                  var className = typesFound[i].name;

                  resourceLink += '#' + createLinkForJavaModule(typesFound[i].extra, packageName + "." + className);
               }
               else {
                  resourceLink += "#" + FileTree.cleanResourcePath(typesFound[i].resource);
               }
               var typeCell = {
                  text: typesFound[i].name + "&nbsp;&nbsp;" + typePackage,
                  link: resourceLink,
                  line: 0,
                  style: typesFound[i].type == 'module' ? 'moduleNode' : 'typeNode'
               };
               var resourceCell = {
                  text: typesFound[i].resource + "&nbsp;&nbsp;" + absolutePath,
                  link: resourceLink,
                  line: 0,
                  style: 'resourceNode'
               };
               typeRows.push([typeCell, resourceCell]);
            }
            onComplete(typeRows, originalExpression);
         });
     }, null, "Search Types");  
   }

   function isJavaModule(libraryPath, moduleName) {
     if(libraryPath) {
        return Common.stringStartsWith(moduleName, "java.") ||
               Common.stringStartsWith(moduleName, "jdk.");
     }
     return false;
   }

   function isJavaResource(libraryPath) {
      return libraryPath && Common.stringEndsWith(libraryPath, ".jar");
   }
   
   function createLinkForJavaResource(libraryPath, className) {
      var jarFile = Common.stringReplaceText(libraryPath, "\\", "/")
      var packageName = createPackageNameFromFullClassName(className);
      var typeName = createTypeNameFromFullClassName(className);
      
      return "/decompile/" + jarFile + "/" + packageName + "/" + typeName + ".java";
   }

   function createLinkForJavaModule(libraryPath, className) {
      var moduleBase = Common.stringReplaceText(libraryPath, "\\", "/")
      var packageName = createPackageNameFromFullClassName(className);
      var typeName = createTypeNameFromFullClassName(className);

      return "/decompile/" + moduleBase + "/" + packageName + "/" + typeName + ".java";
   }

   function createPackageNameFromFullClassName(className) {
      return className.substring(0, className.lastIndexOf('.'));
   }
   
   function createTypeNameFromFullClassName(className) {
      return className.substring(className.lastIndexOf('.')+1);
   }
   
   function findTypesMatching(text, onComplete) {
      let originalExpression = text; // keep track of the requested expression
      
      if(text && text.length > 1) {         
         $.ajax({
            url: '/type/' + Common.getProjectName() + '?expression=' + originalExpression,
            success: function (typeMatches) {
               var sortedMatches = [];
               
               for (var typeMatch in typeMatches) {
                  if (typeMatches.hasOwnProperty(typeMatch)) {
                     sortedMatches.push(typeMatch);
                  }
               }  
               sortedMatches.sort();
               var response = [];
               for(var i = 0; i < sortedMatches.length; i++) {
                  var sortedMatch = sortedMatches[i];
                  var typeReference = typeMatches[sortedMatch];
                  var typeEntry = {
                     name: typeReference.name,
                     resource: typeReference.resource,
                     module: typeReference.module,
                     extra: typeReference.extra,
                     type: typeReference.type,
                     project: Common.getProjectName()
                  };
                  response.push(typeEntry);
               }
               onComplete(response, originalExpression);
            },
            async: true
         });
      } else {
         onComplete([], originalExpression);
      }
   }
   
   export function searchOutline() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete){
         findOutline(text, function(outlinesFound, originalExpression) {
            var outlineRows = [];
            
            for(var i = 0; i < outlinesFound.length; i++) {
               var outlineFound = outlinesFound[i];
               var outlineType = outlineFound.type.toLowerCase();
               var constraintInfo = "<i style='opacity: 0.5'>" + outlineFound.constraint + "<i>";
               var typeName = createTypeNameFromFullClassName(outlineFound.declaringClass);
               var packageName = createPackageNameFromFullClassName(outlineFound.declaringClass);
               var typePackage = "<i style='opacity: 0.5'>" + packageName + "<i>";
               var resource = outlineFound.resource;
               var line = outlineFound.line;
               var resourceLink = null;
               var libraryPath = "";
               
               if(isJavaResource(outlineFound.libraryPath) && outlineFound.declaringClass) { // java class in a JAR file
                  resourceLink = "/project/" + Common.getProjectName() + "#" + createLinkForJavaResource(outlineFound.libraryPath, outlineFound.declaringClass);
                  line = null;
               } else {
                  resource = FileTree.cleanResourcePath("/resource/" + Common.getProjectName() + resource);
               }
               var outlineCell = {
                  text: outlineFound.name + "&nbsp;&nbsp;" + constraintInfo,
                  resource: resource,
                  link: resourceLink,
                  line: line,
                  style: outlineType == 'function' ? 'functionNode' : 'propertyNode'
               };
               var typeCell = {
                  text: typeName + "&nbsp;&nbsp;" + typePackage,
                  resource: resource,
                  link: resourceLink,
                  line: line,
                  style: "resourceNode"
               };
               outlineRows.push([outlineCell, typeCell]);
            }
            onComplete(outlineRows, originalExpression);
         });
      }, null, "Search Outline");  
   }
   
   function findOutline(text, onComplete) {
      let originalExpression = text; // keep track of the requested expression
      
      if(text || text == "") {  
         let line = FileEditor.getCurrentLineForEditor();
         let editorState: FileEditorState = FileEditor.currentEditorState();
         let message = JSON.stringify({
            resource: editorState.getResource().getProjectPath(),
            line: line,
            complete: originalExpression.trim(),
            source: editorState.getSource()
         });
         $.ajax({
            contentType: 'application/json',
            data: message,
            dataType: 'json',
            success: function(response){
               var outlinesFound = response.outlines;
               var outlineDetails = [];
               
               for (var outlineName in outlinesFound) {
                  if (outlinesFound.hasOwnProperty(outlineName)) {
                     var outlineDetail = outlinesFound[outlineName];
                     
                     outlineDetails.push({
                        name: outlineName,
                        type: outlineDetail.type,
                        resource: outlineDetail.resource,
                        line: outlineDetail.line,
                        constraint: outlineDetail.constraint,
                        declaringClass: outlineDetail.declaringClass,
                        libraryPath: outlineDetail.libraryPath
                     });
                  }
               }
               onComplete(outlineDetails, originalExpression);
            },
            error: function(response){
                onComplete([], originalExpression);
                console.log("Could not complete outline for text '" + originalExpression + "'", message);
            },
            async: true,
            processData: false,
            type: 'POST',
            url: '/outline/' + Common.getProjectName()
        });
      } else {
         onComplete([], originalExpression);
      }
   }
   
   export function replaceTokenInFiles(matchText, searchCriteria, filePatterns) {
      findFilesWithText(matchText, filePatterns, searchCriteria, function(filesReplaced){
         var editorState: FileEditorState = FileEditor.currentEditorState();
         
         for(var i = 0; i < filesReplaced.length; i++) {
            var fileReplaced = filesReplaced[i];
            var fileReplacedResource: FilePath = FileTree.createResourcePath("/resource/" + fileReplaced.project + "/" + fileReplaced.resource);
            
            if(editorState.getResource().getResourcePath() == fileReplacedResource.getResourcePath()) {
               FileExplorer.openTreeFile(fileReplacedResource.getResourcePath(), function() {
                  //FileEditor.showEditorLine(record.line);  
               }); 
            }
         }
      });
   }
   
   export function searchFiles(filePatterns) {
      searchOrReplaceFiles(false, filePatterns);
   }
   
   export function searchAndReplaceFiles(filePatterns) {
      searchOrReplaceFiles(true, filePatterns);
   }
   
   function searchOrReplaceFiles(enableReplace, filePatterns) {
      if(!filePatterns) {
         filePatterns = '*.tern,*.properties,*.xml,*.txt,*.json';
      } 
      var searchFunction = DialogBuilder.createTextSearchOnlyDialog;
      
      if(enableReplace) {
         searchFunction = DialogBuilder.createTextSearchAndReplaceDialog;
      }
      searchFunction(function(text, fileTypes, searchCriteria, onComplete){
         findFilesWithText(text, fileTypes, searchCriteria, function(filesFound, originalText) { // don't replace in the search phase
            var fileRows = [];
           
            for(var i = 0; i < filesFound.length; i++) {
               var fileFound = filesFound[i];
               var debugToggle = ";debug";
               var locationPath = window.document.location.pathname;
               var locationHash = window.document.location.hash;
               var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
               var resourceLink = FileTree.cleanResourcePath("/resource/" + fileFound.project + "/" + fileFound.resource);
               var resource = FileTree.cleanResourcePath(fileFound.resource);
               
               var resourceCell = {
                  text: resource,
                  line: fileFound.line,
                  resource: resourceLink,
                  style: 'resourceNode'
               };
   //            var lineCell = {
   //               text: "&nbsp;line&nbsp;" + filesFound[i].line + "&nbsp;",
   //               link: resourceLink,
   //               style: ''
   //            };
               var textCell = {
                  text: fileFound.text,
                  line: fileFound.line,
                  resource: resourceLink,
                  style: 'textNode'
               };
               fileRows.push([resourceCell, /*lineCell, */textCell]);
            }
            return onComplete(fileRows, originalText);
         });
     }, filePatterns, enableReplace ? "Replace Text" : "Find Text");
   }
   
   function findFilesWithText(text, fileTypes, searchCriteria, onComplete) {
      let originalText = text;
      
      if(text && text.length > 0) {
         var searchUrl = '';
         
         searchUrl += '/find/' + Common.getProjectName();
         searchUrl += '?expression=' + encodeURIComponent(originalText);
         searchUrl += '&pattern=' + encodeURIComponent(fileTypes);
         searchUrl += "&caseSensitive=" + encodeURIComponent(searchCriteria.caseSensitive);
         searchUrl += "&regularExpression=" + encodeURIComponent(searchCriteria.regularExpression);
         searchUrl += "&wholeWord=" + encodeURIComponent(searchCriteria.wholeWord);
         searchUrl += "&replace=" + encodeURIComponent(searchCriteria.replace);
         searchUrl += "&enableReplace=" + encodeURIComponent(searchCriteria.enableReplace);
         
         $.ajax({
            url: searchUrl,
            success: function (filesMatched) {
               var response = [];
               
               for(var i = 0; i < filesMatched.length; i++) {
                  var fileMatch = filesMatched[i];
                  var typeEntry = {
                     resource: fileMatch.resource,
                     line: fileMatch.line,
                     project: Common.getProjectName()
                  };
                  response.push(fileMatch);   
               }
               onComplete(response, originalText);
            },
            async: true
         });
      }else {
         onComplete([], originalText);
      }
   }
   
   export function findFileNames() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete){
         findFilesByName(text, function(filesFound, originalText) {
            var fileRows = [];
           
            for(var i = 0; i < filesFound.length; i++) {
               var fileFound = filesFound[i];
               var debugToggle = ";debug";
               var locationPath = window.document.location.pathname;
               var locationHash = window.document.location.hash;
               var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
               var resourceLink = "/project/" + fileFound.project;
               
               if (debug) {
                   resourceLink += debugToggle;
               }
               resourceLink += "#" + FileTree.cleanResourcePath(fileFound.resource);
               
               var resourceCell = {
                  text: fileFound.text,
                  name: fileFound.name,
                  link: resourceLink,
                  style: 'resourceNode'
               };
               fileRows.push([resourceCell]);
            }
            return onComplete(fileRows, originalText);
         });
     }, null, "Find Files");
   }
   
   function findFilesByName(text, onComplete) {
      let originalText = text;
      
      if(text && text.length > 1) {
         $.ajax({
            url: '/file/' + Common.getProjectName() + '?expression=' + originalText,
            success: function (filesMatched) {
               var response = [];
               
               for(var i = 0; i < filesMatched.length; i++) {
                  var fileMatch = filesMatched[i];
                  var typeEntry = {
                     resource: FileTree.cleanResourcePath(fileMatch.resource),
                     path: fileMatch.path,
                     name: fileMatch.name,
                     project: Common.getProjectName()
                  };
                  response.push(fileMatch);
               }
               onComplete(response, originalText);
            },
            async: true
         });
      } else {
         onComplete([], originalText);
      }
   }
   
   export function folderExpand(resourcePath: string) {
      var message = {
         project: Common.getProjectName(),
         folder : resourcePath
      };
      EventBus.sendEvent("FOLDER_EXPAND", message);
   }
   
   export function folderCollapse(resourcePath: string) {
      var message = {
         project: Common.getProjectName(),
         folder : resourcePath
      };
      EventBus.sendEvent("FOLDER_COLLAPSE", message);
   }
   
   export function pingProcess() {
      if(EventBus.isSocketOpen()) {
         EventBus.sendEvent("PING", Common.getProjectName());
      }
   }
   
   export function uploadFileTo(fileName, uploadToPath, encodedFile) {
      var destinationPath: FilePath = FileTree.createResourcePath(uploadToPath);
      var toPath = FileTree.cleanResourcePath(destinationPath.getFilePath() + "/" + fileName);
      
      console.log("source: " + fileName + " destination: " + toPath);
      
      var message = {
         project : Common.getProjectName(),
         name : fileName,
         to: toPath,
         data: encodedFile,
         dragAndDrop: true
      };
      EventBus.sendEvent("UPLOAD", message);
   }
   
   export function isDragAndDropFilePossible(fileToMove: FileNode, moveTo: FileNode) {
      //return moveTo.folder; // only move files and folders to different folders
      return true;
   }
   
   export function dragAndDropFile(fileToMove: FileNode, moveTo: FileNode) {
      if(isDragAndDropFilePossible(fileToMove, moveTo)) {
         var originalPath: FilePath = fileToMove.getResource();
         var destinationPath: FilePath = moveTo.getResource();
         var fromPath = FileTree.cleanResourcePath(originalPath.getFilePath());
         var toPath = FileTree.cleanResourcePath(destinationPath.getFilePath() + "/" + originalPath.getFileName());
         
         console.log("source: " + fromPath + " destination: " + toPath);
         
         var message = {
            project : Common.getProjectName(),
            from : fromPath,
            to: toPath,
            dragAndDrop: true
         };
         EventBus.sendEvent("RENAME", message);

         if(fileToMove.isFolder()) {
            var children = fileToMove.getChildren();

            for(var i = 0; i < children.length; i++) {
                var oldChildPath = children[i];
                var newChildPath = Common.stringReplaceText(oldChildPath, fromPath, toPath);
                
                Project.renameEditorTab(FileTree.createResourcePath(oldChildPath), FileTree.createResourcePath(newChildPath)); // rename tabs if open
            }
         } else {
            Project.renameEditorTab(originalPath, FileTree.createResourcePath(toPath)); // rename tabs if open
         }
      }
   }
   
   export function renameFile(resourcePath: FilePath) {
      var originalFile: string = resourcePath.getFilePath();
      
      DialogBuilder.renameFileTreeDialog(resourcePath, true, function(resourceDetails) {
         var message = {
            project : Common.getProjectName(),
            from : originalFile,
            to: resourceDetails.getFilePath(),
            dragAndDrop: false
         };
         EventBus.sendEvent("RENAME", message);
         Project.renameEditorTab(resourcePath, resourceDetails); // rename tabs if open
      });
   }
     
   export function renameDirectory(resourcePath: FilePath) {
      var originalPath: string = resourcePath.getFilePath();
      var directoryPath: FilePath = FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
      
      DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function(resourceDetails) {
         var message = {
            project : Common.getProjectName(),
            from : originalPath,
            to: resourceDetails.getFilePath()
         };
         EventBus.sendEvent("RENAME", message);
      });
   }
   
   export function newFile(resourcePath) {
      DialogBuilder.newFileTreeDialog(resourcePath, true, function(resourceDetails: FilePath) {
         if(!FileTree.isResourceFolder(resourceDetails.getFilePath())) {
            var message = {
               project : Common.getProjectName(),
               resource : resourceDetails.getFilePath(),
               source : "",
               directory: false,
               create: true
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
            
            var modificationTime: number = new Date().getTime();
            var fileResource: FileResource  = new FileResource(resourceDetails, null, modificationTime, "", null, false, false);
            
            FileEditor.updateEditor(fileResource);
         }
      });
   }
   
   export function newDirectory(resourcePath) {
      DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function(resourceDetails: FilePath) {
         if(FileTree.isResourceFolder(resourceDetails.getFilePath())) {
            var message = {
               project : Common.getProjectName(),
               resource : resourceDetails.getFilePath(),
               source : "",
               directory: true,
               create: true
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
         }
      });
   }
   
   export function saveFile() {
      saveFileWithAction(true, function(functionToExecuteAfterSave){
         functionToExecuteAfterSave();
      });
   }
   
   function saveFileWithAction(update, saveCallback) {
      var editorState: FileEditorState = FileEditor.currentEditorState();
   
      if (editorState.getResource() == null) {
         DialogBuilder.openTreeDialog(null, false, function(resourceDetails: FilePath) {
            var saveFunction = saveEditor(update);
            saveCallback(saveFunction);
         });
      } else {
         if (FileEditor.isEditorChanged()) {
            var saveFunction = saveEditor(update);
            saveCallback(saveFunction);
         } else {
            ProcessConsole.clearConsole();
            saveCallback(function(){});
         }
      }
   }
   
   function saveEditor(update) {
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var editorPath: FilePath = editorState.getResource();
        
      if(editorPath != null) {
         var message = {
            project : Common.getProjectName(),
            resource : editorPath.getFilePath(),
            source : editorState.getSource(),
            directory: false,
            create: false
         };
         ProcessConsole.clearConsole();
         EventBus.sendEvent("SAVE", message);
         
         if(update) { // should the editor be updated?
            return function() {
               var modificationTime: number = new Date().getTime();
               var fileResource: FileResource = new FileResource(editorPath, null, modificationTime, editorState.getSource(), null, false, false);
               
               console.log("Saving editor " + editorPath);
               FileEditor.updateEditor(fileResource);
            };
         }
      }
      return function(){}
   }
   
   export function saveEditorOnClose(editorText, editorResource: FilePath) {
      if (editorResource != null && editorResource.getResourcePath()) {
         DialogBuilder.openTreeDialog(editorResource, true, function(resourceDetails: FilePath) {
            var message = {
               project : Common.getProjectName(),
               resource : editorResource.getFilePath(),
               source : editorText,
               directory: false,
               create: false
            };
            //ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
            FileEditor.clearSavedEditorBuffer(editorResource.getResourcePath()); // make sure its synce
         }, 
         function(resourceDetails) {
            // file was not saved
            FileEditor.clearSavedEditorBuffer(editorResource.getResourcePath()); 
         });
      } 
   }  
   
   export function deleteFile(resourceDetails: FilePath) {
      var editorState: FileEditorState = FileEditor.currentEditorState();
   
      if(resourceDetails == null && editorState.getResource() != null) {
         resourceDetails = editorState.getResource();
      }
      if(resourceDetails != null) {
         var message = "Delete resource " + resourceDetails.getFilePath();
         
         Alerts.createConfirmAlert("Delete File", message, "Delete", "Cancel", 
               function(){
                  var message = {
                     project : Common.getProjectName(),
                     resource : resourceDetails.getFilePath()
                  };
                  ProcessConsole.clearConsole();
                  EventBus.sendEvent("DELETE", message);
                  Project.deleteEditorTab(resourceDetails.getResourcePath()); // rename tabs if open
               },
               function(){});
      }
   } 
   
   export function deleteDirectory(resourceDetails: FilePath) {
      if(resourceDetails != null) {
         var message = {
            project : Common.getProjectName(),
            resource : resourceDetails.getFilePath()
         };
         ProcessConsole.clearConsole();
         EventBus.sendEvent("DELETE", message);
      }
   }
   
   export function createArchive(savePath: FilePath, mainScript: FilePath) {
      DialogBuilder.createArchiveTreeDialog(savePath, function(resourceDetails: FilePath) {
         var message = {
            project: Common.getProjectName(),
            resource: mainScript.getProjectPath(),
            archive: resourceDetails.getProjectPath()
         };
         EventBus.sendEvent("CREATE_ARCHIVE", message);
      });
   }
         
   export function runScript() {
      executeScript(false, false);
   }
 
   export function runScriptWithArguments() {
      executeScript(false, true);
   }

   export function debugScript() {
      executeScript(true, false);
   }  
   
   export function debugScriptWithArguments() {
      executeScript(true, true);
   }  

   function executeScript(debug, requireArguments) {
      var saveFunction = function(functionToExecuteAfterSave) {
         setTimeout(function() {
            if(requireArguments) { // user input required
                var delayFunction = function() {
                    setTimeout(function() {
                       FileEditor.focusEditor();
                       FileEditor.focusEditor();
                       functionToExecuteAfterSave();
                    }, 50);
                 }
                if(debug) {
                    Alerts.createDebugPromptAlert("Debug", "Enter arguments", "Debug", "Cancel", 
                        function(inputArguments) { // yes callback
                            executeScriptWithArguments(true, inputArguments);
                            delayFunction();
                        },
                        function(inputArguments) { // no callback
                            delayFunction();
                        }
                    );
                } else {
                    Alerts.createRunPromptAlert("Run", "Enter arguments", "Run", "Cancel", 
                        function(inputArguments) { // yes callback
                            executeScriptWithArguments(false, inputArguments);
                            delayFunction();
                        },
                        function(inputArguments) { // no callback
                            delayFunction();
                        }
                    );
                }
            } else {
                executeScriptWithArguments(debug, "");
                functionToExecuteAfterSave();
            }
         }, 1);
      };
      saveFileWithAction(true, saveFunction); // save editor
   }
   
   function executeScriptWithArguments(debug, inputArguments) {
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var argumentArray = inputArguments.split(/[ ]+/)
      var message = {
         breakpoints : editorState.getBreakpoints(),
         arguments: argumentArray,
         project : Common.getProjectName(),
         resource : editorState.getResource().getFilePath(),
         source : editorState.getSource(),
         debug: debug ? true: false
      };
      EventBus.sendEvent("EXECUTE", message);
   };
   
   export function attachRemoteDebugger() {
      if(EventBus.isSocketOpen()) {
         Alerts.createRemoteDebugPromptAlert("Remote Debug", "Enter <host>:<port>", "Attach", "Cancel", 
            function(hostAndPort) {
               var message = {
                  project: Common.getProjectName(),
                  address: hostAndPort
               };
               EventBus.sendEvent("REMOTE_DEBUG", message);
            }
         );
      }
   }
   
   export function updateScriptBreakpoints() {
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var message = {
         breakpoints : editorState.getBreakpoints(),
         project : Common.getProjectName(),
      };
      EventBus.sendEvent("BREAKPOINTS", message);
   }
   
   export function stepOverScript() {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.getThread(),
            type: "STEP_OVER"
         };
         FileEditor.clearEditorHighlights();
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stepInScript() {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.getThread(),
            type: "STEP_IN"
         };
         FileEditor.clearEditorHighlights();
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stepOutScript() {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.getThread(),
            type: "STEP_OUT"
         };
         FileEditor.clearEditorHighlights(); 
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function resumeScript() {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.getThread(),
            type: "RUN"
         };
         FileEditor.clearEditorHighlights(); 
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stopScript() {
      EventBus.sendEvent("STOP");
   }
   
   export function browseScriptVariables(variables) {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.getThread(),
            expand: variables
         };
         EventBus.sendEvent("BROWSE", message);
      }
   }
   
   export function browseScriptEvaluation(variables, expression, refresh) {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      if (threadScope != null) {
          var message = {
              thread: threadScope.getThread(),
              expression: expression,
              expand: variables,
              refresh: refresh
          };
          EventBus.sendEvent("EVALUATE", message);
      }
   }
   
   export function attachProcess(process) {
      var statusFocus = DebugManager.currentStatusFocus(); // what is the current focus
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var message = {
         process: process,
         breakpoints : editorState.getBreakpoints(),
         project : Common.getProjectName(),
         focus: statusFocus != process // toggle the focus
      };
      EventBus.sendEvent("ATTACH", message); // attach to process
   }
   
   export function switchLayout() {
      var debugToggle = ";debug";
      var locationPath = window.document.location.pathname;
      var locationHash = window.document.location.hash;
      var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
      
      if(debug) {
         var remainingPath = locationPath.substring(0, locationPath.length - debugToggle.length);
         document.location = remainingPath + locationHash;
      } else {
         document.location = locationPath + debugToggle + locationHash;
      }
   }
   
   export function updateDisplay(displayInfo) {
      if(EventBus.isSocketOpen()) {
         EventBus.sendEvent("DISPLAY_UPDATE", displayInfo); // update and save display
      }
   }
   
   export function evaluateExpression() {
      var threadScope = ThreadManager.focusedThread();
      if (threadScope != null) {
         var selectedText = FileEditor.getSelectedText();
         DialogBuilder.evaluateExpressionDialog(selectedText);
      }
   }
   
   export function refreshScreen() {
      setTimeout(function() {
         location.reload();
      }, 10);
   }
   
   export function switchProject() {
      document.location="/";
   }
}

//ModuleSystem.registerModule("commands", "Commands module: commands.js", null, null, [ "common", "editor", "tree", "threads" ]);