#!/bin/bash
mvn clean install
docker compose -f docker-compose.yaml -f docker-compose-dev.yaml up -d --build