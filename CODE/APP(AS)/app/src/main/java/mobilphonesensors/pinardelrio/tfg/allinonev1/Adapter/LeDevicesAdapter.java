package mobilphonesensors.pinardelrio.tfg.allinonev1.Adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import mobilphonesensors.pinardelrio.tfg.allinonev1.R;

public class LeDevicesAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mLeDevices= new ArrayList<BluetoothDevice>();
    private LayoutInflater mInflator;
    private int[] dbms = new int[40];
    private boolean existdbms= false;
    public LeDevicesAdapter(ArrayList<BluetoothDevice> mLeDevices){
        this.mLeDevices = mLeDevices;
    }
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }
    public void putdbms(int[] dbms){
        this.dbms=dbms;
        this.existdbms=true;
    }
    @Override
    public int getCount() {
        return this.mLeDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mLeDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = (BluetoothDevice) this.getItem(position);
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_info, parent, false);
        ((TextView)convertView.findViewById(R.id.name)).setText(device.getName());
        ((TextView)convertView.findViewById(R.id.address)).setText(device.getAddress());
        if (existdbms){
            ((TextView)convertView.findViewById(R.id.dbm)).setText(dbms[position]+" dBm");
        }

        return convertView;
    }


}
