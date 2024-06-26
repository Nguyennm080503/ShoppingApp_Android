package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailListCallBack;
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

public class CartAction {
    private static CartAction instance;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:5265/api";

    public CartAction(Context context) {
        this.context = context;
    }

    public static CartAction getInstance(Context context) {
        if (instance == null) {
            instance = new CartAction(context);
        }
        return instance;
    }

    public void addCart(int accountID, double totalBill, String address, final MessageCallback callback) {
        String url = BASE_URL + "/cart/create";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("accountID", accountID);
            jsonObject.put("totalBill", totalBill);
            jsonObject.put("address", address);
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
                            callback.onSuccess("Create new cart successfully!");
                        } catch (Exception e) {
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
    public void deleteCart(int orderID, final MessageCallback callback) {
        String url = BASE_URL + "/cart/delete/" + orderID;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess("Deleted cart successfully!");
                        } catch (Exception e) {
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

        Volley.newRequestQueue(context).add(stringRequest);
    }
    public void getAllCartByAccount(int accountID, final CartListCallBack callback) {
        String url = BASE_URL + "/cart/all/account/" + accountID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Cart> cartList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartObject = response.getJSONObject(i);
                                Cart cart = new Cart();
                                cart.setCartID(cartObject.getInt("cartID"));
                                cart.setAccountID(cartObject.getInt("accountID"));
                                cart.setTotalBill(cartObject.getDouble("totalBill"));
                                cart.setAddress(cartObject.getString("addresss"));
                                cart.setOrderDate(cartObject.getString("dateOrder"));
                                cart.setStatus(cartObject.getInt("status"));
                                cartList.add(cart);
                            }
                            callback.onSuccess(cartList);
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

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
    public void getCartByOrderID(int orderID, final CartCallBack callback) {
        String url = BASE_URL + "/cart/order/" + orderID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Cart cart = new Cart();
                            cart.setCartID(response.getInt("cartID"));
                            cart.setAccountID(response.getInt("accountID"));
                            cart.setTotalBill(response.getDouble("totalBill"));
                            cart.setAddress(response.getString("addresss"));

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
    public void getCartNewOrderID(final CartCallBack callback) {
        String url = BASE_URL + "/cart/new";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Cart cart = new Cart();
                            cart.setCartID(response.getInt("cartID"));
                            cart.setAccountID(response.getInt("accountID"));
                            cart.setTotalBill(response.getDouble("totalBill"));
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
    public void getCartPendingByAccountID(int accountID, final CartCallBack callback) {
        String url = BASE_URL + "/cart/pending/account/" + accountID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Cart cart = new Cart();
                            cart.setCartID(response.getInt("cartID"));
                            cart.setAccountID(response.getInt("accountID"));
                            cart.setTotalBill(response.getDouble("totalBill"));

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
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            callback.onError("No cart found for the given account ID.");
                        } else {
                            error.printStackTrace();
                            callback.onError("Volley error: " + error.getMessage());
                        }
                    }
                });

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void updateTotalCart(int cartID, final MessageCallback callback) {
        String url = BASE_URL + "/cart/update/total";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cartID", cartID);
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
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add headers if necessary (e.g., Content-Type)
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void updateStatusCartFinal(int cartID, String address , final MessageCallback callback) {
        String url = BASE_URL + "/cart/checkout";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cartID", cartID);
            jsonBody.put("address", address.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess("Update successfuly!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        callback.onError("Volley error: " + error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add headers if necessary (e.g., Content-Type)
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
    public void updateStatusCart(int cartID, int status, final MessageCallback callback) {
        String url = BASE_URL + "/cart/update";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cartID", cartID);
            jsonBody.put("status", status);
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

    public void getAllCart(final CartListCallBack cartListCallBack){
        String url = BASE_URL + "/cart/all";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Cart> cartList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartObject = response.getJSONObject(i);
                                Cart cart = new Cart();
                                cart.setCartID(cartObject.getInt("cartID"));
                                cart.setAccountID(cartObject.getInt("accountID"));
                                cart.setTotalBill(cartObject.getDouble("totalBill"));
                                cart.setAddress(cartObject.getString("addresss"));
                                cart.setOrderDate(cartObject.getString("dateOrder"));
                                cart.setStatus(cartObject.getInt("status"));
                                System.out.println("Card: " + cart.CartID);
                                cartList.add(cart);
                            }
                            Log.d("getAllCart", "Cart list size: " + cartList.size());
                            cartListCallBack.onSuccess(cartList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartListCallBack.onError("Response parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        cartListCallBack.onError("Volley error: " + error.getMessage());
                    }
                });

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public void GetOrderDetails (int orderId, final CartDetailListCallBack callback){
        String url = BASE_URL + "/cartdetail/all/" + orderId;

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
                                cart.setProductID(cartObject.getInt("productID"));
                                cart.setQuantity(cartObject.getInt("quantity"));
                                cart.setTotal(cartObject.getDouble("unitPrice"));
                                System.out.println("Card: " + cart.getCartDetailID());
                                cartList.add(cart);
                            }
                            Log.d("getAllCart", "Cart list size: " + cartList.size());
                            callback.onSuccess(cartList);
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

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
}
