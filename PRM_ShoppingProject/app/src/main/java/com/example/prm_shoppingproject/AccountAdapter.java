package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Model.Account;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountList;
    private Context context;

    public AccountAdapter(List<Account> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.textViewUsername.setText(account.Username);
        holder.textViewEmail.setText(account.Email);
        holder.textViewStatus.setText(account.Status == 0 ? "Active" : "Block");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountDetailActivity.class);
                intent.putExtra("accountID", account.AccountID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername, textViewEmail, textViewStatus;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
