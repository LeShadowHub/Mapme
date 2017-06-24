package com.leshadow.mapme;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OEM on 6/24/2017.
 */

public class ServerLoginRequest extends StringRequest{
    //private static final String LOGIN_REQUEST_URL = "http://192.168.2.102:801/mapme2/Login.php";
    private static final String LOGIN_REQUEST_URL = "https://leshadow.com/Login.php";
    private Map<String, String> params;

    public ServerLoginRequest(String username, String password, Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
