# TeaStore

The TeaStore is an application provided by DescartedResearch under the [GitHub Repository](https://github.com/DescartesResearch/TeaStore/tree/master)

````bash
git clone git@github.com:DescartesResearch/TeaStore.git
````

More information on how to use the project is provided in the repository 
in the [GET_STARTED](https://github.com/DescartesResearch/TeaStore/blob/master/GET_STARTED.md) Documentation.
In this repository the application is set up in the way that is required for the thesis.

If you want to cite the TeaStore please follow the citing guide of the [source project](https://github.com/DescartesResearch/TeaStore/blob/master/README.md)

# Example Description

This example is intended to provide a traces with [Kieker](http://kieker-monitoring.net/).
Kieker is a trace tool which allows to analyse an applications runtime behaviour.
The TeaStore is already instrumented and will produce Kieker traces after startup when invoked.


## Setups

In the following the different created services of the TeaStore are described and what their characteristics are.

### Traces in Files

Start service in background:
```bash
docker compose -f compose/docker-compose.yml up -d
```

Stop service:
```bash
docker compose -f compose/docker-compose.yml down
```

The example can be accessed under http://localhost:8080 .
When opening and using the application traces will be created which can be seen in the folder `kieker-monitoring`.
Even though Kieker provides different ways to write traces, the easiest for access are (in my opinion) the ones in files.
They can easily be viewed with virtually any editor and manipulated with the `trace-analysis` tool.

Create `.dot` files out of traces:
```bash
TODO
```