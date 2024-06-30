package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnNumberCartChangeListener {
        void onNumberCartChanged(int numberItemInCart);
    }

    private Context context;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;
    private OnNumberCartChangeListener onNumberCartChangeListener;

    public ProductAdapter(Context context, List<Product> productList, OnNumberCartChangeListener onNumberCartChangeListener) {
        this.context = context;
        this.productList = productList;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        this.onNumberCartChangeListener = onNumberCartChangeListener;
        this.cartAction = new CartAction(context);
        this.cartDetailAction = new CartDetailAction(context);
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
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        LinearLayout productDetail;
        Button btnBuy;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            productDetail = itemView.findViewById(R.id.product_detail);
            btnBuy = itemView.findViewById(R.id.btn_addtoCard);
        }

        void bind(Product product) {
            textViewProductName.setText(product.getName());
            textViewProductPrice.setText(String.format("$%.2f", product.getPrice()));

            Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.getImage());
            if (bitmap != null) {
                imageViewProduct.setImageBitmap(bitmap);
            }

            productDetail.setOnClickListener(v -> {
                int productID = product.getProductID();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productID", productID);
                context.startActivity(intent);
            });

            btnBuy.setOnClickListener(v -> {
                int productID = product.getProductID();
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
                        Toast.makeText(context, "Failed to load cart item information", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Failed to create new cart", Toast.LENGTH_SHORT).show();
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
                        notifyNumberCartChanged(cartID);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(context, "Failed to update cart detail", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                cartDetailAction.addCartDetail(cartID, productID, 1, product.getPrice() * 1, new MessageCallback() {
                    @Override
                    public void onSuccess(String message) {
                        notifyNumberCartChanged(cartID);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Failed to calculate total price", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateTotalCart(CartAction cartAction, int accountIDLogin, double sum) {
            cartAction.updateTotalCart(accountIDLogin, sum + 2, new MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "Cart updated successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Failed to update cart total", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void notifyNumberCartChanged(int cartID) {
            cartDetailAction.getAllCartDetailByOrder(cartID, new CartDetailListCallBack() {
                @Override
                public void onSuccess(List<CartDetail> cartList) {
                    if (onNumberCartChangeListener != null) {
                        onNumberCartChangeListener.onNumberCartChanged(cartList.size());
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Failed to retrieve cart details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
