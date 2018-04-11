package mobilphonesensors.pinardelrio.tfg.allinonev1;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.StringEntity;
import mobilphonesensors.pinardelrio.tfg.allinonev1.BeewiSensor.BytesConverter;
import mobilphonesensors.pinardelrio.tfg.allinonev1.BeewiSensor.LeDevicesList;
import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.AllDataGeoJson;
import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.SensorData;
import mobilphonesensors.pinardelrio.tfg.allinonev1.http_responses.CaptureResponseHandler;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter mBluetoothAdapter;
    // Stops scanning after 2,5 seconds.
    private long SCAN_PERIOD = 2500;
    private long STATUS_PERIOD = 2000;
    private Handler mHandler, statusHandler;
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    private int[] dbms = new int[40];
    public Intent Listaintent;
    private String sensordevice = null;
    private boolean takedata = false;
    private SensorData sensorData = null;
    private AllDataGeoJson allDataGeoJson = null;
    private boolean capturing = false;
    private String geojson = null;
    private long time;
    private TextView lastsensortv, lastdatatv, locationtv, serveranstv;
    private Button iniciarbut, detenerbut;
    private TextView gpstv, wifitv, bletv;
    private Button gpsbut, wifibut, blebut;
    private int gps = 0, wifi = 0, ble = 0;
    private LocationManager mlocationmanager;
    private String meassurementid = null;
    private String ipcloud;
    private String misbytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Generar variables con elementos del Layout:
        lastsensortv = (TextView) findViewById(R.id.lastsensor);
        lastdatatv = (TextView) findViewById(R.id.lastdata);
        locationtv = (TextView) findViewById(R.id.locationtv);
        serveranstv = (TextView) findViewById(R.id.serveranstv);
        iniciarbut = (Button) findViewById(R.id.iniciarbut);
        detenerbut = (Button) findViewById(R.id.detenerbut);
        wifibut = (Button) findViewById(R.id.wifibut);
        gpsbut = (Button) findViewById(R.id.gpsbut);
        blebut = (Button) findViewById(R.id.blebut);
        wifitv = (TextView) findViewById(R.id.wifitv);
        gpstv = (TextView) findViewById(R.id.gpstv);
        bletv = (TextView) findViewById(R.id.bletv);
        // Variables de estado:
        wifi = 0;
        gps = 0;
        ble = 0;

        //Comprobar que las variables de la app no tengan valores no validos y corregimos:
        // Sirve para la primera ejecución, tras la instalación:
        SharedPreferences pref = getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = pref.edit();
        SCAN_PERIOD = pref.getLong("speriod", 0);
        ipcloud = pref.getString("ipcloud", "");
        meassurementid = pref.getString("meassurementid", "");
        if (SCAN_PERIOD < 500) {
            SCAN_PERIOD = 2000;
            editor.putLong("speriod", SCAN_PERIOD).commit();
        }
        if (ipcloud.equals("")) {
            ipcloud = "10.42.0.1";
            editor.putString("ipcloud", ipcloud).commit();
        }
        if (meassurementid.equals("")) {
            meassurementid = "default_prueba";
            editor.putString("meassurementid", meassurementid).commit();
        }

        update_lasttv();
        mHandler = new Handler();
        // Inicializar locationmanager y pedir updates para ir afinando ubicación (solo recibirá si gps activado):
        mlocationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SCAN_PERIOD, 0, this);
        // Inicializar el Bluetooth adpter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Comprueba que el Bluetooth esta disponible y activo. De lo contrario,
        // muestra un dialog pidiendo permisos de usuario para activarlo.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Crear y ejecutar hilo que consultará el estado de wifi,bluetooth y gps.
        statusHandler = new Handler();
        mStatusChecker.run();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mLeDevices.clear();
        dbms = new int[40];
        statusHandler.removeCallbacks(mStatusChecker);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        statusHandler.removeCallbacks(mStatusChecker);
    }
    @Override
    protected void onResume() {
        super.onResume();
        update_lasttv();
        mStatusChecker.run();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listintent() {
        Listaintent = new Intent(this, LeDevicesList.class);
        Listaintent.putExtra("mLeDevices", mLeDevices);
        Listaintent.putExtra("dbms", dbms);
        startActivity(Listaintent);
        update_lasttv();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                showToast("Error! Bluetooth desactivado!");

                if (capturing){
                    detenerenvio();
                }
            }else{
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // showToast(String.valueOf("Finished"));
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        if (!takedata) {
                            listintent();
                        }
                    }
                }, SCAN_PERIOD);
                //showToast(String.valueOf("Scan started"));
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

        } else {
            // showToast(String.valueOf("Scan Stopped"));
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }
    // CallBack: Respuestas del escaneo BLE
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Si no estamos buscando la información
                            if (!takedata) {
                                boolean exist = false;
                                for (int i = 0; i < mLeDevices.size(); i++) {
                                    if (device.getAddress().equals(mLeDevices.get(i).getAddress())) {
                                        exist = true;
                                    }

                                }
                                if (!exist) {
                                    dbms[mLeDevices.size()] = rssi;
                                    mLeDevices.add(device);
                                    showToast(device.getName());
                                    Log.d("INFO:RSSIint", device.getName() + "    " + Integer.toString(rssi));
                                    Log.d("INFO:SCANRECORDbytes", openFileToString(scanRecord));

                                }
                            // Si queremos extraer la temperatura y la humedad
                            } else if (takedata) {
                                if (device.toString().equals(sensordevice)) {
                                    BytesConverter bytesConverter = new BytesConverter(scanRecord);
                                    sensorData = bytesConverter.convertData();
                                    misbytes=openFileToString(scanRecord);

                                    update_lasttv();
                                    //parar scaneo:
                                    scanLeDevice(false);
                                }
                            }

                        }
                    });
                }
            };
    // Acciónando boton search->scaneo sin obtencion de datos.
    public void search(View view) {
        SCAN_PERIOD = 4000;
        this.takedata = false;
        scanLeDevice(true);
    }
    // Acciónando boton getsensor data->scaneo CON obtencion de datos.
    public void getsensordata(View view) {
        getsensordata();
    }
    public void getsensordata() {
        update_lasttv();
        if (sensordevice != null) {
            SharedPreferences pref = getSharedPreferences("user",0);
            SCAN_PERIOD = pref.getLong("speriod",0);
            SCAN_PERIOD = (long) (Double.valueOf(SCAN_PERIOD)*0.95);
            this.takedata = true;
            scanLeDevice(true);
        } else {
            showToast("Sorry! First you must select sensor device please!");
        }
    }
    // Acciónando boton showdata->se muestra geojson si lo hay.
    public void showdata(View view) {
        updatecolors();
        if (geojson!=null) {
            showToast(geojson);
        } else {
            showToast("none data taken.");
        }
    }
    // For debug, bytes reading:
    public String openFileToString(byte[] bytes) {
        String file_string = "";
        for (int i = 0; i < bytes.length; i++) {
            int byteint = (int) bytes[i];
            file_string +=i+". | "+byteint+" \n";
        }
        return file_string;
    }
    // Función de actualización de textviews de estado.
    private void update_lasttv() {
        SharedPreferences pref = getSharedPreferences("user", 0);
        sensordevice = pref.getString("sensordevice", "");
        if (sensordevice != null) {
            lastsensortv.setText("LastSENSOR: " + sensordevice);
        }
        if (sensorData != null) {
            lastdatatv.setText("T:" + sensorData.getTemperature() + "ºC  " + "H:" +
                    sensorData.getHumidity() + "% batery: "+sensorData.getBatterylevel()+"%\n");
        }
    }
    // Acciónando boton iniciar captura->se pide actualizacion gps y se actualizan los datos.
    public void iniciarenvio(View view) {
        // Comprobar que BLE este activado
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            // Comprobar que se tenga sensor elegido.
            if (!sensordevice.equals("")) {
                // Comprobar que GPS este activado
                if (mlocationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showToast("Iniciando captura...");
                    capturing = true;
           /// aqui iba el codigo de REQUESTGPSUPDATES, lineas  de codigo 121-133
                    SharedPreferences pref = getSharedPreferences("user",0);
                    SCAN_PERIOD = pref.getLong("speriod",0);
                    capturing = true;
                    detenerbut.setVisibility(View.VISIBLE);
                    iniciarbut.setEnabled(false);
                } else {
                    showToast("Para comenzar la captura debes activar el GPS.");
                }
            } else {
                showToast("Antes debes selecionar un sensor.");
            }
        } else {
            showToast("Para comenzar la captura debes activar el Bluetooth.");
        }
    }
    // Acciónando boton detener->se frena el envio de datos al servidor
    public void detenerenvio(View view) {
        detenerenvio();
    }
    public void detenerenvio(){
      /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocationmanager.removeUpdates(this);
      */
        capturing=false;
        detenerbut.setVisibility(View.INVISIBLE);
        iniciarbut.setVisibility(View.VISIBLE);
        iniciarbut.setEnabled(true);
        showToast("La captura se detuvo.");
    }
    private void showGPSactivation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    public String fechaHoraActual(long time){
        return new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", java.util.Locale.getDefault()).format(time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (capturing){
            detenerenvio();
        }
        if (id == R.id.action_settings){
            Intent settingsintent = new Intent(this, Settings.class);
            startActivity(settingsintent);
        }
        if (id == R.id.action_meassurements){
            if (wifi==3){
                Intent meassurementsintent = new Intent(this, MeassurementsList.class);
                startActivity(meassurementsintent);
            }else{
                showToast("Debes estar conectado al servidor para ver la lista de capturas.");
            }

        }
        return super.onOptionsItemSelected(item);
    }
    // Hilo que atiende cambios de estado y updates de GPS
    @Override
    public void onLocationChanged(Location location) {
        //GPS data recolection
        double mlat,mlong,malt;
        mlat = location.getLatitude();
        mlong = location.getLongitude();
        malt = location.getAltitude();
        if (capturing){
            // Sensor data
            getsensordata();
            while (sensorData==null){
            }
            //data insert in GeoJson Object
            String tag = sensordevice;
            time = System.currentTimeMillis();
            allDataGeoJson = new AllDataGeoJson();
            allDataGeoJson.setGeometry(mlong,mlat,malt);
            allDataGeoJson.setPropierties(fechaHoraActual(time),sensorData.getHumidity(),(float)sensorData.getTemperature(),malt,tag);
            if (allDataGeoJson != null) {
                Gson gson = new Gson();
                geojson = gson.toJson(allDataGeoJson);
                // sending data...
                SharedPreferences pref = getSharedPreferences("user",0);
                ipcloud = pref.getString("ipcloud","");
                meassurementid = pref.getString("meassurementid","");
                // Crear el cliente
                AsyncHttpClient client = new AsyncHttpClient();
                try {
                    StringEntity strent = new StringEntity(geojson);
                    // Enviar solicitud HTTP PUT con json
                    client.put(this, "http://"+ipcloud+"/tesis/index.php/api/v1/update/"+meassurementid,
                            strent, "application/json",
                            new CaptureResponseHandler(this,this));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                showToast(time+": Error! GeoJson Vacio, no se envió.");
            }
        }
        if (sensorData!=null){
            locationtv.setText("lat: "+mlat+" lng: "+mlong+"\r\n alt: "+malt+" time: "+fechaHoraActual(time)+
                   "\r\n temp: "+sensorData.getTemperature()+" hum: "+sensorData.getHumidity()+" batery: "+sensorData.getBatterylevel());
        }else{
            locationtv.setText("lat: "+mlat+" lng: "+mlong+"\r\n alt: "+malt+" time: "+fechaHoraActual(time)+"\r\n");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        if(capturing){
            detenerenvio();
            showToast("Error. GPS desactivado. ");
        }
    }


    // FUNCIONES STATUSCHECK -> PANEL DE ESTADOS WIFI,GPS y BLE
    public Boolean isNetDisponible(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
    public Boolean isOnlineNet() {
        try {
            SharedPreferences pref = getSharedPreferences("user",0);
            ipcloud = pref.getString("ipcloud","");
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 "+ipcloud);
            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    private void setstatuscolor (Button but,TextView tv, int color, String text){
        tv.setText(text);
        if (color==1){
            but.setBackgroundColor(Color.RED);
        }else if (color==2){
            but.setBackgroundColor(Color.parseColor("#ffff8800"));
        }else if (color==3){
            but.setBackgroundColor(Color.GREEN);
        }else if (color==0){
            but.setBackgroundResource(android.R.drawable.btn_default);
        }
    }
    private void wifistate(){
        if (isNetDisponible(this)){
            if (isOnlineNet()){
                wifi=3;
            }else {
                wifi=2;
            }
        }else{
            wifi=1;
        }
    }
    private void gpsstate(){
        if (mlocationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            SharedPreferences pref = getSharedPreferences("user",0);
            SCAN_PERIOD = pref.getLong("speriod",0);
            long ahora = System.currentTimeMillis();
            if (time!=0){
                if (ahora-time < SCAN_PERIOD*1.8){
                    gps=3;
                }else{
                    gps=2;
                }
            }else{
                gps=2;
            }
        } else {
            gps=1;
        }
    }
    private void blestate(){
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            ble=1;
        }else{
            if (!sensordevice.equals("")) {
                ble=3;
            }else {
                ble = 2;
            }
        }
    }
    // Cambiar botones según estados
    private void updatecolors(){
        gpsstate();
        switch (gps){
            case 0:
                setstatuscolor(gpsbut,gpstv,gps,"Unknown state");
                break;
            case 1:
                setstatuscolor(gpsbut,gpstv,gps,"GPS turned off!");
                gpsbut.setClickable(true);
                break;
            case 2:
                setstatuscolor(gpsbut,gpstv,gps,"No Location");
                gpsbut.setClickable(false);
                break;
            case 3:
                setstatuscolor(gpsbut,gpstv,gps,"Receiving GPS location");
                gpsbut.setClickable(false);
                break;
        }
        blestate();
        switch (ble){
            case 0:
                setstatuscolor(blebut,bletv,ble,"Unknown state");
                break;
            case 1:
                setstatuscolor(blebut,bletv,ble,"Blueetooth turned off!");
                break;
            case 2:
                setstatuscolor(blebut,bletv,ble,"No Sensor connection");
                break;
            case 3:
                setstatuscolor(blebut,bletv,ble,"Correct sensor connection ");
                break;
        }
        wifistate();
        switch (wifi){
            case 0:
                setstatuscolor(wifibut,wifitv,wifi,"Unknown state");
                break;
            case 1:
                setstatuscolor(wifibut,wifitv,wifi,"WIFI turned off!");
                break;
            case 2:
                setstatuscolor(wifibut,wifitv,wifi,"No server connection");
                break;
            case 3:
                setstatuscolor(wifibut,wifitv,wifi,"Correct server connection ");
                break;
        }
    }
    public void gpsstate(View view) {
        showGPSactivation();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
               // updateStatus(); //this function can change value of mInterval.
            } finally {
                updatecolors();
                statusHandler.postDelayed(mStatusChecker, STATUS_PERIOD);
            }
        }
    };
}
