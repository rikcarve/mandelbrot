# Mandelbrot
A refresh of a very old Mandelbrot app written by myself


# GraalVM

```bash
java -agentlib:native-image-agent=config-output-dir=config -jar mandelbrot-1.0-SNAPSHOT.jar
native-image --no-fallback -Djava.awt.headless=false -H:ConfigurationFileDirectories=config -jar mandelbrot-1.0-SNAPSHOT.jar
```

```
mandelbrot-1.0-SNAPSHOT.exe -Djava.home="%JAVA_HOME%"
```
