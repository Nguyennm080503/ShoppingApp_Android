package com.example.prm_shoppingproject.Action;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm_shoppingproject.DatabaseHelper;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Interface.Account.AccountListCallback;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Model.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountAction {
    private static AccountAction instance;
    private Context context;
    private static final String BASE_URL = "http://10.0.2.2:5265/api";

    public AccountAction(Context context) {
        this.context = context;
    }

    public static AccountAction getInstance(Context context) {
        if (instance == null) {
            instance = new AccountAction(context);
        }
        return instance;
    }


    public void addAccount(String name, String email, String phone, String username, String password, final MessageCallback messageCallback) {
        String url = BASE_URL + "/register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.has("statusCode") && object.getInt("statusCode") == 400) {
                                String message = object.getString("message");
                                if (message.equals("Username is existed!")) {
                                    messageCallback.onError(message);
                                } else {
                                    messageCallback.onError(message);
                                }
                            } else {
                                messageCallback.onSuccess("Register successfully!");
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
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("username", username);
                params.put("password", password);
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

    public void updateAccountStatus(int accountId, int newStatus, final MessageCallback callback) {
        String url = BASE_URL + "/account/update";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
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
                })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Integer> params = new HashMap<>();
                params.put("accountID", accountId);
                params.put("status", newStatus);
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

    public void updatePassword(int accountId, String password, final MessageCallback callback) {
        String url = BASE_URL + "/account/change-password";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountID", accountId);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess("Account password updated successfully!");
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }


    public void GetAccountIDLogin(String username, String password, final AccountCallback callback) {
        String url = BASE_URL + "/login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            Account account = new Account();
                            account.AccountID = object.getInt("accountID");
                            account.Name = object.getString("name");
                            account.Email = object.getString("email");
                            account.Phone = object.getString("phone");
                            account.Username = object.getString("username");
                            account.RoleID = object.getInt("roleID");
                            account.Status = object.getInt("status");
                            callback.onSuccess(account);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("JSON parsing error: " + e.getMessage());
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
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
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


    public void getAllAccounts(final AccountListCallback callback) {
        String url = BASE_URL + "/accounts/all";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Account> accounts = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                int accountID = object.getInt("accountID");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String phone = object.getString("phone");
                                String username = object.getString("username");
                                int roleID = object.getInt("roleID");
                                int status = object.getInt("status");

                                Account account = new Account(accountID, name, email, phone, username, "", roleID, status);
                                accounts.add(account);
                            }
                            callback.onSuccess(accounts);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    public void getAccountProfile(int id, final AccountCallback callback) {
        String url = BASE_URL + "/account/profile/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int accountID = response.getInt("accountID");
                            String name = response.getString("name");
                            String email = response.getString("email");
                            String phone = response.getString("phone");
                            String username = response.getString("username");
                            int roleID = response.getInt("roleID");
                            int status = response.getInt("status");

                            Account account = new Account(accountID, name, email, phone, username, "", roleID, status);
                            callback.onSuccess(account);
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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
