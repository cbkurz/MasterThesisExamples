# Simple Evaluation Examples

These examples serve to create evaluations for the implementation of the Kieker extension `kieker.extension.performanceanalysis`.
The different examples can be called from the `Driver` class.
To run monitoring on them see the `gradle.kts` task `runMonitoring`.

The examples serve to have a very simple run through the application.
where the input can be checked against the output by hand.
Building enough of such examples will allow that more complex structures do in fact create a valid output.
The assumption is that the larger model is the sum of the parts of the smaller one.

## Example 1
![SimpleExample-1.png](files%2FSimpleExample-1.png)

## Example 2
![SimpleExample-2.png](files%2FSimpleExample-2.png)

## Example 3
![SimpleExample-3.png](files%2FSimpleExample-3.png)

# Docker
## Create the image

```
docker build --tag kurz-ma-simple-example .
```

## Run the container

With this command the docker container is run and the monitoring logs are written in the folder `kieker-monitoring-logs`, 
please make sure to create the folder beforehand:
```
docker run --name example --rm  -v "$PWD/kieker-monitoring-logs:/app/kieker-monitoring-logs" kurz-ma-simple-example
```