package mobilphonesensors.pinardelrio.tfg.allinonev1.BeewiSensor;

import mobilphonesensors.pinardelrio.tfg.allinonev1.Model.SensorData;


public class BytesConverter {
    private byte[] bytearray;

    private SensorData sensorData;
    private float temperature;
    private byte batterylevel,humidity;
    // Constructor para crear el objeto a partir de un byte array.
    public BytesConverter(byte[] bytearray){
        this.bytearray=bytearray;
    }
    // Este método convierte los bytes en información
    // Devuelve un objeto SensorData donde se almacena la info.
    public SensorData convertData(){
        if ((bytearray!= null) && (bytearray.length > 0)) {
            // Aseguramos que es el mensaje que contiene la información:
            if (bytearray[0]==2 && bytearray[1]==1 && bytearray[2]==6 && bytearray[3]==14 && bytearray[4]==-1
                    && bytearray[5]==13 && bytearray[6]==0 && bytearray[7]==5 && bytearray[8]==0){
                batterylevel = (byte) Math.max(0, Math.min(100, bytearray[17]));
                // Cálculo de temperatura.
                humidity = (byte) Math.max(0, Math.min(99, (int) bytearray[12]));
                if ((int) bytearray[9]>=0){
                    temperature = (int) bytearray[9] + (int) bytearray[10] * 256;
                }else{
                    temperature = (int) bytearray[9] + ((int) bytearray[10]+1) * 256;
                }
                temperature= temperature/10;
                // Introducimos la información en el objeto SensorData mediante su constructor.
                sensorData = new SensorData(batterylevel,humidity,temperature);
                return sensorData;
            }else{
                return null;
            }

        }else{
            return null;
        }

    }

}
