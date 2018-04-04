% Abre archivo
nombre='loma21-2';

f = fopen( nombre, 'r' );
tabla=textscan (f,'%f64 %f64 %f64 %f64 %f64');
fclose (f);
x=tabla{1};%asigno cada columna de salida a la variable correspondiente
y=tabla{2};% "
z=tabla{3}; 

minx=min(x);
maxx=max(x);
N=50;
miny=min(y);
maxy=max(y);
% vectores x e y
dx=linspace(minx,maxx,N);
dy=linspace(miny,maxy,N);
% mallado
[qx,qy]=meshgrid(dx,dy);
qz=griddata(x,y,z,qx,qy,'natural');
% graficar
surf(dx,dy,qz);
% estilo del gráfico y títulos
title ('Caso2: Superficie con pendiente');
xlabel('Latitud (º)');
ylabel('Longitud (º)');
zlabel('Altitud (m)');