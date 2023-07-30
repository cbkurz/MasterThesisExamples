#!/bin/bash

echo "Processing directories: "

inputdirs="./kieker-monitoring/teastore-*/kieker*"
out="./output"

count=0
for directory in ${inputdirs}; do
    if [ -d "$directory" ]; then
        count=$((count+1))
        echo "$count $directory"
    fi
done

if [ $count -eq 0 ]; then
  echo "No Directories in the exporession ${inputdirs} present, please check if monitoring data has been collected."
  exit 1;
fi

echo $(mkdir --parents ./output/AggregatedAssemblyCallTree ./output/AggregatedDeploymentCallTree ./output/AssemblyComponetDependencyGraph ./output/AssemblyOperationDependencyGraph ./output/AssemlySequenceDiagrams ./output/CallTrees ./output/ContainerDependencyGraph ./output/DeploymentComponentDependencyGraph ./output/DeploymentOperationDependencyGraph ./output/DeploymentSequenceDiagrams)

echo " " > ./output/KiekerAll.log

trace-analysis --inputdirs $inputdirs -o $out/AggregatedAssemblyCallTree --plot-Aggregated-Assembly-Call-Tree >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/AggregatedDeploymentCallTree --plot-Aggregated-Deployment-Call-Tree >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/AssemblyComponetDependencyGraph --plot-Assembly-Component-Dependency-Graph true >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/AssemblyOperationDependencyGraph --plot-Assembly-Operation-Dependency-Graph true >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/AssemlySequenceDiagrams --plot-Assembly-Sequence-Diagrams >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/CallTrees --plot-Call-Trees >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/ContainerDependencyGraph --plot-Container-Dependency-Graph >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/DeploymentComponentDependencyGraph  --plot-Deployment-Component-Dependency-Graph true >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/DeploymentOperationDependencyGraph  --plot-Deployment-Operation-Dependency-Graph true >> ./output/KiekerAll.log
trace-analysis --inputdirs $inputdirs -o $out/DeploymentSequenceDiagrams  --plot-Deployment-Sequence-Diagrams >> ./output/KiekerAll.log
#trace-analysis --inputdirs $inputdirs -o $out  --print-Assembly-Equivalence-Classes true >> ./output/KiekerAll.log
#trace-analysis --inputdirs $inputdirs -o $out  --print-Deployment-Equivalence-Classes true >> ./output/KiekerAll.log
#trace-analysis --inputdirs $inputdirs -o $out  --print-Execution-Traces true >> ./output/KiekerAll.log
#trace-analysis --inputdirs $inputdirs -o $out  --print-Message-Traces true >> ./output/KiekerAll.log
