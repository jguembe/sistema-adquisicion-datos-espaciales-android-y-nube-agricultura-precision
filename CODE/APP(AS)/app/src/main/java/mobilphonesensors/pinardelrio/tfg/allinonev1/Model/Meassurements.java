package mobilphonesensors.pinardelrio.tfg.allinonev1.Model;

import java.util.ArrayList;
import java.util.List;

public class Meassurements {
    private List<Meassure> Meassurements;

    public List<Meassure> getMeassurements() {
        return Meassurements;
    }

    public void setMeassurements(ArrayList<Meassure> meassurements) {
        Meassurements = meassurements;
    }
    public int getSize(){
        return Meassurements.size();
    }

    public Meassure getMeassure(int position){return this.Meassurements.get(position);}
}
