define(["require", "exports", "jquery", "w2ui"], function (require, exports, $, w2ui_1) {
    "use strict";
    var Common;
    (function (Common) {
        function openDialog(address, name, width, height) {
            var time = currentTime();
            var handle = window.open(address, name + time, 'height=' + height + ',width=' + width);
            if (handle != undefined) {
                if (handle.focus) {
                    handle.resizeTo(width, height);
                    handle.focus();
                }
            }
            return false;
        }
        Common.openDialog = openDialog;
        function getProjectName() {
            var title = document.title;
            if (title) {
                var trim = title.trim();
                var index = trim.lastIndexOf(" ");
                if (index != -1 && index != trim.length) {
                    trim = trim.substring(index + 1, trim.length);
                }
                index = trim.indexOf("/");
                if (index != -1 && index != trim.length) {
                    trim = trim.substring(0, index);
                }
                return trim;
            }
            return "";
        }
        Common.getProjectName = getProjectName;
        function extractParameter(name) {
            var source = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
            var expression = "[\\?&]" + source + "=([^&#]*)";
            var regex = new RegExp(expression);
            var results = regex.exec(window.location.href);
            if (results == null) {
                return "";
            }
            return results[1];
        }
        Common.extractParameter = extractParameter;
        function extractCookie(cname) {
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }
        Common.extractCookie = extractCookie;
        function decodeValue(value) {
            if (value.length > 0) {
                var result = '@' + value; // ensure we do not reference larger parent string
                var text = result.substring(2);
                if (value.charAt(0) == '<') {
                    var encoded = text.toString();
                    var decoded = '';
                    for (var i = 0; i < encoded.length; i += 2) {
                        var next = encoded.substr(i, 2);
                        var decimal = parseInt(next, 16);
                        decoded += String.fromCharCode(decimal);
                    }
                    return decoded;
                }
                return text;
            }
            return null;
        }
        Common.decodeValue = decodeValue;
        function updateTableRecords(update, name) {
            var grid = w2ui_1.w2ui[name];
            if (grid) {
                var scrollTop = $('#grid_' + name + '_records').prop('scrollTop');
                var current = grid.records; // find the table
                var sortData = grid.sortData;
                var different = false;
                if (update.length == current.length) {
                    for (var i = 0; i < update.length; i++) {
                        var currentRow = current[i];
                        var updateRow = update[i];
                        if (!currentRow || currentRow.length != updateRow.length) {
                            different = true;
                            break;
                        }
                        for (var currentColumn in currentRow) {
                            if (currentRow.hasOwnProperty(currentColumn)) {
                                if (!updateRow.hasOwnProperty(currentColumn)) {
                                    different = true;
                                    break;
                                }
                                var currentCell = currentRow[currentColumn];
                                var updateCell = updateRow[currentColumn];
                                if (currentCell != updateCell) {
                                    different = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (different) {
                        grid.records = sortRecords(update, sortData); // maintain the sort
                        grid.refresh();
                    }
                }
                else {
                    grid.records = sortRecords(update, sortData); // maintain the sort
                    grid.refresh();
                    different = true;
                }
                if (update.length > current.length) {
                    grid.reload();
                    $('#grid_' + name + '_records').prop('scrollTop', scrollTop);
                }
                return different;
            }
            return false;
        }
        Common.updateTableRecords = updateTableRecords;
        function sortRecords(records, sortData) {
            if (sortData && sortData.length > 0) {
                return sortOnSingleColumn(records, sortData[0].field, sortData[0].direction);
            }
            return records;
        }
        function sortOnMultipleColumns(records, columns, types) {
            for (var i = columns.length - 1; i <= 0; i++) {
                var type = types[i];
                var column = columns[i];
                if (type) {
                    records = sortOnSingleColumn(records, column, type);
                }
                else {
                    records = sortOnSingleColumn(records, column, 'asc');
                }
            }
        }
        function isObjectString(object) {
            return (typeof object) == 'string';
        }
        function isObjectNumeric(object) {
            return (typeof object) == 'number';
        }
        function sortOnSingleColumn(records, column, type) {
            var sortedRecords = [];
            var sortGroups = {};
            var sortNumeric = true;
            for (var i = 0; i < records.length; i++) {
                var record = records[i];
                if (record) {
                    var columnToSort = record[column];
                    var keyToSort = columnToSort;
                    if (isObjectString(keyToSort)) {
                        keyToSort = keyToSort.toLowerCase();
                    }
                    if (!isObjectNumeric(keyToSort)) {
                        sortNumeric = false;
                    }
                    var sortGroup = sortGroups[keyToSort];
                    if (sortGroup == null) {
                        sortGroup = [];
                        sortGroups[keyToSort] = sortGroup;
                    }
                    sortGroup.push(record);
                }
            }
            var sortedKeys = [];
            for (var sortKey in sortGroups) {
                if (sortGroups.hasOwnProperty(sortKey)) {
                    if (sortNumeric) {
                        sortedKeys.push(parseFloat(sortKey));
                    }
                    else {
                        sortedKeys.push(sortKey);
                    }
                }
            }
            if (sortNumeric) {
                sortedKeys.sort(function (a, b) {
                    return a - b;
                });
            }
            else {
                sortedKeys.sort();
            }
            if (type != 'asc') {
                sortedKeys.reverse();
            }
            for (var i = 0; i < sortedKeys.length; i++) {
                var keyToSort = sortedKeys[i];
                var sortGroup = sortGroups[keyToSort];
                for (var j = 0; j < sortGroup.length; j++) {
                    var record = sortGroup[j];
                    sortedRecords.push(record);
                }
            }
            return sortedRecords;
        }
        function createOneTimeFunction(functionToCall, timeout) {
            return function (optionalArgument) {
                var localFunction = functionToCall;
                functionToCall = null;
                if (localFunction) {
                    if (timeout) {
                        setTimeout(function () {
                            localFunction(optionalArgument);
                        }, timeout);
                    }
                    else {
                        localFunction(optionalArgument);
                    }
                }
            };
        }
        Common.createOneTimeFunction = createOneTimeFunction;
        function createSimpleStateMachineFunction(stateMachineName, functionToCall, eventsRequired, timeout) {
            var oneTimeFunction = createOneTimeFunction(functionToCall, timeout);
            var alreadyDone = [];
            return function (event) {
                if (eventsRequired.length > 0) {
                    if (removeElementFromArray(eventsRequired, event)) {
                        var doneBefore = alreadyDone.indexOf(event);
                        if (doneBefore == -1) {
                            alreadyDone.push(event); // make sure to ignore next time
                        }
                        console.log("[" + stateMachineName + "] Received event '" + event + "' " + eventsRequired.length + " remain");
                    }
                    else {
                        var doneBefore = alreadyDone.indexOf(event);
                        if (doneBefore == -1) {
                            console.warn("[" + stateMachineName + "] Ignoring unknown event '" + event + "' " + eventsRequired.length + " remain");
                        }
                    }
                    if (eventsRequired.length <= 0) {
                        oneTimeFunction();
                    }
                }
            };
        }
        Common.createSimpleStateMachineFunction = createSimpleStateMachineFunction;
        function removeElementFromArray(arrayToModify, arrayElement) {
            var index = arrayToModify.indexOf(arrayElement);
            if (index > -1) {
                arrayToModify.splice(index, 1);
                return true;
            }
            return false;
        }
        function getElementsByClassName(element, className) {
            var matches = [];
            function traverse(node) {
                for (var i = 0; i < node.childNodes.length; i++) {
                    if (node.childNodes[i].childNodes.length > 0) {
                        traverse(node.childNodes[i]);
                    }
                    if (node.childNodes[i].getAttribute && node.childNodes[i].getAttribute('class')) {
                        if (node.childNodes[i].getAttribute('class').split(" ").indexOf(className) >= 0) {
                            matches.push(node.childNodes[i]);
                        }
                    }
                }
            }
            traverse(element);
            return matches;
        }
        Common.getElementsByClassName = getElementsByClassName;
        function isChildElementVisible(parentElement, childElement) {
            var childRect = childElement.getBoundingClientRect();
            var parentRect = parentElement.getBoundingClientRect();
            var topOfChildRect = childRect.top;
            var topOfParentRect = parentRect.top;
            var bottomOfChildRect = childRect.top + childRect.height;
            var bottomOfParentRect = parentRect.top + parentElement.clientHeight;
            return topOfChildRect > topOfParentRect && bottomOfChildRect < bottomOfParentRect;
        }
        Common.isChildElementVisible = isChildElementVisible;
        function calculateScrollOffset(parentElement, childElement) {
            var childRect = childElement.getBoundingClientRect();
            var parentRect = parentElement.getBoundingClientRect();
            var topOfChildRect = childRect.top;
            var topOfParentRect = parentRect.top;
            if (topOfChildRect < topOfParentRect) {
                return topOfChildRect - topOfParentRect;
            }
            var bottomOfChildRect = childRect.top + childRect.height;
            //var bottomOfParentRect = parentRect.top + parentRect.height;
            var bottomOfParentRect = parentRect.top + parentElement.clientHeight;
            if (bottomOfChildRect > bottomOfParentRect) {
                return bottomOfChildRect - bottomOfParentRect;
            }
            return 0;
        }
        Common.calculateScrollOffset = calculateScrollOffset;
        /**
           // 6/19/2019
           formatDateWithPattern(timeInMillis);
          
           // 19-Jun-2019
           formatDateWithPattern(timeInMillis, 'dd-MMM-yyyy');
           
           // Wednesday, June 19, 2019 23:32:26.512 PM
           formatDateWithPattern(timeInMillis, 'EEEE, MMMM d, yyyy HH:mm:ss.S aaa');
           
           // Wed, Jun 19, 2019 23:32
           formatDateWithPattern(timeInMillis, 'EEE, MMM d, yyyy HH:mm');
           
           // 2019-06-19 23:32:26.512
           formatDateWithPattern(timeInMillis, 'yyyy-MM-dd HH:mm:ss.S');
           
           // 6/19/2019 11:32PM
           formatDateWithPattern(timeInMillis, 'M/dd/yyyy h:mmaaa');
         */
        function formatDateWithPattern(timeInMillis, pattern) {
            var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            var dayOfWeekNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
            var twoDigitPad = function (num) {
                return num < 10 ? "0" + num : num;
            };
            var date = new Date(timeInMillis);
            if (!pattern) {
                pattern = 'M/d/yyyy';
            }
            var day = date.getDate(), month = date.getMonth(), year = date.getFullYear(), hour = date.getHours(), minute = date.getMinutes(), second = date.getSeconds(), miliseconds = date.getMilliseconds(), h = hour % 12, hh = twoDigitPad(h), HH = twoDigitPad(hour), mm = twoDigitPad(minute), ss = twoDigitPad(second), aaa = hour < 12 ? 'AM' : 'PM', EEEE = dayOfWeekNames[date.getDay()], EEE = EEEE.substr(0, 3), dd = twoDigitPad(day), M = month + 1, MM = twoDigitPad(M), MMMM = monthNames[month], MMM = MMMM.substr(0, 3), yyyy = year + "", yy = yyyy.substr(2, 2);
            pattern = pattern
                .replace('hh', hh).replace('h', h)
                .replace('HH', HH).replace('H', hour)
                .replace('mm', mm).replace('m', minute)
                .replace('ss', ss).replace('s', second)
                .replace('S', miliseconds)
                .replace('dd', dd).replace('d', day)
                .replace('EEEE', EEEE).replace('EEE', EEE)
                .replace('yyyy', yyyy)
                .replace('yy', yy)
                .replace('aaa', aaa);
            // checks to see if month name will be used
            if (pattern.indexOf('MMM') > -1) {
                pattern = pattern
                    .replace('MMMM', MMMM)
                    .replace('MMM', MMM);
            }
            else {
                pattern = pattern
                    .replace('MM', MM)
                    .replace('M', M);
            }
            return pattern;
        }
        Common.formatDateWithPattern = formatDateWithPattern;
        function formatTimeMillis(timeInMillis) {
            var isoDate = new Date(timeInMillis).toISOString();
            var millisIndex = isoDate.indexOf(".");
            var dateAndTime = isoDate.substring(0, millisIndex);
            return stringReplaceText(dateAndTime, "T", " ");
        }
        Common.formatTimeMillis = formatTimeMillis;
        function createTimeStamp() {
            return formatDateWithPattern(currentTime(), "yyyyMMddHHmmssS");
        }
        Common.createTimeStamp = createTimeStamp;
        function formatDuration(durationMillis) {
            var seconds = Math.floor(durationMillis / 1000);
            var minutes = Math.floor(seconds / 60);
            var hours = Math.floor(minutes / 60);
            var days = Math.floor(hours / 24);
            var description = "";
            if (seconds > 0) {
                hours = hours - (days * 24);
                minutes = minutes - (days * 24 * 60) - (hours * 60);
                seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
                if (days > 0) {
                    description += days;
                    description += " ";
                    description += (days == 1) ? "day" : "days";
                    description += " ";
                }
                if (hours > 0) {
                    description += hours;
                    description += " ";
                    description += (hours == 1) ? "hour" : "hours";
                    description += " ";
                }
                if (minutes > 0) {
                    description += minutes;
                    description += " ";
                    description += (minutes == 1) ? "minute" : "minutes";
                    description += " ";
                }
                if (seconds > 0) {
                    description += seconds;
                    description += " ";
                    description += (seconds == 1) ? "second" : "seconds";
                    description += " ";
                }
            }
            else {
                description = "0 seconds";
            }
            return description.trim();
        }
        Common.formatDuration = formatDuration;
        function stringReplaceText(text, from, to) {
            if (text && from && to) {
                return text.split(from).join(to);
            }
            return text;
        }
        Common.stringReplaceText = stringReplaceText;
        function stringContains(text, token) {
            if (text && token) {
                return text.indexOf(token) !== -1;
            }
            return false;
        }
        Common.stringContains = stringContains;
        function stringEndsWith(text, token) {
            if (text && token && text.length >= token.length) {
                return text.slice(-token.length) == token;
            }
            return token.length == 0 ? true : false;
        }
        Common.stringEndsWith = stringEndsWith;
        function stringStartsWith(text, token) {
            if (text && token && text.length >= token.length) {
                return text.substring(0, token.length) === token;
            }
            return token.length == 0 ? true : false;
        }
        Common.stringStartsWith = stringStartsWith;
        function isStringBlank(text) {
            if (text) {
                if (text == "") {
                    return true;
                }
                return false;
            }
            return true; // it is nothing
        }
        Common.isStringBlank = isStringBlank;
        function isMacintosh() {
            return navigator.platform.indexOf('Mac') > -1;
        }
        Common.isMacintosh = isMacintosh;
        function isWindows() {
            return navigator.platform.indexOf('Win') > -1;
        }
        Common.isWindows = isWindows;
        function escapeHtml(text) {
            return text
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }
        Common.escapeHtml = escapeHtml;
        function clearHtml(text) {
            return text
                .replace(/<br>/g, "")
                .replace(/&quot;/g, "\"")
                .replace(/&lt;/g, "<")
                .replace(/&gt;/g, ">")
                .replace(/&nbsp;/g, " ")
                .replace(/&amp;/g, "&");
        }
        Common.clearHtml = clearHtml;
        function currentTime() {
            var date = new Date();
            return date.getTime();
        }
        Common.currentTime = currentTime;
    })(Common = exports.Common || (exports.Common = {}));
});
//ModuleSystem.registerModule("common", "Common module: common.js", null, null, []); 
