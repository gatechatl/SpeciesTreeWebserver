<html>
<head>

<?php
//echo "hello";
//require "config.php";

//echo "running PHP <br />";

$sna = $_POST["sna"];
$sna_radio = $_POST["SNAradio"];

$uniq_id = uniqid();
$original_path = "Output/" . $uniq_id . "/";

$target_path = "Output/" . $uniq_id . "/";
$zip_output_path = "Output/" . $uniq_id . "/ZipOutput";
$bootstrap_path = "Output/" . $uniq_id . "/Bootstrap";
$input_file = "Output/" . $uniq_id . "/input.zip";
mkdir($target_path, 755);
mkdir($bootstrap_path, 755);
mkdir($zip_output_path, 755);

exec("chmod a+wrx " . $target_path);
exec("chmod a+wrx " . $bootstrap_path);
exec("chmod a+wrx " . $zip_output_path);

//echo $target_path . "<br />";
//$target_path = $original_path . basename( $_FILES['uploadedfile']['name']);
$target_path = $input_file;

$input_filename = $_FILES['uploadedfile']['name'];

$files = "";
$space = 0;
if(move_uploaded_file($_FILES['uploadedZipfile']['tmp_name'], $zip_output_path . "/input.zip")) {}

if ($sna_radio == "text") {
    $file = fopen($original_path . "/Bootstrap/SNA.txt", "w");
    fwrite($file, $sna);
    fclose($file);
} else {
    if(move_uploaded_file($_FILES['SNAuploadfile']['tmp_name'], $original_path . "/Bootstrap/SNA.txt")) {}

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

#exec("ts -S 5");
exec("ts sh runNJstBoot.sh " . $original_path);
#exec("ts sh runBootStrap.sh " . $original_path);
#exec("ts sh runDisplayHTML.sh " . $original_path);

$file = fopen($original_path."index.html_backup", "w");
$index = "<html>

<head> </head> <body>
<title>Species Tree BootStrap Method</title>
<link href=\"../../../site.css\" type=\"text/css\" rel=\"stylesheet\" />
<script type=\"text/javascript\" src=\"../../../jquery.corner.js\"></script>
<script type=\"text/javascript\" src=\"../../../jquery.js\"></script>
<script type=\"text/javascript\" src=\"../../../raphael-min.js\" ></script>

<script type=\"text/javascript\" src=\"../../../jsphylosvg-min.js\"></script>

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
<div class=\"container\" style=\"height:2000%;\">
        <div class=\"top\">
                <a>NJst Tree BootStrap</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../NJst.php\">NJst</a>
        </div>
        <div class=\"content\" style=\"height:150%;\">
                <div class=\"mainpanel\" >
                        <div class=\"register\" >
                        Species Tree in Newick Format (Note that length of tree are not branch length but the number of times that each group appeared in the input tree)<br />
                        <textarea  rows=\"8\" cols=\"80\"  name=\"dna1\">
ReplaceMeHere</textarea><br />

   <div>
        Circular Representation
        <input type='radio' name='myradio' value='first' onclick=\"setDisplay('svgCanvas', true); setDisplay('svgCanvas2', false);\" checked />
        Rectangular Representation
        <input type='radio' name='myradio' value='second' onclick=\"setDisplay('svgCanvas2', true); setDisplay('svgCanvas', false);\" />
    </div>
        <div id=\"download-option\" style=\"display: block;\">
                Save the tree (SVG): <br/>
                <a id=\"download-link\" href=\"#\">Right-click and select \"Save as.. (SVG File)\"</a><br /><br />
                Save as PDF <a href=\"Rplots.pdf\">Click Me</a>
        </div>

        <div id=\"svgCanvas\" style=\"display: block\"> </div>
        <div id=\"svgCanvas2\" style=\"display: none\"> </div>
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
<title>Species Tree BootStrap Method</title>
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
                <a>Species Tree BootStrap</a><br /><br />
        </div>
        <div class=\"navbar\" align=\"left\">
                <a /><a href=\"../../../../index.php\" id=\"first\" >Home</a> <a href=\"../../../BootStrap.php\">BootStrap</a>
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
fclose($file);


echo "<meta HTTP-EQUIV=\"REFRESH\" content=\"0; url=".$original_path."index.html"."\">";

?>


<title>BootStrap Species Tree</title>
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
                <a /><a href="../../../index.php" id="first" >Home</a> <a href="../../../BootStrap.php">BootStrap</a>
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


