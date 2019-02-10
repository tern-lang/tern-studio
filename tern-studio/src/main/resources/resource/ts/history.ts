import * as $ from "jquery"
import {w2ui} from "w2ui"
import {Common} from "./common"
import {FileTree, FilePath} from "tree"
import {FileEditor, FileEditorState} from "editor"
import {FileExplorer} from "explorer"

export module History {

   export function trackHistory() {
      $(window).on('hashchange', function() {
         updateEditorFromHistory();
      });
      updateEditorFromHistory(200);
   } 
   
   export function showFileHistory() {
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var editorPath: FilePath = editorState.getResource();
   
      if(!editorPath) {
         console.log("Editor path does not exist: ", editorState);
      }
      var resource = editorPath.getProjectPath();
      
      $.ajax({
         url: '/history/' + Common.getProjectName() + '/' + resource,
         success: function (currentRecords) {
            var historyRecords = [];
            var historyIndex = 1;
            
            for (var i = 0; i < currentRecords.length; i++) {
               var currentRecord = currentRecords[i];
               var recordResource: FilePath = FileTree.createResourcePath(currentRecord.path);
               
               historyRecords.push({ 
                  recid: historyIndex++,
                  resource: "<div class='historyPath'>" + recordResource.getFilePath() + "</div>", // /blah/file.tern
                  date: currentRecord.date,
                  time: currentRecord.timeStamp,
                  script: recordResource.getResourcePath() // /resource/<project>/blah/file.tern
               });
            }
            w2ui['history'].records = historyRecords;
            w2ui['history'].refresh();
         },
         async: true
      });
   }
   
   export function navigateForward() {
      window.history.forward();
   }
   
   export function navigateBackward() {
      var location = window.location.hash;
      var hashIndex = location.indexOf('#'); // if we are currently on a file
      
      if(hashIndex != -1) {
         window.history.back();
      }
   }
   
   function updateEditorFromHistory(){
      var location = window.location.hash;
      var hashIndex = location.indexOf('#');
      
      if(hashIndex != -1) {
         var resource = location.substring(hashIndex + 1);
         var resourceData: FilePath = FileTree.createResourcePath(resource);
         var editorState: FileEditorState = FileEditor.currentEditorState();
         var editorResource: FilePath = editorState.getResource();
         
         if(editorResource == null || editorResource.getResourcePath() != resourceData.getResourcePath()) { // only if changed
            FileExplorer.openTreeFile(resourceData.getResourcePath(), function() {});
         }
      }
   }
}

//ModuleSystem.registerModule("history", "History module: history.js", null, History.trackHistory, [ "common", "editor" ]);