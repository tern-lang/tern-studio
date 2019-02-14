@echo off

WORKSPACE_PATH=c:/Work/development/ternlang/snap-develop/snap-studio/work

start javaw -jar ternd.jar --directory=$WORKSPACE_PATH --port=4457 --agent-pool=4 --log-level=INFO --client-debug=false
