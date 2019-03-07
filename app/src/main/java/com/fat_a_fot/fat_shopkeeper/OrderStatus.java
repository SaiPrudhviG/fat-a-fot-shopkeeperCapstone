package com.fat_a_fot.fat_shopkeeper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {
    private Context context;
    private ProgressDialog pDialog;

    public OrderStatus(Context context){
        this.context = context;
    }

    public void updateOrderStatus(final String order_id){
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        String tag_string_req = "req_login";
        pDialog.setMessage("Please Wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ORDER_READY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        Toast.makeText(context,jObj.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        hideDialog();
                    } else {
                        Toast.makeText(context,jObj.getString("message"), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                } catch (JSONException e) {
                    hideDialog();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shopkeeper_id", Common.getSavedUserData(context,"userId"));
                params.put("order_id", order_id);
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
