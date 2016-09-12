<html>
<head>
<title>Species Tree</title>
<link href="site.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery.corner.js"></script>
<script type="text/javascript" src="jquery.js"></script>
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
		<a /><a href="../index.php" id="first"  style="color:#75C556;" >Home</a> <a href="mpest.php">MPEST</a> 
	</div>
	<div class="content" style="height:87%;">
		<div class="mainpanel" >
			<div class="register" >


			<span class="heading1">Title: MP-EST. </span>
			<br />
			<br />
                                                                     
                                                                     
                                                                     
                                             
**************************************************************
<br />
 Maximum Pseudo-likelihood Estimate of the Species Tree (MP-EST)
<br />
 Author: Timothy Shaw, Zheng Ruan, Travis Glenn, Liang Liu
<br />
 Last Updated Date: Oct 24, 2012                  
<br />
**************************************************************
The species tree reconstruction webserver consists of a series of phylogenetic tools for
estimating species trees from phylogenomic data: 1) rooting gene trees with outgroup
species, 2) estimating species trees from the rooted gene trees using STAR [1] , MP-EST
[2], NJst methods [3], 3) bootstrap analyses for the species tree reconstruction methods
(STAR, MP-EST, and NJst). The input data are either rooted or unrooted gene trees. If
the gene trees are unrooted, they will be converted to rooted trees using the outgroup
species. The output is the estimate of the species tree. If the user chooses the bootstrap
analysis, the bootstrap support values will appear on the estimate of the species tree.

The species tree reconstruction methods are fundamental tools for the studies in
evolutionary biology, conservation biology, epidemiology, and cancer genetics etc.
The past few years have witnessed a fast expansion of species tree reconstruction
methods, but installation and compilation of the programs for these methods become a
real challenge for biologists and other users who attempt to use these tools to analyze
their data. Moreover, the methods often require specific format of the input data, which
may not be compatible with the data set in hand. This novel webserver for species tree
reconstruction provides a user-friendly web interface for species tree analyses, where
the users do not need to worry about installation, compilation, or the required format of
the input data. The webserver also contains links to the source code of the species tree
reconstruction methods, and the example files for implementing these tree reconstruction
methods.
<br />

<br />


			</div>
		</div>
		
	</div>

</div>
</body>
</html>
