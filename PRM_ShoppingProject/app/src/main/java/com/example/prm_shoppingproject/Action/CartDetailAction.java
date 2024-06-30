package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.Cart.CartListCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailListCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailSumCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDetailAction {
    private static CartDetailAction instance;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:5265/api";

    public CartDetailAction(Context context) {
        this.context = context;
    }

    public static CartDetailAction getInstance(Context context) {
        if (instance == null) {
            instance = new CartDetailAction(context);
        }
        return instance;
    }

    public void addCartDetail(int orderID, int productID, int quantity, double total, final MessageCallback callback) {
        String url = BASE_URL + "/cartdetail/create";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("orderID", orderID);
            jsonObject.put("productID", productID);
            jsonObject.put("quantity", quantity);
            jsonObject.put("unitPrice", total);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("statusCode") && object.getInt("statusCode") == 400) {
                                String message = object.getString("message");
                                callback.onError(message);
                            } else {
                                callback.onSuccess("Create new cart detail successfully!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Response parsing error: " + e.getMessage());
                        }
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.toString().getBytes();
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }
    public void getAllCartDetailByOrder(int orderID, final CartDetailListCallBack callBack) {
        String url = BASE_URL + "/cartdetail/all/" + orderID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<CartDetail> cartList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartObject = response.getJSONObject(i);
                                CartDetail cart = new CartDetail();
                                cart.setCartDetailID(cartObject.getInt("cartDetailID"));
                                cart.setOrderID(cartObject.getInt("cartID"));
                                cart.setQuantity(cartObject.getInt("quantity"));
                                cart.setProductID(cartObject.getInt("productID"));
                                cart.setTotal(cartObject.getDouble("unitPrice"));
                                cartList.add(cart);
                            }
                            callBack.onSuccess(cartList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callBack.onError("Response parsing error: " + e.getMessage());
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

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
    public void getCartDetailItemStatus(int orderId, int productId, final CartDetailCallBack callback) {
        String url = BASE_URL + "/cartdetail/all/" + orderId + "/detail/" + productId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CartDetail cart = new CartDetail();
                            cart.setCartDetailID(response.getInt("cartDetailID"));
                            cart.setOrderID(response.getInt("cartID"));
                            cart.setQuantity(response.getInt("quantity"));
                            cart.setProductID(response.getInt("productID"));
                            cart.setTotal(response.getDouble("unitPrice"));

                            callback.onSuccess(cart);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Response parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void sumTotalPriceInOrder(int orderId, final CartDetailSumCallBack callback) {
        String url = BASE_URL + "/cartdetail/all/" + orderId + "/sum";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double sum = response.getDouble("sum");
                            callback.onSuccess(sum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void updateQuantity(int productID, int quantityStatus, int orderID, int quantity, double total, final MessageCallback callback) {
        String url = BASE_URL + "/cartdetail/update/";
        int new_quantity = 0;
        if(quantityStatus == 1){
            new_quantity = quantity + 1;
        }else{
            new_quantity = quantity - 1;
        }
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cartID", orderID);
            jsonBody.put("productID", productID);
            jsonBody.put("quantity", new_quantity);
            jsonBody.put("unitPrice", total);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess("Update successfully!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void deleteCartDetail(int productID, int orderID, final MessageCallback callback) {
        String url = BASE_URL + "/cartdetail/delete";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cartID", orderID);
            jsonObject.put("productID", productID);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("JSON error: " + e.getMessage());
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("statusCode") && response.getInt("statusCode") == 400) {
                                String message = response.getString("message");
                                callback.onError(message);
                            } else {
                                callback.onSuccess("Deleted cart successfully!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Response parsing error: " + e.getMessage());
                        }
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void updateQuantityReOrder(int productID, int orderID, int quantity, double total, final MessageCallback callback) {
        String url = BASE_URL + "/cartdetail/update/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cartID", orderID);
            jsonBody.put("productID", productID);
            jsonBody.put("quantity", quantity);
            jsonBody.put("unitPrice", total);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess("Update successfully!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}
