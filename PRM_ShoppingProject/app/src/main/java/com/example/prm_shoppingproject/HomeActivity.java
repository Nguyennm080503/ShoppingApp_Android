package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnNumberCartChangeListener {

    private RecyclerView recyclerView;
    private AccountAction accountAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private ProductAction productAction;
    private ProductAdapter productAdapter;
    private int accountIDLogin;
    private ImageView cartView, profileView, notification;
    private TextView number_cart;
    private LinearLayout profileScreen, cartScreen, mapScreen, orderScreen, shoeCate, shortCate, tshirtCate, shirtCate,jacketCate;
    private Account account;
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
        Intent intent = getIntent();
        accountAction = new AccountAction(HomeActivity.this);
        productAction = new ProductAction(HomeActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        accountIDLogin = sharedPreferences.getInt("accountID", -1);
        List<Product> products = productAction.getAllProducts();
        account = accountAction.GetAccountByID(accountIDLogin);
        TextView name = findViewById(R.id.name);
        number_cart = findViewById(R.id.number);
        notification = findViewById(R.id.ic_notification);
        cartAction = new CartAction(HomeActivity.this);
        cartDetailAction = new CartDetailAction(HomeActivity.this);
        Cart cart = cartAction.getCartPendingByOrderID(accountIDLogin);
        if(cart.CartID == 0){
            notification.setVisibility(View.GONE);
            number_cart.setVisibility(View.GONE);
        }else{
            int number = cartDetailAction.getAllCartDetailByOrder(cart.CartID).size();
            number_cart.setText(String.valueOf(number));
            notification.setVisibility(View.VISIBLE);
            number_cart.setVisibility(View.VISIBLE);
        }
        name.setText(account.Name);
        cartView = findViewById(R.id.cart);
        profileView = findViewById(R.id.profile);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        profileScreen = findViewById(R.id.profileIconScreen);
        cartScreen = findViewById(R.id.cartIconScreen);
        orderScreen = findViewById(R.id.orderIconScreen);
        mapScreen = findViewById(R.id.mapIconScreen);
        shoeCate = findViewById(R.id.cate_shoe);
        shirtCate = findViewById(R.id.cate_shirt);
        shortCate = findViewById(R.id.cate_short);
        tshirtCate = findViewById(R.id.cate_tshirt);
        jacketCate = findViewById(R.id.cate_jacket);

        productAdapter = new ProductAdapter(this, products, this);
        recyclerView.setAdapter(productAdapter);

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        cartScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        orderScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        });

        profileScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        shoeCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
                intent.putExtra("categoryID", 1);
                startActivity(intent);
            }
        });

        tshirtCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
                intent.putExtra("categoryID", 2);
                startActivity(intent);
            }
        });

        shortCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
                intent.putExtra("categoryID", 3);
                startActivity(intent);
            }
        });

        shirtCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
                intent.putExtra("categoryID", 4);
                startActivity(intent);
            }
        });

        jacketCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductCategoryActivity.class);
                intent.putExtra("categoryID", 5);
                startActivity(intent);
            }
        });
    }

    public void onNumberCartChanged(int numberItemInCart){
        this.notification.setVisibility(View.VISIBLE);
        this.number_cart.setVisibility(View.VISIBLE);
        this.number_cart.setText(String.valueOf(numberItemInCart));
    }
}