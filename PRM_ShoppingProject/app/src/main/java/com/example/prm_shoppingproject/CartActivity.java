package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnQuantityChangeListener, CartAdapter.OnCartEmptyListener{
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ProductAction productAction;
    private AccountAction accountAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private List<CartProduct> productCartList;
    private LinearLayout cartFull;
    private TextView totalPrice, total, name, phone, emptyCartMessage;
    private ImageView backHome;
    private EditText address;
    private AppCompatButton checkout;
    private String accountID;
    private String EphericalKey;
    private String ClientKey;

    private final String Apikey = "pk_test_51PWSgUD9V5NbhcqDuqScwxUf2suaAeJyq5rwTNuX6aLyItUgQewTpxCv6SPaWoAUre5UytqnDAAyLr6kz1wFGnjE00WFU1dNuy";
    private final String ApiSercret = "sk_test_51PWSgUD9V5NbhcqDsI3wIY0o5CdFsN0J4lWd1x7zvIvxAnc8ojBiiB9S87CdvNNh5LSyNZLwhBzsmtnjQEMmtsNV00InidgtcJ";
    private PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.cartView);
        totalPrice = findViewById(R.id.totalPrice);
        total = findViewById(R.id.total);
        backHome = findViewById(R.id.back_home);
        name = findViewById(R.id.txt_name);
        phone = findViewById(R.id.txt_phone);
        cartFull = findViewById(R.id.cartFull);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        checkout = findViewById(R.id.btn_checkout);
        cartAction = new CartAction(CartActivity.this);
        cartDetailAction = new CartDetailAction(CartActivity.this);
        productAction = new ProductAction(CartActivity.this);
        accountAction = new AccountAction(CartActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        productCartList = new ArrayList<>();
        PaymentConfiguration.init(this, Apikey);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);
        Account account = accountAction.GetAccountByID(accountIDLogin);

        List<CartDetail> cartItems = checkCartPending(accountIDLogin);
        Cart cartOrder = new Cart();
        if (cartItems.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            cartFull.setVisibility(View.GONE);
        } else {
            emptyCartMessage.setVisibility(View.GONE);
            cartFull.setVisibility(View.VISIBLE);
            for (CartDetail item : cartItems) {
                CartProduct cartProduct = new CartProduct();
                Product product = productAction.GetProductByID(item.ProductID);
                cartProduct.Price = product.Price * item.Quantity;
                cartProduct.Image = product.Image;
                cartProduct.ProductName = product.Name;
                cartProduct.Quantity = item.Quantity;
                cartProduct.ProductID = item.ProductID;
                productCartList.add(cartProduct);
            }

            cartAdapter = new CartAdapter(this, productCartList, this, this);
            recyclerView.setAdapter(cartAdapter);
            cartOrder = cartAction.getCartPendingByOrderID(accountIDLogin);
            totalPrice.setText(String.format("$%.2f", cartOrder.Total - 2));
            total.setText(String.format("$%.2f", cartOrder.Total));
            name.setText(account.Name);
            phone.setText(account.Phone);
        }

        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        checkout.setOnClickListener(v -> {
            address = findViewById(R.id.txt_addresss);
            TextView cartTotal = findViewById(R.id.total);
            String txt_address = address.getText().toString().trim();
            if (txt_address.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please enter address to order!", Toast.LENGTH_SHORT).show();
            } else {
                checkoutClicked(txt_address, Double.parseDouble(cartTotal.toString()));
            }
        });
    }

    private void checkoutClicked(String txt_address, double amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            accountID = object.getString("id");
                            getEnphericalKey(accountID, amount);
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
                header.put("Authorization", "Bearer " + ApiSercret);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getEnphericalKey(String accountID, double amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            EphericalKey = object.getString("id");
                            getClientSecret(accountID, EphericalKey, amount);
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
                header.put("Authorization", "Bearer " + ApiSercret);
                header.put("Stripe-Version", "2022-11-15"); // Ensure the version matches the required one
                return header;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String param = "customer=" + accountID;
                return param.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }


    private void getClientSecret(String accountID, String ephericalKey, double amount) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            ClientKey = object.getString("client_secret");
                            PaymentFlow(); // Call PaymentFlow after obtaining ClientKey
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
                header.put("Authorization", "Bearer " + ApiSercret);
                return header;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String param = "customer=" + accountID + "&amount="+ amount + "&currency=usd&automatic_payment_methods[enabled]=true";
                return param.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CartActivity.this);
        requestQueue.add(stringRequest);
    }


    private void PaymentFlow() {
        if (ClientKey != null && accountID != null && EphericalKey != null) {
            paymentSheet.presentWithPaymentIntent(
                    ClientKey, new PaymentSheet.Configuration("Shopping App",
                            new PaymentSheet.CustomerConfiguration(accountID, EphericalKey))
            );
        } else {
            Toast.makeText(this, "Unable to initiate payment. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        CartAction cartActionPayment = new CartAction(CartActivity.this);
        String txt_address = address.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);
        Cart cart = cartAction.getCartPendingByOrderID(accountIDLogin);
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            PaymentOrder(cart.CartID, txt_address, cartActionPayment, 1);
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            Toast.makeText(this, "Payment successfully!", Toast.LENGTH_SHORT).show();
        } else {
            PaymentOrder(cart.CartID, txt_address, cartActionPayment, 2);
            Toast.makeText(this, "Fail to paymet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void PaymentOrder(int cartID, String address, CartAction cartAction, int status) {
        cartAction.updateStatusCart(cartID, address, status);
    }

    private List<CartDetail> checkCartPending(int accountID) {
        List<CartDetail> cartDetails = new ArrayList<>();
        Cart cart = cartAction.getCartPendingByOrderID(accountID);
        if (cart != null) {
            cartDetails = cartDetailAction.getAllCartDetailByOrder(cart.CartID);
        }
        return cartDetails;
    }

    public void onQuantityChanged(double totalPrice) {
        this.totalPrice.setText(String.format("$%.2f", totalPrice));
        this.total.setText(String.format("$%.2f", totalPrice + 2));
    }

    public void onCartEmpty() {
        emptyCartMessage.setVisibility(View.VISIBLE);
        cartFull.setVisibility(View.GONE);
    }
}
