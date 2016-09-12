<html>
<head>
<title>Shannon Entropy-Two</title>
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
		<a>DNA Sequence Shannon Entropy Calculator</a><br /><br />
	</div>
	<div class="navbar" align="left">
		<a /><a href="index.php" id="first" >Home</a> <a href="Entropy.php">Entropy-One</a> <a href="Entropy1.php"   style="color:#75C556;">Entropy-Two</a><a href="About.php">About the Site</a><a href="About.php">How it Works</a>
	</div>
	<div class="content" style="height:87%;">
		<div class="mainpanel" >
			<div class="register" >


			<span class="heading1">Shannon Entropy-Two</span>
			<br />
			<br />
			<form name="input" action="dna/idk.php" method="post" enctype="multipart/form-data">
			Sequence 1: <textarea  rows="6" cols="50"  name="dna1">
			</textarea>
			<br />
			
 Fasta Sequence 1 File: <input type="file" name="file1" id="file1"/><br />


			Sequence 2: <textarea  rows="6" cols="50"  name="dna2">
			</textarea>
			</br>
 Fasta Sequence 2 File: <input name="file2" type="file" /><br />
			Iterations: <input type="text" name="iterations" />
			</br>
			<input type="submit" value="Submit" />
			</form>

			<br />


			</div>
		</div>
		
	</div>

</div>
</body>
</html>





