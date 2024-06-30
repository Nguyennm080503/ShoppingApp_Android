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

import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private SharedPreferences sharedPreferences;

    public ProductManagementAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
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
        holder.textViewProductName.setText(product.Name);
        holder.textViewProductPrice.setText(String.format("$%.2f", product.Price));

        Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.Image);
        holder.imageViewProduct.setImageBitmap(bitmap);

        holder.btnProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productID = product.ProductID;
                Toast.makeText(context, "Product ID: " + productID, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ProductDetailManagementActivity.class);
                intent.putExtra("productID", productID);
                context.startActivity(intent);
            }
        });

//        holder.btnBuy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int productID = product.ProductID;
//                ArrayList<CartItem> cartItems = getCartItems();
//                boolean found = false;
//                for (CartItem item : cartItems) {
//                    if (item.getProductID() == productID) {
//                        item.setQuantity(item.getQuantity() + 1);
//                        found = true;
//                        break;
//                    }
//                }
//
//                if (!found) {
//                    cartItems.add(new CartItem(productID, 1));
//                }
//                saveCartItems(cartItems);
//                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName;
        TextView textViewProductPrice;
        Button btnProductDetail;
        Button btnDeleleteProduct;

        Button btnBuy;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            btnProductDetail = itemView.findViewById(R.id.btnProductDetail);
//            productDetail = itemView.findViewById(R.id.product_detail);
//            btnBuy = itemView.findViewById(R.id.btn_addtoCard);
        }
    }
}
