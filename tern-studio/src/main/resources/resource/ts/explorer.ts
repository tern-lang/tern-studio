import * as $ from "jquery"
import * as w2ui from "w2ui"
import {ace} from "ace"
//import {saveAs} from "filesaver"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree, FilePath} from "tree"
import {FileEditor} from "editor"
import {Command} from "commands"
import {Alerts} from "alert"

export class FileResource {
   
   private _resourcePath: FilePath;
   private _contentType: string;
   private _lastModified: number;
   private _fileContent: string;
   private _downloadURL: string;
   private _isHistorical: boolean;
   private _isError: boolean;
   
   constructor(resourcePath: FilePath, contentType: string, lastModified: number, fileContent: string, downloadURL: string, isHistorical: boolean, isError: boolean) {
      this._resourcePath = resourcePath;
      this._contentType = contentType;
      this._lastModified = lastModified;
      this._fileContent = fileContent;
      this._downloadURL = downloadURL;
      this._isHistorical = isHistorical;
      this._isError = isError;
   }
   
   public getFileContent(): string {
      if(this._fileContent) {
         var filePath = this._resourcePath.getResourcePath();
         var filePathToken = filePath.toLowerCase();
         
         if(Common.stringEndsWith(filePathToken, ".json")) {
            try {
                var jsonObject = JSON.parse(this._fileContent);
                return JSON.stringify(jsonObject, null, 3);
            }catch(e) {
              return this._fileContent;
            }
         }
         return this._fileContent;
      }
      return "";
   }
   
   public getResourcePath(): FilePath {
      return this._resourcePath;
   }
   
   public getContentType(): string {
      return this._contentType;
   }

   public getDownloadURL(): string {
      return this._downloadURL;
   }     
   
   public getTimeStamp(): string {
      return Common.formatTimeMillis(this._lastModified);
   }
   
   public getLastModified(): number {
      return this._lastModified;
   }
   
   public getFileLength(): number{
      return this._fileContent ? this._fileContent.length : -1;
   }
   
   public isHistorical(): boolean {
      return this._isHistorical;
   }
   
   public isError(): boolean {
      return this._isError;
   }
}
 
export module FileExplorer {
   
   class MockResponseHeader {
      
      private _contentType: string;
      private _lastModified: number;
   
      constructor(contentType: string, lastModified: number) {
         this._contentType = contentType;
         this._lastModified = lastModified;
      }
      
      public getResponseHeader(name: string) {
         var key: string = name.toLowerCase();
      
         if(key == "content-type") {
            return this._contentType;
         }
         if(key == "last-modified") {
            return this._lastModified;
         }
         return null;
      }
   }
   
   export function showTree() {
      reloadTreeAtRoot();
      EventBus.createRoute("RELOAD_TREE", reloadTree);
      EventBus.createRoute("OPEN", openFileNotification);   
   }
   
   function reloadTree(socket, type, text) {
      reloadTreeAtRoot();
   }
   
   function openFileNotification(socket, type, text) {
      var message = JSON.parse(text);
      var resource: string = message.resource;
      
      if(resource) {
         openTreeFile(resource, function(){});
      }
   }
   
   function reloadTreeAtRoot() {
      FileTree.createTree("/" + Common.getProjectName(), "explorer", "explorerTree", "/.", false, handleTreeMenu, function(event, data) {
         if (!data.node.isFolder()) {
         	Command.openTreeFile(data.node.tooltip);
         }
      });
   }
   
   export function showAsTreeFile(resourcePath: string, source: string, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      var header = new MockResponseHeader("text/plain", new Date().getTime());
      var responseObject: FileResource = parseResponseMessage(resourcePath, resourcePath, header, source, false, false);
      
      handleOpenTreeFile(responseObject, afterLoad);
   }
   
   export function openTreeFile(resourcePath: string, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      
      if(isJsonXmlOrJavascript(filePath)) { // is it json or javascript
         //var type = header.getResponseHeader("content-type");
         
         $.ajax({
            url: resourcePath, 
            type: "get",
            dataType: 'text',
            success: function(response, status, header) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, resourcePath, header, response, false, false);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function(response) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, resourcePath, null, response, false, true);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      } else {
         $.ajax({
            url: resourcePath,
            type: "get",
            success: function(response, status, header) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, resourcePath, header, response, false, false);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function(response) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, resourcePath, null, response, false, true);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      }
   }
   
   export function openTreeHistoryFile(resourcePath, timeStamp, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      var backupResourcePath = resourcePath.replace(/^\/resource/i, "/history");
      //var backupUrl = backupResourcePath + "?time=" + timeStamp;
      
      if(isJsonXmlOrJavascript(filePath)) { // is it json or javascript
         var downloadURL = backupResourcePath + "?time=" + timeStamp;
         $.ajax({
            url: downloadURL,
            type: "get",
            dataType: 'text',
            success: function (response, status, header) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, downloadURL, header, response, true, false);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function (response) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, downloadURL, null, response, true, true);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      } else {
         var downloadURL = backupResourcePath + "?time=" + timeStamp;
         $.ajax({
            url: downloadURL,
            type: "get",            
            success: function (response, status, header) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, downloadURL, header, response, true, false);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function (response) {
               var responseObject: FileResource = parseResponseMessage(resourcePath, downloadURL, null, response, true, true);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      }
   }
   
   function parseResponseMessage(resourcePath: string, downloadURL: string, responseHeader: any, responseEntity: any, isHistory: boolean, isError: boolean): FileResource {
      var filePath: FilePath = FileTree.createResourcePath(resourcePath);
      var lastModified: number = new Date().getTime();
      var contentType: string = "application/octet-stream";
      
      if(responseHeader && responseEntity != null) {
         var contentTypeHeader = responseHeader.getResponseHeader("content-type");
         var lastModifiedHeader = responseHeader.getResponseHeader("last-modified");
         
         if(lastModifiedHeader) {
            lastModified = new Date(lastModifiedHeader).getTime();
         }
         if(contentTypeHeader) {
            contentType = contentTypeHeader;
         }
         return new FileResource(filePath, contentType, lastModified, responseEntity, downloadURL, isHistory, isError);
      }
      return new FileResource(filePath, "text/plain", lastModified, "// Count not find " + resourcePath, downloadURL, isHistory, isError);
   }
   
   function handleOpenTreeFile(responseObject: FileResource, afterLoad) {
      //console.log(responseObject);
      
      if(isImageFileType(responseObject.getContentType())) {
         handleOpenFileInNewTab(responseObject.getDownloadURL());
      } else if(isBinaryFileType(responseObject.getContentType())) {
         handleDownloadFile(responseObject.getDownloadURL());
      } else {
         FileEditor.updateEditor(responseObject);
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
      return Common.stringEndsWith(filePath, ".json") || 
              Common.stringEndsWith(filePath, ".js") || 
              Common.stringEndsWith(filePath, ".xml") ||
              Common.stringEndsWith(filePath, ".project") ||
              Common.stringEndsWith(filePath, ".classpath") ||
              Common.stringEndsWith(filePath, ".index");
   }
   
   function isImageFileType(contentType) {
      if(contentType) {
         if(Common.stringStartsWith(contentType, "image")) {
            return true;
         }
      }
      return false;
   }
   
   function isBinaryFileType(contentType) {
      if(contentType) {
         if(contentType == "application/json") {
            return false;
         }
         if(contentType == "application/x-javascript") {
            return false;
         }
         if(contentType == "application/javascript") {
            return false;
         }
         if(Common.stringStartsWith(contentType, "application")) {
            return true;
         }
         if(Common.stringStartsWith(contentType, "image")) {
            return true;
         }
         if(Common.stringStartsWith(contentType, "text")) {
            return false;
         }
         return true; // unknown
      }
      return false;
   }
   
   function handleTreeMenu(resourcePath: FilePath, commandName, elementId, isDirectory) {
      if(commandName == "runScript") {
         openTreeFile(resourcePath.getResourcePath(), function(){
            Command.runScript();
         });
      } else if(commandName == "debugScript") {
         openTreeFile(resourcePath.getResourcePath(), function(){
            Command.debugScript();
         });
      } else if(commandName == "createArchive") {
         var savePath: FilePath = FileTree.createResourcePath("/" + Common.getProjectName() + ".jar");
         Command.createArchive(savePath, resourcePath);      
      }else if(commandName == "newFile") {
         Command.newFile(resourcePath);
      }else if(commandName == "newDirectory") {
         Command.newDirectory(resourcePath);
      }else if(commandName == "exploreDirectory") {
         Command.exploreDirectory(resourcePath);
      }else if(commandName == "openTerminal") {
         Command.openTerminal(resourcePath);
      }else if(commandName == "renameFile") {
         if(isDirectory) {
            Command.renameDirectory(resourcePath);
         } else {
            Command.renameFile(resourcePath);
         }
      }else if(commandName == "saveFile") {
         openTreeFile(resourcePath.getResourcePath(), function(){
            Command.saveFile();
         });
      }else if(commandName == "deleteFile") {
         if(FileTree.isResourceFolder(resourcePath.getResourcePath())) {
            Command.deleteDirectory(resourcePath);
         } else {
            Command.deleteFile(resourcePath);
         }
      }
   }
}
//ModuleSystem.registerModule("explorer", "Explorer module: explorer.js", null, FileExplorer.showTree, [ "common", "spinner", "tree", "commands" ]);