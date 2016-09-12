cat 'echo' > $1/echo.txt

cp createBootPhylo.r $1/createBootPhylo.r

cd $1/ZipOutput
unzip input.zip
cd ../../../
java -Djava.awt.headless=true CreateInputMatrix $1/Bootstrap/SNA.txt $1/Bootstrap/spname.txt $1/Bootstrap/numgenenode.txt $1/Bootstrap/taxaname.txt

#java BootstrapSpeciesTree $1/ZipOutput/ $1/Bootstrap/ $1/FinalOutput.tre
java BootstrapSpeciesTreeLowMem $1/ZipOutput/ $1/Bootstrap/ $1/FinalOutput.tre



cp consense $1/consense
cp $1/FinalOutput.tre $1/intree
cp consense.txt $1/consense.txt
cd $1
./consense < consense.txt
cp outtree phylip_output.tre

cd $1

sh ../../dendro.sh FinalOutput.tre
#sumtrees.py --unrooted --decimals=2 -f 0.0 --percentages --output=output.tre --to-newick FinalOutput.tre --support-as-labels -r --no-summary-metadata


#sumtrees.py -d2 -f0.0 --output=output.tre --to-newick FinalOutput.tre --support-as-labels --no-summary-metadata 
#sumtrees.py -d2 -f0.0 --rooted  --output=output.tre --to-newick FinalOutput.tre --support-as-labels --no-summary-metadata 
#/usr/local/src/DendroPy-3.12.0/build/scripts-2.6/sumtrees.py > sumtrees.txt
#/usr/local/src/DendroPy-3.12.0/build/scripts-2.6/sumtrees.py --output=output.tre --to-newick FinalOutput.tre > sumtreesproblem.txt

sed -i 's/\[&U\] //g;' output.tre

R --vanilla < createBootPhylo.r

rm -rf /tmp/ts-out*
