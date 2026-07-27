package kr.adapterz.springboot.global.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(){
        super("user_not_found");
    }
}
