#!/bin/bash

# Load environment variables from .env file
load_env_variables() {
    if [ -f ".env" ]; then
        export $(cat .env | xargs)
    fi
}

# Set app config
set_app_config() {
    APPLICATION_PORT="$1"
    APPLICATION_NAME="$2"
    APPLICATION_VERSION=1.0.0
    APPLICATION_PATH="target/$APPLICATION_NAME-$APPLICATION_VERSION.jar"
    PID_FILE_NAME="$APPLICATION_NAME.pid"
    LOGS_FILE_NAME="$APPLICATION_NAME.log"
}

# Function to start the application
start_app() {
    local CHILD_MODULE_DIR="$1"

    # Navigate to the child module directory
    cd "$CHILD_MODULE_DIR" || { echo "Cannot change directory to '$CHILD_MODULE_DIR'"; exit 1; }

    # Validate application path
    if [ ! -f "$APPLICATION_PATH" ]; then
        echo "$APPLICATION_NAME start operation error"
        echo "Incorrect application path '$APPLICATION_PATH', or test/build phase failed"
        exit 1
    fi

    # Check if the port is free
    if lsof -t "-i:$APPLICATION_PORT" -sTCP:LISTEN &> /dev/null; then
        echo "$APPLICATION_NAME start operation error"
        echo "Port $APPLICATION_PORT is in use."
        exit 1
    fi

    # Start the application
    nohup ../mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dserver.port="$APPLICATION_PORT" >> "../$LOGS_FILE_NAME" 2>&1 &
    APP_PID=$!
    echo "Starting $APPLICATION_NAME with PID $APP_PID"

    # Wait for the application to bind to the port
    for i in {1..30}; do
        echo "$i: $APPLICATION_NAME - $APPLICATION_PORT"
        if lsof -t "-i:$APPLICATION_PORT" -sTCP:LISTEN &> /dev/null; then
            echo "$APPLICATION_NAME STARTED"
            echo "Monitor application output with: tail -500 '$LOGS_FILE_NAME'"
            echo "$APP_PID" > "../$PID_FILE_NAME" || echo "Save PID $APP_PID in '../$PID_FILE_NAME' file operation error."
            cd ".."
            return 0
        fi
        sleep 1
    done

#    # If the application failed to start
#    echo "$APPLICATION_NAME failed to start on port $APPLICATION_PORT"
#    if [ -f "$PID_FILE_NAME" ]; then
#        APP_PID=$(cat "$PID_FILE_NAME")
#        if ps -p $APP_PID > /dev/null; then
#            echo "Stopping $APPLICATION_NAME with PID $APP_PID"
#            kill "$APP_PID"
#            rm "$PID_FILE_NAME"
#        else
#            echo "$APPLICATION_NAME with PID $APP_PID is not running"
#            rm "$PID_FILE_NAME"
#        fi
#    else
#        echo "No PID file found at $PID_FILE_NAME"
#    fi
#
#    kill "$APP_PID" 2>/dev/null || echo "Failed to kill PID $APP_PID"
#    exit 1
}

# Start all applications
load_env_variables

./mvnw clean install -DskipTests || { echo "Build failed"; exit 1; }

set_app_config 8000 "daily-lexika"
start_app "$APPLICATION_NAME"

set_app_config 8080 "admin"
start_app "$APPLICATION_NAME"
