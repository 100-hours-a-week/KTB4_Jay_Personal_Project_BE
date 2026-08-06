#!/bin/bash

set -a
source .env
set +a

PORT=${1:-8080}

./gradlew bootRun --args="--server.port=${PORT}"