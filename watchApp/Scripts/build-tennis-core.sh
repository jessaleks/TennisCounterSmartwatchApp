#!/bin/sh
set -eu

cd "$SRCROOT/.."

if [ -z "${JAVA_HOME:-}" ]; then
    JAVA_HOME="$(/usr/libexec/java_home -v '17+' 2>/dev/null || true)"
    if [ -z "$JAVA_HOME" ]; then
        JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
    fi
    export JAVA_HOME
fi

case "$CONFIGURATION" in
    Release) KOTLIN_CONFIG="Release" ;;
    *)       KOTLIN_CONFIG="Debug" ;;
esac
CONFIG_LC="$(echo "$KOTLIN_CONFIG" | tr '[:upper:]' '[:lower:]')"

if [ -z "${ARCHS:-}" ]; then
    echo "error: ARCHS is empty; cannot select a TennisCore Kotlin target" >&2
    exit 1
fi

KOTLIN_TARGETS=""
for arch in $ARCHS; do
    case "$PLATFORM_NAME:$arch" in
        watchsimulator:arm64)   KOTLIN_TARGETS="$KOTLIN_TARGETS watchosSimulatorArm64" ;;
        watchsimulator:x86_64)  KOTLIN_TARGETS="$KOTLIN_TARGETS watchosX64" ;;
        watchos:arm64_32)       KOTLIN_TARGETS="$KOTLIN_TARGETS watchosArm64" ;;
        watchos:arm64)          KOTLIN_TARGETS="$KOTLIN_TARGETS watchosDeviceArm64" ;;
        *)
            echo "error: unsupported platform/arch '$PLATFORM_NAME:$arch' for TennisCore" >&2
            exit 1
            ;;
    esac
done

GRADLE_TASKS=""
for target in $KOTLIN_TARGETS; do
    GRADLE_TASKS="$GRADLE_TASKS :shared:link${KOTLIN_CONFIG}Framework${target}"
done

./gradlew $GRADLE_TASKS

DEST="$BUILT_PRODUCTS_DIR/TennisCore.framework"
mkdir -p "$DEST"

set -- $KOTLIN_TARGETS
FIRST="$1"
cp -R "shared/build/bin/$FIRST/${CONFIG_LC}Framework/TennisCore.framework/." "$DEST/"

if [ $# -gt 1 ]; then
    set --
    for target in $KOTLIN_TARGETS; do
        set -- "$@" "shared/build/bin/$target/${CONFIG_LC}Framework/TennisCore.framework/TennisCore"
    done
    lipo -create "$@" -output "$DEST/TennisCore"
fi
