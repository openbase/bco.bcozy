#!/bin/bash
set -ev
echo ### start deployment...
mvn deploy -Pdeploy,sonatype --settings .travis/settings.xml -DskipTests=true -B
echo ### deployment successfully finished
