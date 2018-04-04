% Abre archivo
nombre='humedad1';
f = fopen( nombre, 'r' );
% Extraer datos asignando cada columna
tabla=textscan (f,'%f64 %f64 %f64 %f64 %f64');
fclose (f);
x=tabla{1};
y=tabla{2};
z=tabla{3}; 
temp=tabla{4};
hum=tabla{5};

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
% dar forma al mallado
qz=griddata(x,y,z,qx,qy,'natural');
qv=griddata(x,y,hum,qx,qy,'natural');
colormap (flipud(winter));
% graficar
surf(dx,dy,qz,qv);
% estilo del gráfico y títulos
hcb=colorbar
title(hcb,'Humedad (%)');
title ('Caso3: Superficie con sol y sombra');
xlabel('Latitud (º)');
ylabel('Longitud (º)');