package com.example.prm_shoppingproject.Action;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm_shoppingproject.Interface.TypeProduct.TypeProductListCallBack;
import com.example.prm_shoppingproject.Model.TypeProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryAction {
    private final Context context;
    private static final String BASE_URL = "http://10.0.2.2:5265/api";

    public CategoryAction(Context context) {
        this.context = context;
    }


    public void getAllTypeProducts(final TypeProductListCallBack callBack) {
        String url = BASE_URL + "/category/all";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<TypeProduct> products = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productJson = jsonArray.getJSONObject(i);
                                int productID = productJson.getInt("categoryID");
                                String name = productJson.getString("name");

                                TypeProduct product = new TypeProduct(productID, name);
                                products.add(product);
                            }
                            callBack.onSuccess(products);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callBack.onError("JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callBack.onError("JSON parsing error: " + error.getMessage());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
