package mobilphonesensors.pinardelrio.tfg.allinonev1.Model;


public class SensorData {
    // Declaración de variables.
    private byte batterylevel;
    private byte humidity;
    private float temperature;

    // Constructor del objeto.
    public SensorData(byte batterylevel, byte humidity, float temperature) {
        this.batterylevel = batterylevel;
        this.humidity = humidity;
        this.temperature = temperature;
    }

    // Geters y Setters para extraer e introducir los datos de las variables.
    public byte getBatterylevel() {
        return batterylevel;
    }

    public void setBatterylevel(byte batterylevel) {
        this.batterylevel = batterylevel;
    }

    public byte getHumidity() {
        return humidity;
    }

    public void setHumidity(byte humidity) {
        this.humidity = humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

}
