package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderDetailAdapter cartAdapter;
    private ProductAction productAction;
    private CartDetailAction cartDetailAction;
    private CartAction cartAction;
    private List<CartProduct> productCartList;
    private List<CartDetail> cartItems;
    private AppCompatButton cancel;
    private TextView totalPrice, total, address;
    private ImageView backHome;
    private Cart cart, myCart, cartNew;

    @SuppressLint({"DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);
        initializeUIComponents();
        setupEdgeToEdge();
        setupRecyclerView();

        int orderID = getIntent().getIntExtra("cartID", -1);
        int accountIDLogin = getAccountIDFromSharedPreferences();

        loadCartDetails(orderID);
        setupCancelAction(orderID, accountIDLogin);

        backHome.setOnClickListener(v -> navigateToOrderActivity());
    }

    private void initializeUIComponents() {
        recyclerView = findViewById(R.id.cartView);
        totalPrice = findViewById(R.id.totalPrice);
        total = findViewById(R.id.total);
        address = findViewById(R.id.txt_addresss);
        backHome = findViewById(R.id.back_home);
        cancel = findViewById(R.id.btn_cancel);
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order_detail_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
    }

    private int getAccountIDFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("accountID", -1);
    }

    private void loadCartDetails(int orderID) {
        cartDetailAction = new CartDetailAction(OrderDetailActivity.this);
        cartAction = new CartAction(OrderDetailActivity.this);
        productAction = new ProductAction(OrderDetailActivity.this);

        cartAction.getCartByOrderID(orderID, new CartCallBack() {
            @Override
            public void onSuccess(Cart cartLoad) {
                cart = cartLoad;
                updateUIWithCartDetails();
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });

        cartDetailAction.getAllCartDetailByOrder(orderID, new CartDetailListCallBack() {
            @Override
            public void onSuccess(List<CartDetail> cartListLoad) {
                cartItems = cartListLoad;
                loadProductCartList();
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void updateUIWithCartDetails() {
        if (cart.Status != 3 && cart.Status != 4 && cart.Status != 5) {
            cancel.setBackgroundColor(getResources().getColor(R.color.red));
            cancel.setText("Cancel");
        } else {
            cancel.setBackgroundColor(getResources().getColor(R.color.green));
            cancel.setText("Re-order");
        }

        total.setText(String.format("$%.2f", cart.TotalBill));
        totalPrice.setText(String.format("$%.2f", cart.TotalBill - 2));
        address.setText(cart.Address);
    }

    private void loadProductCartList() {
        productCartList = new ArrayList<>();
        for (CartDetail item : cartItems) {
            CartProduct cartProduct = new CartProduct();
            productAction.GetProductByID(item.ProductID, new ProductCallBack() {
                @Override
                public void onSuccess(Product productLoad) {
                    updateCartProduct(cartProduct, productLoad, item);
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        }
        cartAdapter = new OrderDetailAdapter(this, productCartList);
        recyclerView.setAdapter(cartAdapter);
    }

    private void updateCartProduct(CartProduct cartProduct, Product productLoad, CartDetail item) {
        cartProduct.PriceTotal = productLoad.Price * item.Quantity;
        cartProduct.Image = productLoad.Image;
        cartProduct.ProductName = productLoad.Name;
        cartProduct.Quantity = item.Quantity;
        cartProduct.ProductID = item.ProductID;
        productCartList.add(cartProduct);
        cartAdapter.notifyDataSetChanged();
    }

    private void setupCancelAction(int orderID, int accountIDLogin) {
        cancel.setOnClickListener(v -> {
            if (cancel.getText().toString().equals("Cancel")) {
                cancelOrder();
            } else {
                reorderItems(orderID, accountIDLogin);
            }
            navigateToOrderActivity();
        });
    }

    private void cancelOrder() {
        cartAction.updateStatusCart(cart.CartID, 2, new MessageCallback() {
            @Override
            public void onSuccess(String message) {
                // Handle success
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void reorderItems(int orderID, int accountIDLogin) {
        cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
            @Override
            public void onSuccess(Cart cartLoad) {
                myCart = cartLoad;
                if (myCart.CartID == 0) {
                    createNewCartAndAddItems(accountIDLogin);
                } else {
                    addItemsToExistingCart();
                }
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void createNewCartAndAddItems(int accountIDLogin) {
        cartAction.addCart(accountIDLogin, cart.TotalBill, "", new MessageCallback() {
            @Override
            public void onSuccess(String message) {
                cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                    @Override
                    public void onSuccess(Cart cartLoad) {
                        cartNew = cartLoad;
                        addItemsToCart(cartNew.CartID);
                    }

                    @Override
                    public void onError(String error) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void addItemsToCart(int cartID) {
        for (CartDetail item : cartItems) {
            cartDetailAction.addCartDetail(cartID, item.ProductID, item.Quantity, item.Total, new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    // Handle success
                }

                @Override
                public void onError(String error) {
                    // Handle error
                }
            });
        }
    }

    private void addItemsToExistingCart() {
        cartDetailAction.getAllCartDetailByOrder(myCart.CartID, new CartDetailListCallBack() {
            @Override
            public void onSuccess(List<CartDetail> cartListLoad) {
                mergeCartDetails(cartListLoad);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void mergeCartDetails(List<CartDetail> existingCartDetails) {
        for (CartDetail itemReOrder : cartItems) {
            boolean found = false;
            for (CartDetail item : existingCartDetails) {
                if (item.ProductID == itemReOrder.ProductID) {
                    updateQuantityForReorderedItem(item, itemReOrder);
                    found = true;
                    break;
                }
            }
            if (!found) {
                addItemToCart(myCart.CartID, itemReOrder);
            }
        }
    }

    private void updateQuantityForReorderedItem(CartDetail existingItem, CartDetail newItem) {
        productAction.GetProductByID(existingItem.ProductID, new ProductCallBack() {
            @Override
            public void onSuccess(Product productLoad) {
                double productPrice = productLoad.Price;
                cartDetailAction.updateQuantityReOrder(existingItem.ProductID, existingItem.Quantity + newItem.Quantity, myCart.CartID, (existingItem.Quantity + newItem.Quantity) * productPrice, new MessageCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // Handle success
                    }

                    @Override
                    public void onError(String error) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void addItemToCart(int cartID, CartDetail item) {
        cartDetailAction.addCartDetail(cartID, item.ProductID, item.Quantity, item.Total, new MessageCallback() {
            @Override
            public void onSuccess(String message) {
                // Handle success
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }

    private void navigateToOrderActivity() {
        Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
        startActivity(intent);
    }
}
