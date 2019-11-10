package com.fitbit.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;

public class FitbitLogin {


    public final static String AUTH_TOKEN_URL = FitbitDriver.API_BASE_URL + "/oauth2/token?";
    public void generateAuthToken(String code) {
        this.writeAuthTokenInFile(this.generateAuthTokenFromResponse(this.authPostRequest(false, code)));
    }

    public void refreshToken(){
        this.writeAuthTokenInFile(this.generateAuthTokenFromResponse(this.authPostRequest(true, null)));
    }

    public void writeAuthTokenInFile(AuthTokenInfo authTokenInfo){
        try {
            File file = new File("authToken.json");
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(file, authTokenInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getCurrentAuthToken(){
        return this.getAuthTokenObjectFromFile().getAccessToken();
    }


    public String getCurrentRefreshToken(){
        return this.getAuthTokenObjectFromFile().getRefreshToken();
    }


    public AuthTokenInfo getAuthTokenObjectFromFile(){
        AuthTokenInfo authTokenInfo = null;
        try {
            // Deserialize JSON file into Java object.
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("authToken.json");
            authTokenInfo = mapper.readValue(file, AuthTokenInfo.class);
            //System.out.println("AuthToken" + authTokenInfo.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authTokenInfo;
    }

    private String getBase64Encoding(){
        try{
            return  "Basic "+DatatypeConverter.printBase64Binary((FitbitDriver.CLIENT_ID + ":" + FitbitDriver.CLIENT_SECRET).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AuthTokenInfo generateAuthTokenFromResponse(String responseJson){
        AuthTokenInfo authTokenInfo = new AuthTokenInfo();
        JSONParser parser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) parser.parse(responseJson);
            String accessToken = (String) object.get("access_token");
            Long expiresIn = (Long) object.get("expires_in");
            String refreshToken = (String) object.get("refresh_token");
            String scope = (String) object.get("scope");
            String tokenType = (String) object.get("token_type");
            String userId = (String) object.get("user_id");
            authTokenInfo.setAccessToken(accessToken);
            authTokenInfo.setExpiresIn(expiresIn);
            authTokenInfo.setRefreshToken(refreshToken);
            authTokenInfo.setScope(scope);
            authTokenInfo.setTokenType(tokenType);
            authTokenInfo.setUserId(userId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return authTokenInfo;
    }

    private String authPostRequest(boolean refreshToken, String code){
        String responseJson = "";
        try {
            HttpPost post = new HttpPost(AUTH_TOKEN_URL);
            post.addHeader("Content-type", "application/x-www-form-urlencoded");
            post.addHeader("Authorization", getBase64Encoding());
            List<NameValuePair> urlParameters = new ArrayList<>();
            if(!refreshToken && code!=null) {
                urlParameters.add(new BasicNameValuePair("client_id", "22BD44"));
                urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
                urlParameters.add(new BasicNameValuePair("code", code));
                urlParameters.add(new BasicNameValuePair("redirect_uri", "https://www.sanketpatil.com/ab.php"));
            }else if(refreshToken && this.getCurrentRefreshToken()!=null && code==null){
                urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
                urlParameters.add(new BasicNameValuePair("refresh_token", this.getCurrentRefreshToken()));
            }
            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                responseJson = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseJson;
    }
}

