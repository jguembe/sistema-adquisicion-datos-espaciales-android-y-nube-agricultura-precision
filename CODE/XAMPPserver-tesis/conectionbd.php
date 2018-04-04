<?php
  $dbhost="127.0.0.1";
  $dbuser="root";
  $dbpass="";
  $dbname="Meassurements_db";
  // Conexión:
  $db = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
  // Si falla la conexión:
  if (!$db) {
      echo "Error: " . mysqli_connect_error();
  	exit();
  }

?>
