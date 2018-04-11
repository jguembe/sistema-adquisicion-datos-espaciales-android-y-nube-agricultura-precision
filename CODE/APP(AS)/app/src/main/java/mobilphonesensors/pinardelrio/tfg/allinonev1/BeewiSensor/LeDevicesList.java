package mobilphonesensors.pinardelrio.tfg.allinonev1.BeewiSensor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mobilphonesensors.pinardelrio.tfg.allinonev1.Adapter.LeDevicesAdapter;
import mobilphonesensors.pinardelrio.tfg.allinonev1.R;

public class LeDevicesList extends AppCompatActivity {

    private ArrayList<BluetoothDevice> mLeDevices;
    private int[] dbms = new int[40];
    private BluetoothGatt mGatt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_devices_list);

        mLeDevices =(ArrayList<BluetoothDevice>) getIntent().getSerializableExtra("mLeDevices");
        dbms = getIntent().getIntArrayExtra("dbms");
        LeDevicesAdapter adapter = new LeDevicesAdapter(mLeDevices);
        adapter.putdbms(dbms);
        ListView listView=(ListView) findViewById(R.id.LeDeviecesListV);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        AdapterView.OnItemClickListener listener;
        listener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice mDevice = mLeDevices.get(position);
                //connectToDevice(mLeDevices.get(position)); // conect to de selected device.
                SharedPreferences pref = getSharedPreferences("user",0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("sensordevice",mDevice.toString()).commit();
                finish();
            }
        };
        listView.setOnItemClickListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    // Cerramos adecuadamente.
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
