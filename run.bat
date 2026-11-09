@echo off
setlocal

echo ===================================================
echo   Sondhan - Real-Time AI Fact Checker (Native Java)
echo ===================================================
echo.

set SDK_DIR=C:\Users\Udoy Bashak\AppData\Local\Android\Sdk
set ADB="%SDK_DIR%\platform-tools\adb.exe"
set EMULATOR="%SDK_DIR%\emulator\emulator.exe"
set APK_PATH=%~dp0sondhan.apk

echo [1/3] Building latest Java APK with Gradle...
call .\gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Gradle build failed.
    pause
    exit /b %ERRORLEVEL%
)

copy /y "%~dp0android\app\build\outputs\apk\debug\app-debug.apk" "%APK_PATH%" >nul

echo.
echo [2/3] Checking for running Android device or emulator...
%ADB% get-state >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Starting Android Emulator (Pixel_8)...
    start "" %EMULATOR% -avd Pixel_8
    echo Waiting for emulator to start...
    %ADB% wait-for-device
    echo Waiting for Android system to boot...
    powershell -Command "while ((& '%SDK_DIR%\platform-tools\adb.exe' shell getprop sys.boot_completed 2>$null) -ne '1') { Start-Sleep -Seconds 2 }"
)

echo.
echo [3/3] Installing and launching Sondhan app...
%ADB% install -r "%APK_PATH%"
%ADB% shell am start -n com.sondhan.app/.MainActivity

echo.
echo ===================================================
echo   App is now running on your Android screen!
echo ===================================================
echo.

