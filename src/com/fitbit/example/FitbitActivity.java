package com.fitbit.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FitbitActivity {

    String dateStr = "";
    public static final String FITBIT_ACTIVITY = FitbitDriver.API_BASE_URL+"/1/user/-/activities/date/";
    public void writeUserActivityData(String authToken){
        String responseJson = "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        dateStr =dateFormat.format(date);


        try {
            HttpGet getActivity = new HttpGet(FITBIT_ACTIVITY +dateStr+".json");
            getActivity.addHeader("Authorization", "Bearer "+ authToken);
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(getActivity)) {
                responseJson = EntityUtils.toString(response.getEntity());
            }
            File userActivity = new File(dateStr+".json" );
            FileWriter fileWriter = new FileWriter(userActivity);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(responseJson);
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }



        System.out.println(responseJson);
    }
}
