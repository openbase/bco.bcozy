#!/bin/bash

NC='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'
ORANGE='\033[0;33m'
BLUE='\033[0;34m'
WHITE='\033[0;37m'

export BCO_DIST="${BCO_DIST:=$HOME/usr/}"

if [ ! -d ${BCO_DIST} ]; then
    echo "No bco distribution found at: ${BCO_DIST}"
    echo 'Please define the distribution installation target directory by setting the $BCO_DIST environment variable.'
    exit 255
fi


APP_NAME='bcozy'
APP_NAME=${BLUE}${APP_NAME}${NC}
echo -e "=== ${APP_NAME} project ${WHITE}cleanup${NC}" &&
./mvnw clean --quiet $@ &&
echo -e "=== ${APP_NAME} project ${WHITE}installation${NC}" &&
./mvnw install \
        -DassembleDirectory=${BCO_DIST} \
        -DskipTests=true \
        -Dmaven.test.skip=true \
        -Dlicense.skipAddThirdParty=true \
        -Dlicense.skipUpdateProjectLicense=true \
        -Dlicense.skipDownloadLicenses \
        -Dlicense.skipCheckLicense=true \
        -Dmaven.license.skip=true \
        --quiet $@ &&
echo -e "=== ${APP_NAME} was ${GREEN}successfully${NC} installed to ${WHITE}${BCO_DIST}${NC}"
