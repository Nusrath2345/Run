package com.run.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.run.PasswordCheckerUtil;

@RestController
@RequestMapping("/api")
public class PasswordCheckerController 

{

   @PostMapping("/check")
    public Map<String, Object> checkPassword(@RequestBody Map<String, String> request) 
    {

        String password = request.get("password");

        String result = PasswordCheckerUtil.checkStrength(password);

        Map<String, Object> response = new HashMap<>();
        response.put("strength", result);

        return response; 
    }
}
