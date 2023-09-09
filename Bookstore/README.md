# Bookstore - a simple kieker instrumented application

The bookstore is a relatively simple application.
Its purpose is to have short logs that can be transformed to sequence diagrams.
With such simple calls it is possible to verify the resulting sequence diagrams by hand.
It is monitored by kiekers aspectj component.

## Kieker Instrumentation

For the instrumentation it is required to produce outputs that can be handled by the "Kieker2UML" Module.
Furthermore, the logs shall be saved locally.
To instrument the bookstore in such a way the following 4 components are required:
* [kieker-1.15.2-aspectj.jar](libs%2Fkieker-1.15.2-aspectj.jar)
* [kieker.monitoring.properties](src%2Fmain%2Fresources%2FMETA-INF%2Fkieker.monitoring.properties)
* [aop.xml](src%2Fmain%2Fresources%2FMETA-INF%2Faop.xml)
* and the _runMonitoring_ task provided in [build.gradle.kts](build.gradle.kts)