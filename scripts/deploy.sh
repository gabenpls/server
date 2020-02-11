#!/usr/bin/env bash

set -e

sbt debian:packageBin
rsync target/server_1.0-SNAPSHOT_all.deb root@167.172.101.7:~/ -v -r --update
ssh root@167.172.101.7 "dpkg -i server_1.0-SNAPSHOT_all.deb && service server restart"