package com.leshadow.mapme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.Map;

public class LocalServerLoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogReg;
    private RequestQueue requestQueue;
    private static final String URL = "http://192.168.2.102:801/mapme1/DB_user_control.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_server_login);

        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        btnLogReg = (Button) findViewById(R.id.btnLogReg);

        requestQueue = Volley.newRequestQueue(this);
        btnLogReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Log.d("Button Login", "Button Clicked");
                                Toast.makeText(LocalServerLoginActivity.this, "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LocalServerLoginActivity.this, LocalServerUserAreaActivity.class);
                                startActivity(intent);
                            } else{
                                Toast.makeText(LocalServerLoginActivity.this, "ERROR " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError{
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("email", email.getText().toString());
                        hashMap.put("password", password.getText().toString());

                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }
}
