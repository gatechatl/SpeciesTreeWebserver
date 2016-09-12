<html>
<head>

<?php
//echo "hello";
//require "config.php";

//echo "running PHP <br />";
$uniq_id = uniqid();

$sna = $_POST["sna"];

$genetrees = $_POST["geneTreeText"];
$file_radio = $_POST["FILEradio"];

$sna_radio = $_POST["SNAradio"];


$original_path = "Output/" . $uniq_id . "/";

$target_path = "Output/" . $uniq_id . "/";
$input_file = "Output/" . $uniq_id . "/input.tre";
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

if ($file_radio == 'text') {
    $file = fopen($input_file, "w");
    fwrite($file, $genetrees);
    fclose($file);

} else {

    if(move_uploaded_file($_FILES['uploadfilegenetree']['tmp_name'], $target_path)) {}
}
#exec("java -Djava.awt.headless=true GenerateNJstScript " . $original_path);

#exec("cp " . "script.r " . $original_path . "script.r");
#chdir($original_path);
#exec("R --vanilla < script.r");
#chdir("../../");
#chdir("source");
#exec("java -Djava.awt.headless=true TreeVector ../" . $original_path . "/output.tre"); 
#chdir("../temp");
#exec("sh convert.sh");
#exec("cp output.png ../" . $original_path . "/output.png");
#chdir("../");

if ($sna_radio == "text") {
    $file = fopen($original_path . "SNA.txt", "w");
    fwrite($file, $sna);
    fclose($file);
} else {
    if(move_uploaded_file($_FILES['SNAuploadfile']['tmp_name'], $original_path . "SNA.txt")) {}

}

$to = $_POST['emailaddress'];
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/NJst/NJst/" . $original_path;
if (mail($to, $subject, $body)) {

}

$to = "gatechatl@gmail.com";
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/NJst/NJst/" . $original_path . "\n" . $_POST['emailaddress'];
if (mail($to, $subject, $body)) {

}

$to = "speciestree@gmail.com";
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/NJst/NJst/" . $original_path . "\n" . $_POST['emailaddress'];
if (mail($to, $subject, $body)) {

}

#exec("ts -S 10");
exec("ts -n sh runNJstSingle.sh " . $original_path);
#exec("ts sh runNJst.sh " . $original_path);
#exec("ts sh runDisplayHTML.sh " . $original_path);

#if (file_exists($original_path . "/output.tre")) {
#$file = fopen($original_path . "/output.tre", "r");
#$treeoutput = "";
#while (!feof($file)) {
#   $treeoutput = $treeoutput . fgets($file);
#   
#}
#fclose($file);
#}

$file = fopen($original_path."index.html_backup", "w");
$index = "
   
<html>
<head> </head> <body>
<title>Species Tree NJst Method Result</title>
<link href=\"../../../site.css\" type=\"text/css\" rel=\"stylesheet\" />
<script type=\"text/javascript\" src=\"../../../jquery.corner.js\"></script>
<script type=\"text/javascript\" src=\"../../../jquery.js\"></script>

<script type=\"text/javascript\" src=\"../../../raphael-min.js\" ></script>

<script type=\"text/javascript\" src=\"../../../jsphylosvg-min.js\"></script>
<script src='../../../jquery-1.7.2.min.js' type='text/javascript'></script>
<script src='../../../jquery.csv2table-0.02-b-4.1.js' type='text/javascript' charset='utf-8'></script>
<script>
$(function(){
  $('#view0').csv2table('./distance.csv');
});
</script>
<script type=\"text/javascript\">


        window.onload = function(){
                        var dataObject = { newick: 'ReplaceMeHere' };
                        phylocanvas = new Smits.PhyloCanvas(
                                dataObject,
                                'svgCanvas',
                                800, 800,'circular'
                        );
                        phylocanvas2 = new Smits.PhyloCanvas(
                                dataObject,
                                'svgCanvas2',
                                800, 1200
                        );
			/* Download Option */
			var svgSource = phylocanvas.getSvgSource();
			if(svgSource){
				$('#download-link')[0].href = \"data:image/svg+xml,\" + svgSource;
				$('#download-button').show();
			}

        };
        </script>
    <script language=\"javascript\" type=\"text/javascript\">
    function setVisible(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.visibility = visible ? 'visible' : 'hidden';
        if (typeof(o) == 'undefined') alert(\"Element with id '\" + id + \"' not found.\");
    }

    function setDisplay(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.display = visible ? 'block' : 'none';
        if (typeof(o) == 'undefined') alert(\"Element with id '\" + id + \"' not found.\");
    }
    setDisplay('svgCanvas2', false);
    </script>

<script type=\"text/javascript\">
        $(\"#footer\").corner(\"bottom\");
</script>

</head>
<body>
<div class=\"container\">
        <div class=\"top\">
                <a>Species Tree NJst</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../NJst.php\">NJst</a>
        </div>
        <div class=\"content\" style=\"height:auto;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        <span class=\"heading1\">Species Tree in Newick Format</span><br />
                        <textarea  rows=\"8\" cols=\"100\"  name=\"dna1\">
ReplaceMeHere</textarea><br />
<br />
<span class=\"heading1\">Downloadable Output</span><br />
<a href=\"input.tre\">input.tre</a> Original Input file -- Right click to download <br />
<a href=\"output.tre\">output.tre</a> Right click to download <br />
<br />
<span class=\"heading1\">Gene Tree and Species Tree Comparison</span><br />

<a href=\"Report.html\">Click Here</a> for species tree report. <br />
<br />
    <div>
        <span class=\"heading1\">Image of Species Tree</span><br />
        Circular Representation
        <input type='radio' name='myradio' value='first' onclick=\"setDisplay('svgCanvas', true); setDisplay('svgCanvas2', false);\" checked />
        Rectangular Representation
        <input type='radio' name='myradio' value='second' onclick=\"setDisplay('svgCanvas2', true); setDisplay('svgCanvas', false);\" />
    </div>

	<div id=\"download-option\" style=\"display: block;\">
		Save the tree (SVG): 
		<a id=\"download-link\" href=\"#\">Right-click and select \"Save as.. (SVG File)\"</a><br />
                Save as PDF <a href=\"Rplots.pdf\">Click Me</a>
	</div>
        <div id=\"svgCanvas\" style=\"display: block\"> </div>
        <div id=\"svgCanvas2\" style=\"display: none\"> </div>

                        <!--<img src=\"output.png\" alt=\"Output\" height=\"500\" width=\"600\"/> <br />-->
<br /><br /><br />
        <!--                <span class=\"heading1\">Gene Tree Visualization</span><br />
                        <iframe height=\"700\" width=\"95%\" frameBorder=\"0\" src=\"genetree.html\">your browser does not support IFRAMEs</iframe><br /><br /><br />
        <span class=\"heading1\">Gene Tree Distance to Species Tree</span><br />-->
<!--<div id='view0'></div>-->

<br /><br /><br />

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

$file = fopen($original_path."index.html", "w");
$index = "

<html>

<head> <meta HTTP-EQUIV=\"REFRESH\" content=\"5; url=index.html\"> </head> <body>
<title>Species Tree NJst Method</title>
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
                <a>Species Tree NJst</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../NJst.php\">NJst</a>
        </div>
        <div class=\"content\" style=\"height:87%;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        <!--<img src=\"output.png\" alt=\"Output\"/>-->
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
fclose($file);
echo "<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=".$original_path."index.html"."\">";

?>


<title>NJst Species Tree</title>
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
                <a>Species Tree</a><br /><br />
        </div>
        <div class="navbar" align="left">
                <a /><a href="../../../index.php" id="first" >Home</a> <a href="../../../NJst.php">NJst</a>
        </div>
        <div class="content" style="height:87%;">
                <div class="mainpanel" >
                        <div class="register" >
                        <span class=\"heading2\">Job Submitted</span>
                         

                        <br />
                        <br />


                        <br />


                        </div>
                </div>

        </div>

</div>
</body>
</html>


