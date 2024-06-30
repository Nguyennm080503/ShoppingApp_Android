package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

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
    private Cart cart;
    private double sum;

    public CartAdapter(Context context, List<CartProduct> productCartList,
                       OnQuantityChangeListener onQuantityChangeListener,
                       OnCartEmptyListener onCartEmptyListener) {
        this.context = context;
        this.productCartList = productCartList;
        this.onQuantityChangeListener = onQuantityChangeListener;
        this.onCartEmptyListener = onCartEmptyListener;
        productAction = new ProductAction(context);
        cartAction = new CartAction(context);
        cartDetailAction = new CartDetailAction(context);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        try {
            CartProduct cartProduct = productCartList.get(position);
            holder.bind(cartProduct);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("CartAdapter", "Exception in onBindViewHolder: " + ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return productCartList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

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

        public void bind(CartProduct cartProduct) {
            textViewProductName.setText(cartProduct.ProductName);
            textViewProductPrice.setText(String.format("$%.2f", cartProduct.Price));
            quantity.setText(String.valueOf(cartProduct.Quantity));

            // Load product image asynchronously
            Bitmap bitmap = ImageUtil.convertBase64ToBitmap(cartProduct.Image);
            imageViewProduct.setImageBitmap(bitmap);

            // Button listeners
            btnMinus.setOnClickListener(v -> handleQuantityChange(cartProduct, -1));
            btnPlus.setOnClickListener(v -> handleQuantityChange(cartProduct, 1));
            btn_delete.setOnClickListener(v -> removeCartItem(getAdapterPosition(), cartProduct));
        }

        private void handleQuantityChange(CartProduct cartProduct, int change) {
            int currentQuantity = cartProduct.Quantity;
            if (change > 0 || currentQuantity > 1) {
                int newQuantity = currentQuantity + change;
                cartProduct.Quantity = newQuantity;
                quantity.setText(String.valueOf(newQuantity));

                // Update quantity in database
                updateCartDetail(cartProduct, change);

                // Update price and notify listeners
                double newPrice = cartProduct.Price * newQuantity;
                textViewProductPrice.setText(String.format("$%.2f", newPrice));
                notifyQuantityChanged();
            }
        }

        private void updateCartDetail(CartProduct cartProduct, int change) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
            int accountIDLogin = sharedPreferences.getInt("accountID", -1);
            cartAction.getCartPendingByAccountID(accountIDLogin, new CartCallBack() {
                @Override
                public void onSuccess(Cart cartLoad) {
                    cart = cartLoad;
                    cartDetailAction.getCartDetailItemStatus(cart.CartID, cartProduct.ProductID, new CartDetailCallBack() {
                        @Override
                        public void onSuccess(CartDetail cartDetailLoad) {
                            cartDetailAction.updateQuantity(cartDetailLoad.ProductID, change, cartDetailLoad.OrderID, cartDetailLoad.Quantity, cartProduct.Price * cartProduct.Quantity, new MessageCallback() {
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

                @Override
                public void onError(String error) {
                }
            });
        }

        private void removeCartItem(int position, CartProduct cartProduct) {
            int cartID = cart != null ? cart.CartID : 0;
            cartDetailAction.deleteCartDetail(cartID, cartProduct.ProductID, new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    productCartList.remove(position);
                    notifyItemRemoved(position);
                    notifyQuantityChanged();

                    if (productCartList.isEmpty() && onCartEmptyListener != null) {
                        onCartEmptyListener.onCartEmpty();
                    }
                }

                @Override
                public void onError(String error) {
                }
            });
        }
    }

    private void notifyQuantityChanged() {
        double totalPrice = 0;
        for (CartProduct item : productCartList) {
            totalPrice += item.Quantity * item.Price;
        }
        if (onQuantityChangeListener != null) {
            onQuantityChangeListener.onQuantityChanged(totalPrice);
        }
    }
}
