package com.fitbit.example;

import java.util.*;

public class FitbitDriver {

    public final static String CLIENT_SECRET = "Insert your clinet secret";
    public final static String CLIENT_ID= "Insert Clinet ID";
    public final static String API_BASE_URL = "https://api.fitbit.com";


    public static String authToken = "";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please select:");
        System.out.println("1. First time connection:");
        System.out.println("2. Refresh Token");
        System.out.println("3. Get activity of currently logged in user");
        String option = sc.nextLine();

        FitbitLogin login = new FitbitLogin();
        if(option.equals("1")){
            System.out.println("Please enter the code obtained from OAuth");
            String code = sc.nextLine();
            login.generateAuthToken(code);
        } else if(option.equals("2")){
            login.refreshToken();
            System.out.println("Do you want to get user activity date [Y/N]");
            String userSelect = sc.nextLine();
            if(userSelect.equals("Y")){
                option="3";
            }else {
                option="NAN";
            }
        }
        authToken = login.getCurrentAuthToken();
        System.out.println(authToken);
        if(option.equals("3")){
            new FitbitActivity().writeUserActivityData(authToken);
        }
        sc.close();
    }
}

