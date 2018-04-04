% Abre archivo
nombre='loma21-2';

f = fopen( nombre, 'r' );
%fread(f);
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
dx=linspace(minx,maxx,N);
dy=linspace(miny,maxy,N);
[qx,qy]=meshgrid(dx,dy);
qz=griddata(x,y,z,qx,qy,'natural');
surf(dx,dy,qz);
title ('Caso2: Superficie con pendiente');
xlabel('Latitud (º)');
ylabel('Longitud (º)');
zlabel('Altitud (m)');