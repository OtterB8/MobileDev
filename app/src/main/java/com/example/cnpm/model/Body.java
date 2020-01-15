package com.example.cnpm.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Body {
    @SerializedName("Accesstokens")
    @Expose
    private String tokens;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("createat")
    @Expose
    private String createat;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    public Body(String tokens, String username, String birthday, String createat, String phoneNumber) {
        this.tokens = tokens;
        this.username = username;
        this.birthday = birthday;
        this.createat = createat;
        this.phoneNumber = phoneNumber;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreateat() {
        return createat;
    }

    public void setCreateat(String createat) {
        this.createat = createat;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}