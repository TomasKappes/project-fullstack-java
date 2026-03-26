package com.tomas.backend.excetions.custom;

import org.springframework.http.HttpStatus;

public abstract class ApiExepcion extends RuntimeException{

    private final HttpStatus status;

    public ApiExepcion(String message,HttpStatus status){
        super(message);
        this.status=status;
    }


    public HttpStatus getStatus(){
        return status;
    }
}
