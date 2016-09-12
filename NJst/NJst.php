<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>NJst program</title>
<link href="../site.css" type="text/css" rel="stylesheet" />
<!--<meta http-equiv="refresh" content="0; http://odyssey.bioinformatics.uga.edu/~lliu/SpeciesTreeAnalysis/NJst/NJst.php" />-->
<script type="text/javascript" src="jquery.corner.js"></script>
<script type="text/javascript" src="jquery.js"></script>
		<script type="text/javascript" src="../js/lib/prototype/prototype.js"></script>
		<script type="text/javascript" src="../js/lib/scriptaculous/scriptaculous.js"></script>
		<script type="text/javascript" src="../js/src/HelpBalloon.js"></script>
		<script type="text/javascript">
		<!--
		//
		// Override the default settings to point to the parent directory
		//
		HelpBalloon.Options.prototype = Object.extend(HelpBalloon.Options.prototype, {
			icon: '../js/images/icon.gif',
			button: '../js/images/button.png',
			balloonPrefix: '../js/images/balloon-'
		});
		
		//-->
		</script>
<script type="text/javascript">
	$("#footer").corner("bottom");

    function setVisible(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.visibility = visible ? 'visible' : 'hidden';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
        
    }

    function setDisplay(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.display = visible ? 'block' : 'none';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
    }

    function setDisabled(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.disabled = visible ? '' : 'disabled';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
    }

</script>

</head>
<body>
<!--<script type="text/javascript">
        var hb1 = new HelpBalloon({
                title: 'Non-Ajax Balloon',
                content: 'This is an example of static '
                        + 'balloon content.',
                autoHideTimeout: 2000
        });
</script>-->

<div class="container" style="height:200%;">
	<div class="top">
		<a>NJst Program</a><br /><br />
	</div>
	<div class="navbar" align="left">
<font size=4>           <a /><a href="../index.php" id="first" >Home</a> <a href="../STAR/STAR.php">STAR</a> <a href="../mpest/mpest.php">MP-EST</a> <a href="../NJst/NJst.php">NJst</a> <a href="../ExtractSpecies/GenerateSpeciesAllele.php">SpeciesAlleleTableCreator</a> <a href="../RerootTree/unrooted2rooted.php">RerootTree</a><a href="../Help/index.php">Help</a></font>
     </div>
	<div class="content" style="height:200%;">
		<div class="mainpanel" >
			<div class="register" >


			<span class="heading1">NJst Program</span>

                        <script type="text/javascript">
                            var hb1 = new HelpBalloon({
                            title: 'NJst Species Tree',
                            content: 'The NJst method estimates species trees from a collection of unrooted gene trees.',
                            autoHideTimeout: 2000
                            });
                        </script>
<div id="description" style="display: block">
<INPUT type="button" value = "Expand Description >>" name=button1 onclick="setDisplay('description', false); setDisplay('hidedescription', true); setDisplay('div3', true);" style="width:190px;height:30px">
<!--    Hide Description
    <input type='radio' name='myradio' value='first' onclick="setDisplay('div3', false); " checked /> Show Description
    <input type='radio' name='myradio' value='second' onclick="setDisplay('div3', true); " /> <br />-->
</div>
<div id="hidedescription" style="display: none">
<INPUT type="button" value = "Hide Description >>" name=button1 onclick="setDisplay('description', true); setDisplay('hidedescription', false); setDisplay('div3', false);" style="width:160px;height:30px">
</div>
<div id="div3" style="display: none">
<hr >
The NJst method estimates species trees from a collection of <font color="red">unrooted</font> gene trees. The format of the input gene trees for the NJst method is the same as those for the STAR and MP-EST methods, except that NJst accepts both unrooted and rooted gene trees. <a href="../Help/index.php#Example" id="first" >The examples of the input files are available here</a>. When multiple alleles (or sequences) are sampled from each species (or some species), the user must provide a species-allele table to indicate which alleles belong to which species. For example, the following gene trees have taxa S1-6.
<br /><br />

<center><font color="blue">
(((((S1:0.1,S2:0.1):0.1,S4:0.1):0.1,S3:0.1):0.1,S5:0.1):0.1,S6:0.2); <br />
(((((S1:0.1,S2:0.1):0.1,S4:0.1):0.1,S5:0.1):0.1,S3:0.1):0.1,S6:0.2); <br />
(((((S1:0.1,S2:0.1):0.1,S4:0.1):0.1,S3:0.1):0.1,S5:0.1):0.1,S6:0.2); <br /><br /></font></center>

Suppose S1 and S2 were sampled from Human, S3 and S4 were sampled from Ape, S5 were sampled from Gorilla, and S6 was sampled from Chimpanzee. Then the species-allele table may be <br />

<center>
<table border="0" style="color:blue">
<font color="blue">
<tr>
<td>H</td>
<td>2</td>
<td>S1 S2</td>
</tr>
<tr>
<td>A</td>
<td>2</td>
<td>S3 S4</td>
</tr>
<tr>
<td>G</td>
<td>1</td>
<td>S5</td>
</tr>
<tr>
<td>C</td>
<td>1</td>
<td>S6</td>
</tr>
</table>
<br /></center>
</font>
Each line specifies "the species name", "the number of alleles", "the names of the alleles". Note that you could use any names as the species names. For help, click <a href="../ExtractSpecies/GenerateSpeciesAllele.php" id="first" >here</a>. If each species has only one  allele (or sequence), you do not need to provide this table. Just upload the gene tree file and click "submit". When multiple alleles were sampled, please provide the species-allele table in the textbox and then upload the tree file and click "submit". <br />
</div><br >
        <hr>

			<!--<form name="input" action="NJst/runNJst.php" method="post" enctype="multipart/form-data">-->
                        <form name="input" action="../callPostRequest.php" method="post" enctype="multipart/form-data">
			<!--Sequence: <br />
                        <textarea  rows="6" cols="50"  name="dna1">>test
ATGGAAAACAGATGGCAGGTGATGATTGTGTGGCAAGTAGACAGGATGAGGATTAGAACATGGAAAAGTTTAGTAAAACACCATATGTATAGGTCAGGAAAAGCTAAGGGATGGTTTTATAGACATCACTATGAAAGCACTCATCCAAGAATAAGTTCAGAAGTACACATCCCACTAGGGGATGCTAGATTGGTAATAACAACATGTTGGGGTCTGCAGACAGGAGAAAGAGAACGGCATTTGGGCCAGGGAGTCTCCATAGAATGGAGGAAAAAGAGATATAGCACGCAAGTAGACCCTAGCCTAGCAGACCAACTAATTCATCTGTATTACTTTGATTGTTTTTCAGAATCTGCTATAAGAAATGCCATATTAGGACATATAGTTAGTCCTAGTTGTGAATATCAAGCAGGACATAACAAGGTAGGATCTCTGCAGTACTTGGCACTAGCAGCATTAAGAACACCAAAAAAGATAAAGCCACCTTTGCCTAGTGTTACAAAATTGACAGAGGATAGATGGAACAAGCCCCAGAAGACCAAGGGCCACAGAGGGAGCCATACAATGAATGGACACTAG
</textarea>-->

<table style="text-align: left; width: 1338px; height: 60px;" border="0"
cellpadding="2" cellspacing="2">
<tbody>

<tr>
<td width=15%  style="vertical-align: top; background-color:lightblue">
    <div id='geneTree1' style="display: block">
        <span class="heading2">Input Gene Trees</span>
    </div>
    <div id='bootTree1' style="display: none">
        <span class="heading2">Input BootStrap</span>
    </div>

</td>
<td>

                        <script type="text/javascript">
                            var hb1 = new HelpBalloon({
                            title: 'Input Trees',
                            content: 'The input gene trees must be bifurcating rooted trees (w/o branch lengths) in Newick format, for examples, the ML trees generated from PHYML, RAXML, or PHYLIP and rooted with the outgroup species.',
                            autoHideTimeout: 2000
                            });
                        </script>
<span class="heading2">Require Input</span>
</td>
</tr>
<tr>

<td width=15% style="vertical-align: top;">
    <div id='geneTree2' style="display: block">
        <input type='radio' id = 'Fileradio1' name='FILEradio' value='file' onclick="setDisabled('uploadfilegenetree',true);setDisabled('geneTreeText',false);" checked /> <b>Gene Trees File</b>
    </div>
    <div id='bootTree2' style="display: none">
        <input type='radio' name='boottree' value='file' onclick=";" checked /> <b>Bootstrap Trees File</b>
    </div>

</td>
<td style="vertical-align: top;">
    <div id='geneTree3' style="display: block">
        <input id="uploadfilegenetree" name="uploadfilegenetree" type="file" ><br />
    </div>
    <div id='bootTree3' style="display: none">
        <input id="uploadedZipfile" name="uploadedZipfile" type="file" ><br /><br />
Upload a zip file with bootstrapped tree files ending with *.tre *.tree *.txt or *.newick.  See Example File.
    </div>

</td>
</tr>

<tr>
<td width=15% style="vertical-align: top;">
    <div id='geneTree4' style="display: block">
        <input type='radio' id = 'Fileradio2' name='FILEradio' value='text' onclick="setDisabled('uploadfilegenetree',false); setDisabled('geneTreeText',true);" /> <b>Gene Trees (Text)</b> <br />for this option copy and paste the gene trees into the text box
    </div>
    <div id='bootTree4kill' style="display: none">
        <input type='radio' name='boottree' value='text' onclick=";" /> Bootstrap Trees (Text)
    </div>

</td>
<td style="vertical-align: top;">
<div id='geneTree5' style="display: block">
<textarea id="geneTreeText" rows="6" cols="70"  name="geneTreeText" disabled="disabled">
</textarea>
    </div>
</td>
</tr>

<tr>
<td width=15%  style="vertical-align: top;">
<!--<div id='bootTree1' style="display: none">
<span class="heading2">Input BootStrap</span>
</div>-->

</td>
</tr>

<tr color=>
<td width=15%  style="vertical-align: top; background-color:lightblue">
    <div id='geneTree1' style="display: block">
         <span class="heading2">Species Allele Table</span>
    </div>

</td>

<td style="vertical-align: top; ">
                        <script type="text/javascript">
                            var hb1 = new HelpBalloon({
                            title: 'Species Allele Table',
                            content: 'When multiple alleles (or sequences) are sampled from each species (or some species), the user must provide a species-allele table to indicate which alleles belong to which species.',
                            autoHideTimeout: 2000
                            });
                        </script>

<span class="heading2">(Optional)</span>
<!--<div id='allele6' style="display: block">
<input type="button" name="uploadedfile" value="Expand >>" ><br />
</div>-->
</td>

</tr>

<tr >
<td width=15% style="vertical-align: top;">
<input type='radio' name='SNAradio' value='file' onclick="setDisabled('SNAuploadfile',true); setDisabled('SNAFileText',false);" checked /> <b>Species Allele File</b>
</td>
<td style="vertical-align: top;"><input id="SNAuploadfile" name="SNAuploadfile" type="file" ><br />
</td>
</tr>

<tr>
<td width=15% style="vertical-align: top;">
<input type='radio' name='SNAradio' value='text' onclick="setDisabled('SNAuploadfile',false); setDisabled('SNAFileText',true);"

<?php
if (!empty($_POST['control'])) {
  echo " checked ";
}
?>

/> <b>Species Allele (Text)</b> <br>for this option copy and paste the species to allele information into the text box
</td>

<td style="vertical-align: top;">
<textarea id="SNAFileText" rows="6" cols="70"  name="sna"
<?php
if (!empty($_POST['control'])) {

} else {
  echo "disabled='disabled'";
}
?>

>
<?php
if (!empty($_POST['control'])) {
  echo $_POST['control'];
}
?>
</textarea> <br />
</td>
</tr>

<tr color=>
<td width=15%  style="vertical-align: top; background-color:lightblue">
    <div id='geneTree1' style="display: block">
         <span class="heading2">Validation</span>
    </div>

</td>

<td style="vertical-align: top; ">
                        <script type="text/javascript">
                            var hb1 = new HelpBalloon({
                            title: 'Validation',
                            content: 'Use Bootstrap option if multiple gene trees are available.',
                            autoHideTimeout: 2000
                            });
                        </script>
                        <span class="heading2">Option for Verifying the species tree if gene tree bootstrap is present.</span>
<span class="heading2"></span>
<!--<div id='allele6' style="display: block">
<input type="button" name="uploadedfile" value="Expand >>" ><br />
</div>-->
</td>
</tr>

<tr >
<td width=15% style="vertical-align: top;">
    <input type='radio' name='myradio' value='first' onclick="setDisplay('div1', true); setDisplay('div2', false); setDisplay('geneTree1', true);setDisplay('bootTree1', false);setDisplay('geneTree2', true);setDisplay('bootTree2', false);; setDisplay('geneTree3', true);setDisplay('bootTree3', false); ; setDisplay('geneTree4', true);
; setDisplay('geneTree5', true);" checked /><b>No Bootstrap Calculation</b>
</td>
<td style="vertical-align: top;">
To run, upload a standard gene tree file.
</td>
</tr>
<tr >
<td width=15% style="vertical-align: top;">
    <input type='radio' name='myradio' value='second' onclick="setDisplay('div1', false); setDisplay('div2', true); setDisplay('geneTree1', false);setDisplay('bootTree1', true);setDisplay('geneTree2', false);setDisplay('bootTree2', true); setDisplay('geneTree3', false);setDisplay('bootTree3', true); setDisplay('geneTree4', false);setDisplay('geneTree5', false);" /><b>Bootstrap Calculation</b>

</td>
<td style="vertical-align: top;">
To run, upload a one zip file with a folder containing the bootstrap trees for a gene per file.
</td>
</tr>

<tr color=>
<td width=15%  style="vertical-align: top; background-color:lightblue">
    <div id='geneTree1' style="display: block">
         <span class="heading2">Email</span>
    </div>

</td>

<td style="vertical-align: top; ">
                <span class="heading2">(Optional) We will email web link containing species tree result.</span>
<span class="heading2"></span>
<!--<div id='allele6' style="display: block">
<input type="button" name="uploadedfile" value="Expand >>" ><br />
</div>-->
</td>
</tr>


<tr color=>
<td width=15%  style="vertical-align: top;">
    <div id='geneTree1' style="display: block">
         <b>Input Email Address:</b>

    </div>

</td>

<td style="vertical-align: top; ">
<input type="text" id='emailaddress' name='emailaddress' size="40">
                <!--<span class="heading2">Option for Verifying the species tree if gene tree bootstrap is present.</span>-->
<span class="heading2"></span>
<!--<div id='allele6' style="display: block">
<input type="button" name="uploadedfile" value="Expand >>" ><br />
</div>-->
</td>
</tr>


</tbody>
</table>
<br />
<div>
<!--
    Single Tree
    <input type='radio' name='myradio' value='first' onclick="setDisplay('div1', true); setDisplay('div2', false); setDisplay('geneTree1', true);setDisplay('bootTree1', false);setDisplay('geneTree2', true);setDisplay('bootTree2', false);; setDisplay('geneTree3', true);setDisplay('bootTree3', false); ; setDisplay('geneTree4', true);
; setDisplay('geneTree5', true);" checked /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Bootstrap Gene Tree
    <input type='radio' name='myradio' value='second' onclick="setDisplay('div1', false); setDisplay('div2', true); setDisplay('geneTree1', false);setDisplay('bootTree1', true);setDisplay('geneTree2', false);setDisplay('bootTree2', true); setDisplay('geneTree3', false);setDisplay('bootTree3', true); setDisplay('geneTree4', false);
setDisplay('geneTree5', false);" /> <br />

-->
</div>
<hr>
<div id="div1" style="display: block">
                        <INPUT type="button" value = "Submit" name=button1 onclick="return OnButton1();" style="width:100px;height:30px; ">
 <INPUT type="button" value = "Example" name=button3 onclick="return OnButtonFill();" style="width:100px;height:30px; ">
                        <!--<INPUT type="button" value = "(Beta) Access Cluster Version" name=button4 onclick="return OnButton3();" style="width:200px;height:30px">--><br /><br />

</div>

<div id="div2" style="display: none">
                        <INPUT type="button" value = "Submit" name=button2 onclick="return OnButton2();" style="width:100px;height:30px">
                        <!--<INPUT type="button" value = "(Beta) Access Cluster Version" name=button4 onclick="return OnButton3();" style="width:200px;height:30px">-->

</div>

<!--<div id="div1" style="display: block">
Upload rooted gene trees one line per tree: 
                        <input name="uploadedfile" type="file" ><br />
                        <INPUT type="button" value = "Create Single Species Tree" name=button1 onclick="return OnButton1();"><br /> <br />
</div>

<div id="div2" style="display: none">
Upload ZIP file containing the bootstrapped gene trees:
                        <input name="uploadedZipfile" type="file" /><br />
                        <INPUT type="button" value = "Species Tree Bootstrap" name=button2 onclick="return OnButton2();">
</div>
-->
			</form>
			<br />


			</div>
		</div>
		
	</div>

</div>
</body>

</html>
<script language="Javascript">
<!--
function OnButtonFill() {
    document.getElementById("geneTreeText").value = "((S8:0.028616,(((((S1:0.010277,S2:0.010277):0.007777,S4:0.018054):0.000377,S3:0.018431):0.000245,S5:0.018677):0.005845,S7:0.024521):0.004094):0.001399,(S9:0.030009,S6:0.030009):6e-06);\n((S8:0.028616,(((((S1:0.010277,S2:0.010277):0.007777,S4:0.018054):0.000377,S3:0.018431):0.000245,S5:0.018677):0.005845,S7:0.024521):0.004094):0.001399,(S9:0.030009,S6:0.030009):6e-06);\n(S6:0.030046,(((S7:0.025279,(S5:0.018478,((S2:0.009864,S1:0.009864):0.008153,(S4:0.015993,S3:0.015993):0.002025):0.000461):0.006801):0.003254,S8:0.028533):0.00148,S9:0.030013):3.3e-05);\n(((S1:0.020147,((S2:0.018781,S5:0.018781):0.000478,(S3:0.017266,S4:0.017266):0.001994):0.000888):0.009864,S9:0.030011):7e-06,((S7:0.030006,S8:0.030006):2e-06,S6:0.030008):1e-05);\n((S8:0.030054,(S7:0.03002,(S6:0.024093,(S1:0.020418,(S5:0.018394,S4:0.018394):0.002024):0.003675):0.005927):3.5e-05):1e-05,((S2:0.018888,S3:0.018888):0.011113,S9:0.030001):6.3e-05);\n((S6:0.03001,S8:0.03001):5.6e-05,((((S1:0.018738,(S2:0.012624,S3:0.012624):0.006114):0.001729,S5:0.020467):0.000252,S4:0.020719):0.009331,(S9:0.030002,S7:0.030002):4.8e-05):1.6e-05);";

    document.getElementById("Fileradio2").checked = 'checked';
setDisabled('uploadfilegenetree',false); setDisabled('geneTreeText',true);
}

function OnButton1()
{

    var file = document.getElementById("uploadfilegenetree");
    var text = document.getElementById("geneTreeText");

    if ((file.value != "" && !file.disabled) || !text.disabled) {

        document.input.action="NJst/runNJst.php"
        document.input.submit();

    } else {
        window.alert('For Input Gene Trees, please select a gene tree file or text file.');
    }
}

function OnButton2()
{
    if(document.getElementById("uploadedZipfile").value != "") {

        document.input.action="NJst/runBootStrap.php"
        document.input.submit();
    } else {
         window.alert('For Input Bootstrap, please select a zip file containing the bootstrapped gene trees.  (One gene per file)')
    }


}
function OnButton3()
{
    window.location.href='http://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysisCluster/NJst/NJst.php';
}
function setVisible(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.visibility = visible ? 'visible' : 'hidden';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
}

function setDisplay(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.display = visible ? 'block' : 'none';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
}


function setVisible(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.visibility = visible ? 'visible' : 'hidden';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
}

function setDisplay(id, visible) {
        var o = document.getElementById(id);
        if (typeof(o) != 'undefined') o.style.display = visible ? 'block' : 'none';
        if (typeof(o) == 'undefined') alert("Element with id '" + id + "' not found.");
}

-->
</script>

