define(["require", "exports", "spin"], function (require, exports, Spinner) {
    "use strict";
    var LoadSpinner;
    (function (LoadSpinner) {
        var spinnerHiding = false;
        var spinner = null;
        function show() {
            if (spinnerHiding == true) {
                spinnerHiding = false;
                document.getElementById("overlay").style.visibility = 'visible';
            }
        }
        LoadSpinner.show = show;
        function hide() {
            if (spinnerHiding == false) {
                window.setTimeout(function () {
                    spinnerHiding = true;
                    document.getElementById("overlay").style.visibility = 'hidden';
                }, 500);
            }
        }
        LoadSpinner.hide = hide;
        function create() {
            var opts = {
                lines: 13,
                length: 30,
                width: 15,
                radius: 40,
                corners: 1,
                rotate: 0,
                direction: 1,
                color: '#ffffff',
                speed: 1,
                trail: 60,
                shadow: false,
                hwaccel: false,
                className: 'spinner',
                zIndex: 2e9,
                top: '50%',
                left: '50%' // Left position relative to parent
            };
            var target = document.getElementById('spin');
            spinner = new Spinner(opts).spin(target);
        }
        LoadSpinner.create = create;
    })(LoadSpinner = exports.LoadSpinner || (exports.LoadSpinner = {}));
});
//ModuleSystem.registerModule("spinner", "Spinner module: spinner.js", null, LoadSpinner.create, []); 
