define(["require", "exports", "jquery", "dialog", "common"], function (require, exports, $, dialog_1, common_1) {
    "use strict";
    var ProjectSelector;
    (function (ProjectSelector) {
        function showProjectDialog() {
            dialog_1.DialogBuilder.createListDialog(function (text, ignoreMe, onComplete, isSubmit) {
                findExistingProjects(text, onComplete, isSubmit);
            }, null, "Open Project", "", function () {
                setTimeout(showProjectDialog, 500);
            });
        }
        ProjectSelector.showProjectDialog = showProjectDialog;
        function createNewProject(projectName) {
            var address = createProjectLocation(projectName);
            console.log("Creating new project " + projectName + " " + address);
            document.location = address;
        }
        function openExistingProject(projectName) {
            var address = createProjectLocation(projectName);
            console.log("Opening project " + projectName + " " + address);
            document.location = address;
        }
        function findExistingProjects(text, onComplete, isSubmit) {
            var originalExpression = text;
            $.ajax({
                success: function (projectMap) {
                    var projectCells = [];
                    var sortedNames = [];
                    for (var projectName in projectMap) {
                        if (projectMap.hasOwnProperty(projectName)) {
                            if (common_1.Common.stringStartsWith(projectName, originalExpression)) {
                                sortedNames.push(projectName);
                            }
                        }
                    }
                    sortedNames.sort();
                    if (isSubmit && isSubmit == true) {
                        if (!common_1.Common.isStringBlank(originalExpression)) {
                            if (sortedNames.indexOf(originalExpression) == -1) {
                                createNewProject(originalExpression); // create non-existing project
                            }
                            else {
                                openExistingProject(originalExpression);
                            }
                        }
                    }
                    for (var i = 0; i < sortedNames.length; i++) {
                        var projectName_1 = sortedNames[i];
                        var projectPath = projectMap[projectName_1];
                        var projectLink = createProjectLocation(projectName_1);
                        var projectNameCell = {
                            text: "<b style='cursor: pointer;'>" + projectName_1 + "&nbsp;&nbsp;</b>",
                            link: projectLink,
                            style: 'projectNode'
                        };
                        var projectPathCell = {
                            text: "<i style='opacity: 0.5; cursor: pointer;'>" + projectPath + "</i>",
                            link: projectLink,
                            style: 'folderNode'
                        };
                        projectCells.push([projectNameCell, projectPathCell]);
                    }
                    onComplete(projectCells, originalExpression);
                },
                error: function (response) {
                    onComplete([], originalExpression);
                    console.log("Could not complete outline for text '" + originalExpression + "'");
                },
                async: true,
                processData: false,
                type: 'GET',
                url: '/projects/list'
            });
        }
        function showProjectTreeDialog() {
            dialog_1.DialogBuilder.createTreeOpenDialog(function (dialogPathDetails, projectName) {
                if (projectName != "" && projectName != null) {
                    openExistingProject(projectName);
                }
                else {
                    setTimeout(showProjectDialog, 500);
                }
            }, function () {
                setTimeout(showProjectDialog, 500);
            }, "Open Project", "Open", "/");
        }
        ProjectSelector.showProjectTreeDialog = showProjectTreeDialog;
        function createProjectLocation(projectName) {
            var host = window.document.location.hostname;
            var port = window.document.location.port;
            var scheme = window.document.location.protocol;
            var path = window.document.location.pathname;
            var query = window.document.location.search;
            var address = "http://";
            if (scheme.indexOf("https") == 0) {
                address = "https://";
            }
            address += host;
            if ((port - parseFloat(port) + 1) >= 0) {
                address += ":";
                address += port;
            }
            address += "/project/" + projectName;
            address += query;
            return address;
        }
    })(ProjectSelector = exports.ProjectSelector || (exports.ProjectSelector = {}));
});
//ModuleSystem.registerModule("start", "Start module: start.js", null, Start.showProjectDialog, [ "common", "dialog", "tree", "spinner" ]); 
