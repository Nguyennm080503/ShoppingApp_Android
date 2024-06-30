package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.prm_shoppingproject.Util.ImageUtil;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductAction productAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;

    private int productID;
    private Product product;
    private Cart cartPending;
    private CartDetail cartDetail;
    private double sum;

    private ImageView imageProduct, backHome;
    private TextView nameProduct, price, description;
    private AppCompatButton btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);
        initViews();
        setupListeners();

        productAction = new ProductAction(ProductDetailActivity.this);
        cartAction = new CartAction(ProductDetailActivity.this);
        cartDetailAction = new CartDetailAction(ProductDetailActivity.this);

        Intent intent = getIntent();
        productID = intent.getIntExtra("productID", -1);

        loadProductDetails(productID);
    }

    private void initViews() {
        imageProduct = findViewById(R.id.image_product);
        nameProduct = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        description = findViewById(R.id.description_product);
        btn_add = findViewById(R.id.btn_addtoCard);
        backHome = findViewById(R.id.back_home);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupListeners() {
        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        btn_add.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            int accountIDLogin = sharedPreferences.getInt("accountID", -1);

            cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                @Override
                public void onSuccess(Cart cartLoad) {
                    handleCartLoadSuccess(cartLoad, productID, accountIDLogin, product);
                }

                @Override
                public void onError(String error) {
                    handleCartLoadSuccess(null, productID, accountIDLogin, product);
                }
            });
        });
    }

    private void handleCartLoadSuccess(Cart cart, int productID, int accountIDLogin, Product product) {
        if (cart != null && cart.getCartID() != 0) {
            cartDetailAction.getCartDetailItemStatus(productID, cart.getCartID(), new CartDetailCallBack() {
                @Override
                public void onSuccess(CartDetail cartDetailLoad) {
                    handleCartDetailLoadSuccess(cartDetailLoad, productID, cart.getCartID(), accountIDLogin, product);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Failed to load cart item information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            cartAction.addCart(accountIDLogin, (product.Price * 1) + 2, "", new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    cartAction.getCartNewOrderID(new CartCallBack() {
                        @Override
                        public void onSuccess(Cart cart) {
                            handleCartDetailLoadSuccess(null, productID, cart.getCartID(), accountIDLogin, product);
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Failed to create new cart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleCartDetailLoadSuccess(CartDetail cartDetail, int productID, int cartID, int accountIDLogin, Product product) {
        if (cartDetail != null) {
            cartDetailAction.updateQuantity(productID, 1, cartDetail.getOrderID(), cartDetail.getQuantity(), product.getPrice() * (cartDetail.getQuantity() + 1), new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.getOrderID());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Failed to update cart detail", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            cartDetailAction.addCartDetail(cartID, productID, 1, product.getPrice() * 1, new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void calculateTotalPrice(CartAction cartAction, int accountIDLogin, CartDetailAction cartDetailAction, int orderID) {
        cartDetailAction.sumTotalPriceInOrder(orderID, new CartDetailSumCallBack() {
            @Override
            public void onSuccess(double sumLoad) {
                updateTotalCart(cartAction, accountIDLogin, sumLoad);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProductDetailActivity.this, "Failed to calculate total price", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalCart(CartAction cartAction, int accountIDLogin, double sum) {
        cartAction.updateTotalCart(accountIDLogin, sum + 2, new MessageCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ProductDetailActivity.this, "Cart updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProductDetailActivity.this, "Failed to update cart total", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails(int productID) {
        productAction.GetProductByID(productID, new ProductCallBack() {
            @Override
            public void onSuccess(Product productLoad) {
                product = productLoad;
                updateUIWithProductDetails();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProductDetailActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithProductDetails() {
        if (product != null) {
            nameProduct.setText(product.getName());
            price.setText(String.format("$%.2f", product.getPrice()));
            description.setText(product.getDescription());

            if (product.getImage() != null) {
                Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.getImage());
                imageProduct.setImageBitmap(bitmap);
            }
        }
    }
}
