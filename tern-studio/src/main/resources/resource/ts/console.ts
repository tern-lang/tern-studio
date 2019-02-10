import * as $ from "jquery"
import {Common} from "common"
import {Command} from "commands"
import {EventBus} from "socket"

export module ProcessConsole {
   
   var consoleTable = {};
   var consoleCapacity = 5000;
   var consoleProcess = null;
   var consoleFont = null;
   var consoleFontSize = null;
   
   export function registerConsole() {
      EventBus.createRoute("BEGIN", createConsole, null); 
   	EventBus.createRoute('PRINT_ERROR', updateConsole, null);
   	EventBus.createRoute('PRINT_OUTPUT', updateConsole, null);
      EventBus.createRoute('TERMINATE', terminateConsole, null); // clear focus
      EventBus.createRoute('EXIT', terminateConsole, null);
   	setInterval(showConsole, 200); // prevents reflow overload when console is busy
   }
   
   export function updateConsoleFont(fontFamily, fontSize) {
      var consoleElement = document.getElementById("console");
      
      if(consoleElement != null) {
         consoleElement.style.fontFamily = fontFamily;
         consoleElement.style.fontSize = fontSize;
      }   
      consoleFont = fontFamily;
      consoleFontSize = fontSize;
   }
   
   export function updateConsoleCapacity(maxCapacity) {
      consoleCapacity = maxCapacity;    
   }
   
   function terminateConsole(socket, type, text) {
      var message = JSON.parse(text);
      var process: string = message.process;
      
      if(consoleProcess == process) {
         showConsole();
      }
      var consoleData = consoleTable[process];
      
      if(consoleData != null) {
         consoleData.valid = false; // means it should be terminated when unfocused
      }
   }
   
   export function clearConsole() {
      var consoleElement = document.getElementById("console");
   	
      if(consoleElement != null) {
         document.getElementById("console").innerHTML = "";
      }
      consoleProcess = null;
   }
   
   /**
    * This method can be very slow, we need to improve the merging of nodes
    * so that concatenation reduces the overhead.
    */
   export function showConsole() {
   	var consoleElement = document.getElementById("console");
   	var consoleText = null;
   	var previous = null;
   	
   	if(consoleElement != null && consoleProcess != null) {
   	   var currentText = consoleElement.innerHTML;
   	   var consoleData =  consoleTable[consoleProcess]; // is ther an update?
   	   
   	   if(consoleData != null && (currentText == "" || consoleData.update == true)) {
      		consoleData.update = false; // clear the update
      		
            for(var i = 0; i < consoleData.list.length; i++) {
      			var next = consoleData.list[i];
      			
      			if(previous == null) {
      				if(next.error) {
      					consoleText = "<span class='consoleError'>" + next.text;
      				} else {
      					consoleText = "<span class='consoleNormal'>" + next.text;
      				}
      				previous = next.error;
      			} else if(next.error != previous) {
      				consoleText += "</span>";
      				
      				if(next.error) {
      					consoleText += "<span class='consoleError'>" + next.text;
      				} else {
      					consoleText += "<span class='consoleNormal'>" + next.text;
      				}
      				previous = next.error;
      			} else {
      				consoleText += next.text;
      			}
      		}
      		if(consoleText != null) {
      			consoleText += "</span>";
      			consoleElement.innerHTML = consoleText;
      			consoleElement.scrollTop = consoleElement.scrollHeight;
      		}
   	   }
   	   if(consoleFont && consoleFontSize) {
   	      updateConsoleFont(consoleFont, consoleFontSize);
   	   }
   	}
   }
   
   export function updateConsoleFocus(processToFocus) {
      if(consoleProcess != processToFocus) {
         deleteAllInvalidConsoles(processToFocus); // delete only on a change of focus
         clearConsole();
         consoleProcess = processToFocus;
         showConsole();
      }
   }
   
   function deleteAllInvalidConsoles(processToKeep) {
      var validConsoles = {};
      
      for(var processName in consoleTable) {
         if(consoleTable.hasOwnProperty(processName)) {
            var consoleData = consoleTable[processName];
           
            if(consoleData.valid || processName == processToKeep) { // delete unfocused process consoles
               validConsoles[processName] = consoleData;
            }
         }
      } 
      consoleTable = validConsoles; // make sure expired consoles are removed
   }
   
   function createConsole(socket, type, value) {
      var message = JSON.parse(value);
      var newProcess = message.process;
      var consoleData = consoleTable[newProcess];
   
      consoleTable[newProcess] = {
         list: [],
         size: 0,
         update: true,
         valid: true
      }
      updateConsoleFocus(newProcess);
   }
   
   /**
    * This function should probably merge the nodes to some extent, it will improve
    * the performance of the console rendering.
    */
   function updateConsole(socket, type, value) {
      var offset = value.indexOf(':');
      var updateProcess = value.substring(0, offset)
      var updateText = value.substring(offset+1);
      var node = {
   		error: type == 'PRINT_ERROR',
   		text: updateText
   	};
      var consoleData = consoleTable[updateProcess];
   	
      if(consoleData == null) {
         consoleData = {
            list: [],
            size: 0,
            update: true,
            valid: true         
         }
         consoleTable[updateProcess] = consoleData;
      }
      consoleData.list.push(node); // put at the end, i.e index consoleTable.length - 1
      consoleData.size += updateText.length; // update the size of the console
   	
   	while(consoleData.list.length > 3 && consoleData.size > consoleCapacity) { // min of 3 nodes	
         var removeNode = consoleData.list.shift(); // remove from the start, i.e index 0
   	   
   	   if(removeNode != null) {
   	      consoleData.size -= removeNode.text.length;
   	   }
   	}
   	consoleData.update = true;
   }
}

//ModuleSystem.registerModule("console", "Console module: console.js", null, ProcessConsole.registerConsole, ["common", "socket"]);