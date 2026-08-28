$adb = "C:\Users\TechnoPlus\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$emulator = "C:\Users\TechnoPlus\AppData\Local\Android\Sdk\emulator\emulator.exe"
$avdName = "Pixel_9_Pro"

Write-Host "Checking connected Android devices..."
$devicesOutput = & $adb devices
Write-Host $devicesOutput

$hasOnlineDevice = ($devicesOutput -split "`r?`n" | Where-Object { $_ -match "\bdevice\b" -and $_ -notmatch "List of devices attached" }).Count -gt 0

if (-not $hasOnlineDevice) {
    Write-Host "No active emulator found. Starting $avdName..."
    Start-Process -FilePath $emulator -ArgumentList "-avd", $avdName
    
    Write-Host "Waiting for device to attach..."
    & $adb wait-for-device

    Write-Host "Waiting for Android OS to finish booting..."
    $bootCompleted = ""
    $timeout = 120
    $elapsed = 0

    while ($bootCompleted -ne "1" -and $elapsed -lt $timeout) {
        Start-Sleep -Seconds 3
        $elapsed += 3
        try {
            $bootCompleted = (& $adb shell getprop sys.boot_completed).Trim()
            Write-Host "Boot status: '$bootCompleted' ($elapsed s)"
        } catch {
            Write-Host "Waiting for emulator boot... ($elapsed s)"
        }
    }

    if ($bootCompleted -ne "1") {
        Write-Error "Failed to boot emulator $avdName within $timeout seconds."
        exit 1
    }

    # Unlock emulator screen
    & $adb shell input keyevent 82
    Write-Host "Emulator $avdName is online, fully booted, and unlocked!"
} else {
    Write-Host "Active Android device/emulator is already running and ready!"
}