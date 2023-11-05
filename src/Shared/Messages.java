package Shared;

public enum Messages {

    REGISTER_PRESENCE_CODE{
        @Override
        public String toString(){return "Register presence code option";};
    },
    EDIT_PROFILE{
        @Override
        public String toString(){return "edit profile option";}
    },
    GET_PRESENCES{
        @Override
        public String toString(){return "get presences option";}
    },
    GET_CSV_PRESENCES{
        @Override
        public String toString(){return "get csv with presences option";}
    },
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
