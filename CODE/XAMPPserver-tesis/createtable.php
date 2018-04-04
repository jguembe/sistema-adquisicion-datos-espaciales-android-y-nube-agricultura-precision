<?php
include('filtrar.php');
include ('conectionbd.php');
echo "holita";
function creartabla($idtabla){
  mysql_query("CREATE TABLE `Meassurements_db`.`tablename`
    ( `time` VARCHAR(30) NULL , `tag` VARCHAR(30) ,
      `longitude` DOUBLE NOT NULL , `latitude` DOUBLE NOT NULL , `altitude` DOUBLE NOT NULL ,
       `humidity` INT(3) NOT NULL , `temperature` FLOAT(4) NOT NULL, UNIQUE (`time`)) ENGINE = InnoDB;") ;

  return $variabletexto;
}
?>
// simple query
INSERT INTO `tablename3` (`time`, `tag`, `longitude`, `latitude`, `altitude`, `humidity`, `temperature`)
VALUES ('10:11:59_10-01-2018', 'D0:5F:B8:53:6B:13', '-83.6823144823658', '22.413397828176667', '17.763486551095124', '86', '24.9');
