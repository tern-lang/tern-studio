import * as $ from "jquery"
import {DialogBuilder} from "dialog"
import {Common} from "common"
import {Alerts} from "alert"
import {w2popup} from "w2ui"

export module ProjectSelector {   

   export function showProjectDialog() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete, isSubmit){
         findExistingProjects(text, onComplete, isSubmit);
      }, null, "Open Project", "", function() {
         setTimeout(showProjectDialog, 500);
      });
   }
   
   function createNewProject(projectName) {
      document.location = createProjectLocation(projectName);
   }
   
   function findExistingProjects(text, onComplete, isSubmit) {
      const originalExpression = text;
      
      $.ajax({
         success: function(projectMap){
            const projectCells = [];
            const sortedNames = [];
         
            for (var projectName in projectMap) {
               if (projectMap.hasOwnProperty(projectName)) {
                  if(Common.stringStartsWith(projectName, originalExpression)) {
                     sortedNames.push(projectName);
                  }
               }
            }
            sortedNames.sort();

            if(isSubmit && isSubmit == true) {
               if(sortedNames.indexOf(originalExpression) == -1) {
                  createNewProject(originalExpression); // create non-existing project
               }
            }
            for (var i = 0; i < sortedNames.length; i++) {
               const projectName = sortedNames[i];
               const projectPath = projectMap[projectName];
               const projectLink = createProjectLocation(projectName),
               const projectNameCell = {
                  text: "<span style='cursor: pointer;'>" + projectName + "&nbsp;&nbsp;</span>",
                  link: projectLink,
                  style: 'projectNode'   
               };
               const projectPathCell = {
                  text: "<i style='opacity: 0.5; cursor: pointer;'>"+projectPath+"</i>",
                  link: projectLink,
                  style: 'folderNode'
               };
               projectCells.push([projectNameCell, projectPathCell]);
            }
            onComplete(projectCells, originalExpression);
         },
         error: function(response){
             onComplete([], originalExpression);
             console.log("Could not complete outline for text '" + originalExpression + "'");
         },
         async: true,
         processData: false,
         type: 'GET',
         url: '/projects/list'
     });
   }
   
   export function showProjectTreeDialog() {
      DialogBuilder.createTreeOpenDialog(function(dialogPathDetails, projectName) {
         if(projectName != "" && projectName != null) {
            console.log("Opening " + projectName + " " + address);
            document.location = createProjectLocation(projectName);
         } else {
            setTimeout(showProjectDialog, 500);
         }
      }, function() {
         setTimeout(showProjectDialog, 500);
      },
      "Open Project", "Open", "/");
   }
   
   function createProjectLocation(projectName) {
      var host = window.document.location.hostname;
      var port = window.document.location.port;
      var scheme = window.document.location.protocol;
      var path = window.document.location.pathname;
      var query = window.document.location.search;
      var address = "http://";

      if (scheme.indexOf("https") == 0) {
         address = "https://"
      }
      address += host;
      
      if((port - parseFloat(port) + 1) >= 0) {
         address += ":";
         address += port;
      }   
      address += "/project/" + projectName
      address += query;
      
      return address;
   }
}
//ModuleSystem.registerModule("start", "Start module: start.js", null, Start.showProjectDialog, [ "common", "dialog", "tree", "spinner" ]);