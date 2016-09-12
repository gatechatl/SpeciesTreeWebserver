#!/bin/bash

cp createPhylo.r $1/createPhylo.r

cp inputtree_rewrite.py $1/inputtree_rewrite.py
cp calculateDistance.r $1/calculateDistance.r
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


#java -Djava.awt.headless=true CreateInputMatrix $1/SNA.txt $1/spname.txt $1/numgenenode.txt $1/taxaname.txt

#cp inputtree_rewrite.py $1/inputtree_rewrite.py
#cd $1
#./inputtree_rewrite.py input.tre > format_input.tre

#cd ../../


#java -Djava.awt.headless=true GenerateSTARScript $1

cp rooted.r $1/rooted.r
cd $1
R --vanilla < rooted.r
cd ../../

cp script.r $1/script.r
cd $1
R --vanilla < script.r
R --vanilla < createPhylo.r

## might need to add flag to make this an option
## runs too slow during normal calculation
R --vanilla < calculateDistance.r
R --vanilla < treeComp.r
python treeComp.py output.tre format_input.tre
cd ../../
#sh runDisplayHTML.sh $1
java GenerateTable $1/numtipdrop $1/newtreedist $1/table.txt Report.html $1/Report.html

#java ExamineGeneTrees $1/format_input.tre $1/output.tre $1/SNA.txt $1 $1/genetree.html $1/distance_all.csv
#cat $1/distance_all.csv | head -n 50 > $1/distance.csv

#sh tripleDistant.sh $1

cd $1
cd ../../source
java -Djava.awt.headless=true TreeVector ../$1/output.tre
cd ../temp
sh convert.sh
cp output.png ../$1/output.png
cd ../

#sh runDisplay.sh $1
#java -Djava.awt.headless=true ReplaceHTMLFile $1/index.html_backup $1/output.tre $1/index.html
