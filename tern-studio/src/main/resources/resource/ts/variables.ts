import * as $ from "jquery"
import {w2ui} from "w2ui"
import {ThreadManager, ThreadVariables, ThreadScope} from "threads"
import {Common} from "common"
import {Command} from "commands"

export module VariableManager {

   var expandVariableHistory: any = {};
   var expandEvaluationHistory: any = {};
   
   export function toggleExpandVariable(name) {
      var variablePaths = expandVariableTree(name, expandVariableHistory);
      
      if(variablePaths != null) {
         Command.browseScriptVariables(variablePaths);
      }
   }
   
   export function toggleExpandEvaluation(name, expression) {
      var variablePaths = expandVariableTree(name, expandEvaluationHistory);
      
      if(variablePaths != null) {
         Command.browseScriptEvaluation(variablePaths, expression, false);
      }
   }
   
   function expandVariableTree(name, variableHistory) {
      var threadScope: ThreadScope = ThreadManager.focusedThread();
      var expandPath = name + ".*"; // this ensures they sort in sequence with '.' notation, e.g blah.foo.*
      var removePrefix = name + ".";
      
      if(threadScope != null) {
         var variablePaths = variableHistory[threadScope.getThread()];
         
         if(variablePaths == null) {
            variablePaths = [];
            variableHistory[threadScope.getThread()] = variablePaths;
         }
         var removePaths = [];
         
         for(var i = 0; i< variablePaths.length; i++) {
            var currentPath = variablePaths[i];
            
            if(currentPath.indexOf(removePrefix) == 0) {
               removePaths.push(currentPath); // remove variable
            }
         }
         for(var i = 0; i< removePaths.length; i++) {
            var removePath = removePaths[i];
            var removeIndex = variablePaths.indexOf(removePath);
   
            if(removeIndex != -1) {
               variablePaths.splice(removeIndex, 1); // remove variable
            }
         }
         if(removePaths.length == 0) {
            variablePaths.push(expandPath); // add variablePaths}
         }
         return variablePaths;
      }
      return null;
   }
   
   export function showVariables() {
      var localVariables: ThreadVariables = ThreadManager.focusedThreadVariables();
      var evaluationVariables: ThreadVariables = ThreadManager.focusedThreadEvaluation();
      
      showVariablesGrid(localVariables, 'variables', false);
      showVariablesGrid(evaluationVariables, 'evaluation', true);
   }
   
   function showVariablesGrid(threadVariables: ThreadVariables, gridName: string, expressions: boolean) {
      var variables = threadVariables.getVariables();
      var sortedNames = [];
      var variableRecords = [];
      var variableIndex = 1;
      
      for (var variableName in variables) {
         if (variables.hasOwnProperty(variableName)) {
            sortedNames.push(variableName); // add a '.' to ensure dot notation sorts e.g x.y.z
         }
      }
      sortedNames.sort();
      
      for(var i = 0; i < sortedNames.length; i++) {
         var sortedName = sortedNames[i];
         var variable = variables[sortedName];
         var variableExpandable = "" + variable.expandable;
         var variableRoot = variable.depth == 0; // style the root differently
         var variableProperty = ""+variable.property;
         var variableModifiers = variable.modifiers;
         var displayStyle = "variableLeaf";
   
         if(variableRoot && expressions) { // highlight expressions differently
            displayStyle = "variableExpression";
         }else {
            if(variableProperty == "true") {
               if(variableModifiers.indexOf("[private]") != -1) {
                  displayStyle = "variableNodePrivate";
               }
               else if(variableModifiers.indexOf("[protected]") != -1){
                  displayStyle = "variableNodeProtected";
               }
               else if(variableModifiers.indexOf("[public]") != -1){
                  displayStyle = "variableNodePublic";
               }
               else {
                  displayStyle = "variableNode"; // default
               }
            }
         }
         var displayValue = "<div class='variableData'>"+Common.escapeHtml(variable.value)+"</div>";
         var displayName = "<div title='"+Common.escapeHtml(variable.description)+"' style='padding-left: " + 
            (variable.depth * 20)+ 
            "px;'><div class='"+displayStyle+
            "'>"+Common.escapeHtml(variable.name)+"</div></div>";
         
         variableRecords.push({
            recid: variableIndex++,
            path: sortedName,
            name: displayName,
            value: displayValue,
            type: variable.type,
            expandable: variableExpandable == "true"
            //depth: variable.depth // seems to cause issues?
         });
      }
      var variableGrid = w2ui[gridName];
      
      if(variableGrid != null) {
         variableGrid.records = variableRecords;
         variableGrid.refresh();
      }
   }
   
   export function clearEvaluation() {
      expandEvaluationHistory = {};
   //   w2ui['evaluation'].records = [];
   //   w2ui['evaluation'].refresh();
   }
    
   export function clearVariables() {
      expandVariableHistory = {};
      w2ui['variables'].records = [];
      w2ui['variables'].refresh();
   }
}
//ModuleSystem.registerModule("variables", "Variables module: variables.js", null, null, [ "common" ]);