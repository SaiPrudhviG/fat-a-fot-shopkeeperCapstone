package com.fat_a_fot.fat_shopkeeper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.fat_a_fot.fat_shopkeeper.AppController.TAG;

public class LoginActivity extends Activity {
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLLiteHandler db;
    //private AppController appController;
    private MyFirebaseInstanceIdService token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        token = new MyFirebaseInstanceIdService();
      //  appController = AppController.getInstance();
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());


        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {

                    checkLogin(email, password);
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void checkLogin(final String email, final String password) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        session.setLogin(true);
                        JSONObject user = jObj.getJSONObject("user");
                        String id = user.getString("id");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String mobile = user.getString("mobile");
                        String image = user.getString("image");
                        db.addUser(name, email, id, mobile);
                        Common.saveUserData(LoginActivity.this, "email", email);
                        Common.saveUserData(LoginActivity.this, "userId", id+"");
                        Common.saveUserData(LoginActivity.this, "name", name);
                        Common.saveUserData(LoginActivity.this, "mobile", mobile);
                        Common.saveUserData(LoginActivity.this, "image", image);
                        token = new MyFirebaseInstanceIdService();
                        token.onTokenReftesh(LoginActivity.this);
                        token.onTokenRefresh();
                        hideDialog();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("message");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.i("here",error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}