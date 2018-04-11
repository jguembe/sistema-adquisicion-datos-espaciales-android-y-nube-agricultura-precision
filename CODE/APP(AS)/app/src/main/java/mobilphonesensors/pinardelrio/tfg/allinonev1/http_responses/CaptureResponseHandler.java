package mobilphonesensors.pinardelrio.tfg.allinonev1.http_responses;


import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import mobilphonesensors.pinardelrio.tfg.allinonev1.MainActivity;
import mobilphonesensors.pinardelrio.tfg.allinonev1.R;

public class CaptureResponseHandler extends AsyncHttpResponseHandler {
    private Context context;
    private Activity activity;
    private TextView tv;

    // Constructor para pasar variables del MainActivity para poder editar los textView
    public CaptureResponseHandler(Context context,Activity activity){
        this.context=context;
        this.activity=activity;
        this.tv = (TextView) activity.findViewById(R.id.serveranstv) ;

    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        // Acciones a realizar cuando  la respuesta es favorable:
        // Se recoge el mensaje enviado por el servidor:
        String msg_json = null;
        try {
            msg_json = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this.context, msg_json, Toast.LENGTH_LONG).show();
        updatetv(msg_json);
        Gson gson = new Gson();
        //ChatMessages chatMessages = gson.fromJson(msg_json,ChatMessages.class);
        //Chat.mensajes();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        // Acciones a realizar cuando  la respuesta NO es favorable:
        // Se recoge el mensaje enviado por el servidor:
        if(responseBody==null){
            //Toast.makeText(this.context,"Error al enviar",Toast.LENGTH_SHORT).show();
        }else{
            String msg=null;
            try {
                msg = new String(responseBody, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Toast.makeText(this.context,msg,Toast.LENGTH_SHORT).show();
        }
    }
    public void updatetv(String msg){
       String date =  new MainActivity().fechaHoraActual(System.currentTimeMillis());
       tv.setText("ServerResponse ("+date+"): "+msg);
    }
}
