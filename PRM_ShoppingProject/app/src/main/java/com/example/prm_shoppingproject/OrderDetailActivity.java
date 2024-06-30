package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
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

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderDetailAdapter cartAdapter;
    private ProductAction productAction;
    private List<CartProduct> productCartList;
    private CartDetailAction cartDetailAction;
    private CartAction cartAction;
    private Product product;
    private AppCompatButton cancel;
    private TextView totalPrice, total, address;

    private ImageView backHome;
    private Cart myCart, cartNew, cart;
    private List<CartDetail> cartItems, cartDetails;

    @SuppressLint({"DefaultLocale", "ResourceAsColor"})
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
        cancel = findViewById(R.id.btn_cancel);
        int orderID = intent.getIntExtra("cartID", -1);
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);
        cartDetailAction = new CartDetailAction(OrderDetailActivity.this);
        cartAction = new CartAction(OrderDetailActivity.this);
        productAction = new ProductAction(OrderDetailActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        cartAction.getCartByOrderID(orderID, new CartCallBack() {
            @Override
            public void onSuccess(Cart cartLoad) {
                cart = cartLoad;
            }

            @Override
            public void onError(String error) {

            }
        });

        if(cart.Status != 3 || cart.Status != 4 || cart.Status != 5){
            cancel.setBackgroundColor(R.color.red);
            cancel.setText("Cancel");
        }else{
            cancel.setBackgroundColor(R.color.green);
            cancel.setText("Re-order");
        }

        cartDetailAction.getAllCartDetailByOrder(orderID, new CartDetailListCallBack() {
            @Override
            public void onSuccess(List<CartDetail> cartListLoad) {
                cartItems = cartListLoad;
            }

            @Override
            public void onError(String error) {

            }
        });
        productCartList = new ArrayList<>();
        for (CartDetail item: cartItems) {
            CartProduct cartProduct = new CartProduct();
            productAction.GetProductByID(item.ProductID, new ProductCallBack() {
                @Override
                public void onSuccess(Product productLoad) {
                    product = productLoad;
                }

                @Override
                public void onError(String error) {
                }
            });
            cartProduct.Price = product.Price * item.Quantity;
            cartProduct.Image = product.Image;
            cartProduct.ProductName = product.Name;
            cartProduct.Quantity = item.Quantity;
            cartProduct.ProductID = item.ProductID;

            productCartList.add(cartProduct);
        }

        cartAdapter = new OrderDetailAdapter(this, productCartList);
        recyclerView.setAdapter(cartAdapter);
        total.setText(String.format("$%.2f", (cart.TotalBill)));
        totalPrice.setText(String.format("$%.2f", (cart.TotalBill - 2)));
        address.setText(cart.Address);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = cancel.getText().toString();
                cartAction = new CartAction(OrderDetailActivity.this);
                cartDetailAction = new CartDetailAction(OrderDetailActivity.this);
                productAction = new ProductAction(OrderDetailActivity.this);
                if(text.equals("Cancel")){
                    cartAction.updateStatusCart(cart.CartID, 2, new MessageCallback() {
                        @Override
                        public void onSuccess(String message) {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                }
                else{
                    cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                        @Override
                        public void onSuccess(Cart cart) {
                            myCart = cart;
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                    if (myCart.CartID == 0) {
                        cartAction.addCart(accountIDLogin, cart.TotalBill, "", new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {
                            }

                            @Override
                            public void onError(String error) {
                            }
                        });
                        cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                            @Override
                            public void onSuccess(Cart cart) {
                                cartNew = cart;
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                        for (CartDetail item : cartItems) {
                            cartDetailAction.addCartDetail(cartNew.CartID, item.ProductID, item.Quantity, item.Total, new MessageCallback() {
                                @Override
                                public void onSuccess(String message) {

                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        }
                    } else {
                         cartDetailAction.getAllCartDetailByOrder(myCart.CartID, new CartDetailListCallBack() {
                            @Override
                            public void onSuccess(List<CartDetail> cartList) {
                                cartDetails = cartList;
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                        for (CartDetail itemReOrder : cartItems) {
                            boolean found = false;
                            for (CartDetail item : cartDetails) {
                                if (item.ProductID == itemReOrder.ProductID) {
                                    productAction.GetProductByID(item.ProductID, new ProductCallBack() {
                                        @Override
                                        public void onSuccess(Product productLoad) {
                                            product = productLoad;
                                        }

                                        @Override
                                        public void onError(String error) {
                                        }
                                    });
                                    double productPrice = product.Price;
                                    cartDetailAction.updateQuantityReOrder(item.ProductID, item.Quantity + itemReOrder.Quantity, myCart.CartID, (item.Quantity + itemReOrder.Quantity) * productPrice, new MessageCallback() {
                                        @Override
                                        public void onSuccess(String message) {

                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                cartDetailAction.addCartDetail(myCart.CartID, itemReOrder.ProductID, itemReOrder.Quantity, itemReOrder.Total, new MessageCallback() {
                                    @Override
                                    public void onSuccess(String message) {

                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                            }
                        }
                    }

                }
                Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }

}
