package mobilphonesensors.pinardelrio.tfg.allinonev1.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.Meassure;
import mobilphonesensors.pinardelrio.tfg.allinonev1.R;

public class MeassuremetsListAdapter  extends BaseAdapter {
    private List lista;
    private Context context;
    public MeassuremetsListAdapter(List lista,Context context){
        this.lista = lista;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Meassure currentItem= (Meassure)this.getItem(position);
        View cell = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_meassurement,parent,false);
        TextView datetv = (TextView) cell.findViewById(R.id.date);
        TextView idtv = (TextView) cell.findViewById(R.id.id);
        datetv.setText(currentItem.getTimestamp());
        idtv.setText(currentItem.getId());
        return cell;
    }


}
