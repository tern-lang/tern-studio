import * as $ from "jquery"
import * as fancytree from "fancytree"
import {Common} from "common"
import {Command} from "commands"

export class FilePath {
   
   private _resourcePath: string; // /resource/<project>/blah/script.snap
   private _projectPath: string; // /blah/script.snap
   private _projectDirectory: string; // /blah
   private _filePath: string; // /blah/script.snap
   private _fileName: string; // script.snap
   private _fileDirectory: string; // /blah
   private _originalPath: string;
   
   constructor(resourcePath: string, projectPath: string, projectDirectory: string, filePath: string, fileName: string, fileDirectory: string, originalPath: string) {
      this._resourcePath = resourcePath;
      this._projectPath = projectPath;
      this._projectDirectory = projectDirectory;
      this._filePath = filePath;
      this._fileName = fileName;
      this._fileDirectory = fileDirectory;
      this._originalPath = originalPath;
   }
   
   public getResourcePath(): string {
      return this._resourcePath;
   }
   
   public getProjectPath(): string {
      return this._projectPath;
   }
   
   public getProjectDirectory(): string {
      return this._projectDirectory;
   }
   
   public getFilePath(): string {
      return this._filePath;
   }
   
   public getFileName(): string {
      return this._fileName;
   }
   
   public getFileDirectory(): string {
      return this._fileDirectory;
   }
   
   public getOriginalPath(): string {
      return this._originalPath;
   }
}

export class FileNode {

   private _resourcePath: FilePath;
   private _children: any;

   constructor(resourcePath: FilePath, children: any) {
      this._resourcePath = resourcePath;
      this._children = children;
   }

   public getResource(): FilePath {
      return this._resourcePath;
   }

   public getChildren(): any {
       return this._children;
   }

   public isFolder(): boolean {
      return this._children.length > 0;
   }
}

export module FileTree {
   
   export function createTree(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback) { // #explorer
      createTreeOfDepth(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback, 10000); // large random depth
   }
   
   export function createTreeOfDepth(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback, depth) { // #explorer
      $(document).ready(function() {
         var project = Common.getProjectName();
         var requestPath = '/tree' + treePath + "?id=" + id + "&folders=" + foldersOnly + "&depth=" + depth;
         
         if(expandPath != null) {
            requestPath += "&expand="+expandPath;
         }
         $.ajax({
            url: requestPath,
            success: function (response) {
               $('#' + element).html(response);
               showFancyTree(id, !foldersOnly, treeMenuHandler, clickCallback); // show the fancy tree
            },
            async: true
         });

      });
   }
   
   export function showTreeNode(id, treePath: FilePath) {
      if(id && treePath) {
         if(treePath.getResourcePath()) {
            showNodeAndScroll(id, treePath.getResourcePath());
            showNodeAndScroll(id, treePath.getResourcePath()); // do it twice
         }
      }
   }
   
   function showNodeAndScroll(treeId, nodeId) {
      var container = document.getElementById("browseParent");
      var tree = $("#" + treeId).fancytree("getTree");
      
      if(tree && (typeof tree.getNodeByKey === "function")) { // make sure the function exists
         var treeNode = tree.getNodeByKey(nodeId);
   
         if(treeNode) {
            if(treeNode.li && container) {
               if(!Common.isChildElementVisible(container, treeNode.li)) {
                  container.scrollTop = 0; // reset the scroll for better calculation
                  container.scrollTop = Common.calculateScrollOffset(container, treeNode.li);
               }
            }
            treeNode.setActive();
         }
      }
   }
   
   function showFancyTree(id, dragAndDrop, treeMenuHandler, clickCallback) {
       // using default options
       // https://github.com/mar10/fancytree/blob/master/demo/sample-events.html
       $('#' + id).fancytree({
         //autoScroll: true,
         //extensions: dragAndDrop ? ["dnd"] : [],
         click : clickCallback,
         expand: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderExpand(data.node.key);
               setTimeout(function() {
                  addTreeMenuHandler(id, treeMenuHandler);
                  addDragAndDropHandlers(id);
               }, 10);
            }
         },
         collapse: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderCollapse(data.node.key);
               setTimeout(function() {
                  addTreeMenuHandler(id, treeMenuHandler);
                  addDragAndDropHandlers(id);
               }, 10);
            }
         },
         init: function(event, data, flag) {
            addTreeMenuHandler(id, treeMenuHandler);
            addDragAndDropHandlers(id);
         }  
      });

   }
   
   function addDragAndDropHandlers(id) {
      var explorerTree = document.getElementById(id);
      var folders = Common.getElementsByClassName(explorerTree, 'fancytree-folder');
      
      for(var i = 0; i < folders.length; i++) {
         let child = folders[i];
         
         $(child).on("dragenter", function(event) {
            $(child).find('.fancytree-title').addClass("treeFolderDragOver");
         }).on("dragleave", function(event) {
            $(child).find('.fancytree-title').removeClass("treeFolderDragOver");
         }).on("drop", function (event) {
            var folderElement = $(child).find('.fancytree-title');
            var dataTransfer = event.target.dataTransfer || event.originalEvent.dataTransfer;
            var target = event.target || event.currentTarger;
            var dataPath = dataTransfer.getData("resource");
            var folderPath = $(folderElement).attr("title");
            
            $(folderElement).removeClass("treeFolderDragOver");
            event.stopPropagation();
            event.preventDefault();

           var toPath: FilePath = createResourcePath(folderPath);
           var toChildren: any =  findAllChildrenOf(explorerTree, toPath);
           var toNode: FileNode = new FileNode(toPath, toChildren);

            if(dataPath) {
               var fromBlob = JSON.parse(dataPath);
               var fromPath: FilePath = createResourcePath(fromBlob.resource);
               var fromChildren: any =  findAllChildrenOf(explorerTree, fromPath);
               var fromNode: FileNode = new FileNode(fromPath, fromChildren);
     
               handleNodeDroppedOverFolder(event, fromNode, toNode);
            }else {
               handleFileDroppedOverFolder(event, toNode);
            }
        }).on('dragover',function(event){
            event.preventDefault();
        });
        updateNodesAsDraggable(explorerTree);
      }  
   }

   function findAllChildrenOf(explorerTree, parentPath: FilePath) {
      var prefix = parentPath.getResourcePath();
      var tree = $(explorerTree).fancytree("getTree");
      var treeNodes = tree.findAll(function(node) {
          if(node.key != prefix) {
             return Common.stringStartsWith(node.key, prefix);
          }
          return false;
      });

      if(treeNodes && treeNodes.length > 0) {
          var children = [];

          for(var i = 0; i < treeNodes.length; i++) {
              children.push(treeNodes[i].key);
          }
          return children;
      }
      return [];
   }
   
   function updateNodesAsDraggable(nodeElement) {
      var childNodes = Common.getElementsByClassName(nodeElement, 'fancytree-node');
      
      for(var i = 0; i < childNodes.length; i++) {
         let childNode = childNodes[i];
         
         if(childNode){
            childNode.setAttribute("draggable", "true");
            $(childNode).on('dragstart',function(event){
               var dataTransfer = event.target.dataTransfer || event.originalEvent.dataTransfer
               var target = event.target || event.currentTarger;
               var titleNodes = Common.getElementsByClassName(childNode, 'fancytree-title');
               
               if(titleNodes && titleNodes.length > 0) {
                  var titleNode = titleNodes[0];
                  
                  dataTransfer.setData("resource", JSON.stringify({
                     resource: titleNode.getAttribute("title"),
                     folder: isTreeNodeFolder(target) // this does not work
                  }));
               }
            })
         }
      }  
   }
   
   function addTreeMenuHandler(id, treeMenuHandler) {
      if(treeMenuHandler != null) {
         $("#" + id).contextmenu({
              delegate: "span.fancytree-title",
              menu: [
                  {title: "&nbsp;New", uiIcon: "menu-new", children: [
                     {title: "&nbsp;File", cmd: "newFile", uiIcon: "menu-new"},
                     {title: "&nbsp;Directory", cmd: "newDirectory", uiIcon: "menu-new"}
                     ]},              
                  {title: "&nbsp;Save", cmd: "saveFile", uiIcon: "menu-save"}, 
                  {title: "&nbsp;Rename", cmd: "renameFile", uiIcon: "menu-rename"},                       
                  {title: "&nbsp;Delete", cmd: "deleteFile", uiIcon: "menu-trash", disabled: false },
                  {title: "&nbsp;Run", cmd: "runScript", uiIcon: "menu-run"},
                  {title: "&nbsp;Debug", cmd: "debugScript", uiIcon: "menu-debug"},
                  {title: "&nbsp;Export", cmd: "createArchive", uiIcon: "menu-archive"},                  
                  {title: "&nbsp;Explore", cmd: "exploreDirectory", uiIcon: "menu-explore"},
                  {title: "&nbsp;Terminal", cmd: "openTerminal", uiIcon: "menu-terminal"} //,               
                  //{title: "----"},
                  //{title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                  //{title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true }
                  ],
              beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                node.setActive();
                var $menu = ui.menu,
                $target = ui.target,
                extraData = ui.extraData; // passed when menu was opened by call to open()
  
                ui.menu.zIndex( $(event.target).zIndex() + 2000);
              },
              select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);

                if(node) {
                    var resourcePath = createResourcePath(node.tooltip);
                    var commandName = ui.cmd;
                    var elementId = ui.key;
                
                    treeMenuHandler(resourcePath, commandName, elementId, node.isFolder());
                }
              }
         });         
     }
   }

   function handleFileDroppedOverFolder(dropEvent, folderPath) {
      var droppedFiles = dropEvent.target.files || dropEvent.originalEvent.dataTransfer.files || dropEvent.dataTransfer.files;

      if(droppedFiles) {
         // process all File objects
         for (var i = 0; i < droppedFiles.length; i++) {
            var droppedFile = droppedFiles[i];
                
            if(isAdvancedFileUpload()) {
               console.log("file="+droppedFile.name+" folder=" + folderPath);
               
               var reader = new FileReader();
               
               reader.onload = function (event) {
                  var encodedFile = encodeFileArrayBufferAsBase64(event.target.result);
                  Command.uploadFileTo(droppedFile.name, folderPath, encodedFile);
               };
               reader.readAsArrayBuffer(droppedFile);
            } 
         }
      }
   }
   
   function handleNodeDroppedOverFolder(dropEvent, fromPath: FileNode, toPath: FileNode) {
      Command.dragAndDropFile(fromPath, toPath);
   }
   
   function isTreeNodeFolder(nodeElement) {
      var folders = $(nodeElement).filter('.fancytree-folder');
      if(folders) {
         return folders.length > 0;
      }
      return false;
   }
   
   function encodeFileArrayBufferAsBase64(fileAsArrayBuffer) {
      var binary = '';
      var bytes = new Uint8Array(fileAsArrayBuffer);
      var length = bytes.byteLength;
      for (var i = 0; i < length; i++) {
          binary += String.fromCharCode(bytes[ i ]);
      }
      return window.btoa(binary);
   }
   
   function isAdvancedFileUpload() {
      var div = document.createElement('div');
      return (('draggable' in div) || ('ondragstart' in div && 'ondrop' in div)) && 'FormData' in window && 'FileReader' in window;
   }
   
   export function isResourceFolder(path) {
      if(!Common.stringEndsWith(path, "/")) {
         var parts = path.split(".");
         
         if(path.length === 1 || (parts[0] === "" && parts.length === 2)) {
             return true;
         }
         var extension = parts.pop();
         var slash = extension.indexOf('/');
         
         return slash >= 0;
      }
      return true;
   }
   
   export function cleanResourcePath(path) {
      if(path != null) {
         var cleanPath = path.replace(/\/+/, "/").replace(/\.#/, ""); // replace // with /
         
         while(cleanPath.indexOf("//") != -1) {
            cleanPath = cleanPath.replace("//", "/"); // remove double slashes like /x/y//z.snap
         }
         if(Common.stringEndsWith(cleanPath, "/")) {
            cleanPath = cleanPath.substring(0,cleanPath.length-1);
         }
         return cleanPath;
      }
      return null;
   }
   
   export function createResourcePath(path: string): FilePath { 
      var resourcePathPrefix = "/resource/" + Common.getProjectName() + "/";
      var resourcePathRoot = "/resource/" + Common.getProjectName();
      
      while(path.indexOf("//") != -1) {
         path = path.replace("//", "/"); // remove double slashes like /x/y//z.snap
      }
      if(path == resourcePathRoot || path == resourcePathPrefix) { // its the root /
//         var currentPathDetails = {
//            resourcePath: resourcePathPrefix, // /resource/<project>/blah/script.snap
//            projectPath: "/", // /blah/script.snap
//            projectDirectory: "/", // /blah
//            filePath: "/", // /blah/script.snap
//            fileName: null, // script.snap
//            fileDirectory: "/", // /blah
//            originalPath: path
//         };
         //var currentPathText = JSON.stringify(currentPathDetails);
         //console.log("FileTree.createResourcePath(" + path + "): " + currentPathText);
         return new FilePath(
               cleanResourcePath(resourcePathPrefix), // /resource/<project>/blah/script.snap
               "/", // /blah/script.snap
               "/", // /blah
               "/", // /blah/script.snap
               null, // script.snap
               "/", // /blah
               path
            );
      }
      //console.log("FileTree.createResourcePath(" + path + ")");
      
      if(path.indexOf("/") != 0) {  // script.snap
         path = "/" + path; // /snap.script
      }
      if(path.indexOf(resourcePathPrefix) != 0) { // /resource/<project>/(<file-path>)
         path = "/resource/" + Common.getProjectName() + path;
      }
      var isFolder = isResourceFolder(path); // /resource/<project>/blah/
      var pathSegments: string[] = path.split("/"); // [0="", 1="resource", 2="<project>", 3="blah", 4="script.snap"]
      var currentResourcePath: string = "/resource/" + Common.getProjectName();
      var currentProjectPath: string = "";
      var currentProjectDirectory: string = "";   
      var currentFileName: string = null;
      var currentFilePath: string = "";
      var currentFileDirectory: string = "";
      
      for(var i = 3; i < pathSegments.length; i++) { 
         currentResourcePath += "/" + pathSegments[i];
         currentProjectPath += "/" + pathSegments[i];
         currentFilePath += "/" + pathSegments[i];
      }
      if(isFolder) { // /resource/<project>/blah/
         var currentFileName: string = pathSegments[pathSegments.length - 1];
         
         if(pathSegments.length > 3) {
            for(var i = 3; i < pathSegments.length; i++) { 
               currentProjectDirectory += "/" + pathSegments[i];
               currentFileDirectory += "/" + pathSegments[i];
            }
         } else {
            currentFileDirectory = "/";
         }
      } else { // /resource/<project>/blah/script.snap
         var currentFileName: string = pathSegments[pathSegments.length - 1];
         
         if(pathSegments.length > 4) {
            for(var i = 3; i < pathSegments.length - 1; i++) { 
               currentProjectDirectory += "/" + pathSegments[i];
               currentFileDirectory += "/" + pathSegments[i];
            }
         } else {
            currentFileDirectory = "/";
         }
      }
      return new FilePath(
         cleanResourcePath(currentResourcePath), // /resource/<project>/blah/script.snap
         cleanResourcePath(currentProjectPath), // /blah/script.snap
         cleanResourcePath(currentProjectDirectory == "" ? "/" : currentProjectDirectory), // /blah
         cleanResourcePath(currentFilePath), // /blah/script.snap
         cleanResourcePath(currentFileName), // script.snap
         cleanResourcePath(currentFileDirectory), // /blah
         path
      );
   }
}

//ModuleSystem.registerModule("tree", "Tree module: tree.js", null, null, [ "common" ]);