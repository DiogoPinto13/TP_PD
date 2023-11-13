package Shared;

public enum Messages {

    REQUEST_EDIT_PROFILE{
        @Override
        public String toString(){return "Request edit profile";}
    },
    EDIT_PROFILE_SUCCESS{
        @Override
        public String toString(){return "Successfully changed!";}
    },
    EDIT_PROFILE_ERROR{
        @Override
        public String toString(){return "an error occurred";}
    },
    PRESENCE_CODE_REGISTED{
        @Override
        public String toString(){return "the presence code was registed";}
    },
    INVALID_PRESENCE_CODE{
      @Override
      public String toString(){return "the presence code is not valid!";}
    },
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
