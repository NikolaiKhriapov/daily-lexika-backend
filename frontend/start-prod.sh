#!/bin/bash
until lt npx localtunnel --port 3000 --subdomain learn-chinese
do
  echo "Try again"
done

# curl ipv4.icanhazip.com
