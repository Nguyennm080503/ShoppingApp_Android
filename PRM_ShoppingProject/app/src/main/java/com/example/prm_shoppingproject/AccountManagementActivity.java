package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Model.Account;

import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAccounts;
    private AccountAdapter accountAdapter;
    private AccountAction accountAction;
    private List<Account> accountList;
    private ImageView backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        recyclerViewAccounts = findViewById(R.id.recyclerViewAccounts);
        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(this));
        accountAction = new AccountAction(AccountManagementActivity.this);
        backHome = findViewById(R.id.back_home);
        accountList = accountAction.getAllAccount();


        accountAdapter = new AccountAdapter(accountList, this);
        recyclerViewAccounts.setAdapter(accountAdapter);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountManagementActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
