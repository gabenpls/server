#!/usr/bin/env bash

set -e

sbt dist
rm -r target/universal/server-1.0-SNAPSHOT
unzip target/universal/server-1.0-SNAPSHOT.zip -d target/universal
rsync target/universal/server-1.0-SNAPSHOT/ root@167.172.101.7:~/dist/ -v -r --update