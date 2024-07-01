package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailListCallBack;
import com.example.prm_shoppingproject.Interface.Product.ProductListCallBack;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.Product;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnNumberCartChangeListener {

    private static final String TAG = "HomeActivity";
    private RecyclerView recyclerView;
    private AccountAction accountAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private ProductAction productAction;
    private ProductAdapter productAdapter;
    private int accountIDLogin;
    private ImageView cartView, profileView, notification;
    private TextView number_cart;
    private LinearLayout profileScreen, cartScreen, mapScreen, orderScreen, shoeCate, shortCate, tshirtCate, shirtCate, jacketCate;
    private Account account;
    private int number;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        accountIDLogin = sharedPreferences.getInt("accountID", -1);

        if (accountIDLogin != -1) {
            loadAccountProfile();
            loadCartDetails();
            loadProducts();
        } else {
            Toast.makeText(this, "No account ID found. Please log in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        cartView = findViewById(R.id.cart);
        profileView = findViewById(R.id.profile);
        number_cart = findViewById(R.id.number);
        notification = findViewById(R.id.ic_notification);
        profileScreen = findViewById(R.id.profileIconScreen);
        cartScreen = findViewById(R.id.cartIconScreen);
        orderScreen = findViewById(R.id.orderIconScreen);
        mapScreen = findViewById(R.id.mapIconScreen);
        shoeCate = findViewById(R.id.cate_shoe);
        shirtCate = findViewById(R.id.cate_shirt);
        shortCate = findViewById(R.id.cate_short);
        tshirtCate = findViewById(R.id.cate_tshirt);
        jacketCate = findViewById(R.id.cate_jacket);

        accountAction = new AccountAction(this);
        cartAction = new CartAction(this);
        cartDetailAction = new CartDetailAction(this);
        productAction = new ProductAction(this);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        cartView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        profileView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        cartScreen.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        orderScreen.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, OrderActivity.class)));
        profileScreen.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        shoeCate.setOnClickListener(v -> startCategoryActivity(1));
        tshirtCate.setOnClickListener(v -> startCategoryActivity(2));
        shortCate.setOnClickListener(v -> startCategoryActivity(3));
        shirtCate.setOnClickListener(v -> startCategoryActivity(4));
        jacketCate.setOnClickListener(v -> startCategoryActivity(5));
    }

    private void startCategoryActivity(int categoryID) {
        Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
        intent.putExtra("categoryID", categoryID);
        startActivity(intent);
    }

    private void loadAccountProfile() {
        accountAction.getAccountProfile(accountIDLogin, new AccountCallback() {
            @Override
            public void onSuccess(Account accountLoad) {
                account = accountLoad;
                updateUIWithAccountInfo();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load account: " + error);
            }
        });
    }

    private void updateUIWithAccountInfo() {
        TextView name = findViewById(R.id.name);
        name.setText(account.getName());
    }

    private void loadCartDetails() {
        cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
            @Override
            public void onSuccess(Cart cartLoad) {
                cart = cartLoad;
                updateCartDetails();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load cart: " + error);
                if (error.equals("No cart found for the given account ID.")) {
                    handleNoCartFound();
                } else {
                    Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleNoCartFound() {
        notification.setVisibility(View.GONE);
        number_cart.setVisibility(View.GONE);
        Toast.makeText(this, "No cart found for your account.", Toast.LENGTH_SHORT).show();
    }

    private void updateCartDetails() {
        if (cart == null) {
            notification.setVisibility(View.GONE);
            number_cart.setVisibility(View.GONE);
        } else {
            cartDetailAction.getAllCartDetailByOrder(cart.getCartID(), new CartDetailListCallBack() {
                @Override
                public void onSuccess(List<CartDetail> cartList) {
                    number = cartList.size();
                    updateCartNumber();
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to load cart details: " + error);
                }
            });
        }
    }

    private void updateCartNumber() {
        number_cart.setText(String.valueOf(number));
        notification.setVisibility(View.VISIBLE);
        number_cart.setVisibility(View.VISIBLE);
    }

    private void loadProducts() {
        productAction.getAllProductsActive(new ProductListCallBack() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter = new ProductAdapter(HomeActivity.this, products, HomeActivity.this::onNumberCartChanged);
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load products: " + error);
            }
        });
    }

    @Override
    public void onNumberCartChanged(int numberItemInCart) {
        notification.setVisibility(View.VISIBLE);
        number_cart.setVisibility(View.VISIBLE);
        number_cart.setText(String.valueOf(numberItemInCart));
    }
}
