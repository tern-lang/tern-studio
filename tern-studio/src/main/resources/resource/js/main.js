require.config({
    paths: {
        'ace': 'external/ace/ace',
        'ext-language_tools': 'external/ace/ext-language_tools',
        'jquery': 'external/jquery${JS_EXTENSION}',
        'jquery-ui': 'external/jquery-ui${JS_EXTENSION}',
        'jquery-context-menu': 'external/jquery-context-menu${JS_EXTENSION}',
        'mousetrap': 'external/mousetrap',
        'mousetrap-global-bind': 'external/mousetrap-global-bind',
        'spin': 'external/spin',
        'fancytree': 'external/fancytree',
        'fancytree-dnd': 'external/fancytree.dnd',
        'w2ui': 'external/w2ui',
        'filesaver': 'external/filesaver${JS_EXTENSION}',
        'md5': 'external/md5',
        'alert': './alert${JS_EXTENSION}',
        'commands': './commands${JS_EXTENSION}',
        'common': './common${JS_EXTENSION}',
        'console': './console${JS_EXTENSION}',
        'debug': './debug${JS_EXTENSION}',
        'dialog': './dialog${JS_EXTENSION}',
        'editor': './editor${JS_EXTENSION}',
        'explorer': './explorer${JS_EXTENSION}',
        'history': './history${JS_EXTENSION}',
        'keys': './keys${JS_EXTENSION}',
        'problem': './problem${JS_EXTENSION}',
        'profiler': './profiler${JS_EXTENSION}',
        'project': './project${JS_EXTENSION}',
        'select': './select${JS_EXTENSION}',
        'socket': './socket${JS_EXTENSION}',
        'spinner': './spinner${JS_EXTENSION}',
        'status': './status${JS_EXTENSION}',
        'threads': './threads${JS_EXTENSION}',
        'tree': './tree${JS_EXTENSION}',
        'variables': './variables${JS_EXTENSION}'
    },
    shim: {
        "w2ui": {
            deps: ['jquery'],
            exports: 'w2ui',
            init: function () {
                return {
                    'w2ui': w2ui,
                    'w2obj': w2obj,
                    'w2utils': w2utils,
                    'w2popup': w2popup,
                    'w2confirm': w2confirm,
                    'w2alert': w2alert
                };
            }
        },
        "spin": {
            exports: '$'
        },
        "fancytree": {
            deps: ["jquery", "jquery-ui"]
        },
        "fancytree-dnd": {
            deps: ["fancytree"]
        },
        "jquery-ui": {
            deps: ["jquery"],
            exports: 'ui',
            init: function () {
                return {
                    'ui': ui
                };
            }
        },
        "jquery-context-menu": {
            deps: ["jquery", "jquery-ui"]
        },
        "ace": {
            exports: 'ace',
            init: function () {
                return {
                    'ace': ace
                };
            }
        },
        "ext-language_tools": {
            deps: ["ace"]
        },
        "mousetrap": {
            exports: 'Mousetrap',
            init: function () {
                return {
                    'Mousetrap': Mousetrap
                };
            }
        },
        "mousetrap-global-bind": {
            deps: ["mousetrap"]
        }
    }
});
define(["require",
    "exports",
    "fancytree",
    "fancytree-dnd",
    'jquery-ui',
    "jquery-context-menu",
    "ace",
    "ext-language_tools",
    "mousetrap",
    "mousetrap-global-bind",
    "socket",
    "spinner",
    "project",
    "problem",
    "explorer",
    "editor",
    "history",
    "console",
    "threads",
    "debug",
    "profiler",
    "alert",
    "select"], function (require, exports, fancytree, fancytreeDnd, // force load
    jqueryUi, // foce load
    jqueryContextMenu, // force load
    ace, aceLanguageTools, // force load
    mousetrap, mousetrapBindGlobal, // force load
    socket, spinner, project, problem, explorer, editor, history, console, threads, debug, profiler, alert, select) {
    "use strict";
    var path = window.location.pathname;
    if (path == "/") {
        select.ProjectSelector.showProjectDialog();
    }
    else {
        var setupFunction = function () {
            alert.Alerts.registerAlerts();
            console.ProcessConsole.registerConsole();
            explorer.FileExplorer.showTree();
            editor.FileEditor.createEditor();
        };
        var startFunction = function () {
            history.History.trackHistory();
            threads.ThreadManager.createThreads();
            debug.DebugManager.createStatus();
            profiler.Profiler.startProfiler();
            problem.ProblemManager.registerProblems();
            socket.EventBus.startSocket();
        };
        spinner.LoadSpinner.create();
        project.Project.createMainLayout(setupFunction, startFunction);
    }
});
