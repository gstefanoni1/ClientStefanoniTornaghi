import java.io.Serializable;

public class Risorsa implements Serializable {
    private String nome;
    private Data data;
    private String timestamp;

    public Risorsa(){}
    public Risorsa(String nome, Data data, String timestamp){
        this.nome = nome;
        this.data = data;
        this.timestamp = timestamp;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return nome;
    }
}