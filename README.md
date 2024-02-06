# log4jconverter

1. Build with Maven.
2. Copy jar where you need it e.g.: `copy target\log4jconverter-*-SNAPSHOT-jar-with-dependencies.jar ..\log4jconverter.jar`
3. Execute with java:
```bash
java log4jconverter.jar -i "your-project\src\main\resources\log4j.xml"
```

Building should work with JDK 1.8 (e.g. [Temurin builds from Adoptium](https://adoptium.net/temurin/archive/?version=8)) and Maven 3.8.
