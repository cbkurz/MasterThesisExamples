#!/bin/bash

for file in graphs/*.dot; do
    if [ -f "$file" ]; then
        filename="${file##*/}"
        dot -Tpng "$file" -o ./output/"$filename".png
    fi
done