<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;
require_once 'vendor/autoload.php';
include('filtrar.php');
require ('conectionbd.php');


$app = new Slim\App;
//prueba de funcionamiento
$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    $response->getBody()->write("Hello, $name");
    $strFile = 'Hello.txt';
    if (!file_exists($strFile)) {
    	//chmod($strFile,0755);
    	$file=fopen($strFile, 'w');
   	//chown($file, 'www');
	fwrite($file, $name.PHP_EOL);
	fclose($file);
    }else{
	//chmod($strFile,0755);
    	$file=fopen($strFile, 'a');
	fwrite($file, $name.PHP_EOL);
	fclose($file);

    }
    return $response;
});


// API group
$app->group('/api', function () use ($app) {
    // Version group
 $app->group('/v1', function () use ($app) {
   // Peticiones REST a servir
 	$app->get('/meassurements', 'getMeassurements');
	$app->get('/meassurements/{id}', 'getMeassurement');
 	$app->post('/create', 'addMeassurement');
 	$app->put('/update/{id}', 'updateMeassurement');
 	$app->delete('/delete/{id}', 'deleteMeassurement');
 });
});

// Funciones que atienden las peticiones:
function getMeassurements(Request $request, Response $response) {
  $response->getBody()->write("measurementsjsonresponse");
  return $response;

}

function getMeassurement (Request $request, Response $response) {
    $mid = $request->getAttribute('id');
    $response->getBody()->write("$mid meassurement geo.json");
    return $response;
}

function addMeassurement(Request $request, Response $response) {
	$newm = json_decode($request->getBody());
	$jsonencode = json_encode($newm);
 	$response->getBody()->write("created! $jsonencode\n");
	return $response;

}

function updateMeassurement(Request $request, Response $response) {
  // Fichero necesario para la conexión
  include ('conectionbd.php');
  // Filtrar variable para evitar problemas de seguridad.
	$mid = filtrar($request->getAttribute('id'));

  // Comprobar que la longitud  de la variable de entrada es correcta y proceder:
  if (strlen($mid)<=25){
    $mjson = $request->getBody();
    //Extraemos las variables
    $marray = json_decode($mjson);
    $long = $marray->geometry->coordinates[0];
    $lat =  $marray->geometry->coordinates[1];
    $alt =  $marray->geometry->coordinates[2];
    $time = $marray->properties->time;
    $tag = $marray->properties->tag;
    $hum = $marray->properties->humidity;
    $temp = $marray->properties->temperature;

    $strFile = 'Meassurements/Meassurement_'.$mid.'.geojson';
    if (!file_exists($strFile)) {
        // Creamos el fichero
        $file=fopen($strFile, 'w');
        fwrite($file,'{"type":"FeatureCollection","features":['.PHP_EOL);
        fwrite($file, $mjson.','.PHP_EOL);
        fclose($file);
        // New Row in meassurement_files table (SQL code)
        $query = mysqli_query($db,"INSERT INTO `MEASSUREMENT_FILES` (`id`, `filename`) VALUES ('$mid', '$strFile')");
        if ($query) {
          // Creating the new meassurement table (SQL code).
          $query2 = mysqli_query($db,"CREATE TABLE `Meassurements_db`.`$mid`
            ( `time` VARCHAR(30) NULL , `tag` VARCHAR(30) ,
              `longitude` DOUBLE NOT NULL , `latitude` DOUBLE NOT NULL , `altitude` DOUBLE NOT NULL ,
               `humidity` INT(3) NOT NULL , `temperature` FLOAT(4) NOT NULL , UNIQUE (`time`)) ENGINE = InnoDB;") ;

          if ($query2){

            //Insertamos valores del JSON en tabla  (SQL code)
            $query3 = mysqli_query($db,"INSERT INTO `$mid` (`time`, `tag`, `longitude`, `latitude`, `altitude`, `humidity`, `temperature`)
            VALUES ('$time', '$tag', '$long', '$lat', '$alt', '$hum', '$temp');");
            if($query3){
              $newResponse = $response->getBody()->write(json_encode(array('estado'=>true,'mensaje'=>"Created new meassurement: $mid")));
              $newResponse = $response->withStatus(201);

            }else{
              $newResponse = $response->getBody()->write(json_encode(array('estado'=>false,'mensaje'=>"Cant insert data in db")));
              $newResponse = $response->withStatus(400);

            }


          }else{
            $newResponse = $response->getBody()->write(json_encode(array('estado'=>false,'mensaje'=>'Cant create new db table')));
            $newResponse = $response->withStatus(400);
          }

        } else {
          $newResponse = $response->getBody()->write(json_encode(array('estado'=>false,'mensaje'=>'Cant create row in db table')));
          $newResponse = $response->withStatus(400);

        }
      }else{
        // Insertar nueva medición en fichero
        $file=fopen($strFile, 'a');
        fwrite($file, $mjson.','.PHP_EOL);
        fclose($file);

        //Insertar valores del JSON en tabla
        $query3 = mysqli_query($db,"INSERT INTO `$mid` (`time`, `tag`, `longitude`, `latitude`, `altitude`, `humidity`, `temperature`)
        VALUES ('$time', '$tag', '$long', '$lat', '$alt', '$hum', '$temp');");
        if($query3){
          $newResponse = $response->getBody()->write(json_encode(array('estado'=>true,'mensaje'=>"Updated \"$mid\" meassurement")));
          $newResponse = $response->withStatus(200);

        }else{
          $newResponse = $response->getBody()->write(json_encode(array('estado'=>false,'mensaje'=>"Cant insert data in db")));
          $newResponse = $response->withStatus(400);

        }
    }
  }else{
    $newResponse= $response->getBody()->write (json_encode( array('estado' =>false ,'mensaje'=>'id too long! Only 25 char.' )));
    $newResponse = $response->withStatus(400);
  }
	return $newResponse;
}


function deleteMeassurement(Request $request, Response $response) {
	$mid = $request->getAttribute('id');
 	$response->getBody()->write("deleted! \n");
	return $response;
}

$app->run();



?>
