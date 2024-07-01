package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailListCallBack;
import com.example.prm_shoppingproject.Interface.Product.ProductCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderDetailManagementAdapter cartAdapter;
    private ProductAction productAction;
    private List<CartProduct> productCartList;
    private CartDetailAction cartDetailAction;
    private CartAction cartAction;
    private Product product;
    private TextView totalPrice, total, address;

    private ImageView backHome;
    private Cart myCart, cartNew, cart;
    private List<CartDetail> cartItems, cartDetails;

    @SuppressLint({"DefaultLocale", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orderdetail_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order_detail_management_screen), (v, insets) -> {
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
        cartDetailAction = new CartDetailAction(OrderDetailManagementActivity.this);
        cartAction = new CartAction(OrderDetailManagementActivity.this);
        productAction = new ProductAction(OrderDetailManagementActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        cartAction.getCartByOrderID(orderID, new CartCallBack() {
            @Override
            public void onSuccess(Cart cartLoad) {
                cart = cartLoad;
                total.setText(String.format("$%.2f", (cart.TotalBill)));
                totalPrice.setText(String.format("$%.2f", (cart.TotalBill - 2)));
                address.setText(cart.Address);
            }

            @Override
            public void onError(String error) {

            }
        });
        cartDetailAction.getAllCartDetailByOrder(orderID, new CartDetailListCallBack() {
            @Override
            public void onSuccess(List<CartDetail> cartListLoad) {
                cartItems = cartListLoad;
                for (int i = 0; i < cartItems.size(); i++){
                    Log.d("cartList", "Received cart list: " + cartItems.get(i).getCartDetailID());
                }

                productCartList = new ArrayList<>();
                for (CartDetail item: cartItems) {
                    CartProduct cartProduct = new CartProduct();
                    productAction.GetProductByID(item.ProductID, new ProductCallBack() {
                        @Override
                        public void onSuccess(Product productLoad) {
                            product = productLoad;
                            cartProduct.PriceTotal = product.Price * item.Quantity;
                            cartProduct.Image = product.Image;
                            cartProduct.ProductName = product.Name;
                            cartProduct.Quantity = item.Quantity;
                            cartProduct.ProductID = item.ProductID;

                            productCartList.add(cartProduct);
                            cartAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });

                }

                cartAdapter = new OrderDetailManagementAdapter(OrderDetailManagementActivity.this, productCartList);
                recyclerView.setAdapter(cartAdapter);
            }

            @Override
            public void onError(String error) {

            }
        });



        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailManagementActivity.this, OrderManagementActivity.class);
                startActivity(intent);
            }
        });
    }
}
