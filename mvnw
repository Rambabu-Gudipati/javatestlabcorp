#!/bin/sh
# Maven Wrapper Script
# Downloads Maven if not cached and delegates to it.

set -e

MAVEN_VERSION="3.9.6"
MAVEN_DIR="$HOME/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_BIN="$MAVEN_DIR/bin/mvn"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

if [ ! -f "$MAVEN_BIN" ]; then
    echo "[mvnw] Maven ${MAVEN_VERSION} not found. Downloading..."
    mkdir -p "$MAVEN_DIR"
    TMPFILE=$(mktemp /tmp/maven-XXXXXX.tar.gz)
    curl -fsSL "$MAVEN_URL" -o "$TMPFILE"
    tar -xzf "$TMPFILE" -C "$MAVEN_DIR" --strip-components=1
    rm -f "$TMPFILE"
    echo "[mvnw] Maven downloaded to $MAVEN_DIR"
fi

exec "$MAVEN_BIN" "$@"
