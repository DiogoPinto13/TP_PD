package Shared;

public enum ErrorMessages {

    INVALID_PASSWORD{
        @Override
        public String toString() {return "The password is not valid";}
    },
    USERNAME_ALREADY_EXISTS{
      @Override
      public String toString(){return "This username already exists";}
    },
    SQL_ERROR{
        @Override
        public String toString(){return "An error occurred with the Database";}
    };
    @Override
    public abstract String toString();
}
