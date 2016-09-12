#!/bin/bash


if [ -f "$1/rooted.txt" ];
then
    if [ -f "$1/output.tre" ];
    then
        java -Djava.awt.headless=true ReplaceHTMLFile $1/index.html_backup $1/output.tre $1/index.html
    else
    cp bad.html $1/bad.html_backup
    java -Djava.awt.headless=true ReplaceHTMLFile $1/bad.html_backup $1/input.tre $1/index.html

    fi

else
cp rooted.html $1/reroot.html_backup
    java -Djava.awt.headless=true ReplaceHTMLFile $1/reroot.html_backup $1/format_input.tre $1/index.html
fi
