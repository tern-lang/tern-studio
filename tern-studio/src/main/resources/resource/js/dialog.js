define(["require", "exports", "jquery", "w2ui", "common", "commands", "variables", "explorer", "editor", "tree"], function (require, exports, $, w2ui_1, common_1, commands_1, variables_1, explorer_1, editor_1, tree_1) {
    "use strict";
    var DialogBuilder;
    (function (DialogBuilder) {
        function evaluateExpressionDialog(expressionToEvaluate) {
            createEvaluateDialog(expressionToEvaluate, "Evaluate Expression");
        }
        DialogBuilder.evaluateExpressionDialog = evaluateExpressionDialog;
        function openTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback) {
            if (resourceDetails != null) {
                createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, false, "Save Changes");
            }
            else {
                createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, false, "Save As");
            }
        }
        DialogBuilder.openTreeDialog = openTreeDialog;
        function renameFileTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback) {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, false, "Rename File");
        }
        DialogBuilder.renameFileTreeDialog = renameFileTreeDialog;
        function renameDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback) {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, false, "Rename Directory");
        }
        DialogBuilder.renameDirectoryTreeDialog = renameDirectoryTreeDialog;
        function newFileTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback) {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, true, "New File");
        }
        DialogBuilder.newFileTreeDialog = newFileTreeDialog;
        function newDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback) {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, true, "New Directory");
        }
        DialogBuilder.newDirectoryTreeDialog = newDirectoryTreeDialog;
        function createArchiveTreeDialog(resourceDetails, saveCallback, ignoreOrCancelCallback) {
            createProjectDialog(resourceDetails, true, saveCallback, ignoreOrCancelCallback, false, "Create Archive");
        }
        DialogBuilder.createArchiveTreeDialog = createArchiveTreeDialog;
        function createProjectDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, nameIsBlank, dialogTitle) {
            createTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, nameIsBlank, dialogTitle, "/" + common_1.Common.getProjectName());
        }
        function createTreeDialog(resourceDetails, foldersOnly, saveCallback, ignoreOrCancelCallback, nameIsBlank, dialogTitle, treePath) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var dialogExpandPath = "/";
            if (resourceDetails != null) {
                dialogExpandPath = resourceDetails.getProjectDirectory(); // /src/blah
            }
            var dialogBody = createFileSelectionDialogLayout(dialogExpandPath, '');
            var focusInput = function () {
                var element = document.getElementById('dialogPath');
                element.contentEditable = "true";
                element.focus();
            };
            var createFinalPath = function () {
                var originalDialogFileName = $('#dialogPath').html();
                var originalDialogFolder = $('#dialogFolder').html();
                var dialogPathName = tree_1.FileTree.cleanResourcePath(originalDialogFileName);
                var dialogFolder = tree_1.FileTree.cleanResourcePath(originalDialogFolder);
                var dialogProjectPath = dialogFolder + "/" + dialogPathName; // /src/blah/script.tern
                var dialogPathDetails = tree_1.FileTree.createResourcePath(dialogProjectPath);
                return dialogPathDetails;
            };
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogSave" class="btn dialogButton">Save</button><button id="dialogCancel" class="btn dialogButton">Cancel</button>',
                width: Math.max(500, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: true,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init();
                        focusInput();
                    }, 200);
                },
                onClose: function (event) {
                    console.log('close');
                },
                onMax: function (event) {
                    console.log('max');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onMin: function (event) {
                    console.log('min');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onKeydown: function (event) {
                    console.log('keydown');
                }
            });
            $("#dialogSave").click(function () {
                if (saveCallback) {
                    var dialogPathDetails = createFinalPath();
                    saveCallback(dialogPathDetails);
                }
                w2ui_1.w2popup.close();
            });
            $("#dialogCancel").click(function () {
                if (ignoreOrCancelCallback) {
                    var dialogPathDetails = createFinalPath();
                    ignoreOrCancelCallback(dialogPathDetails);
                }
                w2ui_1.w2popup.close();
            });
            if (resourceDetails != null) {
                $('#dialogFolder').html(tree_1.FileTree.cleanResourcePath(resourceDetails.getProjectDirectory())); // /src/blah
                if (!nameIsBlank) {
                    $('#dialogPath').html(tree_1.FileTree.cleanResourcePath(resourceDetails.getFileName())); // script.tern
                }
            }
            tree_1.FileTree.createTree(treePath, "dialog", "dialogTree", dialogExpandPath, foldersOnly, null, function (event, data) {
                var selectedFileDetails = tree_1.FileTree.createResourcePath(data.node.tooltip);
                if (data.node.isFolder()) {
                    $('#dialogFolder').html(tree_1.FileTree.cleanResourcePath(selectedFileDetails.getProjectDirectory()));
                }
                else {
                    $('#dialogFolder').html(tree_1.FileTree.cleanResourcePath(selectedFileDetails.getProjectDirectory())); // /src/blah
                    $('#dialogPath').html(tree_1.FileTree.cleanResourcePath(selectedFileDetails.getFileName())); // file.tern
                }
            });
        }
        function createTreeOpenDialog(openCallback, closeCallback, dialogTitle, buttonText, treePath) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var completeFunction = function () {
                var originalDialogFolder = $('#dialogPath').html();
                var dialogFolder = tree_1.FileTree.cleanResourcePath(originalDialogFolder); // clean up path
                var dialogPathDetails = tree_1.FileTree.createResourcePath(dialogFolder);
                var selectedDirectory = dialogPathDetails.getProjectDirectory();
                if (selectedDirectory.indexOf("/") == 0) {
                    selectedDirectory = selectedDirectory.substring(1);
                }
                openCallback(dialogPathDetails, selectedDirectory);
            };
            var dialogBody = createFileFolderSelectionDialogLayout();
            var focusInput = function () {
                var element = document.getElementById('dialogPath');
                element.contentEditable = "true";
                element.focus();
            };
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogSave" class="btn dialogButton">' + buttonText + '</button>',
                width: Math.max(500, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: true,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init();
                        focusInput();
                    }, 200);
                },
                onClose: function (event) {
                    closeCallback(); // this should probably be a parameter
                },
                onMax: function (event) {
                    console.log('max');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onMin: function (event) {
                    console.log('min');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onKeydown: function (event) {
                    console.log('keydown');
                }
            });
            $("#dialogSave").click(function () {
                completeFunction();
                w2ui_1.w2popup.close();
            });
            tree_1.FileTree.createTreeOfDepth(treePath, "dialog", "dialogTree", "/" + common_1.Common.getProjectName(), true, null, function (event, data) {
                var selectedFileDetails = tree_1.FileTree.createResourcePath(data.node.tooltip);
                var selectedDirectory = selectedFileDetails.getProjectDirectory();
                if (selectedDirectory.indexOf("/") == 0) {
                    selectedDirectory = selectedDirectory.substring(1);
                }
                $('#dialogPath').html(tree_1.FileTree.cleanResourcePath(selectedDirectory));
            }, 2);
        }
        DialogBuilder.createTreeOpenDialog = createTreeOpenDialog;
        function createListDialog(listFunction, patternList, dialogTitle, initialLoadText, closeFunction) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var dialogBody = createListDialogLayout();
            var focusInput = function () {
                var element = document.getElementById('dialogPath');
                element.contentEditable = "true";
                element.focus();
            };
            var updateList = function (expressionText, isSubmit) {
                var expressionPattern = null;
                if (patternList) {
                    expressionPattern = extractTextFromElement("dialogFolder");
                }
                listFunction(expressionText, expressionPattern, function (list, requestedExpression) {
                    var currentExpression = extractTextFromElement("dialogPath");
                    if (!requestedExpression || requestedExpression == currentExpression) {
                        var content = createDialogListTable(list);
                        if (content.content) {
                            $("#dialog").html(content.content);
                        }
                        else {
                            $("#dialog").html('');
                        }
                        // this is kind of crap, but we need to be sure the html is rendered before binding
                        if (content.init) {
                            setTimeout(content.init, 100); // register the init function to run 
                        }
                    }
                }, isSubmit);
            };
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogCancel" class="btn dialogButton">Cancel</button>',
                width: Math.max(800, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: true,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init();
                        $('#dialogPath').on('change keyup paste', function (event) {
                            var expressionText = extractTextFromElement("dialogPath");
                            if (isKeyReturn(event)) {
                                updateList(expressionText, true); // submit text
                            }
                            // add a delay before you execute
                            executeIfTextUnchanged(expressionText, "dialogPath", 300, function () {
                                updateList(expressionText, false);
                            });
                        });
                        focusInput();
                        if (initialLoadText || initialLoadText == "") {
                            setTimeout(function () {
                                updateList(initialLoadText, false);
                            }, 100);
                        }
                    }, 200);
                },
                onMax: function (event) {
                    console.log('max');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onMin: function (event) {
                    console.log('min');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onClose: function (event) {
                    if (closeFunction) {
                        closeFunction();
                    }
                }
            });
            $("#dialogSave").click(function () {
                if (closeFunction) {
                    closeFunction();
                }
                w2ui_1.w2popup.close();
            });
            $("#dialogCancel").click(function () {
                if (closeFunction) {
                    closeFunction();
                }
                w2ui_1.w2popup.close();
            });
        }
        DialogBuilder.createListDialog = createListDialog;
        function createTextSearchOnlyDialog(listFunction, fileFilterPatterns, dialogTitle) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var focusInput = function () {
                var element = document.getElementById('searchText');
                element.contentEditable = "true";
                element.focus();
            };
            var executeSearch = function () {
                var expressionText = extractTextFromElement("searchText");
                // add a delay before you execute
                executeIfTextUnchanged(expressionText, "searchText", 300, function () {
                    var searchCriteria = {
                        caseSensitive: isCheckboxSelected("inputCaseSensitive"),
                        regularExpression: isCheckboxSelected("inputRegularExpression"),
                        wholeWord: isCheckboxSelected("inputWholeWord")
                    };
                    var expressionPattern = null;
                    if (fileFilterPatterns) {
                        expressionPattern = extractTextFromElement("fileFilterPatterns");
                    }
                    listFunction(expressionText, expressionPattern, searchCriteria, function (list, requestedText) {
                        var currentText = extractTextFromElement("searchText");
                        if (!requestedText || currentText == requestedText) {
                            var content = createDialogListTable(list);
                            if (content.content) {
                                $("#dialog").html(content.content);
                            }
                            else {
                                $("#dialog").html('');
                            }
                            // this is kind of crap, but we need to be sure the html is rendered before binding
                            if (content.init) {
                                setTimeout(content.init, 100); // register the init function to run 
                            }
                        }
                    });
                });
            };
            var dialogBody = createTextSearchOnlyDialogLayout(fileFilterPatterns, '', executeSearch);
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogCancel" class="btn dialogButton">Cancel</button>',
                width: Math.max(800, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: true,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init();
                        $('#searchText').on('change keyup paste', executeSearch);
                        //               $('#inputCaseSensitive').change(executeSearch);
                        //               $('#inputRegularExpression').change(executeSearch);
                        //               $('#inputWholeWord').change(executeSearch);
                        focusInput();
                    }, 200);
                },
                onMax: function (event) {
                    console.log('max');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onMin: function (event) {
                    console.log('min');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                }
            });
            $("#dialogSave").click(function () {
                w2ui_1.w2popup.close();
            });
            $("#dialogCancel").click(function () {
                w2ui_1.w2popup.close();
            });
        }
        DialogBuilder.createTextSearchOnlyDialog = createTextSearchOnlyDialog;
        function createTextSearchAndReplaceDialog(listFunction, fileFilterPatterns, dialogTitle) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var focusInput = function () {
                var element = document.getElementById('searchText');
                element.contentEditable = "true";
                element.focus();
            };
            var executeSearch = function () {
                var expressionText = extractTextFromElement("searchText");
                // add a delay before you execute
                executeIfTextUnchanged(expressionText, "searchText", 300, function () {
                    var searchCriteria = {
                        caseSensitive: isCheckboxSelected("inputCaseSensitive"),
                        regularExpression: isCheckboxSelected("inputRegularExpression"),
                        wholeWord: isCheckboxSelected("inputWholeWord")
                    };
                    var expressionPattern = null;
                    if (fileFilterPatterns) {
                        expressionPattern = extractTextFromElement("fileFilterPatterns");
                    }
                    listFunction(expressionText, expressionPattern, searchCriteria, function (list, requestedText) {
                        var currentText = extractTextFromElement("searchText");
                        if (!requestedText || currentText == requestedText) {
                            var content = createDialogListTable(list);
                            if (content.content) {
                                $("#dialog").html(content.content);
                            }
                            else {
                                $("#dialog").html('');
                            }
                            // this is kind of crap, but we need to be sure the html is rendered before binding
                            if (content.init) {
                                setTimeout(content.init, 100); // register the init function to run 
                            }
                        }
                    });
                });
            };
            var dialogBody = createTextSearchAndReplaceDialogLayout(fileFilterPatterns, '', executeSearch);
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogSave" class="btn dialogButton">Replace</button><button id="dialogCancel" class="btn dialogButton">Cancel</button>',
                width: Math.max(800, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: true,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init();
                        $('#searchText').on('change keyup paste', executeSearch);
                        //               $('#inputCaseSensitive').change(executeSearch);
                        //               $('#inputRegularExpression').change(executeSearch);
                        //               $('#inputWholeWord').change(executeSearch);
                        focusInput();
                    }, 200);
                },
                onMax: function (event) {
                    console.log('max');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                },
                onMin: function (event) {
                    console.log('min');
                    $(window).trigger('resize');
                    event.onComplete = function () {
                        focusInput();
                    };
                }
            });
            $("#dialogSave").click(function () {
                var searchText = extractTextFromElement("searchText");
                var replaceText = extractTextFromElement("replaceText");
                var filePatterns = extractTextFromElement("fileFilterPatterns");
                var searchCriteria = {
                    caseSensitive: isCheckboxSelected("inputCaseSensitive"),
                    regularExpression: isCheckboxSelected("inputRegularExpression"),
                    wholeWord: isCheckboxSelected("inputWholeWord"),
                    enableReplace: true,
                    replace: replaceText
                };
                commands_1.Command.replaceTokenInFiles(searchText, searchCriteria, filePatterns);
                w2ui_1.w2popup.close();
            });
            $("#dialogCancel").click(function () {
                w2ui_1.w2popup.close();
            });
        }
        DialogBuilder.createTextSearchAndReplaceDialog = createTextSearchAndReplaceDialog;
        function createEvaluateDialog(inputText, dialogTitle) {
            var windowHeight = $(window).height(); // returns height of browser viewport
            var windowWidth = $(window).width(); // returns width of browser viewport
            var dialogBody = createGridDialogLayout(inputText ? common_1.Common.escapeHtml(inputText) : '');
            var focusInput = function () {
                var element = document.getElementById('dialogPath');
                element.contentEditable = "true";
                element.focus();
            };
            var executeEvaluation = function () {
                var text = $("#dialogPath").html();
                var expression = common_1.Common.clearHtml(text);
                commands_1.Command.browseScriptEvaluation([], expression, true); // clear the variables 
            };
            w2ui_1.w2popup.open({
                title: dialogTitle,
                body: dialogBody.content,
                buttons: '<button id="dialogSave" class="btn dialogButton">Evaluate</button>',
                width: Math.max(700, windowWidth / 2),
                height: Math.max(400, windowHeight / 2),
                overflow: 'hidden',
                color: '#999',
                speed: '0.3',
                opacity: '0.8',
                modal: false,
                showClose: true,
                showMax: true,
                onOpen: function (event) {
                    setTimeout(function () {
                        dialogBody.init(); // bind the functions
                        $('#dialog').w2grid({
                            recordTitles: false,
                            name: 'evaluation',
                            columns: [{
                                    field: 'name',
                                    caption: 'Name',
                                    size: '40%',
                                    sortable: false
                                }, {
                                    field: 'value',
                                    caption: 'Value',
                                    size: '30%',
                                    sortable: false
                                }, {
                                    field: 'type',
                                    caption: 'Type',
                                    size: '30%'
                                }],
                            onClick: function (event) {
                                var grid = this;
                                event.onComplete = function () {
                                    var sel = grid.getSelection();
                                    if (sel.length == 1) {
                                        var record = grid.get(sel[0]);
                                        var text = $("#dialogPath").html();
                                        var expression = common_1.Common.clearHtml(text);
                                        variables_1.VariableManager.toggleExpandEvaluation(record.path, expression);
                                    }
                                    grid.selectNone();
                                    grid.refresh();
                                };
                            }
                        });
                        focusInput();
                        setTimeout(function () {
                            variables_1.VariableManager.showVariables();
                        }, 200);
                    }, 200);
                },
                onClose: function (event) {
                    w2ui_1.w2ui['evaluation'].destroy(); // destroy grid so you can recreate it
                    //$("#dialog").remove(); // delete the element
                    variables_1.VariableManager.clearEvaluation();
                    commands_1.Command.browseScriptEvaluation([], "", true); // clear the variables
                },
                onMax: function (event) {
                    event.onComplete = function () {
                        w2ui_1.w2ui['evaluation'].refresh(); // resize
                        focusInput();
                    };
                    $(window).trigger('resize');
                },
                onMin: function (event) {
                    event.onComplete = function () {
                        w2ui_1.w2ui['evaluation'].refresh(); // resize
                        focusInput();
                    };
                    $(window).trigger('resize');
                },
                onKeydown: function (event) {
                    console.log('keydown');
                }
            });
            $("#dialogSave").click(function () {
                executeEvaluation();
            });
        }
        function createDialogListTable(list) {
            var content = "<table id='dialogListTable' class='dialogListTable' width='100%'>";
            var selectedIndex = selectedIndexOfDialogListTable();
            var mouseOverFunctions = {};
            var clickFunctions = {};
            for (var i = 0; i < list.length; i++) {
                var dialogListEntryId = "dialogListEntry" + i;
                var row = list[i];
                content += "<tr ";
                if (i == selectedIndex) {
                    content += " class='dialogListTableRowSelected' ";
                }
                content += " id='" + dialogListEntryId + "'>";
                /*
                mouseOverFunctions[i] = function(rowId) {
                   var selectedIndex = selectedIndexOfDialogListTable();
                   selectDialogListTableRow(selectedIndex, rowId);
                };
                */
                var _loop_1 = function() {
                    var cell = row[j];
                    var entryId = "listEntry_" + i + "_" + j;
                    if (j > 0) {
                        content += "<td>&nbsp;&nbsp;</td>"; // if there is overflow we should show a space
                    }
                    content += "<td width='50%' nowrap><div id='" + entryId + "' class='";
                    content += cell.style;
                    content += "'>";
                    content += cell.text;
                    content += "</div></td>";
                    clickFunctions[i] = function () {
                        if (cell.line) {
                            return submitDialogListResource(cell.resource, cell.line);
                        }
                        else {
                            return submitDialogListResource(cell.link);
                        }
                    };
                };
                for (var j = 0; j < row.length; j++) {
                    _loop_1();
                }
                content += "<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>";
                content += "</tr>";
            }
            content += "</table>";
            return {
                content: content,
                init: function () {
                    // initialize all functions
                    var _loop_2 = function() {
                        if (clickFunctions.hasOwnProperty(clickFunctionId)) {
                            var clickFunction_1 = clickFunctions[clickFunctionId];
                            var rowId = clickFunctionId;
                            $('#dialogListEntry' + rowId).on('click', function (e) {
                                return clickFunction_1();
                            });
                        }
                    };
                    for (var clickFunctionId in clickFunctions) {
                        _loop_2();
                    }
                    var _loop_3 = function() {
                        if (mouseOverFunctions.hasOwnProperty(mouseOverFunctionId)) {
                            var mouseOverFunction_1 = mouseOverFunctions[mouseOverFunctionId];
                            var rowId_1 = mouseOverFunctionId;
                            $('#dialogListEntry' + rowId_1).on('mouseenter', function (e) {
                                return mouseOverFunction_1(rowId_1);
                            });
                        }
                    };
                    for (var mouseOverFunctionId in mouseOverFunctions) {
                        _loop_3();
                    }
                }
            };
        }
        function navigateDialogListTable(event) {
            if (isKeyReturn(event)) {
                var selectedRowIndex = selectedIndexOfDialogListTable();
                if (selectedRowIndex >= 0) {
                    $('#dialogListEntry' + selectedRowIndex).click(); // click on return
                }
            }
            else if (isKeyDown(event)) {
                navigateDialogListTableDown();
            }
            else if (isKeyUp(event)) {
                navigateDialogListTableUp();
            }
        }
        function navigateDialogListTableDown() {
            var selectedRowIndex = selectedIndexOfDialogListTable();
            if (selectedRowIndex == -1) {
                selectDialogListTableRow(selectedRowIndex, 0);
            }
            else {
                selectDialogListTableRow(selectedRowIndex, selectedRowIndex + 1);
            }
        }
        function navigateDialogListTableUp() {
            var selectedRowIndex = selectedIndexOfDialogListTable();
            if (selectedRowIndex == -1) {
                selectDialogListTableRow(selectedRowIndex, 0);
            }
            else {
                selectDialogListTableRow(selectedRowIndex, selectedRowIndex - 1);
            }
        }
        function selectDialogListTableRow(selectedRowIndex, nextRowIndex) {
            var selectedRow = document.getElementById("dialogListEntry" + selectedRowIndex);
            var nextRow = document.getElementById("dialogListEntry" + nextRowIndex);
            if (nextRow) {
                var container = document.getElementById("dialog");
                var offsetY = common_1.Common.calculateScrollOffset(container, nextRow);
                console.log("offset: " + offsetY);
                if (selectedRow) {
                    selectedRow.className = "";
                }
                container.scrollTop = container.scrollTop + offsetY;
                nextRow.className = "dialogListTableRowSelected";
            }
        }
        function selectedIndexOfDialogListTable() {
            var table = document.getElementById("dialogListTable");
            if (table) {
                var dialogRows = table.rows;
                for (var i = 0; i < dialogRows.length; i++) {
                    var dialogRow = dialogRows[i];
                    if (dialogRow) {
                        if (dialogRow.classList.contains("dialogListTableRowSelected")) {
                            var rowIndex = dialogRow.id.replace("dialogListEntry", "");
                            return parseInt(rowIndex);
                        }
                    }
                }
            }
            return -1;
        }
        function createGridDialogLayout(inputText) {
            if (!inputText) {
                inputText = '';
            }
            return {
                content: '<div id="dialogContainerBig">' +
                    '   <div id="dialog" class="dialog"></div>' +
                    '</div>' +
                    '<div id="dialogPath" contenteditable="true">' + inputText + '</div>',
                init: function () {
                    $('#dialogPath').on('click', function (e) {
                        return focusDialogInput('dialogPath');
                    });
                    $('#dialogPath').on('paste', function (e) {
                        return pasteInPlainText('dialogPath', e);
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                }
            };
        }
        function createListDialogLayout() {
            return {
                content: '<div id="dialogContainerBig">' +
                    '   <div id="dialog" class="dialog"></div>' +
                    '</div>' +
                    '<div id="dialogPath" contenteditable="true"></div>',
                init: function () {
                    $('#dialogPath').on('click', function (e) {
                        return focusDialogInput('dialogPath');
                    });
                    $('#dialogPath').on('paste', function (e) {
                        return pasteInPlainText('dialogPath', e);
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                }
            };
        }
        function createFileFolderSelectionDialogLayout() {
            return {
                content: '<div id="dialogContainerBig">\n' +
                    '   <div id="dialog" class="dialogTree"></div>\n' +
                    '</div>\n' +
                    '<div id="dialogPath" contenteditable="true"></div>',
                init: function () {
                    $('#dialogPath').on('click', function (e) {
                        return focusDialogInput('dialogPath');
                    });
                    $('#dialogPath').on('paste', function (e) {
                        return pasteInPlainText('dialogPath', e);
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                }
            };
        }
        function createFileSelectionDialogLayout(selectedFileFolder, selectedFile, withArguments) {
            if (!selectedFileFolder) {
                selectedFileFolder = '';
            }
            if (!selectedFile) {
                selectedFile = '';
            }
            return {
                content: '<div id="dialogContainer">\n' +
                    '   <div id="dialog" class="dialogTree"></div>\n' +
                    '</div>\n' +
                    '<div id="dialogFolder">' + selectedFileFolder + '</div>\n' +
                    '<div id="dialogPath" contenteditable="true">' + selectedFile + '</div>',
                init: function () {
                    $('#dialogPath').on('click', function (e) {
                        return focusDialogInput('dialogPath');
                    });
                    $('#dialogPath').on('paste', function (e) {
                        return pasteInPlainText('dialogPath', e);
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                }
            };
        }
        function createTextSearchOnlyDialogLayout(fileFilterPatterns, searchText, refreshFunction) {
            if (!searchText) {
                searchText = '';
            }
            return {
                content: '<div id="dialogContainer">\n' +
                    '   <div id="dialog" class="dialog"></div>\n' +
                    '</div>\n' +
                    '<div id="fileFilterPatterns" class="searchFileFilterInputBox" contenteditable="true"">' + fileFilterPatterns + '</div>\n' +
                    '<div id="searchText" class="searchValueInputBox" contenteditable="true"">' + searchText + '</div>\n' +
                    '<div class="searchCheckBoxPanel">\n' +
                    '   <table border="0" cellspacing="5">\n' +
                    '      <tr id="inputCaseSensitiveRow">\n' +
                    '         <td><input type="checkbox" name="caseSensitive" id="inputCaseSensitive"><label></label>&nbsp;&nbsp;Case sensitive</td>\n' +
                    '      </tr>\n' +
                    '      <tr><td height="5px"></td></tr>\n' +
                    '      <tr id="inputRegularExpressionRow">\n' +
                    '         <td><input type="checkbox" name="regex" id="inputRegularExpression"><label></label>&nbsp;&nbsp;Regular expression</td>\n' +
                    '      </tr>\n' +
                    '      <tr><td height="5px"></td></tr>\n' +
                    '      <!--tr id="inputWholeWordRow">\n' +
                    '         <td><input type="checkbox" name="wholeWord" id="inputWholeWord"><label></label>&nbsp;&nbsp;Whole word</td>\n' +
                    '      </tr-->\n' +
                    '   </table>\n' +
                    '</div>',
                init: function () {
                    $('#fileFilterPatterns').on('click', function (e) {
                        return focusDialogInput('fileFilterPatterns');
                    });
                    $('#fileFilterPatterns').on('paste', function (e) {
                        return pasteInPlainText('fileFilterPatterns', e);
                    });
                    $('#searchText').on('click', function (e) {
                        return focusDialogInput('searchText');
                    });
                    $('#searchText').on('paste', function (e) {
                        return pasteInPlainText('searchText', e);
                    });
                    $('#inputCaseSensitiveRow').on('click', function (e) {
                        toggleCheckboxSelection('inputCaseSensitive');
                        refreshFunction();
                        return false;
                    });
                    $('#inputRegularExpressionRow').on('click', function (e) {
                        toggleCheckboxSelection('inputRegularExpression');
                        refreshFunction();
                        return false;
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                    //                     $('#inputWholeWordRow').on('click', function(e) {
                    //                        return toggleCheckboxSelection('inputWholeWord');
                    //                     });
                }
            };
        }
        function createTextSearchAndReplaceDialogLayout(fileFilterPatterns, searchText, refreshFunction) {
            if (!searchText) {
                searchText = '';
            }
            return {
                content: '<div id="dialogContainerSmall">\n' +
                    '   <div id="dialog" class="dialog"></div>\n' +
                    '</div>\n' +
                    '<div id="fileFilterPatterns" class="searchAndReplaceFileFilterInputBox" contenteditable="true"">' + fileFilterPatterns + '</div>\n' +
                    '<div id="searchText" class="searchAndReplaceValueInputBox" contenteditable="true">' + searchText + '</div>\n' +
                    '<div id="replaceText" class="searchAndReplaceInputBox" contenteditable="true"></div>\n' +
                    '<div class="searchAndReplaceCheckBoxPanel">\n' +
                    '   <table border="0" cellspacing="5">\n' +
                    '      <tr id="inputCaseSensitiveRow">\n' +
                    '         <td><input type="checkbox" name="caseSensitive" id="inputCaseSensitive"><label></label>&nbsp;&nbsp;Case sensitive</td>\n' +
                    '      </tr>\n' +
                    '      <tr><td height="5px"></td></tr>\n' +
                    '      <tr id="inputRegularExpressionRow">\n' +
                    '         <td><input type="checkbox" name="regex" id="inputRegularExpression"><label></label>&nbsp;&nbsp;Regular expression</td>\n' +
                    '      </tr>\n' +
                    '      <tr><td height="5px"></td></tr>\n' +
                    '      <!--tr id="inputWholeWordRow">\n' +
                    '         <td><input type="checkbox" name="wholeWord" id="inputWholeWord"><label></label>&nbsp;&nbsp;Whole word</td>\n' +
                    '      </tr-->\n' +
                    '   </table>\n' +
                    '</div>',
                init: function () {
                    $('#fileFilterPatterns').on('click', function (e) {
                        return focusDialogInput('fileFilterPatterns');
                    });
                    $('#fileFilterPatterns').on('paste', function (e) {
                        return pasteInPlainText('fileFilterPatterns', e);
                    });
                    $('#searchText').on('click', function (e) {
                        return focusDialogInput('searchText');
                    });
                    $('#searchText').on('paste', function (e) {
                        return pasteInPlainText('searchText', e);
                    });
                    $('#replaceText').on('click', function (e) {
                        return focusDialogInput('replaceText');
                    });
                    $('#replaceText').on('paste', function (e) {
                        return pasteInPlainText('replaceText', e);
                    });
                    $('#inputCaseSensitiveRow').on('click', function (e) {
                        toggleCheckboxSelection('inputCaseSensitive');
                        refreshFunction();
                        return false;
                    });
                    $('#inputRegularExpressionRow').on('click', function (e) {
                        toggleCheckboxSelection('inputRegularExpression');
                        refreshFunction();
                        return false;
                    });
                    $('#w2ui-popup').on('keydown', function (e) {
                        navigateDialogListTable(e);
                        return submitDialog(e);
                    });
                    //            $('#inputWholeWordRow').on('click', function(e) {
                    //               return toggleCheckboxSelection('inputWholeWord');
                    //            });
                }
            };
        }
        function submitDialogListResource(resource, line) {
            $("#dialogCancel").click(); // force the click
            if (line) {
                explorer_1.FileExplorer.openTreeFile(resource, function () {
                    window.setTimeout(function () {
                        editor_1.FileEditor.showEditorLine(line);
                    }, 100); // delay focus on line, some bug here that needs a delay
                });
            }
            else {
                location.href = resource;
            }
            return false;
        }
        function focusDialogInput(name) {
            document.getElementById(name).contentEditable = "true";
            document.getElementById(name).focus();
            document.getElementById(name).focus();
            return true;
        }
        function pasteInPlainText(name, event) {
            if (event && event.originalEvent) {
                var text = event.originalEvent.clipboardData.getData("text/plain");
                var element = document.getElementById(name);
                if (text && element) {
                    text = text.replace(/[\n\r]/g, '');
                    text = common_1.Common.escapeHtml(text);
                    element.innerHTML = text;
                    return false;
                }
            }
            return true;
        }
        function executeIfTextUnchanged(text, nameOfElement, delay, functionToExecute) {
            setTimeout(function () {
                var currentText = $("#" + nameOfElement).html();
                if (text == currentText) {
                    functionToExecute();
                }
                else {
                    console.log("Ignoring '" + text + "' as its not current");
                }
            }, delay);
        }
        function extractTextFromElement(id) {
            var inputField = document.getElementById(id);
            if (inputField) {
                var value = inputField.innerHTML;
                if (value) {
                    return common_1.Common.clearHtml(value);
                }
            }
            return "";
        }
        function isCheckboxSelected(input) {
            var inputField = document.getElementById(input);
            if (inputField) {
                return inputField.checked;
            }
            return false;
        }
        function toggleCheckboxSelection(input) {
            var inputField = document.getElementById(input);
            if (inputField) {
                inputField.checked = !inputField.checked;
            }
            return false;
        }
        function isKeyUp(e) {
            var evt = e || window.event;
            return evt.keyCode === 38;
        }
        function isKeyDown(e) {
            var evt = e || window.event;
            return evt.keyCode === 40;
        }
        function isKeyReturn(e) {
            var evt = e || window.event;
            return evt.keyCode === 13;
        }
        function submitDialog(e) {
            var evt = e || window.event;
            // "e" is the standard behavior (FF, Chrome, Safari, Opera),
            // while "window.event" (or "event") is IE's behavior
            if (isKeyReturn(e)) {
                $("#dialogSave").click(); // force the click
                return false;
            }
        }
    })(DialogBuilder = exports.DialogBuilder || (exports.DialogBuilder = {}));
});
//ModuleSystem.registerModule("dialog", "Dialog module: dialog.js", null, null, [ "common", "tree" ]);
