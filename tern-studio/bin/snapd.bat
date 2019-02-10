@echo off

WORKSPACE_PATH=c:/Work/development/snapscript/snap-develop/snap-studio/work

start javaw -jar snapd.jar --directory=$WORKSPACE_PATH --port=4457 --agent-pool=4 --log-level=INFO --client-debug=false 
