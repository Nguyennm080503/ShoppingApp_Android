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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Interface.Cart.CartCallBack;
import com.example.prm_shoppingproject.Model.Cart;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductManagementAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductManagementAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_management, parent, false);
        return new ProductManagementAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        Button btnProductDetail;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            btnProductDetail = itemView.findViewById(R.id.btnProductDetail);
        }

        void bind(Product product) {
            textViewProductName.setText(product.getName());
            textViewProductPrice.setText(String.format("$%.2f", product.getPrice()));

            Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.getImage());
            if (bitmap != null) {
                imageViewProduct.setImageBitmap(bitmap);
            }

            btnProductDetail.setOnClickListener(v -> {
                int productID = product.getProductID();
                Intent intent = new Intent(context, ProductDetailManagementActivity.class);
                intent.putExtra("productID", productID);
                context.startActivity(intent);
            });
        }
    }
}
