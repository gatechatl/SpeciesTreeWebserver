
cp createBootPhylo.r $1/createBootPhylo.r
cd $1/ZipOutput
unzip input.zip
cd ../../../
java -Djava.awt.headless=true CreateInputMatrix $1/Bootstrap/SNA.txt $1/Bootstrap/spname.txt $1/Bootstrap/numgenenode.txt $1/Bootstrap/taxaname.txt

#java BootstrapSpeciesTreeMPEST $1/ZipOutput/ $1/Bootstrap/ $1/FinalOutput.tre
java BootstrapSpeciesTreeMPESTLowMem $1/ZipOutput/ $1/Bootstrap/ $1/FinalOutput.tre

cp dendro.sh $1

cp consense $1/consense
cp $1/FinalOutput.tre $1/intree
cp consense.txt $1/consense.txt
cd $1
./consense < consense.txt
cp outtree output.tre


cd $1

sh ../../dendro.sh FinalOutput.tre
#/usr/local/src/DendroPy-3.12.0/build/scripts-2.6/sumtrees.py --rooted --decimals=2 -f 0.0 --percentages --output=output.tre --to-newick FinalOutput.tre --support-as-labels -r --no-summary-metadata > dendropy.txt


R --vanilla < createBootPhylo.r > dontdie

rm -rf /tmp/ts-out* > removefiles
