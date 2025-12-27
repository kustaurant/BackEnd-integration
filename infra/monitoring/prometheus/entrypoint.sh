#!/bin/sh
set -e

TEMPLATE=/etc/prometheus/prometheus.yml.tpl
CONFIG=/etc/prometheus/prometheus.yml

cp "$TEMPLATE" "$CONFIG"

for var in $(grep -o '\${[A-Za-z0-9_]\+}' "$CONFIG" | tr -d '${}' | sort -u); do
  value=$(printenv "$var")
  if [ -n "$value" ]; then
    sed -i "s|\${$var}|$value|g" "$CONFIG"
  fi
done

echo "[prometheus] using config:"
cat "$CONFIG"

exec /bin/prometheus \
  --config.file="$CONFIG" \
  --storage.tsdb.path=/prometheus
