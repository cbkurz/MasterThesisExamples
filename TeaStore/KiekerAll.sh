#!/bin/bash

base_dir=`pwd`

# Initialize the Logfile
echo "Start KiekerAll" > KiekerAll.log
# Get the logfile absolute path
log="${base_dir}/KiekerAll.log"
date >> $log

# Get the required variables
# Input
inputdirs_string="./kieker-monitoring/teastore-*/kieker*"
case $inputdirs_string in
  /*) echo "inputdirs are absolute paths" >> $log;;
  *) echo "inputdirs are relative paths" >> $log;
    inputdirs_string="${base_dir}/${inputdirs_string}";;
esac
inputdirs=($inputdirs_string)

# Output
out="./output"
case $out in
  /*) echo "outputdir is absolute path" >> $log;;
  *) echo "outputdir is relative path" >> $log;
    out="${base_dir}/${out}";;
esac


# Log variables
echo "Base Directory:" >> $log
echo "Input Directory: $inputdirs_string" >> $log
echo "Output Directory: $out" >> $log
mkdir --parents $out || exit 1
pwd >> $log
echo "Processing directories: " >> $log

check_input_directories() {
  count=0
  for directory in "${inputdirs[@]}"; do
      if [ -d "$directory" ]; then
          count=$((count+1))
          echo "$count $directory" >> $log
      fi
  done

  if [ $count -eq 0 ]; then
    echo "No Directories in the expression ${inputdirs_string} present, please check if monitoring data has been collected."
    exit 1;
  fi
}

check_required_tools() {

  echo "---" >> $log
  echo "Start to check for required tools" >> $log
  echo "---" >> $log

  echo "Check if dot is installed" >> $log
  if ! command -v dot &> /dev/null
  then
    echo "dot could not be found" >> $log
    echo "please install the dot tool with 'apt install graphviz' or the respective package manager from your distribution." >> $log
    exit
  else
    echo "dot is installed!" >> $log
  fi

  echo "Check if pic2plot is installed" >> $log
  if ! command -v pic2plot &> /dev/null
  then
    echo "pic2plot could not be found" >> $log
    echo "pic2plot is part of the plotutils package" >> $log
    echo "please install the pic2plot tool with 'apt install plotutils' or the respective package manager from your distribution." >> $log
    exit
  else
    echo "pic2plot is installed!" >> $log
  fi

  echo "Check if trace-analysis is installed" >> $log
  if ! command -v trace-analysis &> /dev/null
  then
    echo "trace-analysis could not be found" >> $log
    echo "please install the trace-analysis tool with providing a symbolic link from '~/bin/trace-analysis' to '<kieker-dir>/tools/trace-analysis-<version>/bin/trace-analysis'." >> $log
    exit
  else
    echo "trace-analysis is installed!" >> $log
  fi

  echo "---" >> $log
  echo "All required tools are present" >> $log
  echo "---" >> $log

}

do_trace_analysis() {
  cd ${base_dir} || exit 1
  echo "---" >> $log
  echo "Start trace-analysis" >> $log
  echo "---" >> $log

  mkdir --parents $out/trace-analysis >> $log
  cd $out/trace-analysis || exit 1
  pwd >> $log
  mkdir --parents AggregatedAssemblyCallTree AggregatedDeploymentCallTree AssemblyComponetDependencyGraph AssemblyOperationDependencyGraph AssemblySequenceDiagrams CallTrees ContainerDependencyGraph DeploymentComponentDependencyGraph DeploymentOperationDependencyGraph DeploymentSequenceDiagrams >> $log


  trace-analysis --inputdirs "${inputdirs[@]}" -o AggregatedAssemblyCallTree --plot-Aggregated-Assembly-Call-Tree >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o AggregatedDeploymentCallTree --plot-Aggregated-Deployment-Call-Tree >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o AssemblyComponetDependencyGraph --plot-Assembly-Component-Dependency-Graph true >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o AssemblyOperationDependencyGraph --plot-Assembly-Operation-Dependency-Graph true >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o AssemblySequenceDiagrams --plot-Assembly-Sequence-Diagrams >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o CallTrees --plot-Call-Trees >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o ContainerDependencyGraph --plot-Container-Dependency-Graph >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o DeploymentComponentDependencyGraph  --plot-Deployment-Component-Dependency-Graph true >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o DeploymentOperationDependencyGraph  --plot-Deployment-Operation-Dependency-Graph true >> $log
  trace-analysis --inputdirs "${inputdirs[@]}" -o DeploymentSequenceDiagrams  --plot-Deployment-Sequence-Diagrams >> $log

  #trace-analysis --inputdirs "${inputdirs[@]}" -o .  --print-Assembly-Equivalence-Classes true >> $log
  #trace-analysis --inputdirs "${inputdirs[@]}" -o .  --print-Deployment-Equivalence-Classes true >> $log
  #trace-analysis --inputdirs "${inputdirs[@]}" -o .  --print-Execution-Traces true >> $log
  #trace-analysis --inputdirs "${inputdirs[@]}" -o .  --print-Message-Traces true >> $log
  echo "---" >> $log
  echo "Finished trace-analysis" >> $log
  echo "---" >> $log
  cd ${base_dir} || exit 1
}

do_dot_transformation() {
    cd ${base_dir} || exit 1
    mkdir --parents $out/dot >> $log
    cd ${out}/dot || exit 1
    echo "---" >> $log
    echo "Start dot" >> $log
    echo "---" >> $log

    # Create dot search string
    dot_search_string="${out}/trace-analysis/*/*.dot"
    echo "dot search string: ${dot_search_string}" >> $log
    pic_files=($dot_search_string)

    for file in "${pic_files[@]}"; do
        echo "Processing Dot File: $file" >> $log
        if [ -f "$file" ]; then

            # Get required variables
            filename="${file##*/}"
            foldername=`dirname ${file}`
            foldername="${foldername##*/}"
            mkdir "${foldername}" >> $log
            echo "output file: ${out}/dot/${foldername}/${filename}.svg" >> $log

            # Actual call to dot
            dot -Tsvg "$file" -o "${out}/dot/${foldername}/${filename}.svg" >> $log
        fi
    done
    echo "---" >> $log
    echo "End dot" >> $log
    echo "---" >> $log
    cd ${base_dir} || exit 1
}


do_pic_transformation() {
    cd ${base_dir} || exit 1
    mkdir --parents $out/pic >> $log
    cd ${out}/pic || exit 1
    echo "---" >> $log
    echo "Start pic2plot" >> $log
    echo "---" >> $log

    # Create pic search string
    pic_search_string="${out}/trace-analysis/*/*.pic"
    echo "pic search string: ${pic_search_string}" >> $log
    pic_files=($pic_search_string)

    for file in "${pic_files[@]}"; do
        echo "Processing Pic File: $file" >> $log
        if [ -f "$file" ]; then

            # Get required variables
            filename="${file##*/}"
            foldername=`dirname ${file}`
            foldername="${foldername##*/}"
            mkdir "${foldername}" >> $log
            echo "output file: ${out}/pic/${foldername}/${filename}.svg" >> $log

            # Actual call to pic
            pic2plot "$file" -T svg > "${out}/pic/${foldername}/${filename}.svg"
        fi
    done
    echo "---" >> $log
    echo "End pic2plot" >> $log
    echo "---" >> $log
    cd ${base_dir} || exit 1
}

check_required_tools
check_input_directories
do_trace_analysis
do_dot_transformation
do_pic_transformation


