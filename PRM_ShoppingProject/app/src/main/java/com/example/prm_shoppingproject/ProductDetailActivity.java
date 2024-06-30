package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Product.ProductCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductAction productAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private int productID;
    private Product product;
    ImageView imageProduct, backHome;
    AppCompatButton btn_add;
    TextView nameProduct, price, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productdetail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productAction = new ProductAction(ProductDetailActivity.this);
        cartAction = new CartAction(ProductDetailActivity.this);
        cartDetailAction = new CartDetailAction(ProductDetailActivity.this);
        Intent intent = getIntent();
        productID = intent.getIntExtra("productID", -1);
        productAction.GetProductByID(productID, new ProductCallBack() {
            @Override
            public void onSuccess(Product productLoad) {
                product = productLoad;
            }

            @Override
            public void onError(String error) {
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);

        imageProduct = findViewById(R.id.image_product);
        nameProduct = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        description = findViewById(R.id.description_product);
        backHome = findViewById(R.id.back_home);
        btn_add = findViewById(R.id.btn_addtoCard);

        if (product != null) {
            nameProduct.setText(product.getName());
            price.setText(String.format("$%.2f", product.getPrice()));
            description.setText(product.getDescription());

            if (product.Image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(product.Image, 0, product.Image.length);
                imageProduct.setImageBitmap(bitmap);
            }
        }

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productID = product.ProductID;
                Cart cart = cartAction.getCartPendingByOrderID(accountIDLogin);
                if(cart.CartID != 0){
                    CartDetail cartDetail = cartDetailAction.getCartDetailByProductIDPending(productID, cart.CartID);
                    if (cartDetail != null){
                        cartDetailAction.updateQuantity(productID, 1, cartDetail.OrderID, cartDetail.Quantity,product.Price * (cartDetail.Quantity + 1));
                        calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.OrderID);
                    }else{
                        Cart cartPending = cartAction.getCartPendingByOrderID(accountIDLogin);
                        cartDetailAction.addCartDetail(cartPending.CartID, productID, 1, product.Price * 1);
                    }
                }
                else{
                    cartAction.addCart(accountIDLogin, (product.Price * 1) + 2 ,"", 0);
                    Cart cartPending = cartAction.getCartPendingByOrderID(accountIDLogin);
                    cartDetailAction.addCartDetail(cartPending.CartID, productID, 1, product.Price * 1);
                }
                Toast.makeText(ProductDetailActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void calculateTotalPrice(CartAction cartAction, int accountIDLogin, CartDetailAction cartDetailAction, int orderID) {
        double sum = cartDetailAction.sumTotalPriceInOrder(orderID);
        cartAction.updateTotalCart(accountIDLogin, sum + 2);
    }
}
