package com.leshadow.mapme;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Register request for ServerRegisterActivity
 * Created by OEM on 6/24/2017.
 */
public class ServerRegisterRequest extends StringRequest{
    //private static final String REGISTER_REQUEST_URL = "http://192.168.2.102:801/mapme2/Register.php";
    private static final String REGISTER_REQUEST_URL = "https://leshadow.com/Register.php";
    private Map<String, String> params;

    public ServerRegisterRequest(String name, String username, int age, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("age", age + "");
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
