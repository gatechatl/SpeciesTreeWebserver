<html>
<head>
<title>Species Tree</title><link rel="shortcut icon" href="http://bioinformatics.publichealth.uga.edu/favicon.ico" type="image/x-icon" />
<link href="site.css" type="text/css" rel="stylesheet" />
<!--script type="text/javascript" src="jquery.corner.js"></script-->
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
<!--script type="text/javascript">
	$("#footer").corner("bottom");
</script-->
</head>
<body>
<div class="container">
	<div class="top">
		<a><img src="images/WebServerLogo.png" alt="some_text" width="60" height="60" ALIGN=LEFT>STRAW: Species TRee Analysis Web server</a><br /><br />
	</div>
    <div class="navbar" align="left">
       <font size=4>  <a /><a href="index.php" id="first" style="color:#75C556;">Home</a> <a href="STAR/STAR.php">STAR</a> <a href="mpest/mpest.php">MP-EST</a> <a href="NJst/NJst.php">NJst</a> <a href="ExtractSpecies/GenerateSpeciesAllele.php">SpeciesAlleleTableCreator</a> <a href="RerootTree/unrooted2rooted.php">RerootTree</a><a href="Help/index.php">Help</a></font>
	</div>
	<div class="content">
		<div class="mainpanel" >
			<div class="register" >
		<!--<span class="heading1">Species Tree Tool Suite. </span> -->

<br />

<span class="heading1">Introduction</span><br />
<img src="/images/model1-framed.jpg" alt="some_text" width="250" height="250" ALIGN=RIGHT><font size="4">The species tree reconstruction webserver [1] consists of a series of phylogenetic tools for estimating species trees from phylogenomic data: 1) rooting gene trees with outgroup species, 2) estimating species trees from the rooted gene trees using STAR [2], MP-EST [3], NJst methods [4], 3) bootstrap analyses for the species tree reconstruction methods (STAR, MP-EST, and NJst). <!--For larger queries that exceed our requirement could be submitted to our the cluster version of our webserver.  The cluster version could be accessed <a href="http://bioinformatics.publichealth.uga.edu/SpeciesTreeAnalysisCluster/index.php">Here</a>.--> <br />
*Note* We've removed the cluster version back to development due to recurring disconnectivity with UGA GACRC zcluster.  As a result, we added additional job processing power within our server to run multiple job sumissions.</font>

<br /><br />
<span class="heading1">News</span><br />
March 13 2013-- Added capacity for running multiple jobs at the same time. (Previously could only process one job at a time);<br />
March 14 2013-- Fixed problem with embedded tree files within bootstrapping.<br />
March 17 2013-- Added function for taking input trees with inconsistent branch length. <br />
March 19 2013-- Added recogniztion of *.nwk as file type for bootstrap zipped file.  Previously only files ending with file type *.tre, *.phylip, *.txt and *.new were accepted for the zipped file for bootstrapping. <br />
April 03 2013-- I talked with some of the user and realized that some users found their jobs still running.  There was a power outage and affected the running jobs.<br />
April 03 2013-- Added report style distance measures. <br />
April 03 2013-- Added species tree branch distance within bootstrap tre file result.<br />
June 13 2013-- There were some hardware issues with the server and we had to reboot the system. Sorry if any jobs were submitted that showed invalid input etc... The pipeline stopped working due to some hardware problem.<br />
Dec 11 2014-- Updated the bootstrapping feature to lower the memory usage.
<br />
<br />
<span class="heading1">References</span><br />
[1] Shaw, T., Z. Ruan, T. Glenn, and Liu, L. STRAW: Species TRee Analysis Web server. Nucleic Acids Research. 2013, doi: 10.1093/nar/gkt377 <br />
[2] Liu, L., L. Yu, D.K. Pearl, and S.V. Edwards. Syst. Biol. 2009, 58(5):468-477. <br />
[3] Liu, L., L. Yu, S.V. Edwards. BMC Evol. Biol. 2010, 10:302. <br />
[4] Liu, L., and L. Yu. Syst. Biol. 2011, 60: 661-667. <br />

<br />
<br />
<span class="heading1">Contributors</span><br />
Timothy Shaw, Zheng Ruan, Travis Glenn, Liang Liu. <br />
(Website designed by Albert Shaw <br />
For questions, contact lliu@uga.edu. <br />
Last Updated Date: Oct 31, 2012 <br />
<br />
<br />
<span class="heading1">Sponsored By</span><br />
<img src="/images/nsf.jpg" alt="some_text" width="60" height="60" ALIGN=CENTER>  <a href="http://iob.uga.edu"><img src="/images/iob.jpg" alt="some_text" width="200" height="60" ALIGN=CENTER></a> <a href="http://www.uga.edu"><img src="/images/statistics.jpg" alt="some_text" width="200" height="60" ALIGN=CENTER></a>
<br />
<br />
<br />

<span class="heading1">Disclaimer</span><br />
Please use a modern browser (ie Firefox, Safari, Chrome) with javascript enabled to have the best viewing capability of the result from the species tree analysis.  At this time, the regular web server cannot perform species tree analyses involving more than 200 gene trees or greather than 50 species. We are in the process of developing a cluster version of the server for handling larger queries, or you may download the software and run the analysis on your local computer.  If you need help with running large queries send an email to gatechatl@gmail.com
			</div>
		</div>
		
	</div>

</div>
</body>
<script type="text/javascript">
<!--
   #alert("Warning... Our server will be taken off line for a couple hours Today Jan 8th. ");
//-->
</script>

</html>
