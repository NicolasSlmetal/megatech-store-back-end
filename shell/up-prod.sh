#!/bin/bash
mvn clean install
docker compose -f docker-compose.yaml -f docker-compose-prod.yaml up -d --build