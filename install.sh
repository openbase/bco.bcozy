#!/bin/bash
APP_NAME='bcozy'
clear &&
echo "=== clean ${APP_NAME} ===" &&
mvn clean $@ &&
clear &&
echo "=== deploy ${APP_NAME} to ${prefix} ===" &&
mvn install \
        -DassembleDirectory=${prefix} \
        -DskipTests=true \
        -Dmaven.test.skip=true \
        -Dlicense.skipAddThirdParty=true \
        -Dlicense.skipUpdateProjectLicense=true \
        -Dlicense.skipDownloadLicenses \
        -Dlicense.skipCheckLicense=true \
        -Dmaven.license.skip=true $@ &&
clear &&
echo "=== ${APP_NAME} is successfully installed to ${prefix} ==="
