#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "$DIR/gradle-8.1.1/bin/gradle" "$@"
