package com.example.monthstrial;

public class UserInfo {

    String Username;
    String Password;
    String AuthToken;
    String LongAuthToken;

    public UserInfo(){

    }
    public UserInfo(String usernameIn, String passwordIn){
        Username = usernameIn;
        Password = passwordIn;
    }
}
