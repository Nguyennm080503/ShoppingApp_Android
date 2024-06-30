package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Product.ProductCallBack;
import com.example.prm_shoppingproject.Interface.Product.ProductListCallBack;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Model.ProductUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAction {
    private DatabaseHelper openHelper;
    private SQLiteDatabase database;
    private static ProductAction instance;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:5265/api";

    public ProductAction(Context context) {
        this.openHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public static ProductAction getInstance(Context context) {
        if (instance == null) {
            instance = new ProductAction(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void addProduct(String name, double price, byte[] image, String description, int typeID, final MessageCallback callback) {
        String url = BASE_URL + "/product/create";

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("name", name);
            jsonRequest.put("price", price);
            jsonRequest.put("image", Base64.encodeToString(image, Base64.DEFAULT));
            jsonRequest.put("description", description);
            jsonRequest.put("categoryID", typeID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("statusCode") && response.getInt("statusCode") == 400) {
                                String message = response.getString("message");
                                callback.onError(message);
                            } else {
                                callback.onSuccess("Create new product successfully!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    public void GetProductByID(int productID, final ProductCallBack callBack) {
        String url = BASE_URL + "/product/detail/" + productID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int productID = response.getInt("productID");
                            String name = response.getString("name");
                            double price = response.getDouble("price");
                            String description = response.getString("description");
                            String base64Image = response.getString("image");
                            byte[] image = Base64.decode(base64Image, Base64.DEFAULT);
                            String categoryName = response.getString("category");
                            int status = response.getInt("status");

                            Product product = new Product(productID, name, price, image, description, categoryName, status);
                            callBack.onSuccess(product);
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
                        callBack.onError("Volley error: " + error.getMessage());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public void getAllProductsActive(final ProductListCallBack callBack) {
        String url = BASE_URL + "/product/all/active";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Product> products = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productJson = jsonArray.getJSONObject(i);
                                int productID = productJson.getInt("productID");
                                String name = productJson.getString("name");
                                double price = productJson.getDouble("price");
                                String description = productJson.getString("description");
                                String base64Image = productJson.getString("image");
                                byte[] image = Base64.decode(base64Image, Base64.DEFAULT);
                                String categoryName = productJson.getString("category");
                                int status = productJson.getInt("status");

                                Product product = new Product(productID, name, price, image, description, categoryName, status);
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

    public void getAllProducts(final ProductListCallBack callBack) {
        String url = BASE_URL + "/product/all";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Product> products = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productJson = jsonArray.getJSONObject(i);
                                int productID = productJson.getInt("productID");
                                String name = productJson.getString("name");
                                double price = productJson.getDouble("price");
                                String description = productJson.getString("description");
                                String base64Image = productJson.getString("image");
                                byte[] image = Base64.decode(base64Image, Base64.DEFAULT);
                                String categoryName = productJson.getString("category");
                                int status = productJson.getInt("status");

                                Product product = new Product(productID, name, price, image, description, categoryName, status);
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

    public void getAllProductsByCategory(int typeID, final ProductListCallBack callBack) {
        String url = BASE_URL + "/product/all/cate/" + typeID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Product> products = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productJson = jsonArray.getJSONObject(i);
                                int productID = productJson.getInt("productID");
                                String name = productJson.getString("name");
                                double price = productJson.getDouble("price");
                                String description = productJson.getString("description");
                                String base64Image = productJson.getString("image");
                                byte[] image = Base64.decode(base64Image, Base64.DEFAULT);
                                String categoryName = productJson.getString("category");
                                int status = productJson.getInt("status");

                                Product product = new Product(productID, name, price, image, description, categoryName, status);
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

    public void UpdateProduct(ProductUpdate product, final MessageCallback callback) {
        String url = BASE_URL + "/update" + product.ProductID;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess("Account status updated successfully!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Integer> params = new HashMap<>();
                params.put("accountID", product.ProductID);
                return new JSONObject(params).toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
