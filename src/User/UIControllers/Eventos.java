package User.UIControllers;

public class Eventos {
    int id;
    String designcao, local, horaInicio, horaFim;

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

    public void setPlace(String place){
        this.local=place;
    }
}
