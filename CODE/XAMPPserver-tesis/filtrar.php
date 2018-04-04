<?php
// Elimina backslashes \caracteres especiales
function filtrar($variabletexto){
  $variabletexto=addslashes(htmlentities($variabletexto , ENT_QUOTES | ENT_IGNORE,'UTF-8'));
  $variabletexto=str_replace("\\r\\n",'<br>',$variabletexto);
  $variabletexto=nl2br(str_replace('\r\n','\n',$variabletexto));
  return $variabletexto;
}
?>
