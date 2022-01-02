package com.example.devhubxgd.Data;

public class GlobalData {
    public static String userToken;
    public static String Location;

    public static String getUserToken(){
        return userToken;
    }
    public static void setUserToken(String token){
        userToken = token;
    }


    public static String getLocation(){
        return Location;
    }
    public static void setLocation(String location){
        Location = location;
    }
}
