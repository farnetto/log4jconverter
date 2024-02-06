@echo off

rem Prepare Java and Maven
set JAVA_HOME=c:\Prog\Java\jdk1.8.latest

rem Maven 3.8.x requires JDK 1.7 or above to execute. It still allows you to build against 1.3 and other JDK versions by using toolchains.
rem Maven 3.9.x requires JDK 1.8 or above to execute.
set M2_HOME=C:\Prog\Maven\apache-maven-3.8.8
