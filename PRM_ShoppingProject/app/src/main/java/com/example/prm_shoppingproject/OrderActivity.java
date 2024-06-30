package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.HomeActivity;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.Cart.CartListCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.OrderAdapter;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private CartAction cartAction;
    private ImageView backHome;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.orderView);
        backHome = findViewById(R.id.back_home);
        empty = findViewById(R.id.emptyCartMessage);

        cartAction = new CartAction(this);

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);

        cartAction.getAllCartByAccount(accountIDLogin, new CartListCallBack() {
            @Override
            public void onSuccess(List<Cart> cartList) {
                // Update UI with cart data
                if (cartList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    orderAdapter = new OrderAdapter(OrderActivity.this, cartList);
                    recyclerView.setLayoutManager(new GridLayoutManager(OrderActivity.this, 1));
                    recyclerView.setAdapter(orderAdapter);
                }
            }

            @Override
            public void onError(String error) {
                empty.setVisibility(View.VISIBLE);
                empty.setText("Error fetching cart data: " + error);
                recyclerView.setVisibility(View.GONE);
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
