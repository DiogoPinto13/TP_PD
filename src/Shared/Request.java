package Shared;

public class Request {
    Messages typeMessage;
    String Message;

    public Request(Messages typeMessage, String message) {
        this.typeMessage = typeMessage;
        Message = message;
    }

    public Messages getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(Messages typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
