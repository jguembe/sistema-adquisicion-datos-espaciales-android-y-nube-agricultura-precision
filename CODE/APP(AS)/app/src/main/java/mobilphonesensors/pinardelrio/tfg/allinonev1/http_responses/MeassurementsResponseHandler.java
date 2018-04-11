package mobilphonesensors.pinardelrio.tfg.allinonev1.http_responses;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.UnsupportedEncodingException;
import java.util.List;
import cz.msebera.android.httpclient.Header;
import mobilphonesensors.pinardelrio.tfg.allinonev1.Adapter.MeassuremetsListAdapter;
import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.Meassure;
import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.Meassurements;
import mobilphonesensors.pinardelrio.tfg.allinonev1.R;

public class MeassurementsResponseHandler extends AsyncHttpResponseHandler {
    private Context context=null;
    private Activity activity;
    public MeassurementsResponseHandler(Context context, Activity activity){
        this.context=context;
        this.activity=activity;
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String msg_json = null;
        try {
            msg_json = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Meassurements meassurements=gson.fromJson(msg_json,Meassurements.class);
        final List<Meassure> meassures=meassurements.getMeassurements();
        MeassuremetsListAdapter adapter = new MeassuremetsListAdapter(meassures,this.context);
        ListView listview=(ListView) activity.findViewById(R.id.MeassurementslistV);
        listview.setAdapter(adapter);
        AdapterView.OnItemClickListener listener;
        listener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(context,position,Toast.LENGTH_LONG).show();

            }

        };
        listview.setOnItemClickListener(listener);
        Toast.makeText(this.context,"Lista actualizada",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if(responseBody==null){
            Toast.makeText(this.context,"Error al descargar la lista",Toast.LENGTH_LONG).show();
        }else{
            String msg=null;
            try {
                msg = new String(responseBody, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.context,msg,Toast.LENGTH_LONG).show();
        }
    }
}
