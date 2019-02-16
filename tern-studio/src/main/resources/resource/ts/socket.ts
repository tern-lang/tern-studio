import {LoadSpinner} from "spinner"

export module EventBus {
   
   var subscription: any = {};
   var routes: any = {};
   var disconnect = [];
   var socket = null;
   var connections = 0;
   var attempts = 0;
   var total = 1;
   
   export function startSocket() {
      createSubscription();
      openSocket(); /* delay connect */
   }
   
   function refreshSocket() {
      if(socket) {
         socket.close();
      }
   }
   
   export function isSocketOpen() {
      if(socket){
         return socket.readyState == 1;
      }
      return false;
   }
   
   export function sendEvent(type, message) {
      if(message) {
         var payload = JSON.stringify(message);

         if(socket) {
            socket.send(type + ":" + payload);
         }
      } else {
         socket.send(type);
      }
   }
   
   function createSubscription() {
      var host = window.document.location.hostname;
      var port = window.document.location.port;
      var scheme = window.document.location.protocol;
      var path = window.document.location.pathname;
      var query = window.document.location.search;
   
      subscription['panic'] = false;
      subscription['query'] = query;
   
      var address = "ws://";
   
      if (scheme.indexOf("https") == 0) {
         address = "wss://"
      }
      address += host;
      
      if((port - parseFloat(port) + 1) >= 0) {
         address += ":";
         address += port;
      }   
      var segments = path.split("/");
   
      if (segments.length > 2) {
         address += "/connect/" + segments[2];
      } else {
         address += "/connect"
      }   
      address += query;
   
      subscription['address'] = address;
   }
   
   function disableRoutes() {
      if (subscription.panic == false) {
         subscription['panic'] = true;
         socket.close();      
      }
   }
   
   function enableRoutes() {
      if (subscription.panic == true) {
         subscription['panic'] = false;   
         socket.close();
      }
   }
   
   function openSocket() {
      socket = new WebSocket(subscription.address);
   
      socket.onopen = function() {
         attempts = 1;
         connections++;
         LoadSpinner.hide(); // on hide overlay
         console.log("Socket connected to '" + subscription.address + "'");
      };
   
      socket.onerror = function(message) {
         var length = disconnect.length;
         
         for(var i = 0; i < length; i++) {
            var callback = disconnect[i];
            
            if(callback != null) {
               callback(); // disconnected
            }
         }
         LoadSpinner.show();
         console.log("Error connecting to '" + subscription.address + "'");
      };
   
      socket.onclose = function(message) {
         var exponent = Math.pow(2, attempts++);
         var interval = (exponent - 1) * 1000;
         var length = disconnect.length;
         var reference = openSocket();
   
         if (interval > 30 * 1000) {
            interval = 30 * 1000;
         }
         setTimeout(reference, interval);
         
         for(var i = 0; i < length; i++) {
            var callback = disconnect[i];
            
            if(callback != null) {
               callback(); // disconnected
            }
         }
         LoadSpinner.show(); // on disconnect show spinner
         console.log("Connection closed to '" + subscription.address + "' reconnecting in " + interval + " ms");
      };
   
      socket.onmessage = function(message) {
         var data = message.data;
         var index = data.indexOf(':');
         var value = null;
         var type = null;
         
         if(index != -1) {
            value = data.substring(index + 1);
            type = data.substring(0, index);
         } else {
            type = data;
         }
         var route = routes[type];
         
         if(route != undefined) {
            if(!subscription.panic) { // this might cause problems with missing messages
               for(var i = 0; i < route.length; i++) {
                  var func = route[i];
                  
                  if(func != null) {
                     func(this, type, value);
                  }
               }
            }
         } else {
            console.log("No route defined for '" + type+ "' with '" + value + "'");
         }
         LoadSpinner.hide(); // hide the spinner
      };
   }
   
   export function createRoute(code, method, failure) {
      var name = code.toUpperCase();
      
      if(name != code) {
         alert("Illegal route '" + name + "' is not in upper case");
      }
      var route = routes[code];
      
      if(route == null) {
         route = [];
         routes[code] = route;
      }
      if(method != null) {
         var length = route.length;
         var exists = false;
         
         for(var i = 0; i < length; i++) {
            var callback = route[i];
            
            if(callback == method) { // don't add twice
               exists = true;
            }
         }
         if(!exists) {
            route.push(method); // add a route listener
         }
      }
      createTermination(failure);
      refreshSocket();
   }
   
   export function createTermination(failure) {
      if(failure != null) {
         var length = disconnect.length;
         var exists = false;
         
         for(var i = 0; i < length; i++) {
            var callback = disconnect[i];
            
            if(callback == failure) { // don't add twice
               exists = true;
            }
         }
         if(!exists) {
            disconnect.push(failure); // add a disconnect listener
         }
      }
   }
}

//ModuleSystem.registerModule("socket", "Socket subscription module: socket.js", null, EventBus.startSocket, []);