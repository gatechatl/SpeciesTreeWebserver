<html>
<head>

<?php
//echo "hello";
//require "config.php";

//echo "running PHP <br />";
$triple = $_POST["triple"];
$seed = $_POST["Seed"];
$sna = $_POST["sna"];
$initialtree = $_POST["usertree"];

$genetrees = $_POST["geneTreeText"];
$file_radio = $_POST["FILEradio"];

$sna_radio = $_POST["SNAradio"];



$uniq_id = uniqid();

$original_path = "Output/" . $uniq_id . "/";

$target_path = "Output/" . $uniq_id . "/";
$input_file = "Output/" . $uniq_id . "/input.tre";

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

exec("cp Output/" . $uniq_id . "/input.tre mpest_1.2/data/genetree.tree");

$countGeneTrees = 0;
$genetreeFile = fopen("Output/" . $uniq_id . "/input.tre", "r");

while(!feof($genetreeFile)) { 
    $line = fgets($genetreeFile);
    if (strlen(trim($line)) > 1) {
        $countGeneTrees++;
    }
}



fclose($genetreeFile);

//chdir("./mpest_1.2/data");
//exec("rm control");

//$file = fopen("control", "w");


$startTree = "";
$array = explode("\n", $initialtree);
$randomTree = 0;
//foreach ($array as &$value) {
//    if (strlen(trim($value)) > 1) {
//        $startTree = $startTree . str_replace("\r", "", $value);
//        $randomTree = 1;
//    }
//}

if ($sna_radio == "text") {
    $file = fopen($original_path . "SNA.txt", "w");
    fwrite($file, $sna);
    fclose($file);
} else {
    if(move_uploaded_file($_FILES['SNAuploadfile']['tmp_name'], $original_path . "SNA.txt")) {
       $sna = file_get_contents($original_path . "SNA.txt"); 
    }
}

$realsna = "";
$array = explode("\n", $sna);
$countspecies = 0;
foreach ($array as &$value) {
    if (strlen(trim($value)) > 1) {
        $realsna = $realsna . str_replace("\r", "", $value) . "\n";
        $countspecies++;
    }
}

if ($realsna == "") {

    exec("sh runCONTROL.sh " . $original_path);
    $sna = file_get_contents($original_path . "/controlFile");

    $array = explode("\n", $sna);
    $countspecies = 0;
    foreach ($array as &$value) {
            if (strlen(trim($value)) > 1) {
                $realsna = $realsna . str_replace("\r", "", $value) . "\n";
                $countspecies++;
            }
    }


}

#$output = "input.tre\n" . "0" . "\n" . $seed . "\n" . $countGeneTrees . " " . $countspecies . "\n" . $realsna . $randomTree . "\n" . $startTree;
$output = "input.tre\n" . "0" . "\n" . "-1" . "\n" . $countGeneTrees . " " . $countspecies . "\n" . $realsna . "0" . "\n" . "";
$file = fopen("Output/" . $uniq_id . "/control", "w");
fwrite($file, $output);
fclose($file);

//chdir("../../");

#exec("../src/mpest control");
#chdir("../../");
#chdir("./mpest_1.2/data");
#exec("R --vanilla < convert.R");
#chdir("../../");
#exec("cp ./mpest_1.2/data/genetree.tree.tre " . $original_path . "/output.nex");
#exec("cat ./mpest_1.2/data/output.tre | tail -n 1 > " . $original_path . "/output.tre");
#chdir("source");
#exec("java -Djava.awt.headless=true TreeVector ../" . $original_path . "/output.tre");
#chdir("../temp");
#exec("sh convert.sh");
#exec("cp output.png ../" . $original_path . "/output.png");
#chdir("../");

$to = $_POST['emailaddress'];
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/mpest/mpest/" . $original_path;
if (mail($to, $subject, $body)) {

}

$to = "gatechatl@gmail.com";
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/mpest/mpest/" . $original_path . "\n" . $_POST['emailaddress'];
if (mail($to, $subject, $body)) {

}

$to = "speciestree@gmail.com";
$subject = "STRAW Job Submitted";
$body = "Hi there,\n\nHere's the link to your job:\nhttp://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysis/mpest/mpest/" . $original_path . "\n" . $_POST['emailaddress'];
if (mail($to, $subject, $body)) {

}

#exec("ts -r > " . $original_path . "/tsr1.txt");
#exec("ts -r > " . $original_path . "/tsr2.txt");
#exec("ts -r > " . $original_path . "/tsr3.txt");
#exec("ts -r > " . $original_path . "/tsr4.txt");
#exec("ts -r 59");
#exec("ts -r 61");
#exec("ts -r 62");
#exec("ts -r 66");
exec("ts -l > " . $original_path . "/ts.txt");
#exec("ts -S 6");
#exec("ts -S 19");
exec("ts -n sh runMPESTSingle.sh " . $original_path);
#exec("ts sh runMPESTSingle.sh " . $original_path);
#exec("ts sh runMPEST.sh ". $original_path);
#exec("ts sh runDisplayHTML.sh " . $original_path);
#if (file_exists($original_path . "/output.nex")) {
#$file = fopen($original_path . "/output.nex", "r");
#$treeoutput = "";
#while (!feof($file)) {
#   $treeoutput = $treeoutput . fgets($file);
   
#}
#fclose($file);
#}

$file = fopen($original_path."index.html_backup", "w");

$index = "
   
<html>
<head> </head> <body>
<title>MP-EST tree</title>
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
                <a>Species Tree MP-EST</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../mpest.php\">MP-EST</a>
        </div>
        <div class=\"content\" style=\"height:auto;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        <span class=\"heading1\">Species Tree in Newick Format</span><br />
                        <textarea  rows=\"8\" cols=\"100\"  name=\"dna1\">
ReplaceMeHere</textarea><br />
<br />
<span class=\"heading1\">Input and Output Downloads</span><br />

<a href=\"input.tre\">input.tre</a> Original Input file -- Right click to download <br />
<a href=\"output.tre\">output.tre</a> Output Tre File -- Right click to download <br />
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
                        <!--<span class=\"heading1\">Gene Tree Visualization</span><br />
                        <iframe height=\"700\" width=\"95%\" frameBorder=\"0\" src=\"genetree.html\">your browser does not support IFRAMEs</iframe><br /><br /><br />-->
<!--<div id='view0'>-->

</div><br /><br /><br />

                        <br />

                        </div>

                </div>

        </div>

</div>
</body>
</html>
";

$index2 = "<html>

<head> </head> <body>
<title>MP-EST tree</title>
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
                <a>MP-EST</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../mpest.php\">MP-EST</a>
        </div>
        <div class=\"content\" style=\"height:150%;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        Species Tree in Newick Format <br />
                        <textarea  rows=\"8\" cols=\"80\"  name=\"dna1\">
ReplaceMeHere</textarea><br />
                        <img src=\"output.png\" alt=\"Output\" height=\"500\" width=\"600\"/> <br />

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
<title>Species Tree mpest Method</title>
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
                <a>Species Tree mpest</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../mpest.php\">mpest</a>
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


<title>MP-EST Species Tree</title>
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
                <a /><a href="../../../index.php" id="first" >Home</a> <a href="../../../mpest.php">MP-EST</a>
        </div>
        <div class="content" style="height:87%;">
                <div class="mainpanel" >
                        <div class="register" >
                        <!--<img src=\"output.png\" alt=\"Output\"/>-->
                        <span class="heading1">Proccessing Query</span>

                        <br />
                        <br />


                        <br />


                        </div>
                </div>

        </div>

</div>
</body>
</html>


