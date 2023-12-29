#!/bin/bash

. ./config.sh # config data

./mvnw clean install # includes test & install phases

# if there is no .jar file in target folder
if [ ! -f "$APPLICATION_PATH" ]; # if NOT OK
then
    echo "$APPLICATION_NAME START OPERATION ERROR"
    echo "Incorrect application path '$APPLICATION_PATH', or test / build phase failed"
    exit 1
fi

# if some app is already using this port
if lsof -t "-i:$APPLICATION_PORT" -sTCP:LISTEN &> /dev/null;
then
    echo "$APPLICATION_NAME START OPERATION ERROR"
    echo "Port $APPLICATION_PORT is in use.";
    exit 1
fi

nohup ./mvnw spring-boot:run -D spring-boot.run.profiles=env -D server.port="$APPLICATION_PORT" >> "$OUTPUT_PATH" 2>&1 &

if [ "$?" -eq 0 ];
then
    echo "$APPLICATION_NAME STARTED"
    echo "Monitor application output with: tail -500 '$OUTPUT_PATH'"
    echo "$!" > "$PID_PATH" || echo "Save PID $! in '$PID_PATH' file operation error."
    exit 0
else
    echo "$APPLICATION_NAME START OPERATION ERROR"
    exit 1
fi