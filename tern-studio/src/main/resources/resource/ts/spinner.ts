import * as Spinner from "spin" 

export module LoadSpinner {
   
   var spinnerHiding = false;
   var spinner = null;
   
   export function show(){
      if(spinnerHiding == true) {  
         spinnerHiding = false;
         document.getElementById("overlay").style.visibility = 'visible';      
      }
   }
   
   export function hide(){
      if(spinnerHiding == false) {  
         window.setTimeout(function(){
            spinnerHiding = true;
            document.getElementById("overlay").style.visibility = 'hidden'; 
         }, 500);      
      }
   }
   
   export function create() {
      var opts = {
            lines: 13, // The number of lines to draw
            length: 30, // The length of each line
            width: 15, // The line thickness
            radius: 40, // The radius of the inner circle
            corners: 1, // Corner roundness (0..1)
            rotate: 0, // The rotation offset
            direction: 1, // 1: clockwise, -1: counterclockwise
            color: '#ffffff', // #rgb or #rrggbb or array of colors
            speed: 1, // Rounds per second
            trail: 60, // Afterglow percentage
            shadow: false, // Whether to render a shadow
            hwaccel: false, // Whether to use hardware acceleration
            className: 'spinner', // The CSS class to assign to the spinner
            zIndex: 2e9, // The z-index (defaults to 2000000000)
            top: '50%', // Top position relative to parent
            left: '50%' // Left position relative to parent
          };
          var target = document.getElementById('spin');
          spinner = new Spinner(opts).spin(target);
   }
}

//ModuleSystem.registerModule("spinner", "Spinner module: spinner.js", null, LoadSpinner.create, []);