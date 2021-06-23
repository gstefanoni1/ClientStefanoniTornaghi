import java.io.Serializable;

public class Data implements Serializable {
    private String valore;

    public Data(){}
    public Data(String valore){ this.valore = valore;}

    public void setValore(String v){ valore = v;}
    public String getValore(){return valore; }
}