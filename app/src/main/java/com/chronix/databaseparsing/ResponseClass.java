package com.chronix.databaseparsing;

/**
 * Created by chronix on 2/23/16.
 */
public class ResponseClass {

    /**
     * success : 1
     * message : Email is already registered.
     */

    private int success;
    private String message;

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
