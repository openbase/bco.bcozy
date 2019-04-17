#!/bin/bash
set -ev
echo '### start deployment...'
if [ "${AUTO_DEPLOY}" = False ] ; then
    echo '### Skip deloyment because auto deployment is disabled.'
else 
    mvn deploy -Pdeploy,sonatype --settings .travis/settings.xml -DskipTests=true -B
    echo '### deployment successfully finished'
fi
