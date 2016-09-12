<html>
<head>

<?php
//echo "hello";
//require "config.php";

$dna1=$_POST["dna1"];
$dna1 = preg_replace('/\r\r/', '\r', $dna1);
$organism = $_POST["organism"];
$num_method = $_POST["num_method"];
//$con = mysql_connect($config_mysql_server,$config_mysql_user,$config_mysql_password);
  
	//echo $name;
//$path=$config_path1.$name;
//mkdir($path);
//echo "<br/>".$path;
//$file = fopen("$path."/input.fasta", "w" );

//echo "write microRNAList.txt";
$file = fopen("microRNAList.txt", "w");

fwrite($file,$dna1);

fclose( $file );

$file = fopen("organism.txt", "w");
fwrite($file, $organism);
fclose($file);

$file = fopen("num_method.txt", "w");
fwrite($file, $num_method);
fclose($file);

//$file = fopen( $config_path1.$name.".txt", "w" );
//fwrite($file,$times);
//fclose($file);



//$file = fopen( $path."/index.html", "w" );

chdir('/var/www/html/MingLabmicroRNATarget/MingMicroRNATargetPipeline');
//$output = shell_exec("runAll.sh");
//echo(nl2br($output));
#echo exec("sh runAll.sh");
$id = uniqid();
$file = fopen("currentID.txt", "w");
fwrite($file, $id);
fclose($file);

$path = 'Result/' . $id;
$path2 = 'Result/' . $id . '/Output';
mkdir($path, 755);
mkdir($path2, 755);

$file = fopen($path."/index.html", "w");
$index = "<html>
<head> <meta HTTP-EQUIV=\"REFRESH\" content=\"5; url=index.html\"> </head> <body>

<title>Ming Lab microNRA Target Predictions</title>
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
                <a /><a href=\"../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../microRNATarget.php\">microRNATarget</a>
        </div>
        <div class=\"content\" style=\"height:87%;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >

                        <span class=\"heading2\">Processing Query <img src=\"../../../images/ajax-loader.gif\" alt=\"some_text\"/></span>
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


$output = exec("ts sh runAll.sh $id");
echo $output;

echo "<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=".$path."/index.html"."\">";

chdir('/var/www/html/MingLabmicroRNATarget/MingMicroRNATargetPipeline');
?>

<title>Ming Lab microNRA Target Predictions</title>
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
                <a>microRNA Target Prediction</a><br /><br />
        </div>
        <div class="navbar" align="left">
                <a /><a href="../index.php" id="first" >Home</a> <a href="../microRNATarget.php">microRNATarget</a>
        </div>
        <div class="content" style="height:87%;">
                <div class="mainpanel" >
                        <div class="register" >
                        
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




