#!/bin/sh

cp ./src/main/resources/application.conf ./config/application.conf

docker-compose up -d postgres_scala_server_dev

docker-compose build scala-server-dev

docker-compose up -d scala-server-dev
