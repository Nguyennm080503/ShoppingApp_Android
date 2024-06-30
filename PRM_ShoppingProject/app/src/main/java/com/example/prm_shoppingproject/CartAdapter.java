package com.example.prm_shoppingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.CartAction;
import com.example.prm_shoppingproject.Action.CartDetailAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailCallBack;
import com.example.prm_shoppingproject.Interface.CartDetail.CartDetailSumCallBack;
import com.example.prm_shoppingproject.Interface.Product.ProductCallBack;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.CartDetail;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    public interface OnQuantityChangeListener {
        void onQuantityChanged(double totalPrice);
    }

    public interface OnCartEmptyListener {
        void onCartEmpty();
    }
    private Context context;
    private List<CartProduct> productCartList;
    private ProductAction productAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private OnQuantityChangeListener onQuantityChangeListener;
    private OnCartEmptyListener onCartEmptyListener;
    private Product product;
    private Cart cart;
    private CartDetail cartDetail;
    private double sum;


    public CartAdapter(Context context, List<CartProduct> productCartList, OnQuantityChangeListener onQuantityChangeListener, OnCartEmptyListener onCartEmptyListener) {
        this.context = context;
        this.productCartList = productCartList;
        this.onQuantityChangeListener = onQuantityChangeListener;
        this.onCartEmptyListener = onCartEmptyListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            CartProduct cartProduct = productCartList.get(position);
            holder.textViewProductName.setText(cartProduct.ProductName);
            holder.textViewProductPrice.setText(String.format("$%.2f", cartProduct.Price));
            holder.quantity.setText(String.valueOf(cartProduct.Quantity));
            int productId = cartProduct.ProductID;
            productAction = new ProductAction(this.context);
            cartAction = new CartAction(this.context);
            cartDetailAction = new CartDetailAction(this.context);
            productAction.GetProductByID(productId, new ProductCallBack() {
                @Override
                public void onSuccess(Product productLoad) {
                    product = productLoad;
                }

                @Override
                public void onError(String error) {
                }
            });
            SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
            int accountIDLogin = sharedPreferences.getInt("accountID", -1);
            cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                @Override
                public void onSuccess(Cart cartLoad) {
                    cart = cartLoad;
                }

                @Override
                public void onError(String error) {

                }
            });
             cartDetailAction.getCartDetailItemStatus(cart.CartID, cartProduct.ProductID, new CartDetailCallBack() {
                @Override
                public void onSuccess(CartDetail cartDetailLoad) {
                    cartDetail = cartDetailLoad;
                }

                @Override
                public void onError(String error) {

                }
            });

            Bitmap bitmap = ImageUtil.convertBase64ToBitmap(cartProduct.Image);
            holder.imageViewProduct.setImageBitmap(bitmap);
            holder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                    if (currentQuantity > 1) {
                        cartProduct.Quantity = currentQuantity - 1;
                        holder.quantity.setText(String.valueOf(currentQuantity - 1));
                        cartDetailAction.updateQuantity(cartDetail.ProductID, -1, cartDetail.OrderID, cartDetail.Quantity, product.Price * cartProduct.Quantity, new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                        holder.textViewProductPrice.setText(String.format("$%.2f", product.Price * cartProduct.Quantity));
                        calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.OrderID);
                        notifyQuantityChanged();
                    }
                }
            });

            holder.btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                    cartProduct.Quantity = currentQuantity + 1;
                    holder.quantity.setText(String.valueOf(currentQuantity + 1));
                    cartDetailAction.updateQuantity(cartDetail.ProductID, 1, cartDetail.OrderID, cartDetail.Quantity, product.Price * cartProduct.Quantity, new MessageCallback() {
                        @Override
                        public void onSuccess(String message) {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                    holder.textViewProductPrice.setText(String.format("$%.2f", product.Price * cartProduct.Quantity));
                    calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.OrderID);
                    notifyQuantityChanged();
                }
            });

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position, cartDetail.OrderID);
                    if (productCartList.size() == 0){
                        cartAction.deleteCart(cartDetail.OrderID, new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {
                            }

                            @Override
                            public void onError(String error) {
                            }
                        });
                    }else{
                        calculateTotalPrice(cartAction, accountIDLogin, cartDetailAction, cartDetail.OrderID);
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("CartAdapter", "Exception in onBindViewHolder: " + ex.getMessage());
        }

    }

    private void removeItem(int position, int cartID) {
        CartProduct cartProduct = productCartList.get(position);
        cartDetailAction.deleteCartDetail(cartID, cartProduct.ProductID, new MessageCallback() {
            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onError(String error) {

            }
        });
        productCartList.remove(position);
        notifyItemRemoved(position);
        notifyQuantityChanged();
        if (productCartList.size() == 0) {
            if (onCartEmptyListener != null) {
                onCartEmptyListener.onCartEmpty();
            }
        }
    }

    private void notifyQuantityChanged() {
        double totalPrice = 0;
        for (CartProduct item : productCartList) {
            productAction.GetProductByID(item.ProductID, new ProductCallBack() {
                @Override
                public void onSuccess(Product productLoad) {
                    product = productLoad;
                }

                @Override
                public void onError(String error) {
                }
            });
            totalPrice += item.Quantity * product.Price;
        }
        if (onQuantityChangeListener != null) {
            onQuantityChangeListener.onQuantityChanged(totalPrice);
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

    public List<CartProduct> getCartProducts() {
        return productCartList;
    }


    @Override
    public int getItemCount() {
        return productCartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct, btn_delete;
        TextView textViewProductName, textViewProductPrice, btnPlus, btnMinus, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.productPrice);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            quantity = itemView.findViewById(R.id.quantity);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }


}
