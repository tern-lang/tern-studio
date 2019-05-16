hterm.defaultStorage = new lib.Storage.Memory();

var terminal = new hterm.Terminal("cloudterm");
var session = null; // this is a connected web socket	
var ready = false;
var init = false;
var dimension = null;
var application = {
	
	onStart: function(socket) {
		session = socket; // we are now connected
		
		if(init) {
			onfTerminalInit();
		}
		if(ready) {
			onTerminalReady();
		}
		if(dimension) {
			resizeTerminal(dimension.columns, dimension.rows);
		}
	},
    
	onTerminalInit: function() {
        if(session) {
        	session.send(action("TERMINAL_INIT"));
        }
        init = true;
    },
    
    onCommand: function(command) {
    	if(session) {
    		session.send(action("TERMINAL_COMMAND", {
	            command
	        }));
    	} 
    },
    
    resizeTerminal: function(columns, rows) {
        if(session) {
        	dimension = {
        	    columns: columns,
        	    rows: rows
        	};
        	session.send(action("TERMINAL_RESIZE", {
	            columns, rows
	        }));
        }
    },
    
    onTerminalReady: function() {
        if(session) {
        	session.send(action("TERMINAL_READY"));
        }
        ready = true;
    }
};

setPreferences();
openSocket();

function action(type, data) {
    var action = Object.assign({
        type
    }, data);

    return JSON.stringify(action);
}

function setPreferences() {
	terminal.getPrefs().set("send-encoding", "utf-8");
	terminal.getPrefs().set("receive-encoding", "utf-8");
	
	// terminal.getPrefs().set("use-default-window-copy", true);
	terminal.getPrefs().set("clear-selection-after-copy", true);
	terminal.getPrefs().set("copy-on-select", true);
	terminal.getPrefs().set("ctrl-c-copy", true);
	terminal.getPrefs().set("ctrl-v-paste", true);
	// terminal.getPrefs().set("cursor-color", "black");
	// terminal.getPrefs().set("background-color", "white");
	// terminal.getPrefs().set("font-size", 12);
	// terminal.getPrefs().set("foreground-color", "black");
	// terminal.getPrefs().set("cursor-blink", false);
	// terminal.getPrefs().set("scrollbar-visible", true);
	// terminal.getPrefs().set("scroll-wheel-move-multiplier", 0.1);
	// terminal.getPrefs().set("user-css", "/afx/resource/?p=css/hterm.css");
	terminal.getPrefs().set("enable-clipboard-notice", true);
	
	terminal.onTerminalReady = function () {
	
	    application.onTerminalInit();
	
	    var io = terminal.io.push();
	
	    io.onVTKeystroke = function (str) {
	        application.onCommand(str);
	    };
	
	    io.sendString = io.onVTKeystroke;
	
	    io.onTerminalResize = function (columns, rows) {
	        application.resizeTerminal(columns, rows);
	    };
	
	    terminal.installKeyboard();
	    application.onTerminalReady();
	
	}
}

function openSocket() {
	var protocol = "ws://";
	
	if (window.document.location.protocol.indexOf("https") == 0) {
		protocol = "wss://";
	}
	var segments = window.document.location.pathname.split("/");
	var address = protocol + location.host;
	
	for(var i = 1; i < segments.length - 1; i++) {
		address += "/" + segments[i];
	}
	
	address += "/session";
	var socket = new WebSocket(address);
	
	terminal.decorate(document.querySelector('#terminal'));
	
	socket.onopen = () => {
		application.onStart(socket);
		terminal.decorate(document.querySelector('#terminal'));
	    terminal.showOverlay("Connection established", 1000);
	}
	
	socket.onerror = () => {
	    terminal.showOverlay("Connection error", 3000);
	}
	
	socket.onclose = () => {
	    terminal.showOverlay("Connection closed", 3000);
	}
	
	socket.onmessage = (e) => {
	    var data = JSON.parse(e.data);
	    switch (data.type) {
	        case "TERMINAL_PRINT":
	            terminal.io.print(data.text);
	    }
	}
}
