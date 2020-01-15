package com.example.cnpm.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ResultLogin {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("body")
    @Expose
    private Body body;
    public ResultLogin(String message, Body body) {
        this.message = message;
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
