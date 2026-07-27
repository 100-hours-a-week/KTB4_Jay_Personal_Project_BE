package kr.adapterz.springboot.global.exception;

public class ForbiddenException extends RuntimeException{

    public ForbiddenException(){
        super("not_author");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
