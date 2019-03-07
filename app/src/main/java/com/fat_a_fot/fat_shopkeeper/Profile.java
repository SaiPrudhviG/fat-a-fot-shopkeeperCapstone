package com.fat_a_fot.fat_shopkeeper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fat_a_fot.fat_shopkeeper.AppController.TAG;

public class Profile extends Fragment {
    EditText name,email,number,address,shop_details,password;
    Button updateprofile;
    private ProgressDialog pDialog;
    SQLLiteHandler db;
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        name = (EditText) getView().findViewById(R.id.name);
        email = (EditText) getView().findViewById(R.id.email);
        number = (EditText) getView().findViewById(R.id.number);
        address = (EditText) getView().findViewById(R.id.address);
        shop_details = (EditText) getView().findViewById(R.id.shop_detail);
        password = (EditText) getView().findViewById(R.id.password);
        updateprofile = (Button) getView().findViewById(R.id.updateprofile);
        db = new SQLLiteHandler(view.getContext());
        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile(view);
//                mainActivity.setproifiledata(view.getContext());
            }
        });
        updatefield(view);
    }
    public void updatefield(final View view){
        pDialog = new ProgressDialog(view.getContext());
        pDialog.setCancelable(false);
        String tag_string_req = "req_login";
        pDialog.setMessage("Feching Profile Details ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "PROFILE Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        JSONObject user = jObj.getJSONObject("shopkeeper");
                        name.setText(user.getString("name"));
                        email.setText(user.getString("email"));
                        number.setText(user.getString("mobile"));
                        address.setText(user.getString("address"));
                        shop_details.setText(user.getString("shop_details"));
                        password.setText(user.getString("password"));

                        String id = user.getString("id");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String mobile = user.getString("mobile");
                        String image = user.getString("image");

                        Common.saveUserData(view.getContext(), "email", email);
                        Common.saveUserData(view.getContext(), "userId", id+"");
                        Common.saveUserData(view.getContext(), "name", name);
                        Common.saveUserData(view.getContext(), "mobile", mobile);
                        Common.saveUserData(view.getContext(), "image", image);
                        hideDialog();
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(view.getContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(view.getContext(),error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shopkeeper_id", Common.getSavedUserData(view.getContext(),"userId"));
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_main, container, false);
    }


    public void updateprofile(final View view){
        pDialog = new ProgressDialog(view.getContext());
        pDialog.setCancelable(false);
        String tag_string_req = "req_login";
        pDialog.setMessage("Updating Profile Details ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATEPROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "PROFILE Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        hideDialog();
                        Toast.makeText(view.getContext(),"Profile Update Successful.", Toast.LENGTH_LONG).show();
                        updatefield(view);
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(view.getContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Shopkeeper update profile Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(view.getContext(),error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shopkeeper_id", Common.getSavedUserData(view.getContext(),"userId"));
                params.put("shopkeeper_name", name.getText().toString());
                params.put("shopkeeper_email", email.getText().toString());
                params.put("shopkeeper_mobile", number.getText().toString());
                params.put("shopkeeper_address", address.getText().toString());
                params.put("shopkeeper_shop_details", shop_details.getText().toString());
                params.put("shopkeeper_password", password.getText().toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
