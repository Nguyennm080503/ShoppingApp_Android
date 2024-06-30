package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailListCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailSumCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnNumberCartChangeListener {
        void onNumberCartChanged(int numberItemInCart);
    }
    private Context context;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private Cart cart, cartPending;
    private int numberItemCart = 0;
    private CartDetail cartDetail;
    private double sum;

    private OnNumberCartChangeListener onNumberCartChangeListener;


    public ProductAdapter(Context context, List<Product> productList, OnNumberCartChangeListener onNumberCartChangeListener) {
        this.context = context;
        this.productList = productList;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        this.onNumberCartChangeListener = onNumberCartChangeListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewProductName.setText(product.Name);
        holder.textViewProductPrice.setText(String.format("$%.2f", product.Price));
        cartAction = new CartAction(this.context);
        cartDetailAction = new CartDetailAction(this.context);
        Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.Image);
        holder.imageViewProduct.setImageBitmap(bitmap);
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);

        holder.productDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productID = product.ProductID;
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productID", productID);
                context.startActivity(intent);
            }
        });

        holder.btnBuy.setOnClickListener(new View.OnClickListener() {
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
                        notifyNumberCartChanged(cart.CartID);
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
                        notifyNumberCartChanged(cartPending.CartID);
                    }
                }
                else{

                    cartAction.addCart(accountIDLogin, (product.Price * 1) + 2 ,"", new MessageCallback() {
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
                            notifyNumberCartChanged(cartPending.CartID);
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                }
                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void notifyNumberCartChanged(int cartID) {
        cartDetailAction.getAllCartDetailByOrder(cartID, new CartDetailListCallBack() {
            @Override
            public void onSuccess(List<CartDetail> cartList) {
                numberItemCart = cartList.size();
            }

            @Override
            public void onError(String error) {

            }
        });
        if (onNumberCartChangeListener != null) {
            onNumberCartChangeListener.onNumberCartChanged(numberItemCart);
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        LinearLayout productDetail;

        Button btnBuy;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            productDetail = itemView.findViewById(R.id.product_detail);
            btnBuy = itemView.findViewById(R.id.btn_addtoCard);
        }
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
