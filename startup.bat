@echo off
echo ========================================
echo MELI Order Management System
echo ========================================
echo.
echo Starting application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if PostgreSQL is accessible
echo Checking database connection...
psql -U meli_user -d meli_order_db -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Cannot connect to database
    echo Please ensure PostgreSQL is running
    echo.
)

REM Start the application
echo Starting Spring Boot application...
echo.
call mvnw.cmd spring-boot:run

pause