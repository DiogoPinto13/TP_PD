package Shared;

public enum Messages {

    CLOSE{
        @Override
        public String toString(){return "Close";}
    },
    UNKNOWN_COMMAND {
        @Override
        public String toString(){return "Unknown command.";}
    };
    @Override
    public abstract String toString();
}
