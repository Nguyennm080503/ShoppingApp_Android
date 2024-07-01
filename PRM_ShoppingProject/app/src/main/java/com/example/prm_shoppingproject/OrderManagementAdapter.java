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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderManagementAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.orderID.setText(String.valueOf(cart.CartID));
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        }
        String orderDateTrimmed = cart.OrderDate.substring(0, 19);
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.parse(orderDateTrimmed, formatter);
        }
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        holder.orderDate.setText(formattedDate);
        holder.totalPrice.setText(String.format("$%.2f", cart.TotalBill));
        holder.status.setText(getStringStatus(cart.Status));

        switch (cart.Status) {
            case 1:
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case 2:
                holder.status.setTextColor(context.getResources().getColor(R.color.red));
                break;
            case 3:
                holder.status.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case 4:
                holder.status.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            case 5:
                holder.status.setTextColor(context.getResources().getColor(R.color.black));
                break;
            default:
                holder.status.setTextColor(context.getResources().getColor(R.color.black));
                break;
        }

        holder.orderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orderID = cart.CartID;
                Intent intent = new Intent(context, OrderDetailManagementActivity.class);
                intent.putExtra("cartID", orderID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
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
