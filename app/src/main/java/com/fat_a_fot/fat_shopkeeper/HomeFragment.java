package com.fat_a_fot.fat_shopkeeper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.fat_a_fot.fat_shopkeeper.AppController.TAG;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private ProgressDialog pDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("New Orders");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        this.view = view;
        if (Detectconnection.checkInternetConnection(view.getContext())) {
            if (Common.getSavedUserData(view.getContext(), "email").equalsIgnoreCase("")) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(view.getContext(), " not Login.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(), "Check Internet Connection.", Toast.LENGTH_LONG).show();
            Intent noconnection = new Intent(view.getContext(), NoInternetConnectionActivity.class);
            startActivity(noconnection);
        }
        updateorderlist(view);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public void updateorderlist(final View view){
        pDialog = new ProgressDialog(view.getContext());
        pDialog.setCancelable(false);
        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching New Orders Details...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_NEWORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(jObj.getJSONArray("order"),view.getContext());
                        RecyclerView myView =  (RecyclerView)getView().findViewById(R.id.recyclerview);
                        myView.setHasFixedSize(true);
                        myView.setAdapter(adapter);
                        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        myView.setLayoutManager(llm);
                        hideDialog();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(view.getContext(),errorMsg, Toast.LENGTH_LONG).show();
                        hideDialog();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onRefresh() {
        updateorderlist(view);
    }

}
