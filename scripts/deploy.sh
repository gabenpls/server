#!/usr/bin/env bash

set -e

sbt debian:packageBin
scp target/server_1.0-SNAPSHOT_all.deb root@167.172.101.7:~/server_1.0-SNAPSHOT_all.deb
ssh root@167.172.101.7 "dpkg -i server_1.0-SNAPSHOT_all.deb && service server restart"