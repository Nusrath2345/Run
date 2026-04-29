package com.run.services;

import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URI;


@Service 
public class BreachCheckerService 
{

    private static final String API_KEY = "51e0dbff3c8b48a4a981adbe10dad0bc";

    public String checkEmail(String email) {
        try {
            String encodedEmail = java.net.URLEncoder.encode(email, "UTF-8");
            URI uri = URI.create("https://haveibeenpwned.com/api/v3/breachedaccount/" + encodedEmail);

            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("hibp-api-key", API_KEY);
            conn.setRequestProperty("user-agent", "RunApp");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            switch (responseCode) {
                case 200: return "BREACHED";
                case 404: return "CLEAR";
                case 401: return "INVALID_KEY";
                case 403: return "FORBIDDEN";
                case 429: return "RATE_LIMITED";
                default: return "ERROR_" + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "EXCEPTION";
        }
}
}