package User.UIControllers;

public class Eventos {
    public int id;
    public String designcao;
    public String local;
    public String horaInicio;
    public String horaFim;

    public void setID(int id) {
        this.id=id;
    }

    public void setDesignacao(String designacao) {
        this.designcao=designacao;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio=horaInicio;
    }

    public void setHoraFim(String h) {
        horaFim=h;
    }

    public void setLocal(String place){
        this.local=place;
    }
}
