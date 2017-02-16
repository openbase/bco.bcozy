#!/bin/bash
set -ev
echo ### start deployment...
mvn deploy -Pdeploy,sonatype --settings .travis/settings.xml -DskipTests=true -B -q -U
echo ### deployment successfully finished
