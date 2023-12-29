#!/bin/bash

until lt npx localtunnel --port 8000 --subdomain learn-chinese-api
do
  echo "Try again"
done
