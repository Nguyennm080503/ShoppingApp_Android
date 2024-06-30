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
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailSumCallBack;
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
    private ImageView imageProduct, backHome;
    private AppCompatButton btn_add;
    private TextView nameProduct, price, description;
    private Cart cartPending, cart;
    private CartDetail cartDetail;
    private double sum;
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
                cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                    @Override
                    public void onSuccess(Cart cartLoad) {
                        cart = cartLoad;
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
                if(cart.CartID != 0){
                    cartDetailAction.getCartDetailItemStatus(productID, cart.CartID, new CartDetailCallBack() {
                        @Override
                        public void onSuccess(CartDetail cartDetailLoad) {
                            cartDetail = cartDetailLoad;
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                    if (cartDetail != null){
                        cartDetailAction.updateQuantity(productID, 1, cartDetail.OrderID, cartDetail.Quantity, product.Price * (cartDetail.Quantity + 1), new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                        calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.OrderID);
                    }else{
                        cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                            @Override
                            public void onSuccess(Cart cart) {
                                cartPending = cart;
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                        cartDetailAction.addCartDetail(cartPending.CartID, productID, 1, product.Price * 1, new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                }
                else{
                    cartAction.addCart(accountIDLogin, (product.Price * 1) + 2 , "", new MessageCallback() {
                        @Override
                        public void onSuccess(String message) {
                            cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                                @Override
                                public void onSuccess(Cart cart) {
                                    cartPending = cart;
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                            cartDetailAction.addCartDetail(cartPending.CartID, productID, 1, product.Price * 1, new MessageCallback() {
                                @Override
                                public void onSuccess(String message) {

                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                }
                Toast.makeText(ProductDetailActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void calculateTotalPrice(CartAction cartAction, int accountIDLogin, CartDetailAction cartDetailAction, int orderID) {
        cartDetailAction.sumTotalPriceInOrder(orderID, new CartDetailSumCallBack() {
            @Override
            public void onSuccess(double sumLoad) {
                sum = sumLoad;
            }

            @Override
            public void onError(String error) {

            }
        });
        cartAction.updateTotalCart(accountIDLogin, sum + 2, new MessageCallback() {
            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
