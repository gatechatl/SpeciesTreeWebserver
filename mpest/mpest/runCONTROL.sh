echo hello > $1/hello
cp createSpeciesList.r $1
cd $1
R --vanilla < createSpeciesList.r

cd ../../

java CreateSNAFile $1/speciesnames.txt $1/controlFile

ts > $1/ts.txt
