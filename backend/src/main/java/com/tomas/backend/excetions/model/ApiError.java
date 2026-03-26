package com.tomas.backend.excetions.model;

import java.time.LocalDateTime;

public class ApiError {
    private final LocalDateTime timestamp;
    private final int status;
    private final String message;
    private final String path;

    public ApiError(LocalDateTime timestamp, int status, String message, String path){
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public int getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }



    public String getPath() {
        return path;
    }


}
