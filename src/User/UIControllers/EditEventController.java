package User.UIControllers;

import User.Admin;

public class EditEventController {
    String designacao;

    EditEventController(String s){designacao=s;}

    public void initialize(){

        Admin.GetInfoAboutEvent(designacao);






    }
}
