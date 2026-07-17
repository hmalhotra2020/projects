#!/bin/bash
set -e
ROOT_DIR="$(pwd)"
MODULES=(catalog pricing api registry)

for module in "${MODULES[@]}"; do
  cd "$ROOT_DIR/$module"
  now=$(date)
  echo "Script executed from: ${PWD} at $now"

  if command -v docker >/dev/null 2>&1; then
    docker run --rm -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:8.6-jdk17 ./gradlew clean build -x test --no-daemon
  else
    ./gradlew clean build -x test --no-daemon
  fi

  now=$(date)
  echo "Script finished at $now"
done
