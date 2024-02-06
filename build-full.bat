@echo off

rem Prepare Java and Maven
CALL __setup_env.bat

call %M2_HOME%\bin\mvn.cmd -Dfile.encoding=UTF-8 clean install

PAUSE
