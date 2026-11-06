#!/bin/sh
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$WRAPPER" ]; then
  printf 'Gradle Wrapper ausente. Baixe gradle-8.11.1-wrapper.jar da distribuicao oficial para gradle/wrapper.\n' >&2
  exit 1
fi
if [ -n "${JAVA_HOME:-}" ]; then JAVA="$JAVA_HOME/bin/java"; else JAVA=java; fi
exec "$JAVA" -Dorg.gradle.appname=gradlew -classpath "$WRAPPER" org.gradle.wrapper.GradleWrapperMain "$@"
