package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Model.Cart;

import java.util.List;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.OrderViewHolder>{
    private Context context;
    private List<Cart> cartList;
    private SharedPreferences sharedPreferences;

    public OrderManagementAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public OrderManagementAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new OrderManagementAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.orderID.setText(cart.CartID);
        holder.orderDate.setText(cart.OrderDate.toString());
        holder.totalPrice.setText(String.format("$%.2f", cart.Total));
        holder.status.setText(getStringStatus(cart.Status));

        holder.orderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orderID = cart.CartID;
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("cartID", orderID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderID, orderDate, totalPrice, status;
        LinearLayout orderDetail;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            orderDate = itemView.findViewById(R.id.orderDate);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            status = itemView.findViewById(R.id.status);
            orderDetail = itemView.findViewById(R.id.detail_order);
        }
    }

    private String getStringStatus(int status){
        switch (status){
            case 1:
                return "Payment success!";
            case 2:
                return "Payment fail!";
            case 3:
                return "Preparing order!";
            case 4:
                return "Delivering order!";
            case 5:
                return "Delivery success";
            default:
                return "Cancel order!";
        }
    }
}
