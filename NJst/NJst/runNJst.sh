#!/bin/bash

cp createPhylo.r $1/createPhylo.r
cp calculateDistance.r $1/calculateDistance.r
cp script.r $1/script.r
cp inputtree_rewrite.py $1/inputtree_rewrite.py
cp treeComp.r $1/treeComp.r
cp treeComp.py $1/treeComp.py
cd $1
./inputtree_rewrite.py input.tre > format_input.tre

cd ../../

if [ -f "$1/SNA.txt" ];
then
java -Djava.awt.headless=true CreateInputMatrix $1/SNA.txt $1/spname.txt $1/numgenenode.txt $1/taxaname.txt
else
cp runSNA.r $1/runSNA.r
cd $1
R --vanilla < runSNA.r
cd ../../
java CreateSNAFile $1/speciesname.txt | 1 $1/SNA.txt
java -Djava.awt.headless=true CreateInputMatrix $1/SNA.txt $1/spname.txt $1/numgenenode.txt $1/taxaname.txt
fi

#cp script.r $1/script.r
#cp inputtree_rewrite.py $1/inputtree_rewrite.py
#cd $1
#./inputtree_rewrite.py input.tre > format_input.tre

cd $1

R --vanilla < script.r
R --vanilla < createPhylo.r

R --vanilla < calculateDistance.r
R --vanilla < treeComp.r
python treeComp.py output.tre format_input.tre
cd ../../
sh runDisplayHTML.sh $1
java GenerateTable $1/numtipdrop $1/newtreedist $1/table.txt Report.html $1/Report.html
#java ExamineGeneTrees $1/format_input.tre $1/output.tre $1/genetree.html $1/distance_all.csv $1
#java ExamineGeneTrees $1/format_input.tre $1/output.tre $1/SNA.txt $1 $1/genetree.html $1/distance_all.csv

#sh tripleDistant.sh $1

#cat $1/distance_all.csv | head -n 50 > $1/distance.csv

cd source
java -Djava.awt.headless=true TreeVector ../$1/output.tre
cd ../temp
sh convert.sh
cp output.png ../$1/output.png
cd ../
#cp $1/index.html $1/index.html_backup
#java -Djava.awt.headless=true ReplaceHTMLFile $1/index.html_backup $1/output.tre $1/index.html

