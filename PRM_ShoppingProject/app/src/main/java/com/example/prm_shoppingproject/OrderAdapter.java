package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Model.Cart;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private SharedPreferences sharedPreferences;

    public OrderAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        try{
            Cart cart = cartList.get(position);
            holder.orderID.setText(String.valueOf(cart.CartID));
            holder.orderDate.setText(cart.OrderDate.toString());
            holder.totalPrice.setText(String.format("$%.2f", cart.TotalBill));
            holder.status.setText(getStringStatus(cart.Status));

            if (cart.Status == 1) {
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
            } else if(cart.Status == 2)  {
                holder.status.setTextColor(context.getResources().getColor(R.color.red));
            } else if(cart.Status == 3){
                holder.status.setTextColor(context.getResources().getColor(R.color.orange));
            } else if(cart.Status == 4){
                holder.status.setTextColor(context.getResources().getColor(R.color.blue));
            }
            else if(cart.Status == 5){
                holder.status.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.orderDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int orderID = cart.CartID;
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("cartID", orderID);
                    context.startActivity(intent);
                }
            });
        }catch (Exception ex){
            Log.d(String.valueOf(this.context), ex.toString());
        }
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
