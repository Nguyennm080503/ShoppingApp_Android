package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.CartItem;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ProductAction productAction;
    private AccountAction accountAction;
    private List<CartProduct> productCartList;
    private LinearLayout cartFull;
    private TextView totalPrice, total, name, phone, emptyCartMessage;
    private ImageView backHome;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
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

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);
        accountAction = new AccountAction(CartActivity.this);
        Account account = accountAction.GetAccountByID(accountIDLogin);
        productAction = new ProductAction(CartActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productCartList = new ArrayList<>();
        ArrayList<CartItem> cartItems = getCartItems();
        if (cartItems.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            cartFull.setVisibility(View.GONE);
        } else {
            emptyCartMessage.setVisibility(View.GONE);
            cartFull.setVisibility(View.VISIBLE);
            for (CartItem item : cartItems) {
                CartProduct cartProduct = new CartProduct();
                Product product = productAction.GetProductByID(item.ProductID);
                cartProduct.Price = product.Price * item.Quantity;
                cartProduct.Image = product.Image;
                cartProduct.ProductName = product.Name;
                cartProduct.Quantity = item.Quantity;
                cartProduct.ProductID = item.ProductID;
                productCartList.add(cartProduct);
            }

            cartAdapter = new CartAdapter(this, productCartList);
            recyclerView.setAdapter(cartAdapter);
            totalPrice.setText(String.format("$%.2f", cartAdapter.calculateTotalPrice()));
            total.setText(String.format("$%.2f", (cartAdapter.calculateTotalPrice() + 2)));
            name.setText(account.Name);
            phone.setText(account.Phone);
        }

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<CartItem> getCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        ArrayList<CartItem> cartItems = new ArrayList<>();
        Set<String> cartSet = sharedPreferences.getStringSet("cart", new HashSet<>());
        for (String item : cartSet) {
            String[] parts = item.split(",");
            if (parts.length == 2) {
                int productID = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                cartItems.add(new CartItem(productID, quantity));
            }
        }
        return cartItems;
    }
}
