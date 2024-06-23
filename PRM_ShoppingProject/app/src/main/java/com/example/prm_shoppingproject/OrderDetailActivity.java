package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ProductAction productAction;
    private List<CartProduct> productCartList;
    private CartDetailAction cartDetailAction;
    private CartAction cartAction;
    private TextView totalPrice, total, address;

    private ImageView backHome;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orderdetail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order_detail_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.cartView);
        totalPrice = findViewById(R.id.totalPrice);
        total = findViewById(R.id.total);
        address = findViewById(R.id.txt_addresss);
        backHome = findViewById(R.id.back_home);
        int orderID = intent.getIntExtra("cartID", -1);
        cartDetailAction = new CartDetailAction(OrderDetailActivity.this);
        cartAction = new CartAction(OrderDetailActivity.this);
        productAction = new ProductAction(OrderDetailActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        Cart cart = cartAction.getCartByOrderID(orderID);

        List<CartDetail> cartItems =  cartDetailAction.getAllCartDetailByOrder(orderID);
        productCartList = new ArrayList<>();
        for (CartDetail item: cartItems) {
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
        total.setText(String.format("$%.2f", (cart.Total)));
        totalPrice.setText(String.format("$%.2f", (cart.Total - 2)));
        address.setText(cart.Address);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}
