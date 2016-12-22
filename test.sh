#!/bin/sh

docker-compose up -d postgres_scala_server_test

docker-compose build scala-server-test

docker-compose run scala-server-test

docker-compose down
