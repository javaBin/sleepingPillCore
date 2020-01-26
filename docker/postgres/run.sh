#!/usr/bin/env bash
docker run -d -p 5432:5432 --name sleepingpill-dev-pg sleepingpill-dev-pg-image:1.0
