#!/bin/bash
_file="$1"
[ $# -eq 0 ] && { echo "Usage: $0 filename"; exit 1; }
[ ! -f "$_file" ] && { echo "Error: $0 file not found."; exit 2; }
 
if [ -s "$_file" ]
then
sumtrees.py --unrooted --decimals=2 -f 0.0 --percentages --output=output.tre --to-newick FinalOutput.tre --support-as-labels -r --no-summary-metadata
#  /usr/local/src/DendroPy-3.12.0/build/scripts-2.6/sumtrees.py --rooted --decimals=2 -f 0.0 --percentages --output=output.tre --to-newick FinalOutput.tre --support-as-labels -r --no-summary-metadata > dendropy.txt 
fi
