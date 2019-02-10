define(["require", "exports", "socket"], function (require, exports, socket_1) {
    "use strict";
    var Alerts;
    (function (Alerts) {
        function registerAlerts() {
            socket_1.EventBus.createRoute('ALERT', createAlert);
        }
        Alerts.registerAlerts = registerAlerts;
        function isAlertPossible() {
            return w2popup.status == "closed";
        }
        function createAlert(socket, type, object) {
            if (isAlertPossible()) {
                var message = JSON.parse(object);
                var text = message.message;
                w2alert('<table border="0" width="100%">' +
                    '  <tr>' +
                    '    <td>&nbsp;&nbsp</td>' +
                    '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>' +
                    '    <td align="left"><div class="alertText">' + text + '</div></td>' +
                    '  </tr>' +
                    '</table>');
            }
        }
        function createConfirmAlert(title, message, yesButton, noButton, yesCallback, noCallback) {
            if (isAlertPossible()) {
                var text = '<table border="0" width="100%">' +
                    '  <tr>' +
                    '    <td>&nbsp;&nbsp</td>' +
                    '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>' +
                    '    <td align="left"><div class="alertText">' + message + '</div></td>' +
                    '  </tr>' +
                    '</table>';
                var options = {
                    msg: text,
                    title: title,
                    width: 450,
                    height: 220,
                    yes_text: yesButton,
                    yes_class: 'btn dialogButton',
                    yes_style: '',
                    yes_callBack: yesCallback,
                    no_text: noButton,
                    no_class: 'btn dialogButton',
                    no_style: '',
                    no_callBack: noCallback,
                    callBack: null // common callBack
                };
                w2confirm(options);
            }
        }
        Alerts.createConfirmAlert = createConfirmAlert;
        function createRunPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback) {
            createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback, "${IMAGE_FOLDER}/run.png", "Arguments");
        }
        Alerts.createRunPromptAlert = createRunPromptAlert;
        function createDebugPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback) {
            createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback, "${IMAGE_FOLDER}/debug.png", "Arguments");
        }
        Alerts.createDebugPromptAlert = createDebugPromptAlert;
        function createRemoteDebugPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback) {
            createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback, "${IMAGE_FOLDER}/debug.png", "Address");
        }
        Alerts.createRemoteDebugPromptAlert = createRemoteDebugPromptAlert;
        function createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, cancelCallback, iconFile, textLabel) {
            if (isAlertPossible()) {
                var text = '<table border="0" width="100%">' +
                    '  <tr>' +
                    '    <td>&nbsp;&nbsp</td>' +
                    '    <td align="right"><img src="' + iconFile + '" height="20px"></td>' +
                    '    <td>&nbsp;</td>' +
                    '    <td align="left">' + textLabel + '</td>' +
                    '    <td>&nbsp;</td>' +
                    '    <td align="left"><input id="textToSearchFor" type="text" placeholder="' + placeholder + '" name="token" width="180"></td>' +
                    '  </tr>' +
                    '</table>';
                var findCallback = function () {
                    var element = document.getElementById("textToSearchFor");
                    if (element && yesCallback) {
                        yesCallback(element.value);
                        yesCallback = null;
                    }
                };
                var noCallback = function () {
                    if (cancelCallback) {
                        cancelCallback();
                    }
                };
                var focusCallback = function () {
                    var element = document.getElementById("textToSearchFor");
                    if (element) {
                        console.log("Taking focus of dialog");
                        element.focus();
                    }
                    else {
                        console.log("No element to focus dialog");
                    }
                };
                var options = {
                    msg: text,
                    title: title,
                    width: 450,
                    height: 220,
                    yes_text: yesButton,
                    yes_class: 'btn dialogButton',
                    yes_style: '',
                    yes_callBack: findCallback,
                    no_text: noButton,
                    no_class: 'btn dialogButton',
                    no_style: '',
                    no_callBack: noCallback,
                    onOpen: focusCallback
                };
                w2confirm(options);
            }
        }
    })(Alerts = exports.Alerts || (exports.Alerts = {}));
});
//ModuleSystem.registerModule("alert", "Alert module: alert.js", null, Alerts.registerAlerts, ["common", "socket"]); 
