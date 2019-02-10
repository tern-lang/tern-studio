import * as $ from "jquery"
import {w2ui} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree, FilePath} from "tree"

export module Profiler {
   
   export function startProfiler() {
      EventBus.createRoute("PROFILE", updateProfiler, clearProfiler);
   }
   
   function updateProfiler(socket, type, text) {
      var profileResult = JSON.parse(text);
      var profileRecords = profileResult.results;
      var profilerRecords = [];
      var profilerWidths = [];
      var profilerIndex = 1;
      var totalTime = 0;
      var currentHeight = w2ui['profiler'].records;
     
      for(var i = 0; i < profileRecords.length; i++) {
         totalTime += profileRecords[i].time;
      }
      for(var i = 0; i < profileRecords.length; i++) {
         var recordTime = profileRecords[i].time;
         
         if(recordTime > 0) {
            var percentageTime: number = (recordTime/totalTime)*100;
            var percentage: number = percentageTime;
            
            profilerWidths[i] = percentage;
         }
      }
      for(var i = 0; i < profileRecords.length; i++) {
         var profileRecord = profileRecords[i];
         var sortableProfilerWidth = ('0000'+ profilerWidths[i]).slice(-4); // padd with leading zeros
         var resourcePath: FilePath = FileTree.createResourcePath(profileRecord.resource);
         var displayName = "<div class='profilerRecord'>"+resourcePath.getProjectPath()+"</div>";
         var percentageBar = "<!-- " + sortableProfilerWidth + " --><div style='padding: 2px;'><div style='height: 10px; background: #ed6761; width: "+profilerWidths[i]+"%;'></div></div>";
         var averageTime = (profileRecord.time / profileRecord.count) / 1000; // average time in seconds

         profilerRecords.push({
            recid: profilerIndex++,
            resource: displayName,
            percentage: percentageBar,
            duration: profileRecord.time,
            line: profileRecord.line,
            count: profileRecord.count,
            average: averageTime.toFixed(5),
            script: resourcePath.getResourcePath()
         });
      }
      Common.updateTableRecords(profilerRecords, 'profiler');
   }
   
   export function clearProfiler() {
      w2ui['profiler'].records = [];
      w2ui['profiler'].refresh();
   }
}

//ModuleSystem.registerModule("profiler", "Profiler module: profiler.js", null, Profiler.startProfiler, [ "common", "socket" ]);