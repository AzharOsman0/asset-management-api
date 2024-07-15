#!/bin/bash
# This script fixes the introduced concurrency bug in the AssetController

sed -i 's/public ResponseEntity<Asset> updateAsset/public synchronized ResponseEntity<Asset> updateAsset/' src/main/java/com/example/demo/AssetController.java
