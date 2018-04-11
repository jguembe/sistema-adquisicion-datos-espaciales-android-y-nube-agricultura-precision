package mobilphonesensors.pinardelrio.tfg.allinonev1;

import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    private String sensordevice=null;
    private String ipcloud=null;
    private String meassurementid=null;
    private long scanperiod=0;
    private EditText editip;
    private EditText editid;
    private EditText editsperiod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editip = (EditText) findViewById(R.id.editIP);
        editid = (EditText) findViewById(R.id.editID);
        editsperiod = (EditText) findViewById(R.id.editsperiod);
        actualizartext();

    }

    public void editID(View view) {
        EditText editid = (EditText) findViewById(R.id.editID);
        SharedPreferences pref = getSharedPreferences("user",0);
        SharedPreferences.Editor editor = pref.edit();
        String newid = String.valueOf(editid.getText());
        if (!newid.equals("")){
            if (newid.length()<=25){
                editor.putString("meassurementid", newid).commit();
                actualizartext();
                finish();
                showToast("Bien! la Id ahora es: "+newid);
            }else{
                showToast("ERROR! Longitud máxima 20 caracteres.");
            }
        }else{

            showToast("ERROR! El campo id no puede estar vacio");
        }
    }

    public void editIP(View view) {
        EditText editip = (EditText) findViewById(R.id.editIP);
        SharedPreferences pref = getSharedPreferences("user",0);
        SharedPreferences.Editor editor = pref.edit();
        String newip = String.valueOf(editip.getText());
        if (!newip.equals("")){
            if (newip.equals("windows")){
                newip="192.168.201.1";
            }else if(newip.equals("ubuntu")){
                newip="10.42.0.1";
            }
            editor.putString("ipcloud", newip).commit();
            actualizartext();
            finish();
            showToast("Bien! la IP ahora es: "+newip);

        }else{
            showToast("ERROR! El campo IP no puede estar vacio");
        }
    }
    public void editsperiod(View view) {
        EditText editsperiod = (EditText) findViewById(R.id.editsperiod);
        SharedPreferences pref = getSharedPreferences("user",0);
        SharedPreferences.Editor editor = pref.edit();
        long newsperiod=0;
        try {
            newsperiod = Long.valueOf(String.valueOf(editsperiod.getText()));
        }catch (NumberFormatException e){
            showToast(String.valueOf(e));
        }
        if (newsperiod!=0){
            if (newsperiod>500){
                editor.putLong("speriod", newsperiod).commit();
                actualizartext();
                finish();
                showToast("Bien! El periodo de muestreo ahora es: "+newsperiod);
            }else{
                showToast("ERROR! El periodo debe ser >500ms");
            }


        }else{
            showToast("ERROR! El campo no puede estar vacio");
        }
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void actualizartext(){
        SharedPreferences pref = getSharedPreferences("user",0);
        ipcloud = pref.getString("ipcloud","");
        meassurementid = pref.getString("meassurementid","");
        scanperiod = pref.getLong("speriod",0);

      /*  if (ipcloud.equals("windows")){
            ipcloud="192.168.201.1";
        }else if(ipcloud.equals("ubuntu") || ipcloud.equals("")){
            ipcloud="10.42.0.1";
        }
        if (meassurementid.equals("")){
            meassurementid="prueba";
        }
        if (scanperiod==0){
            scanperiod=4000;
        }*/
        editid.clearComposingText();
        editip.clearComposingText();
        editsperiod.clearComposingText();
        editid.setHint(meassurementid);
        editip.setHint(ipcloud);
        editsperiod.setHint(String.valueOf(scanperiod));
    }


}
