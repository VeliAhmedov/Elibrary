package az.project.springbootproject.exception;

import java.net.Inet4Address;

public class LibraryException extends RuntimeException {

    private Integer code;
    public LibraryException(){
        super();
    }
    public LibraryException(Integer code, String message){
        super(message);
        this.code = code;
    }
    public Integer getCode(){
        return code;
    }
}
