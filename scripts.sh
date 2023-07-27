#!/bin/bash
folder="/Users/jixiang/Documents/scripts"
for file in `find $folder -type f`
do
    if [ "${file##*.}"x = "jmx"x ];then
      jmeter -n -t "$file"
    fi
done
