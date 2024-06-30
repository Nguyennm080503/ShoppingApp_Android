package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Model.Account;

public class AccountDetailActivity extends AppCompatActivity {
    private AccountAction accountAction;
    private Button btnChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        accountAction = new AccountAction(AccountDetailActivity.this);

        TextView name = findViewById(R.id.name);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);
        TextView username = findViewById(R.id.username);
        TextView status = findViewById(R.id.status);
        ImageView backHome = findViewById(R.id.back_home);
        btnChangeStatus = findViewById(R.id.btnChangeStatus);

        int accountID = getIntent().getIntExtra("accountID", -1);

        accountAction.getAccountProfile(accountID, new AccountCallback() {
            @Override
            public void onSuccess(Account account) {
                Toast.makeText(AccountDetailActivity.this, "Account loaded successfully!", Toast.LENGTH_SHORT).show();
                name.setText(account.Name);
                phone.setText(account.Phone);
                email.setText(account.Email);
                username.setText(account.Username);
                status.setText(account.Status == 1 ? "Block" : "Active");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AccountDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountDetailActivity.this, AccountManagementActivity.class);
                startActivity(intent);
            }
        });

        btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountAction.getAccountProfile(accountID, new AccountCallback() {
                    @Override
                    public void onSuccess(Account account) {
                        int newStatus = (account.Status == 0) ? 1 : 0;

                        accountAction.updateAccountStatus(accountID, newStatus, new MessageCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(AccountDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                status.setText(newStatus == 1 ? "Block" : "Active");
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AccountDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AccountDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
