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
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://localhost:7111/api/product/create",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.has("statusCode") && object.getInt("statusCode") == 400) {
                                String message = object.getString("message");
                                if (message.equals("Product name is existed!")) {
                                    callback.onError(message);
                                } else {
                                    callback.onError(message);
                                }
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
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=UTF-8");
                return header;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("name", name);
                    jsonBody.put("price", price);
                    jsonBody.put("image", image);
                    jsonBody.put("description", description);
                    jsonBody.put("categoryID", typeID);

                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void GetProductByID(int productID, final ProductCallBack callBack){
        String url = "https://localhost:7111/api/product/detail/" + productID;

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
        String url = "https://localhost:7111/api/product/all/active";
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
        String url = "https://localhost:7111/api/product/all";
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
        String url = "https://localhost:7111/api/product/all/cate/" + typeID;
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

    public void UpdateProduct(ProductUpdate product) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", product.getName());
        values.put("Price", product.getPrice());
        values.put("Image", product.getImage());
        values.put("Description", product.getDescription());
        values.put("TypeID", product.getTypeID());
        values.put("Status", product.getStatus());

        db.update("Product", values, "ProductID = ?", new String[]{String.valueOf(product.getProductID())});
        db.close();
    }
}
