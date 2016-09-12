cp consense $1/consense
cp $1/FinalOutput.tre $1/intree
cp consense.txt $1/consense.txt
cd $1
./consense < consense.txt
cp outtree output.tre

R --vanilla < createBootPhylo.r

