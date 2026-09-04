@echo off
set ADB=C:\Users\stand\AppData\Local\Android\Sdk\platform-tools\adb.exe
set EMULATOR=C:\Users\stand\AppData\Local\Android\Sdk\emulator\emulator.exe
set APK=app\build\outputs\apk\debug\app-debug.apk

echo === sound-healing-app 起動スクリプト ===
echo.

REM エミュレータ起動
echo [1/4] エミュレータ起動中...
start "" "%EMULATOR%" -avd Pixel_3a_API_33_x86_64 -no-snapshot-load
timeout /t 5 /nobreak > nul

REM ブート待機（最大60秒）
echo [2/4] ブート待機中...
for /l %%i in (1,1,60) do (
    for /f "tokens=2" %%a in ('%ADB% shell getprop sys.boot_completed 2^>nul') do (
        if "%%a"=="1" goto BOOTED
    )
    timeout /t 1 /nobreak > nul
)
:BOOTED
echo   完了

REM APKインストール
echo [3/4] APKインストール中...
%ADB% install -r "%APK%"
if %errorlevel% neq 0 (
    echo   エラー: インストール失敗
    pause
    exit /b 1
)

REM アプリ起動
echo [4/4] アプリ起動中...
%ADB% shell am start -n com.example.soundhealing/.MainActivity
echo.
echo === 起動完了 ===
pause
