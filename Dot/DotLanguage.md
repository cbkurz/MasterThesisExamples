# Dot Language

The dot language is a possibility to write down graphs.
The `dot` tool is able to create visual representations out of this graphs.
More information can be obtained at [graphviz.org](https://www.graphviz.org/).

# Usage

```bash
dot -Tpng graphs/SimpleCyclicGraph.dot -o SimpleGraph.png
```

Description:
* `-Tpng` states that a PNG should be created
* `SimpleGraph.dot` is the file which contains the graph that will be made into a PNG.
* `-o SimpleGraph.png` declares that the output should be written to the SimpleGraph.png file, if the files does not exist it will be created.