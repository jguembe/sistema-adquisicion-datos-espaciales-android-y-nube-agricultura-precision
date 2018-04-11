package mobilphonesensors.pinardelrio.tfg.allinonev1.Model;

public class AllDataGeoJson {
    // Crear subobjetos.
    private String type="Feature";
    private Propierties properties;
    private Geometry geometry;

    // Constructor.
    public AllDataGeoJson(){}

    public class Propierties{
        // Declaración de variables.
        private String time;
        private byte humidity;
        private float temperature;
        private double altitude;
        private String tag;
        // Constructor.
        public Propierties(String time, byte humidity, float temperature,double altitude,String tag){
            this.time = time;
            this.humidity = humidity;
            this.temperature = temperature;
            this.altitude = altitude;
            this.tag = tag;
        }
    }

    public class Geometry{
        // Declaración de variables.
        private String type="Point";
        private double[] coordinates=new double[3];
        // Constructor.
        private Geometry(double longitude,double latitude,double altitude){
            this.coordinates[0]=longitude;
            this.coordinates[1]=latitude;
            this.coordinates[2]=altitude;
        }
    }
    // Métodos para intoducir la información del objeto.
    public void setPropierties(String time, byte humidity, float temperature, double altitude, String tag){
        this.properties= new Propierties(time, humidity, temperature, altitude, tag);
    }
    public void setGeometry(double longitude,double latitude,double altitude){
        this.geometry=new Geometry(longitude,latitude,altitude);
    }
}
