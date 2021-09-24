# Wiki Stats Demo app

> Created for Sytac DevJam 2021: https://2021.devjam.io/

## About

This is a basic Kafka Streams application that aggregates Wikipedia events by counting per namespace.

Producing/consuming events is done from Jupyter notebooks (Docker image included).

## Install and run

1. `docker-compose up -d zookeeper`
2. `docker-compose up -d kafka`
3. `./create-topics.sh`
4. `docker-compose up jupyter` - find the link with token in log output, open in browser
5. `docker-compose up streams` to start Streams App
6. Now, open notebook "Wiki events producer" and run it
7. Finally, open notebook "Wiki stats consumer" and run it to render a histogram
