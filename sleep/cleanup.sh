#!/bin/bash
# Cleanup script: removes containers, volumes, images, and database directory
# Run this to start fresh before rebuilding

set -e

echo "🛑 Stopping containers and removing volumes..."
docker compose down -v

echo "🗑️  Removing Docker images..."
docker rmi -f sleep-sleep_api 2>/dev/null || true

echo "📁 Clearing database directory..."
rm -rf db && mkdir db

echo "✅ Cleanup complete! You can now run:"



