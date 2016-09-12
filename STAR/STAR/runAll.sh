cd HXB2Script
touch $2
java HXB2Alignment $1 $2

cd ..
cd SHAPEFold

java HXB2SHAPE2AlignmentCutoff HXB2_structure.txt $2 $3 $4 $5


java -cp VARNAv3-8.jar fr.orsay.lri.varna.applications.VARNAcmd -i $3 -o $3.png -resolution 10 -algorithm naview
java -cp VARNAv3-8.jar fr.orsay.lri.varna.applications.VARNAcmd -i $4 -o $4.png -resolution 10 -algorithm naview

cd ..
cd $6
chmod a+wrx *
zip -r PredictionResult.zip *

