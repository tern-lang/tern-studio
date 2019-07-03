import * as $ from "jquery"
import {Common} from "./common"
import {FileTree, FilePath} from "tree"

export module StatusPanel {
   
   export function showProcessStatus(resource: string, agent, debug) {
      var resourcePath: FilePath = FileTree.createResourcePath(resource);
      var processFile = resourcePath.getFileName();
      var statusClass = "statusPanelRunning";
      var processDetail = "";
      
      if(debug) {
         statusClass = "statusPanelDebugging";
      }
      processDetail += "<table border='0'>\n";
      processDetail += "<tr>\n";
      processDetail += "<td><div class='" + statusClass + "'></div></td>\n";
      processDetail += "<td>"+decodeURIComponent(processFile)+"</td>\n";
      processDetail += "</tr>";
      processDetail += "</table>";
      
      $("#process").html(processDetail); // ("+message.process+") "+message.duration+" milliseconds</i>");
   }
   
   export function showActiveFile(resource: string) {
      var resourcePath: FilePath = FileTree.createResourcePath(resource);
      var pathSegments = resourcePath.getProjectPath().split("/");
      var pathBreadcrumb = "";
      
      pathBreadcrumb += "<table border='0'>\n";
      pathBreadcrumb += "<tr>\n";
      pathBreadcrumb += "<td><div class='treeIndexFolder'></div><td>\n";
      pathBreadcrumb += "<td>"+Common.getProjectName()+"</td>\n";
      
      for(var i = 0; i < pathSegments.length; i++) {
         var segment = pathSegments[i];
         
         if(segment.length > 0) {
            pathBreadcrumb += "<td><div class='";
            
            if(segment.indexOf(".") != -1){
               pathBreadcrumb += "treeFile";
            } else {
               pathBreadcrumb += "treeFileFolder";
            }
            pathBreadcrumb += "'></div>";
            pathBreadcrumb += "</td>\n<td style='white-space: nowrap;'>";
            pathBreadcrumb += decodeURIComponent(segment);
            pathBreadcrumb += "</td>\n";
         }
      }
      pathBreadcrumb += "</table>";
      
      $("#currentFile").html(pathBreadcrumb);
   }
}

//ModuleSystem.registerModule("status", "Status panel module: status.js", null, null, []);