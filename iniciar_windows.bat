@echo off
cd /d "%~dp0"
where mvn >nul 2>nul
if errorlevel 1 (
 echo Maven no esta en PATH. Abri este proyecto desde NetBeans y pulsa Run.
 pause
 exit /b 1
)
call mvn javafx:run
pause
