package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Interface.Account.AccountListCallback;
import com.example.prm_shoppingproject.Model.Account;

import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAccounts;
    private AccountAdapter accountAdapter;
    private AccountAction accountAction;
    private ImageView backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        recyclerViewAccounts = findViewById(R.id.recyclerViewAccounts);
        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(this));
        accountAction = new AccountAction(AccountManagementActivity.this);
        backHome = findViewById(R.id.back_home);
        accountAction.getAllAccounts(new AccountListCallback() {
            @Override
            public void onSuccess(List<Account> accounts) {
                accountAdapter = new AccountAdapter(accounts, AccountManagementActivity.this);
                recyclerViewAccounts.setAdapter(accountAdapter);
                Toast.makeText(AccountManagementActivity.this, "Accounts loaded successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AccountManagementActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });


        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManagementActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
