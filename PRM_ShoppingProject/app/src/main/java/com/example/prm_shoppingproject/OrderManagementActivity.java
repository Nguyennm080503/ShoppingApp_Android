package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Model.Cart;

import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderManagementAdapter orderAdapter;
    private CartAction cartAction;
    private List<Cart> cartList;
    private ImageView backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrder);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        cartAction = new CartAction(OrderManagementActivity.this);
        backHome = findViewById(R.id.back_home);
        cartList = cartAction.getAllCartByAccount(1);


        orderAdapter = new OrderManagementAdapter(this, cartList);
        recyclerViewOrders.setAdapter(orderAdapter);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderManagementActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
