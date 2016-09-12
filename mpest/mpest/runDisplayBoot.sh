#!/bin/bash
    if [ -f "$1/output.tre" ];
    then
        java -Djava.awt.headless=true ReplaceHTMLFile $1/index.html_backup $1/output.tre $1/index.html
    else

    cp bad_boot.html $1/bad_boot.html_backup
    java -Djava.awt.headless=true ReplaceHTMLFile $1/bad_boot.html_backup input.tre $1/index.html

     
#        cp bad.html $1/bad.html_backup
#        java -Djava.awt.headless=true ReplaceHTMLFile $1/bad.html_backup $1/input.tre $1/index.html

    fi
