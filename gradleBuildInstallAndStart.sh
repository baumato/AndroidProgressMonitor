#!/bin/bash

# build
gradlew clean assembleDebug

# connect to device
if [ $# -gt 0 ]
then
 sudo $ANDROID_HOME/platform-tools/adb kill-server
 sudo $ANDROID_HOME/platform-tools/adb start-server
 sudo $ANDROID_HOME/platform-tools/adb devices
fi

# (re)install app
$ANDROID_HOME/platform-tools/adb install -reinstall ./app/build/outputs/apk/app-debug.apk

# launch app
$ANDROID_HOME/platform-tools/adb shell monkey -p de.baumato.android.progress.app -c android.intent.category.LAUNCHER 1

