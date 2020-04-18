#!/usr/bin/env bash

set -e

HOST="167.172.101.7"

sbt debian:packageBin
ssh-keyscan -H ${HOST} >> ~/.ssh/known_hosts
scp target/server_1.0-SNAPSHOT_all.deb root@${HOST}:~/server_1.0-SNAPSHOT_all.deb
ssh root@${HOST} "dpkg -i server_1.0-SNAPSHOT_all.deb && service server restart"w