package com.example.prm_shoppingproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.prm_shoppingproject.Model.CartProduct;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.util.List;

public class OrderDetailManagementAdapter extends RecyclerView.Adapter<OrderDetailManagementAdapter.OrderDetailManagement>{
    private Context context;
    private List<CartProduct> productCartList;
    private ProductAction productAction;
    private CartAction cartAction;
    private CartDetailAction cartDetailAction;


    public OrderDetailManagementAdapter(Context context, List<CartProduct> productCartList) {
        this.context = context;
        this.productCartList = productCartList;
    }

    @NonNull
    @Override
    public OrderDetailManagementAdapter.OrderDetailManagement onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_management, parent, false);
        return new OrderDetailManagementAdapter.OrderDetailManagement(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailManagement holder, int position) {
        try {
            CartProduct cartProduct = productCartList.get(position);
            holder.textViewProductName.setText(cartProduct.ProductName);
            holder.textViewProductPrice.setText(String.format("$%.2f", cartProduct.PriceTotal));
            holder.quantity.setText(String.valueOf(cartProduct.Quantity));
            productAction = new ProductAction(this.context);
            cartAction = new CartAction(this.context);
            cartDetailAction = new CartDetailAction(this.context);

            Bitmap bitmap = ImageUtil.convertBase64ToBitmap(cartProduct.Image);
            holder.imageViewProduct.setImageBitmap(bitmap);

        }catch (Exception ex){
            ex.printStackTrace();
            // Log the exception
            Log.e("CartAdapter", "Exception in onBindViewHolder: " + ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return productCartList != null ? productCartList.size() : 0;
    }

    static class OrderDetailManagement extends RecyclerView.ViewHolder {

        ImageView imageViewProduct;
        TextView textViewProductName, textViewProductPrice, quantity;

        public OrderDetailManagement(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
