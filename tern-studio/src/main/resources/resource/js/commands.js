define(["require", "exports", "jquery", "common", "project", "alert", "socket", "console", "editor", "tree", "threads", "dialog", "explorer", "debug"], function (require, exports, $, common_1, project_1, alert_1, socket_1, console_1, editor_1, tree_1, threads_1, dialog_1, explorer_1, debug_1) {
    "use strict";
    var Command;
    (function (Command) {
        function searchTypes() {
            dialog_1.DialogBuilder.createListDialog(function (text, ignoreMe, onComplete) {
                findTypesMatching(text, function (typesFound, originalExpression) {
                    var typeRows = [];
                    for (var i = 0; i < typesFound.length; i++) {
                        var debugToggle = ";debug";
                        var locationPath = window.document.location.pathname;
                        var locationHash = window.document.location.hash;
                        var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                        var resourceLink = "/project/" + typesFound[i].project;
                        var typePackage = "<i style='opacity: 0.5'>" + typesFound[i].module + "<i>";
                        var absolutePath = "";
                        var decompile = false;
                        if (typesFound[i].extra) {
                            absolutePath = "<i style='opacity: 0.5'>" + typesFound[i].extra + "<i>";
                        }
                        if (debug) {
                            resourceLink += debugToggle;
                        }
                        if (isJavaResource(typesFound[i].extra)) {
                            var packageName = typesFound[i].module;
                            var className = typesFound[i].name;
                            resourceLink += '#' + createLinkForJavaResource(typesFound[i].extra, packageName + "." + className);
                        }
                        else {
                            resourceLink += "#" + typesFound[i].resource;
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
        Command.searchTypes = searchTypes;
        function isJavaResource(libraryPath) {
            return libraryPath && common_1.Common.stringEndsWith(libraryPath, ".jar");
        }
        function createLinkForJavaResource(libraryPath, className) {
            var jarFile = common_1.Common.stringReplaceText(libraryPath, "\\", "/");
            var packageName = createPackageNameFromFullClassName(className);
            var typeName = createTypeNameFromFullClassName(className);
            return "/decompile/" + jarFile + "/" + packageName + "/" + typeName + ".java";
        }
        function createPackageNameFromFullClassName(className) {
            return className.substring(0, className.lastIndexOf('.'));
        }
        function createTypeNameFromFullClassName(className) {
            return className.substring(className.lastIndexOf('.') + 1);
        }
        function findTypesMatching(text, onComplete) {
            var originalExpression = text; // keep track of the requested expression
            if (text && text.length > 1) {
                $.ajax({
                    url: '/type/' + common_1.Common.getProjectName() + '?expression=' + originalExpression,
                    success: function (typeMatches) {
                        var sortedMatches = [];
                        for (var typeMatch in typeMatches) {
                            if (typeMatches.hasOwnProperty(typeMatch)) {
                                sortedMatches.push(typeMatch);
                            }
                        }
                        sortedMatches.sort();
                        var response = [];
                        for (var i = 0; i < sortedMatches.length; i++) {
                            var sortedMatch = sortedMatches[i];
                            var typeReference = typeMatches[sortedMatch];
                            var typeEntry = {
                                name: typeReference.name,
                                resource: typeReference.resource,
                                module: typeReference.module,
                                extra: typeReference.extra,
                                type: typeReference.type,
                                project: common_1.Common.getProjectName()
                            };
                            response.push(typeEntry);
                        }
                        onComplete(response, originalExpression);
                    },
                    async: true
                });
            }
            else {
                onComplete([], originalExpression);
            }
        }
        function searchOutline() {
            dialog_1.DialogBuilder.createListDialog(function (text, ignoreMe, onComplete) {
                findOutline(text, function (outlinesFound, originalExpression) {
                    var outlineRows = [];
                    for (var i = 0; i < outlinesFound.length; i++) {
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
                        if (isJavaResource(outlineFound.libraryPath) && outlineFound.declaringClass) {
                            resourceLink = "/project/" + common_1.Common.getProjectName() + "#" + createLinkForJavaResource(outlineFound.libraryPath, outlineFound.declaringClass);
                            line = null;
                        }
                        else {
                            resource = "/resource/" + common_1.Common.getProjectName() + resource;
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
        Command.searchOutline = searchOutline;
        function findOutline(text, onComplete) {
            var originalExpression = text; // keep track of the requested expression
            if (text || text == "") {
                var line = editor_1.FileEditor.getCurrentLineForEditor();
                var editorState = editor_1.FileEditor.currentEditorState();
                var message_1 = JSON.stringify({
                    resource: editorState.getResource().getProjectPath(),
                    line: line,
                    complete: originalExpression.trim(),
                    source: editorState.getSource()
                });
                $.ajax({
                    contentType: 'application/json',
                    data: message_1,
                    dataType: 'json',
                    success: function (response) {
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
                    error: function (response) {
                        onComplete([], originalExpression);
                        console.log("Could not complete outline for text '" + originalExpression + "'", message_1);
                    },
                    async: true,
                    processData: false,
                    type: 'POST',
                    url: '/outline/' + common_1.Common.getProjectName()
                });
            }
            else {
                onComplete([], originalExpression);
            }
        }
        function replaceTokenInFiles(matchText, searchCriteria, filePatterns) {
            findFilesWithText(matchText, filePatterns, searchCriteria, function (filesReplaced) {
                var editorState = editor_1.FileEditor.currentEditorState();
                for (var i = 0; i < filesReplaced.length; i++) {
                    var fileReplaced = filesReplaced[i];
                    var fileReplacedResource = tree_1.FileTree.createResourcePath("/resource/" + fileReplaced.project + "/" + fileReplaced.resource);
                    if (editorState.getResource().getResourcePath() == fileReplacedResource.getResourcePath()) {
                        explorer_1.FileExplorer.openTreeFile(fileReplacedResource.getResourcePath(), function () {
                            //FileEditor.showEditorLine(record.line);  
                        });
                    }
                }
            });
        }
        Command.replaceTokenInFiles = replaceTokenInFiles;
        function searchFiles(filePatterns) {
            searchOrReplaceFiles(false, filePatterns);
        }
        Command.searchFiles = searchFiles;
        function searchAndReplaceFiles(filePatterns) {
            searchOrReplaceFiles(true, filePatterns);
        }
        Command.searchAndReplaceFiles = searchAndReplaceFiles;
        function searchOrReplaceFiles(enableReplace, filePatterns) {
            if (!filePatterns) {
                filePatterns = '*.tern,*.properties,*.xml,*.txt,*.json';
            }
            var searchFunction = dialog_1.DialogBuilder.createTextSearchOnlyDialog;
            if (enableReplace) {
                searchFunction = dialog_1.DialogBuilder.createTextSearchAndReplaceDialog;
            }
            searchFunction(function (text, fileTypes, searchCriteria, onComplete) {
                findFilesWithText(text, fileTypes, searchCriteria, function (filesFound, originalText) {
                    var fileRows = [];
                    for (var i = 0; i < filesFound.length; i++) {
                        var fileFound = filesFound[i];
                        var debugToggle = ";debug";
                        var locationPath = window.document.location.pathname;
                        var locationHash = window.document.location.hash;
                        var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                        var resourceLink = "/resource/" + fileFound.project + "/" + fileFound.resource;
                        var resourceCell = {
                            text: fileFound.resource,
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
                        fileRows.push([resourceCell, textCell]);
                    }
                    return onComplete(fileRows, originalText);
                });
            }, filePatterns, enableReplace ? "Replace Text" : "Find Text");
        }
        function findFilesWithText(text, fileTypes, searchCriteria, onComplete) {
            var originalText = text;
            if (text && text.length > 1) {
                var searchUrl = '';
                searchUrl += '/find/' + common_1.Common.getProjectName();
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
                        for (var i = 0; i < filesMatched.length; i++) {
                            var fileMatch = filesMatched[i];
                            var typeEntry = {
                                resource: fileMatch.resource,
                                line: fileMatch.line,
                                project: common_1.Common.getProjectName()
                            };
                            response.push(fileMatch);
                        }
                        onComplete(response, originalText);
                    },
                    async: true
                });
            }
            else {
                onComplete([], originalText);
            }
        }
        function findFileNames() {
            dialog_1.DialogBuilder.createListDialog(function (text, ignoreMe, onComplete) {
                findFilesByName(text, function (filesFound, originalText) {
                    var fileRows = [];
                    for (var i = 0; i < filesFound.length; i++) {
                        var fileFound = filesFound[i];
                        var debugToggle = ";debug";
                        var locationPath = window.document.location.pathname;
                        var locationHash = window.document.location.hash;
                        var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                        var resourceLink = "/project/" + fileFound.project;
                        if (debug) {
                            resourceLink += debugToggle;
                        }
                        resourceLink += "#" + fileFound.resource;
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
        Command.findFileNames = findFileNames;
        function findFilesByName(text, onComplete) {
            var originalText = text;
            if (text && text.length > 1) {
                $.ajax({
                    url: '/file/' + common_1.Common.getProjectName() + '?expression=' + originalText,
                    success: function (filesMatched) {
                        var response = [];
                        for (var i = 0; i < filesMatched.length; i++) {
                            var fileMatch = filesMatched[i];
                            var typeEntry = {
                                resource: fileMatch.resource,
                                path: fileMatch.path,
                                name: fileMatch.name,
                                project: common_1.Common.getProjectName()
                            };
                            response.push(fileMatch);
                        }
                        onComplete(response, originalText);
                    },
                    async: true
                });
            }
            else {
                onComplete([], originalText);
            }
        }
        function openTerminal(resourcePath) {
            if (tree_1.FileTree.isResourceFolder(resourcePath.getFilePath())) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    resource: resourcePath.getFilePath(),
                    terminal: true
                };
                socket_1.EventBus.sendEvent("EXPLORE", message);
            }
        }
        Command.openTerminal = openTerminal;
        function exploreDirectory(resourcePath) {
            if (tree_1.FileTree.isResourceFolder(resourcePath.getFilePath())) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    resource: resourcePath.getFilePath(),
                    terminal: false
                };
                socket_1.EventBus.sendEvent("EXPLORE", message);
            }
        }
        Command.exploreDirectory = exploreDirectory;
        function folderExpand(resourcePath) {
            var message = {
                project: common_1.Common.getProjectName(),
                folder: resourcePath
            };
            socket_1.EventBus.sendEvent("FOLDER_EXPAND", message);
        }
        Command.folderExpand = folderExpand;
        function folderCollapse(resourcePath) {
            var message = {
                project: common_1.Common.getProjectName(),
                folder: resourcePath
            };
            socket_1.EventBus.sendEvent("FOLDER_COLLAPSE", message);
        }
        Command.folderCollapse = folderCollapse;
        function pingProcess() {
            if (socket_1.EventBus.isSocketOpen()) {
                socket_1.EventBus.sendEvent("PING", common_1.Common.getProjectName());
            }
        }
        Command.pingProcess = pingProcess;
        function uploadFileTo(fileName, uploadToPath, encodedFile) {
            var destinationPath = tree_1.FileTree.createResourcePath(uploadToPath);
            var toPath = tree_1.FileTree.cleanResourcePath(destinationPath.getFilePath() + "/" + fileName);
            console.log("source: " + fileName + " destination: " + toPath);
            var message = {
                project: common_1.Common.getProjectName(),
                name: fileName,
                to: toPath,
                data: encodedFile,
                dragAndDrop: true
            };
            socket_1.EventBus.sendEvent("UPLOAD", message);
        }
        Command.uploadFileTo = uploadFileTo;
        function isDragAndDropFilePossible(fileToMove, moveTo) {
            //return moveTo.folder; // only move files and folders to different folders
            return true;
        }
        Command.isDragAndDropFilePossible = isDragAndDropFilePossible;
        function dragAndDropFile(fileToMove, moveTo) {
            if (isDragAndDropFilePossible(fileToMove, moveTo)) {
                var originalPath = fileToMove.getResource();
                var destinationPath = moveTo.getResource();
                var fromPath = tree_1.FileTree.cleanResourcePath(originalPath.getFilePath());
                var toPath = tree_1.FileTree.cleanResourcePath(destinationPath.getFilePath() + "/" + originalPath.getFileName());
                console.log("source: " + fromPath + " destination: " + toPath);
                var message = {
                    project: common_1.Common.getProjectName(),
                    from: fromPath,
                    to: toPath,
                    dragAndDrop: true
                };
                socket_1.EventBus.sendEvent("RENAME", message);
                if (fileToMove.isFolder()) {
                    var children = fileToMove.getChildren();
                    for (var i = 0; i < children.length; i++) {
                        var oldChildPath = children[i];
                        var newChildPath = common_1.Common.stringReplaceText(oldChildPath, fromPath, toPath);
                        project_1.Project.renameEditorTab(tree_1.FileTree.createResourcePath(oldChildPath), tree_1.FileTree.createResourcePath(newChildPath)); // rename tabs if open
                    }
                }
                else {
                    project_1.Project.renameEditorTab(originalPath, tree_1.FileTree.createResourcePath(toPath)); // rename tabs if open
                }
            }
        }
        Command.dragAndDropFile = dragAndDropFile;
        function renameFile(resourcePath) {
            var originalFile = resourcePath.getFilePath();
            dialog_1.DialogBuilder.renameFileTreeDialog(resourcePath, true, function (resourceDetails) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    from: originalFile,
                    to: resourceDetails.getFilePath(),
                    dragAndDrop: false
                };
                socket_1.EventBus.sendEvent("RENAME", message);
                project_1.Project.renameEditorTab(resourcePath, resourceDetails); // rename tabs if open
            });
        }
        Command.renameFile = renameFile;
        function renameDirectory(resourcePath) {
            var originalPath = resourcePath.getFilePath();
            var directoryPath = tree_1.FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
            dialog_1.DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function (resourceDetails) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    from: originalPath,
                    to: resourceDetails.getFilePath()
                };
                socket_1.EventBus.sendEvent("RENAME", message);
            });
        }
        Command.renameDirectory = renameDirectory;
        function newFile(resourcePath) {
            dialog_1.DialogBuilder.newFileTreeDialog(resourcePath, true, function (resourceDetails) {
                if (!tree_1.FileTree.isResourceFolder(resourceDetails.getFilePath())) {
                    var message = {
                        project: common_1.Common.getProjectName(),
                        resource: resourceDetails.getFilePath(),
                        source: "",
                        directory: false,
                        create: true
                    };
                    console_1.ProcessConsole.clearConsole();
                    socket_1.EventBus.sendEvent("SAVE", message);
                    var modificationTime = new Date().getTime();
                    var fileResource = new explorer_1.FileResource(resourceDetails, null, modificationTime, "", null, false, false);
                    editor_1.FileEditor.updateEditor(fileResource);
                }
            });
        }
        Command.newFile = newFile;
        function newDirectory(resourcePath) {
            dialog_1.DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function (resourceDetails) {
                if (tree_1.FileTree.isResourceFolder(resourceDetails.getFilePath())) {
                    var message = {
                        project: common_1.Common.getProjectName(),
                        resource: resourceDetails.getFilePath(),
                        source: "",
                        directory: true,
                        create: true
                    };
                    console_1.ProcessConsole.clearConsole();
                    socket_1.EventBus.sendEvent("SAVE", message);
                }
            });
        }
        Command.newDirectory = newDirectory;
        function saveFile() {
            saveFileWithAction(true, function (functionToExecuteAfterSave) {
                functionToExecuteAfterSave();
            });
        }
        Command.saveFile = saveFile;
        function saveFileWithAction(update, saveCallback) {
            var editorState = editor_1.FileEditor.currentEditorState();
            if (editorState.getResource() == null) {
                dialog_1.DialogBuilder.openTreeDialog(null, false, function (resourceDetails) {
                    var saveFunction = saveEditor(update);
                    saveCallback(saveFunction);
                });
            }
            else {
                if (editor_1.FileEditor.isEditorChanged()) {
                    // XXX don't prompt
                    //DialogBuilder.openTreeDialog(editorState.getResource(), true, function(resourceDetails: FilePath) {
                    var saveFunction = saveEditor(update);
                    saveCallback(saveFunction);
                }
                else {
                    console_1.ProcessConsole.clearConsole();
                    saveCallback(function () { });
                }
            }
        }
        function saveEditor(update) {
            var editorState = editor_1.FileEditor.currentEditorState();
            var editorPath = editorState.getResource();
            if (editorPath != null) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    resource: editorPath.getFilePath(),
                    source: editorState.getSource(),
                    directory: false,
                    create: false
                };
                console_1.ProcessConsole.clearConsole();
                socket_1.EventBus.sendEvent("SAVE", message);
                if (update) {
                    return function () {
                        var modificationTime = new Date().getTime();
                        var fileResource = new explorer_1.FileResource(editorPath, null, modificationTime, editorState.getSource(), null, false, false);
                        editor_1.FileEditor.updateEditor(fileResource);
                    };
                }
            }
            return function () { };
        }
        function saveEditorOnClose(editorText, editorResource) {
            if (editorResource != null && editorResource.getResourcePath()) {
                dialog_1.DialogBuilder.openTreeDialog(editorResource, true, function (resourceDetails) {
                    var message = {
                        project: common_1.Common.getProjectName(),
                        resource: editorResource.getFilePath(),
                        source: editorText,
                        directory: false,
                        create: false
                    };
                    //ProcessConsole.clearConsole();
                    socket_1.EventBus.sendEvent("SAVE", message);
                    editor_1.FileEditor.clearSavedEditorBuffer(editorResource.getResourcePath()); // make sure its synce
                }, function (resourceDetails) {
                    // file was not saved
                    editor_1.FileEditor.clearSavedEditorBuffer(editorResource.getResourcePath());
                });
            }
        }
        Command.saveEditorOnClose = saveEditorOnClose;
        function deleteFile(resourceDetails) {
            var editorState = editor_1.FileEditor.currentEditorState();
            if (resourceDetails == null && editorState.getResource() != null) {
                resourceDetails = editorState.getResource();
            }
            if (resourceDetails != null) {
                var message = "Delete resource " + resourceDetails.getFilePath();
                alert_1.Alerts.createConfirmAlert("Delete File", message, "Delete", "Cancel", function () {
                    var message = {
                        project: common_1.Common.getProjectName(),
                        resource: resourceDetails.getFilePath()
                    };
                    console_1.ProcessConsole.clearConsole();
                    socket_1.EventBus.sendEvent("DELETE", message);
                    project_1.Project.deleteEditorTab(resourceDetails.getResourcePath()); // rename tabs if open
                }, function () { });
            }
        }
        Command.deleteFile = deleteFile;
        function deleteDirectory(resourceDetails) {
            if (resourceDetails != null) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    resource: resourceDetails.getFilePath()
                };
                console_1.ProcessConsole.clearConsole();
                socket_1.EventBus.sendEvent("DELETE", message);
            }
        }
        Command.deleteDirectory = deleteDirectory;
        function createArchive(savePath, mainScript) {
            dialog_1.DialogBuilder.createArchiveTreeDialog(savePath, function (resourceDetails) {
                var message = {
                    project: common_1.Common.getProjectName(),
                    resource: mainScript.getProjectPath(),
                    archive: resourceDetails.getProjectPath()
                };
                socket_1.EventBus.sendEvent("CREATE_ARCHIVE", message);
            });
        }
        Command.createArchive = createArchive;
        function runScript() {
            executeScript(false);
        }
        Command.runScript = runScript;
        function debugScript() {
            executeScript(true);
        }
        Command.debugScript = debugScript;
        function executeScript(debug) {
            var saveFunction = function (functionToExecuteAfterSave) {
                setTimeout(function () {
                    var delayFunction = function () {
                        setTimeout(function () {
                            editor_1.FileEditor.focusEditor();
                            functionToExecuteAfterSave();
                        }, 1);
                    };
                    if (debug) {
                        alert_1.Alerts.createDebugPromptAlert("Debug", "Enter arguments", "Debug", "Cancel", function (inputArguments) {
                            executeScriptWithArguments(true, inputArguments);
                            delayFunction();
                        }, function (inputArguments) {
                            delayFunction();
                        });
                    }
                    else {
                        alert_1.Alerts.createRunPromptAlert("Run", "Enter arguments", "Run", "Cancel", function (inputArguments) {
                            executeScriptWithArguments(false, inputArguments);
                            delayFunction();
                        }, function (inputArguments) {
                            delayFunction();
                        });
                    }
                }, 1);
            };
            saveFileWithAction(true, saveFunction); // save editor
        }
        function executeScriptWithArguments(debug, inputArguments) {
            var editorState = editor_1.FileEditor.currentEditorState();
            var argumentArray = inputArguments.split(/[ ]+/);
            var message = {
                breakpoints: editorState.getBreakpoints(),
                arguments: argumentArray,
                project: common_1.Common.getProjectName(),
                resource: editorState.getResource().getFilePath(),
                source: editorState.getSource(),
                debug: debug ? true : false
            };
            socket_1.EventBus.sendEvent("EXECUTE", message);
        }
        ;
        function attachRemoteDebugger() {
            if (socket_1.EventBus.isSocketOpen()) {
                alert_1.Alerts.createRemoteDebugPromptAlert("Remote Debug", "Enter <host>:<port>", "Attach", "Cancel", function (hostAndPort) {
                    var message = {
                        project: common_1.Common.getProjectName(),
                        address: hostAndPort
                    };
                    socket_1.EventBus.sendEvent("REMOTE_DEBUG", message);
                });
            }
        }
        Command.attachRemoteDebugger = attachRemoteDebugger;
        function updateScriptBreakpoints() {
            var editorState = editor_1.FileEditor.currentEditorState();
            var message = {
                breakpoints: editorState.getBreakpoints(),
                project: common_1.Common.getProjectName()
            };
            socket_1.EventBus.sendEvent("BREAKPOINTS", message);
        }
        Command.updateScriptBreakpoints = updateScriptBreakpoints;
        function stepOverScript() {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    type: "STEP_OVER"
                };
                editor_1.FileEditor.clearEditorHighlights();
                socket_1.EventBus.sendEvent("STEP", message);
            }
        }
        Command.stepOverScript = stepOverScript;
        function stepInScript() {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    type: "STEP_IN"
                };
                editor_1.FileEditor.clearEditorHighlights();
                socket_1.EventBus.sendEvent("STEP", message);
            }
        }
        Command.stepInScript = stepInScript;
        function stepOutScript() {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    type: "STEP_OUT"
                };
                editor_1.FileEditor.clearEditorHighlights();
                socket_1.EventBus.sendEvent("STEP", message);
            }
        }
        Command.stepOutScript = stepOutScript;
        function resumeScript() {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    type: "RUN"
                };
                editor_1.FileEditor.clearEditorHighlights();
                socket_1.EventBus.sendEvent("STEP", message);
            }
        }
        Command.resumeScript = resumeScript;
        function stopScript() {
            socket_1.EventBus.sendEvent("STOP");
        }
        Command.stopScript = stopScript;
        function browseScriptVariables(variables) {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    expand: variables
                };
                socket_1.EventBus.sendEvent("BROWSE", message);
            }
        }
        Command.browseScriptVariables = browseScriptVariables;
        function browseScriptEvaluation(variables, expression, refresh) {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var message = {
                    thread: threadScope.getThread(),
                    expression: expression,
                    expand: variables,
                    refresh: refresh
                };
                socket_1.EventBus.sendEvent("EVALUATE", message);
            }
        }
        Command.browseScriptEvaluation = browseScriptEvaluation;
        function attachProcess(process) {
            var statusFocus = debug_1.DebugManager.currentStatusFocus(); // what is the current focus
            var editorState = editor_1.FileEditor.currentEditorState();
            var message = {
                process: process,
                breakpoints: editorState.getBreakpoints(),
                project: common_1.Common.getProjectName(),
                focus: statusFocus != process // toggle the focus
            };
            socket_1.EventBus.sendEvent("ATTACH", message); // attach to process
        }
        Command.attachProcess = attachProcess;
        function switchLayout() {
            var debugToggle = ";debug";
            var locationPath = window.document.location.pathname;
            var locationHash = window.document.location.hash;
            var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
            if (debug) {
                var remainingPath = locationPath.substring(0, locationPath.length - debugToggle.length);
                document.location = remainingPath + locationHash;
            }
            else {
                document.location = locationPath + debugToggle + locationHash;
            }
        }
        Command.switchLayout = switchLayout;
        function updateDisplay(displayInfo) {
            if (socket_1.EventBus.isSocketOpen()) {
                socket_1.EventBus.sendEvent("DISPLAY_UPDATE", displayInfo); // update and save display
            }
        }
        Command.updateDisplay = updateDisplay;
        function evaluateExpression() {
            var threadScope = threads_1.ThreadManager.focusedThread();
            if (threadScope != null) {
                var selectedText = editor_1.FileEditor.getSelectedText();
                dialog_1.DialogBuilder.evaluateExpressionDialog(selectedText);
            }
        }
        Command.evaluateExpression = evaluateExpression;
        function refreshScreen() {
            setTimeout(function () {
                location.reload();
            }, 10);
        }
        Command.refreshScreen = refreshScreen;
        function switchProject() {
            document.location = "/";
        }
        Command.switchProject = switchProject;
    })(Command = exports.Command || (exports.Command = {}));
});
//ModuleSystem.registerModule("commands", "Commands module: commands.js", null, null, [ "common", "editor", "tree", "threads" ]); 
