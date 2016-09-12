<html>
<head>
<?php
error_reporting(0);
$firstname=ereg_replace("[^A-Za-z0-9]", "", $_POST["firstname"]);
$lastname=ereg_replace("[^A-Za-z0-9]", "", $_POST["lastname"]);
$email=$_POST["email"];
$email_confirm=$_POST["email_confirm"];

$exp=$_POST["exp"];
$exp_program=$_POST["exp_program"];
$language=ereg_replace("[^A-Za-z0-9]", "", $_POST["language"]);

$i_program=$_POST["i_program"];
$i_language=ereg_replace("[^A-Za-z0-9]", "", $_POST["i_language"]);

$leader=ereg_replace("[^A-Za-z0-9[:space:]]", "", $_POST["leader"]);

if($firstname==""||$lastname==""||$email==""||$email_confirm==""||$exp=="")
{
?>
<title>Programming Club</title>
<link href="site.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery.corner.js"></script>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript">
	$("#footer").corner("bottom");
</script>
</head>
<body>
<div class="container" style="height:950px;">
	<div class="top">
		<a>Programming Club</a><br />
	</div>
	<div class="navbar" align="left">
		<a /><a href="index.php" id="first" >Home</a> <a href="register.php" style="color:#75C556;">Registraton</a> <a href="attend.php">Attendence</a>
	</div>
	<div class="content" style="height:700px;">
		<div class="mainpanel" >
			<div class="register" >
			<span class="heading1">Registration</span>
			<br />
<form action="register.php" method="post">
<table>
<tr />
<tr />
<tr />
<tr />
<tr>
<td>First Name: </td>
<td><input type="text" name="firstname" /></td>
</tr>
<tr><td>Last Name: </td><td><input type="text" name="lastname" /></td></tr>
<tr><td>Email: </td><td><input type="text" name="email" /></td></tr>
<tr><td>Confirm Email: </td><td><input type="text" name="email_confirm" /></td></tr>
</table>
<br />
How would you rate your computer programming experience?<br />
<Table>
<tr><td><input type="radio" name="exp" value="1" /> 1 (No experience)</td><td width="2"/>
<td><input type="radio" name="exp" value="2" />2  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="3" />3  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="4" />4  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="5" />5  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="6" />6  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="7" />7  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="8" />8  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="9" />9  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="10" />10 (Programming God)</td></tr>
</table>
<br />
If you had had programming experience, which programming languages have you had experience with.<br />
<Table>
<tr><td><input type="checkbox" value="x" name="c" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="c++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="java" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="vb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="html" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="php" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="perl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="python" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="flash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="language" />
<br />
<br />

What languages are you interested in learning/using in this club?
<Table>
<tr><td><input type="checkbox" value="x" name="ic" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ic++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ijava" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ivb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ihtml" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iphp" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iperl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ipython" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iflash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="i_language" />
<br />
<br />
Are you interested in being a leader in this club?(vice-president, secretary, group leader)
<br />
<textarea name="leader" ROWS=4 COLS=50> </textarea>
<br /> <br />
<input type="submit">
</form>

	
<?php
if($firstname==""&&$lastname==""&&$email==""&&$email_confirm==""&&$exp=="")
{
}
else
{
	echo "You didn't fill in all the required forms.";	
}
}
else
{
	if($email!=$email_confirm)
	{
		?><title>Programming Club</title>
<link href="site.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery.corner.js"></script>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript">
	$("#footer").corner("bottom");
</script>
</head>
<body>
<div class="container" style="height:950px;">
	<div class="top">
		<a>Programming Club</a><br />
	</div>
	<div class="navbar" align="left">
		<a /><a href="index.php" id="first" >Home</a> <a href="register.php" style="color:#75C556;">Registraton</a> <a href="attend.php">Attendence</a>
	</div>
	<div class="content" style="height:700px;">
		<div class="mainpanel" >
			<div class="register" >
			<span class="heading1">Registration</span>
			<br />
<form action="register.php" method="post">
<table>
<tr />
<tr />
<tr />
<tr />
<tr>
<td>First Name: </td>
<td><input type="text" name="firstname" /></td>
</tr>
<tr><td>Last Name: </td><td><input type="text" name="lastname" /></td></tr>
<tr><td>Email: </td><td><input type="text" name="email" /></td></tr>
<tr><td>Confirm Email: </td><td><input type="text" name="email_confirm" /></td></tr>
</table>
<br />
How would you rate your computer programming experience?<br />
<Table>
<tr><td><input type="radio" name="exp" value="1" /> 1 (No experience)</td><td width="2"/>
<td><input type="radio" name="exp" value="2" />2  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="3" />3  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="4" />4  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="5" />5  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="6" />6  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="7" />7  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="8" />8  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="9" />9  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="10" />10 (Programming God)</td></tr>
</table>
<br />
If you had had programming experience, which programming languages have you had experience with.<br />
<Table>
<tr><td><input type="checkbox" value="x" name="c" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="c++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="java" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="vb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="html" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="php" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="perl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="python" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="flash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="language" />
<br />
<br />

What languages are you interested in learning/using in this club?
<Table>
<tr><td><input type="checkbox" value="x" name="ic" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ic++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ijava" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ivb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ihtml" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iphp" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iperl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ipython" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iflash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="i_language" />
<br />
<br />
Are you interested in being a leader in this club?(vice-president, secretary, group leader)
<br />
<textarea name="leader" ROWS=4 COLS=50> </textarea>
<br /> <br />
<input type="submit">
</form> Your emails did not match
<?php		
		echo $email;
	}
	else
	{
		if(!filter_var($email, FILTER_VALIDATE_EMAIL))
		{
		?>
		<title>Programming Club</title>
<link href="site.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery.corner.js"></script>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript">
	$("#footer").corner("bottom");
</script>
</head>
<body>
<div class="container" style="height:950px;">
	<div class="top">
		<a>Programming Club</a><br />
	</div>
	<div class="navbar" align="left">
		<a /><a href="index.php" id="first" >Home</a> <a href="register.php" style="color:#75C556;">Registraton</a> <a href="attend.php">Attendence</a>
	</div>
	<div class="content" style="height:700px;">
		<div class="mainpanel" >
			<div class="register" >
			<span class="heading1">Registration</span>
			<br />
<form action="register.php" method="post">
<table>
<tr />
<tr />
<tr />
<tr />
<tr>
<td>First Name: </td>
<td><input type="text" name="firstname" /></td>
</tr>
<tr><td>Last Name: </td><td><input type="text" name="lastname" /></td></tr>
<tr><td>Email: </td><td><input type="text" name="email" /></td></tr>
<tr><td>Confirm Email: </td><td><input type="text" name="email_confirm" /></td></tr>
</table>
<br />
How would you rate your computer programming experience?<br />
<Table>
<tr><td><input type="radio" name="exp" value="1" /> 1 (No experience)</td><td width="2"/>
<td><input type="radio" name="exp" value="2" />2  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="3" />3  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="4" />4  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="5" />5  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="6" />6  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="7" />7  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="8" />8  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="9" />9  </td></td><td width="2"/>
<td><input type="radio" name="exp" value="10" />10 (Programming God)</td></tr>
</table>
<br />
If you had had programming experience, which programming languages have you had experience with.<br />
<Table>
<tr><td><input type="checkbox" value="x" name="c" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="c++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="java" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="vb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="html" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="php" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="perl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="python" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="flash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="language" />
<br />
<br />

What languages are you interested in learning/using in this club?
<Table>
<tr><td><input type="checkbox" value="x" name="ic" />C<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ic++" />C++<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ijava" /> Java<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ivb" />Visual Basic<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ihtml" />Html<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iphp" />PHP<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iperl" />Perl<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="ipython" />Python<br /></td><td width="4"/>
<td><input type="checkbox" value="x" name="iflash" />Flash<br /></td><td width="4"/>
</tr>
</table>
Other: <input type="text" name="i_language" />
<br />
<br />
Are you interested in being a leader in this club?(vice-president, secretary, group leader)
<br />
<textarea name="leader" ROWS=4 COLS=50> </textarea>
<br /> <br />
<input type="submit">
</form> Your email was invalid
		<?php
		
		}
		else
		{
			$con = mysql_connect("mysql.chsprogram.com","ashaw596","school");
			if (!$con)
			{
				die('Could not connect: ' . mysql_error());
			}
		
			mysql_select_db("chsprogram", $con);
			$query="INSERT INTO members (first_name, last_name, email, experience, leader, c, `c++`, java, vb, html, php
			, perl, python, flash, other, ic, `ic++`, ijava, ivb, ihtml, iphp, iperl, ipython, iflash, iother) 
			Values ('$firstname', '$lastname', '$email', '$exp', '$leader', '".$_POST['c']."', '".$_POST['c++'].
			"', '".$_POST['java']."', '".$_POST['vb']."', '".$_POST['html']."', '".$_POST['php']."', '".
			$_POST['perl']."', '".$_POST['python']."', '".$_POST['flash']."', '".$_POST['language']."', '".$_POST['ic']
			."', '".$_POST['ic++']."', '".$_POST['ijava']."', '".$_POST['ivb']."', '".$_POST['ihtml']
			."', '".$_POST['iphp']."', '".$_POST['iperl']."', '".$_POST['ipython']."', '".$_POST['iflash']."', '".$_POST['i_language']."')";
			
			mysql_query($query) or die('Query failed: ' . mysql_error());
			echo "You have successfully register for Programming Club<br />";
			echo "Please check to make sure that your info is correct. <br />(I didn't output the experience stuff for you to check, but it was stored. I hope you were able to use check boxes correctly.)";
			echo "<br/>Name: $firstname $lastname <br />
				  Email: $email </br>";
			echo "Leader: $leader";
			
		}
	}
}

//first, last, email, experience, 9,lang,9,lang,leader
?>
</div>
		</div>
		
	</div>

</div>
</body>
</html>

