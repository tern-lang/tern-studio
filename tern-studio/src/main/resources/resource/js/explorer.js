define(["require", "exports", "jquery", "common", "socket", "tree", "editor", "commands"], function (require, exports, $, common_1, socket_1, tree_1, editor_1, commands_1) {
    "use strict";
    var FileResource = (function () {
        function FileResource(resourcePath, contentType, lastModified, fileContent, downloadURL, isHistorical, isError) {
            this._resourcePath = resourcePath;
            this._contentType = contentType;
            this._lastModified = lastModified;
            this._fileContent = fileContent;
            this._downloadURL = downloadURL;
            this._isHistorical = isHistorical;
            this._isError = isError;
        }
        FileResource.prototype.getFileContent = function () {
            if (this._fileContent) {
                var filePath = this._resourcePath.getResourcePath();
                var filePathToken = filePath.toLowerCase();
                if (common_1.Common.stringEndsWith(filePathToken, ".json")) {
                    try {
                        var jsonObject = JSON.parse(this._fileContent);
                        return JSON.stringify(jsonObject, null, 3);
                    }
                    catch (e) {
                        return this._fileContent;
                    }
                }
                return this._fileContent;
            }
            return "";
        };
        FileResource.prototype.getResourcePath = function () {
            return this._resourcePath;
        };
        FileResource.prototype.getContentType = function () {
            return this._contentType;
        };
        FileResource.prototype.getDownloadURL = function () {
            return this._downloadURL;
        };
        FileResource.prototype.getTimeStamp = function () {
            return common_1.Common.formatTimeMillis(this._lastModified);
        };
        FileResource.prototype.getLastModified = function () {
            return this._lastModified;
        };
        FileResource.prototype.getFileLength = function () {
            return this._fileContent ? this._fileContent.length : -1;
        };
        FileResource.prototype.isHistorical = function () {
            return this._isHistorical;
        };
        FileResource.prototype.isError = function () {
            return this._isError;
        };
        return FileResource;
    }());
    exports.FileResource = FileResource;
    var FileExplorer;
    (function (FileExplorer) {
        var MockResponseHeader = (function () {
            function MockResponseHeader(contentType, lastModified) {
                this._contentType = contentType;
                this._lastModified = lastModified;
            }
            MockResponseHeader.prototype.getResponseHeader = function (name) {
                var key = name.toLowerCase();
                if (key == "content-type") {
                    return this._contentType;
                }
                if (key == "last-modified") {
                    return this._lastModified;
                }
                return null;
            };
            return MockResponseHeader;
        }());
        function showTree() {
            reloadTreeAtRoot();
            socket_1.EventBus.createRoute("RELOAD_TREE", reloadTree);
            socket_1.EventBus.createRoute("OPEN", openFileNotification);
        }
        FileExplorer.showTree = showTree;
        function reloadTree(socket, type, text) {
            reloadTreeAtRoot();
        }
        function openFileNotification(socket, type, text) {
            var message = JSON.parse(text);
            var resource = message.resource;
            if (resource) {
                openTreeFile(resource, function () { });
            }
        }
        function reloadTreeAtRoot() {
            tree_1.FileTree.createTree("/" + common_1.Common.getProjectName(), "explorer", "explorerTree", "/.", false, handleTreeMenu, function (event, data) {
                if (!data.node.isFolder()) {
                    commands_1.Command.openTreeFile(data.node.tooltip);
                }
            });
        }
        function showAsTreeFile(resourcePath, source, afterLoad) {
            var filePath = resourcePath.toLowerCase();
            var header = new MockResponseHeader("text/plain", new Date().getTime());
            var responseObject = parseResponseMessage(resourcePath, resourcePath, header, source, false, false);
            handleOpenTreeFile(responseObject, afterLoad);
        }
        FileExplorer.showAsTreeFile = showAsTreeFile;
        function openTreeFile(resourcePath, afterLoad) {
            var filePath = resourcePath.toLowerCase();
            if (isJsonXmlOrJavascript(filePath)) {
                //var type = header.getResponseHeader("content-type");
                $.ajax({
                    url: resourcePath,
                    type: "get",
                    dataType: 'text',
                    success: function (response, status, header) {
                        var responseObject = parseResponseMessage(resourcePath, resourcePath, header, response, false, false);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    error: function (response) {
                        var responseObject = parseResponseMessage(resourcePath, resourcePath, null, response, false, true);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    async: false
                });
            }
            else {
                $.ajax({
                    url: resourcePath,
                    type: "get",
                    success: function (response, status, header) {
                        var responseObject = parseResponseMessage(resourcePath, resourcePath, header, response, false, false);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    error: function (response) {
                        var responseObject = parseResponseMessage(resourcePath, resourcePath, null, response, false, true);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    async: false
                });
            }
        }
        FileExplorer.openTreeFile = openTreeFile;
        function openTreeHistoryFile(resourcePath, timeStamp, afterLoad) {
            var filePath = resourcePath.toLowerCase();
            var backupResourcePath = resourcePath.replace(/^\/resource/i, "/history");
            //var backupUrl = backupResourcePath + "?time=" + timeStamp;
            if (isJsonXmlOrJavascript(filePath)) {
                var downloadURL = backupResourcePath + "?time=" + timeStamp;
                $.ajax({
                    url: downloadURL,
                    type: "get",
                    dataType: 'text',
                    success: function (response, status, header) {
                        var responseObject = parseResponseMessage(resourcePath, downloadURL, header, response, true, false);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    error: function (response) {
                        var responseObject = parseResponseMessage(resourcePath, downloadURL, null, response, true, true);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    async: false
                });
            }
            else {
                var downloadURL = backupResourcePath + "?time=" + timeStamp;
                $.ajax({
                    url: downloadURL,
                    type: "get",
                    success: function (response, status, header) {
                        var responseObject = parseResponseMessage(resourcePath, downloadURL, header, response, true, false);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    error: function (response) {
                        var responseObject = parseResponseMessage(resourcePath, downloadURL, null, response, true, true);
                        handleOpenTreeFile(responseObject, afterLoad);
                    },
                    async: false
                });
            }
        }
        FileExplorer.openTreeHistoryFile = openTreeHistoryFile;
        function parseResponseMessage(resourcePath, downloadURL, responseHeader, responseEntity, isHistory, isError) {
            var filePath = tree_1.FileTree.createResourcePath(resourcePath);
            var lastModified = new Date().getTime();
            var contentType = "application/octet-stream";
            if (responseHeader && responseEntity != null) {
                var contentTypeHeader = responseHeader.getResponseHeader("content-type");
                var lastModifiedHeader = responseHeader.getResponseHeader("last-modified");
                if (lastModifiedHeader) {
                    lastModified = new Date(lastModifiedHeader).getTime();
                }
                if (contentTypeHeader) {
                    contentType = contentTypeHeader;
                }
                return new FileResource(filePath, contentType, lastModified, responseEntity, downloadURL, isHistory, isError);
            }
            return new FileResource(filePath, "text/plain", lastModified, "// Count not find " + resourcePath, downloadURL, isHistory, isError);
        }
        function handleOpenTreeFile(responseObject, afterLoad) {
            //console.log(responseObject);
            if (isImageFileType(responseObject.getContentType())) {
                handleOpenFileInNewTab(responseObject.getDownloadURL());
            }
            else if (isBinaryFileType(responseObject.getContentType())) {
                handleDownloadFile(responseObject.getDownloadURL());
            }
            else {
                editor_1.FileEditor.updateEditor(responseObject);
            }
            afterLoad();
        }
        function handleOpenFileInNewTab(downloadURL) {
            var newTab = window.open(downloadURL, '_blank');
            newTab.focus();
        }
        function handleDownloadFile(downloadURL) {
            window.location.href = downloadURL;
        }
        function isJsonXmlOrJavascript(filePath) {
            return common_1.Common.stringEndsWith(filePath, ".json") ||
                common_1.Common.stringEndsWith(filePath, ".js") ||
                common_1.Common.stringEndsWith(filePath, ".xml") ||
                common_1.Common.stringEndsWith(filePath, ".project") ||
                common_1.Common.stringEndsWith(filePath, ".classpath") ||
                common_1.Common.stringEndsWith(filePath, ".index");
        }
        function isImageFileType(contentType) {
            if (contentType) {
                if (common_1.Common.stringStartsWith(contentType, "image")) {
                    return true;
                }
            }
            return false;
        }
        function isBinaryFileType(contentType) {
            if (contentType) {
                if (contentType == "application/json") {
                    return false;
                }
                if (contentType == "application/x-javascript") {
                    return false;
                }
                if (contentType == "application/javascript") {
                    return false;
                }
                if (common_1.Common.stringStartsWith(contentType, "application")) {
                    return true;
                }
                if (common_1.Common.stringStartsWith(contentType, "image")) {
                    return true;
                }
                if (common_1.Common.stringStartsWith(contentType, "text")) {
                    return false;
                }
                return true; // unknown
            }
            return false;
        }
        function handleTreeMenu(resourcePath, commandName, elementId, isDirectory) {
            if (commandName == "runScript") {
                openTreeFile(resourcePath.getResourcePath(), function () {
                    commands_1.Command.runScript();
                });
            }
            else if (commandName == "debugScript") {
                openTreeFile(resourcePath.getResourcePath(), function () {
                    commands_1.Command.debugScript();
                });
            }
            else if (commandName == "createArchive") {
                var savePath = tree_1.FileTree.createResourcePath("/" + common_1.Common.getProjectName() + ".jar");
                commands_1.Command.createArchive(savePath, resourcePath);
            }
            else if (commandName == "newFile") {
                commands_1.Command.newFile(resourcePath);
            }
            else if (commandName == "newDirectory") {
                commands_1.Command.newDirectory(resourcePath);
            }
            else if (commandName == "exploreDirectory") {
                commands_1.Command.exploreDirectory(resourcePath);
            }
            else if (commandName == "openTerminal") {
                commands_1.Command.openTerminal(resourcePath);
            }
            else if (commandName == "renameFile") {
                if (isDirectory) {
                    commands_1.Command.renameDirectory(resourcePath);
                }
                else {
                    commands_1.Command.renameFile(resourcePath);
                }
            }
            else if (commandName == "saveFile") {
                openTreeFile(resourcePath.getResourcePath(), function () {
                    commands_1.Command.saveFile();
                });
            }
            else if (commandName == "deleteFile") {
                if (tree_1.FileTree.isResourceFolder(resourcePath.getResourcePath())) {
                    commands_1.Command.deleteDirectory(resourcePath);
                }
                else {
                    commands_1.Command.deleteFile(resourcePath);
                }
            }
        }
    })(FileExplorer = exports.FileExplorer || (exports.FileExplorer = {}));
});
//ModuleSystem.registerModule("explorer", "Explorer module: explorer.js", null, FileExplorer.showTree, [ "common", "spinner", "tree", "commands" ]); 
