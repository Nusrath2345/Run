package com.run.services;

import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;


@Service 
public class BreachCheckerService 
{

    private static final String API_KEY = "YOUR_API_KEY";

    public String checkEmail(String email) 
    {
        try 
        {
            URL url = new URL("https://haveibeenpwned.com/api/v3/breachedaccount/" + email);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("hibp-api-key", API_KEY);
            conn.setRequestProperty("user-agent", "JavaApp");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                return "BREACHED";
            } else if (responseCode == 404) {
                return "CLEAR";
            } else {
                return "ERROR";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}