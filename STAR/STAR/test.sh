#!/bin/bash

FILE=$1/rooted.txt
if [ -f "$1/rooted.txt" ];
then
     echo "found"
else
     echo "not found"
fi
