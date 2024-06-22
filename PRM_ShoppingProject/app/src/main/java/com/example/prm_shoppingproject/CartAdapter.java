package com.example.prm_shoppingproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Model.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private Context context;
    private List<CartProduct> productCartList;
    private ProductAction productAction;

    public CartAdapter(Context context, List<CartProduct> productCartList) {
        this.context = context;
        this.productCartList = productCartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartProduct cartProduct = productCartList.get(position);
        holder.textViewProductName.setText(cartProduct.ProductName);
        holder.textViewProductPrice.setText(String.format("$%.2f", cartProduct.Price));
        int productId = cartProduct.ProductID;
        productAction = new ProductAction(this.context);
        Product product = productAction.GetProductByID(productId);

        Bitmap bitmap = BitmapFactory.decodeByteArray(cartProduct.Image, 0, cartProduct.Image.length);
        holder.imageViewProduct.setImageBitmap(bitmap);

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (currentQuantity > 1) {
                    holder.quantity.setText(String.valueOf(currentQuantity - 1));
                    cartProduct.Price = product.Price * (currentQuantity - 1);
                    holder.textViewProductPrice.setText(String.format("$%.2f", product.Price * currentQuantity - 1));
                    calculateTotalPrice();
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                holder.quantity.setText(String.valueOf(currentQuantity + 1));
                cartProduct.Price = product.Price * (currentQuantity + 1);
                holder.textViewProductPrice.setText(String.format("$%.2f", product.Price * currentQuantity + 1));
                calculateTotalPrice();
            }
        });
    }

    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartProduct cartProduct : productCartList) {
            totalPrice += cartProduct.Price;
        }
        return totalPrice;
    }

    @Override
    public int getItemCount() {
        return productCartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName, textViewProductPrice, btnPlus, btnMinus, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
