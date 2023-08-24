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

#### Run 

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

#### Analysis

Further information about the trace-analysis tool can be found [here](../documentation/TraceAnalysis.md).

Create call tree `.dot` files out of traces:
```bash
trace-analysis --inputdirs ./kieker-monitoring/teastore-*/kieker* -o ./output/dot --plot-Call-Trees
```

Explanation:
- `--inputdirs ./kieker-monitoring/teastore-*/kieker*` provides the directories to which the trace logs haven been written. In this command the `*` operator (similar to any) is used to create the set of directories which happen to be filled with Kieker traces.
- `-o ./output` is the directory to which the output is written. If the directory does not exist the trace-analyse tool will fail. 
- `--plot-Call-Trees` this is tells the tool to create call trees in `.dot` notation. 

Create component dependency graphs `.dot` files out of traces:
```bash
trace-analysis --inputdirs ./kieker-monitoring/teastore-*/kieker* -o ./output --plot-Assembly-Component-Dependency-Graph true
```
Only the `assemblyComponentDependencyGraph.out.svg` file is created in the output directory.

Explanation:

- `--plot-Assembly-Component-Dependency-Graph true` tells the tool to create the output, `true` is required since an input is expected and default is false.

#### View

To create SVGs out of the created `.dot` files use the [graphviz](https://graphviz.org/) tool:
```bash
dot -Tsvg output/assemblyComponentDependencyGraph.dot > assemblyComponentDependencyGraph.out.svg
```

After the execution, the file `assemblyComponentDependencyGraph.out.svg` is created and can now be viewed with the viewer of your choise.

Explanation:
- `-Tsvg` sets the output format to SVG, other formats are possible like PNG, simply write `-Tpng` instead of.
- `output/assemblyComponentDependencyGraph.dot` is the input file, please make sure that the file exists.
- `>` the `dot` tool will write to stdout, which is usually the console, therefore we have to specify the target of stdout.
- `assemblyComponentDependencyGraph.out.svg` the file to which the output is written.

