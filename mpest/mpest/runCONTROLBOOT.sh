cp createSpeciesList.r $1
cd $1
R --vanilla < createSpeciesList.r

cd ../../../

java CreateSNAFile $1/speciesnames.txt $1/controlFile
