@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR%"=="" set "SCRIPT_DIR=.\"
cd /d "%SCRIPT_DIR%"

set "WRAPPER_PROPERTIES=%SCRIPT_DIR%.mvn\wrapper\maven-wrapper.properties"
if not exist "%WRAPPER_PROPERTIES%" (
  echo [ERROR] Maven wrapper properties not found at %WRAPPER_PROPERTIES%
  exit /b 1
)

set "DOWNLOAD_URL="
for /f "tokens=1,* delims==" %%A in ('findstr /b "distributionUrl=" "%WRAPPER_PROPERTIES%"') do set "DOWNLOAD_URL=%%B"
if not defined DOWNLOAD_URL (
  echo [ERROR] distributionUrl not found in %WRAPPER_PROPERTIES%
  exit /b 1
)

for %%I in ("%DOWNLOAD_URL%") do set "MAVEN_ZIP=%%~nxI"
set "MAVEN_HOME_NAME=%MAVEN_ZIP:.zip=%"
set "MAVEN_CACHE=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_HOME_NAME%"
set "MAVEN_BIN="

call :find_maven
if not defined MAVEN_BIN (
  set "MVNW_DIR=%USERPROFILE%\.m2\wrapper\dists"
  if not exist "%MVNW_DIR%" mkdir "%MVNW_DIR%"
  echo Downloading Maven from %DOWNLOAD_URL%
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%MVNW_DIR%\%MAVEN_ZIP%')"
  if errorlevel 1 (
    echo [ERROR] Failed to download Maven from %DOWNLOAD_URL%
    exit /b 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory('%MVNW_DIR%\%MAVEN_ZIP%', '%MAVEN_CACHE%')"
  if errorlevel 1 (
    echo [ERROR] Failed to extract Maven archive
    exit /b 1
  )
  call :find_maven
)

if not defined MAVEN_BIN (
  echo [ERROR] Could not find mvn.cmd in %MAVEN_CACHE%
  exit /b 1
)

call "%MAVEN_BIN%" %*
exit /b %ERRORLEVEL%

:find_maven
for /f "delims=" %%F in ('dir /s /b "%MAVEN_CACHE%\mvn.cmd" 2^>nul') do (
  set "MAVEN_BIN=%%F"
  goto :eof
)
for /f "delims=" %%F in ('dir /s /b "%MAVEN_CACHE%\bin\mvn.cmd" 2^>nul') do (
  set "MAVEN_BIN=%%F"
  goto :eof
)
goto :eof
