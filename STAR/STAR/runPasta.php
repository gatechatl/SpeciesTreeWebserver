<html>
<head>

<?php
//echo "hello";
//require "config.php";

//echo "running PHP <br />";
$uniq_id = uniqid();

$original_path = "Output/" . $uniq_id . "/";

$target_path = "Output/" . $uniq_id . "/";
$input_file = "Output/" . $uniq_id . "/data1.txt";
$hxb2_file = "Output/" . $uniq_id . "/hxb2.txt";
$refold_file = "Output/" . $uniq_id . "/query_refold.fasta";
$covar_file = "Output/" . $uniq_id . "/query_covar.fasta";
$output_png = "Output/" . $uniq_id . "/Output.png";
$win_size = 120;


$png_file = "Output/" . $uniq_id . "/output.png";
$R_file = "Output/" . $uniq_id . "/Rscript.r";
mkdir($target_path, 755);

exec("chmod a+wrx " . $target_path);

//echo $target_path . "<br />";
//$target_path = $original_path . basename( $_FILES['uploadedfile']['name']);
$target_path = $input_file;

$input_filename = $_FILES['uploadedfile']['name'];

$files = "";
$space = 0;
if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
    if ($space == 0) {
        $files = $files . $input_file . " " . $param_file;
    } else {
        $files = $files . " " .$input_file . " " . $param_file;
    }
    $space = 1;
    exec("cp " . $target_path . " " . $input_path);
    //echo "The file ".  basename( $_FILES['uploadedfile']['name']).
    //" has been uploaded";
} else{
    //echo "There was an error uploading the file, please try again!";
}

$title = $_POST["Title1"];
$yaxis = $_POST["Yaxis1"];
$colors = $_POST["Colors1"];
$plottype = $_POST["PlotType1"];

$title2 = $_POST["Title2"];
$yaxis2 = $_POST["Yaxis2"];
$colors2 = $_POST["Colors2"];
$plottype2 = $_POST["PlotType2"];

$title3 = $_POST["Title3"];
$yaxis3 = $_POST["Yaxis3"];
$colors3 = $_POST["Colors3"];
$plottype3 = $_POST["PlotType3"];


$title4 = $_POST["Title4"];
$yaxis4 = $_POST["Yaxis4"];
$colors4 = $_POST["Colors4"];
$plottype4 = $_POST["PlotType4"];

$title5 = $_POST["Title5"];
$yaxis5 = $_POST["Yaxis5"];
$colors5 = $_POST["Colors5"];
$plottype5 = $_POST["PlotType5"];

exec("sh runAll.sh ../" . $input_file . " ../" . $hxb2_file . " ../" . $refold_file . " ../" . $covar_file . " " . $win_size . " " . $original_path . " ../" . $output_png);
//exec("cd HXB2Script");
//exec("touch ../" . $hxb2_file);
//exec("chmod a+wrx ../" . $hxb2_file);
//exec("java HXB2Alignment ../" . $input_file . " ../" . $hxb2_file);

//echo $title;
//echo $yaxis;
//echo $colors;
//$file = fopen("runCreateInput.sh", "w");

//fwrite($file,$title);

//fclose( $file );


/*exec("java CreateInputFileAndParameter \"" . $input_path . "\" " . "RAW" . " \"" . $input_file . "\" \"". $param_file . "\" \"" . $title . "\" \"" . $yaxis . "\" \"" . $colors . "\" \"" . $plottype . "\"");
exec("java CreateInputFileAndParameter \"" . $input_path2 . "\" " . "RAW" . " \"" . $input_file2 . "\" \"". $param_file2 . "\" \"" . $title2 . "\" \"" . $yaxis2 . "\" \"" . $colors2 . "\" \"" . $plottype2 . "\"");
exec("java CreateInputFileAndParameter \"" . $input_path3 . "\" " . "RAW" . " \"" . $input_file3 . "\" \"". $param_file3 . "\" \"" . $title3 . "\" \"" . $yaxis3 . "\" \"" . $colors3 . "\" \"" . $plottype3 . "\"");
exec("java CreateInputFileAndParameter \"" . $input_path4 . "\" " . "RAW" . " \"" . $input_file4 . "\" \"". $param_file4 . "\" \"" . $title4 . "\" \"" . $yaxis4 . "\" \"" . $colors4 . "\" \"" . $plottype4 . "\"");
exec("java CreateInputFileAndParameter \"" . $input_path5 . "\" " . "RAW" . " \"" . $input_file5 . "\" \"". $param_file5 . "\" \"" . $title5 . "\" \"" . $yaxis5 . "\" \"" . $colors5 . "\" \"" . $plottype5 . "\"");

$file2 = fopen($original_path."execFile.sh", "w");
fwrite($file2, "java CreateInputFileAndParameter " . $input_path . " " . "RAW" . " " . $input_file . " ". $param_file . " " . $title . " " . $yaxis . " " . $colors . " " . $plottype . "\n");
fwrite($file2, "java CreateInputFileAndParameter " . $input_path2 . " " . "RAW" . " " . $input_file2 . " ". $param_file2 . " " . $title2 . " " . $yaxis2 . " " . $colors2 . " " . $plottype2 . "\n");
fwrite($file2, "java CreateInputFileAndParameter " . $input_path3 . " " . "RAW" . " " . $input_file3 . " ". $param_file3 . " " . $title3 . " " . $yaxis3 . " " . $colors3 . " " . $plottype3 . "\n");
fwrite($file2, "java CreateInputFileAndParameter " . $input_path4 . " " . "RAW" . " " . $input_file4 . " ". $param_file4 . " " . $title4 . " " . $yaxis4 . " " . $colors4 . " " . $plottype4 . "\n");
fwrite($file2, "java CreateInputFileAndParameter " . $input_path5 . " " . "RAW" . " " . $input_file5 . " ". $param_file5 . " " . $title5 . " " . $yaxis5 . " " . $colors5 . " " . $plottype5 . "\n");



//echo "java CreateInputFileAndParameter " . $input_path . "_RAW_" . $input_file . "_" . $param_file . "_" . $title . "_" . $yaxis . "_" . $colors;

//$files = $input_file . " " . $param_file;

exec("java ViralRPlotting " . $png_file . " " . $files . " " . $R_file);

fwrite($file2, "java ViralRPlotting " . $png_file . " " . $files . " " . $R_file . "\n");
//echo("R --vanilla < " . $R_file);
exec("R --vanilla < " . $R_file);

fwrite($file2, "R --vanilla < " . $R_file . "\n");

fclose($file2);
*/
//echo("finiahed");
//$path1= "".$HTTP_POST_FILES['ufile']['name'][0];
//copy($HTTP_POST_FILES['ufile']['tmp_name'][0], $path1);
//$con = mysql_connect($config_mysql_server,$config_mysql_user,$config_mysql_password);

        //echo $name;
//$path=$config_path1.$name;
//mkdir($path);
//echo "<br/>".$path;
//$file = fopen("$path."/input.fasta", "w" );

//echo "write microRNAList.txt";


//$file = fopen("microRNAList.txt", "w");

//fwrite($file,$dna1);

//fclose( $file );

//chdir('/var/www/html/ViralPlot/ViralPlotPipeline');
//chdir($target_path);

//
//<head> <meta HTTP-EQUIV=\"REFRESH\" content=\"5; url=index.html\"> </head> <body>
$file = fopen($original_path."index.html", "w");
$index = "<html>

<head> </head> <body>
<title>Ming Zhang Lab HXB2 Histogram Plot</title>
<link href=\"../../../site.css\" type=\"text/css\" rel=\"stylesheet\" />
<script type=\"text/javascript\" src=\"../../../jquery.corner.js\"></script>
<script type=\"text/javascript\" src=\"../../../jquery.js\"></script>
<script type=\"text/javascript\">
        $(\"#footer\").corner(\"bottom\");
</script>
</head>
<body>
<div class=\"container\" style=\"height:90%;\">
        <div class=\"top\">
                <a>microRNA Target Prediction</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../ViralPlot.php\">Viral Plot</a>
        </div>
        <div class=\"content\" style=\"height:150%;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        <a href=\"PredictionResult.zip\">Full Prediction Result Download</a> <br /> <br />
                        Pasta Fold Result: <br />
                        <img src=\"query_refold.fasta.png\" alt=\"Output\" height=\"500\" width=\"600\"/> <br />

                        Inferred Base on Covariance: <br />
                        <img src=\"query_refold.fasta.png\" alt=\"Output\" height=\"500\" width=\"600\"/> <br />
                        <br />
                        <br />


                        <br />


                        </div>
                </div>

        </div>

</div>
</body>
</html>
";

fwrite($file, $index);
fclose($file);
echo "<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=".$original_path."index.html"."\">";

?>


<title>Ming Lab Plot HIV Plot</title>
<link href="../site.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="../jquery.corner.js"></script>
<script type="text/javascript" src="../jquery.js"></script>
<script type="text/javascript">
        $("#footer").corner("bottom");
</script>
</head>
<body>
<div class="container" style="height:90%;">
        <div class="top">
                <a>HIV Plotn</a><br /><br />
        </div>
        <div class="navbar" align="left">
                <a /><a href="../../../index.php" id="first" >Home</a> <a href="../../../ViralPlot.php">Viral Plot</a>
        </div>
        <div class="content" style="height:87%;">
                <div class="mainpanel" >
                        <div class="register" >
                        <!--<img src=\"output.png\" alt=\"Output\"/>-->
                        <span class="heading2">Proccessing Query</span>

                        <br />
                        <br />


                        <br />


                        </div>
                </div>

        </div>

</div>
</body>
</html>


