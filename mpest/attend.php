
<html>
<head>
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
		<a /><a href="index.php" id="first"  >Home</a> <a href="register.php">Registraton</a> <a href="attend.php"  style="color:#75C556;">Attendence</a>
	</div>
	<div class="content" style="height:700px;">
		<div class="mainpanel" >
			<div class="register" >
			<span class="heading1">Attendence</span>
			<br />
<?php

$name=$_POST["name"];
$email=$_POST["email"];
$email1=$_POST["email1"];
$phone=$_POST["phone"];

if($email!="" || $email1!="")
{
if($email!="" && $email1!="")
{
if($email==$email1)
{
if(filter_var($email,FILTER_VALIDATE_EMAIL))
{
$email=filter_var($email, FILTER_SANITIZE_MAGIC_QUOTES);

			$con = mysql_connect("mysql.chsprogram.com","ashaw596","school");
			
		
			mysql_select_db("chsprogram", $con) or die('Could not select database');
		
    //Select the database
	$query1="SELECT first_name, last_name FROM members	WHERE email = '$email'";
	
	$result = mysql_query($query1) or die('Query failed: ' . mysql_error());

	while($row = mysql_fetch_array($result))
		{
		if ($row['first_name']!="")
			{
			
			$timezone = new DateTimeZone( "EST" ); 
			$date = new DateTime(); 
			$date->setTimezone( $timezone ); 
			$date1=  $date->format( 'D M j' ); 
			$time= $date->format('g:i:s A');
			$query= "UPDATE members SET date='$date1',`time`='$time' WHERE email = '$email'";
			mysql_query($query) or die('Query failed: ' . mysql_error());
			$as=$row['first_name']." ".$row['last_name']; 
			echo "<br /><br />$as has signed in today";
			$asd=1234;
			}
		}
		
	if($asd!=1234)
		{
			echo "The email was not found";
			$a=true;
		}	
    


}
else
{
$a=true;
$b="The email was invalid";
}
}
else
{
$a=true;
$b="The emails did not match";
}

}
else
{
$a=true;
$b="You left a field blank.";
}
}
else
{
$a=true;
}

if( $a==true)
{
?>





			







<form action="attend.php" method="post" >
<br />
email:<br /> <input type="text" name="email" value=""/><br />
confirm email:<br /> <input type="text" name="email1" value=""/><br />
<input type="submit" />



</form>
<?php
}
if($b!="")
{
echo "<br />";
echo $b;
}
?>
</div>
		</div>
		
	</div>

</div>
</body>
</html>