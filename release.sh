#!/usr/bin/env bash

./gradlew clean build generatePomFileForMavenPublication :compiler:bintrayUpload -PbintrayUser=$bintrayUser -PbintrayKey=$bintrayKey -PdryRun=false
./gradlew :library:bintrayUpload -PbintrayUser=$bintrayUser -PbintrayKey=$bintrayKey -PdryRun=false