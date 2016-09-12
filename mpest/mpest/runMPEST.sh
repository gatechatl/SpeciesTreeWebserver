#!/bin/bash

cat 'echo' > $1/echo.txt
cp createPhylo.r $1/createPhylo.r
cp calculateDistance.r $1/calculateDistance.r
cp inputtree_rewrite.py $1/inputtree_rewrite.py
cp treeComp.r $1/treeComp.r
cp treeComp.py $1/treeComp.py
cd $1
./inputtree_rewrite.py input.tre > format_input.tre
cd ../../

if [ -f "$1/SNA.txt" ];
then
cat hello
else
cp runSNA.r $1/runSNA.r
cd $1
R --vanilla < runSNA.r
cd ../../
java CreateSNAFileMPEST $1/speciesname.txt _ 1 $1/SNA.txt
fi

cp rooted.r $1/rooted.r
cd $1
R --vanilla < rooted.r
if [ -f "rooted.txt" ];
then
    ../../mpest_1.2/src/mpest control > mpest_progress.txt
    cp ../../convert.R convert.R
    R --vanilla < convert.R
    cat output.nex | tail -n 1 > output.tre


    R --vanilla < createPhylo.r

    # tree comparison need to add flag for turning this on or off
    R --vanilla < calculateDistance.r
    R --vanilla < treeComp.r


    python treeComp.py output.tre format_input.tre
    echo "working" > working
    cd ../../

    sh tripleDistant.sh $1
    sh runDisplayHTML.sh $1
    java GenerateTable $1/tripleControl.txt $1/newtreedist $1/numtipdrop $1/table.txt Report.html $1/Report.html

    echo "success" > $1/success
    #java ExamineGeneTrees $1/format_input.tre $1/output.tre $1/SNA.txt $1 $1/genetree.html $1/distance_all.csv
    #cat $1/distance_all.csv | head -n 50 > $1/distance.csv
    cd $1
    #R --vanilla < createPhylo.r
    #R --vanilla < calculateDistance.r
    #R --vanilla < treeComp.r
    cd ../../
    #sh tripleDistant.sh $1
    #java GenerateTable $1/tripleControl.txt $1/newtreedist $1/numtipdrop $1/table.txt Report.html $1/Report.html
fi

#if [ `ls -l "output.tre" | awk '{print $5}'` -gt 1 ]
#then
#cd ../../
#cd source
#java -Djava.awt.headless=true TreeVector ../$1/output.tre
#cd ../temp
#rm output.png
#sh convert.sh
#cp output.png ../$1/output.png
#cd ../
#fi

#java -Djava.awt.headless=true ReplaceHTMLFile $1/index.html_backup $1/output.nex $1/index.html
