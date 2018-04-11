package mobilphonesensors.pinardelrio.tfg.allinonev1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import mobilphonesensors.pinardelrio.tfg.allinonev1.http_responses.MeassurementsResponseHandler;

public class MeassurementsList extends AppCompatActivity {
    private String ipcloud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meassurements_list);

        getMeassurements();

    }
    private void getMeassurements(){
        SharedPreferences pref = getSharedPreferences("user",0);
        ipcloud = pref.getString("ipcloud","");
        // Crear el cliente
        AsyncHttpClient client = new AsyncHttpClient();
        // Enviar solicitud HTTP GET
        client.get(this, "http://"+ipcloud+"/tesis/index.php/api/v1/meassurements",
                new MeassurementsResponseHandler(this,this));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meassurements, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent settingsintent = new Intent(this, Settings.class);
            startActivity(settingsintent);
        }
        if (id == R.id.action_capture){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onResume(){
        super.onResume();
        getMeassurements();
    }



}
