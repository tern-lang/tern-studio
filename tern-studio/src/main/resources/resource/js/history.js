define(["require", "exports", "jquery", "w2ui", "./common", "tree", "editor", "explorer"], function (require, exports, $, w2ui_1, common_1, tree_1, editor_1, explorer_1) {
    "use strict";
    var History;
    (function (History) {
        function trackHistory() {
            $(window).on('hashchange', function () {
                updateEditorFromHistory();
            });
            updateEditorFromHistory(200);
        }
        History.trackHistory = trackHistory;
        function showFileHistory() {
            var editorState = editor_1.FileEditor.currentEditorState();
            var editorPath = editorState.getResource();
            if (!editorPath) {
                console.log("Editor path does not exist: ", editorState);
            }
            var resource = editorPath.getProjectPath();
            $.ajax({
                url: '/history/' + common_1.Common.getProjectName() + '/' + resource,
                success: function (currentRecords) {
                    var historyRecords = [];
                    var historyIndex = 1;
                    for (var i = 0; i < currentRecords.length; i++) {
                        var currentRecord = currentRecords[i];
                        var recordResource = tree_1.FileTree.createResourcePath(currentRecord.path);
                        historyRecords.push({
                            recid: historyIndex++,
                            resource: "<div class='historyPath'>" + recordResource.getFilePath() + "</div>",
                            date: currentRecord.date,
                            time: currentRecord.timeStamp,
                            script: recordResource.getResourcePath() // /resource/<project>/blah/file.tern
                        });
                    }
                    w2ui_1.w2ui['history'].records = historyRecords;
                    w2ui_1.w2ui['history'].refresh();
                },
                async: true
            });
        }
        History.showFileHistory = showFileHistory;
        function navigateForward() {
            window.history.forward();
        }
        History.navigateForward = navigateForward;
        function navigateBackward() {
            var location = window.location.hash;
            var hashIndex = location.indexOf('#'); // if we are currently on a file
            if (hashIndex != -1) {
                window.history.back();
            }
        }
        History.navigateBackward = navigateBackward;
        function updateEditorFromHistory() {
            var location = window.location.hash;
            var hashIndex = location.indexOf('#');
            if (hashIndex != -1) {
                var resource = location.substring(hashIndex + 1);
                var resourceData = tree_1.FileTree.createResourcePath(resource);
                var editorState = editor_1.FileEditor.currentEditorState();
                var editorResource = editorState.getResource();
                if (editorResource == null || editorResource.getResourcePath() != resourceData.getResourcePath()) {
                    explorer_1.FileExplorer.openTreeFile(resourceData.getResourcePath(), function () { });
                }
            }
        }
    })(History = exports.History || (exports.History = {}));
});
//ModuleSystem.registerModule("history", "History module: history.js", null, History.trackHistory, [ "common", "editor" ]); 
