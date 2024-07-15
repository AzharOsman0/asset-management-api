@echo off
REM This script fixes the introduced concurrency bug in the AssetController

powershell -Command "(gc src/main/java/com/example/demo/AssetController.java) -replace 'public ResponseEntity<Asset> updateAsset', 'public synchronized ResponseEntity<Asset> updateAsset' | Out-File -encoding ASCII src/main/java/com/example/demo/AssetController.java"
