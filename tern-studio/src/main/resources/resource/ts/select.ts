import * as $ from "jquery"
import {DialogBuilder} from "dialog"

export module ProjectSelector {

   export function showProjectDialog() {
      DialogBuilder.createTreeOpenDialog(function(dialogPathDetails, projectName) {
         if(projectName != "" && projectName != null) {
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
   
            console.log("Opening " + projectName + " " + address);
            document.location = address;
         } else {
            setTimeout(showProjectDialog, 500);
         }
      }, function() {
         setTimeout(showProjectDialog, 500);
      },
      "Open Project", "Open", "");
   }
}
//ModuleSystem.registerModule("start", "Start module: start.js", null, Start.showProjectDialog, [ "common", "dialog", "tree", "spinner" ]);