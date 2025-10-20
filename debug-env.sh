#!/bin/bash

echo "=== ENVIRONMENT VARIABLE DEBUG ==="
echo ""
echo "1. Current process environment:"
env | grep -E "(DB_|SPRING_DATASOURCE)" || echo "  No DB_ variables found"

echo ""
echo "2. Git Bash parent environment:"
printenv | grep -E "(DB_|SPRING)" || echo "  No DB/SPRING variables found"

echo ""
echo "3. Checking .env files in project:"
for file in .env .env.production .env.production.local .env.template; do
    if [ -f "$file" ]; then
        echo "  Found: $file"
        echo "  Contents:"
        grep -E "DB_" "$file" | head -5
    fi
done

echo ""
echo "4. Checking Windows environment (User):"
reg query "HKCU\Environment" 2>/dev/null | grep -E "DB_|SPRING" || echo "  None found"

echo ""
echo "5. Checking Windows environment (System):"
reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" 2>/dev/null | grep -E "DB_|SPRING" || echo "  None found"

echo ""
echo "6. Checking if variables are in application.properties:"
grep -r "DB_USERNAME\|DB_PASSWORD" src/main/resources/*.properties | grep -v "#"

echo ""
echo "==================================="