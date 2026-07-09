#!/bin/bash
# run.sh – Bootstrap Maven (if needed) and start the Spring Boot application

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
MAVEN_VERSION="3.9.6"
MAVEN_DIR="$HOME/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_BIN="$MAVEN_DIR/bin/mvn"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

echo "================================================="
echo " Employee Management System – Spring Boot"
echo " Java: $(java -version 2>&1 | head -1)"
echo "================================================="

# ── Download Maven if not already cached ─────────────────────────────────────
if [ ! -f "$MAVEN_BIN" ]; then
    echo ""
    echo "[run.sh] Maven ${MAVEN_VERSION} not found in cache. Downloading (~10 MB)..."
    mkdir -p "$MAVEN_DIR"
    TMPFILE=$(mktemp /tmp/maven-XXXXXX.tar.gz)
    curl -fsSL "$MAVEN_URL" -o "$TMPFILE"
    tar -xzf "$TMPFILE" -C "$MAVEN_DIR" --strip-components=1
    rm -f "$TMPFILE"
    echo "[run.sh] Maven installed to: $MAVEN_DIR"
fi

echo ""
echo "[run.sh] Building project (skipping tests for speed)..."
"$MAVEN_BIN" -f "$PROJECT_DIR/pom.xml" clean package -DskipTests -q

echo ""
echo "[run.sh] Starting application on http://localhost:8080 ..."
echo ""
java -jar "$PROJECT_DIR/target/employee-management-1.0.0.jar"
