# Create the image

```
docker build --tag kurz-ma-simple-example .
```

# Run the container

With this command the docker container is run and the monitoring logs are written in the folder `kieker-monitoring-logs`, 
please make sure to create the folder beforehand:
```
docker run --name example --rm  -v "$PWD/kieker-monitoring-logs:/app/kieker-monitoring-logs" kurz-ma-simple-example
```