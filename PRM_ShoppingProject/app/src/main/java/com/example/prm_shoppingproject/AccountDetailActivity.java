package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Model.Account;

public class AccountDetailActivity extends AppCompatActivity {
    private AccountAction accountAction;
    private Account account;
    private Button btnChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account_detail_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        accountAction = new AccountAction(AccountDetailActivity.this);

        TextView name = findViewById(R.id.name);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);
        TextView username = findViewById(R.id.username);
        TextView status = findViewById(R.id.status);
        ImageView backHome = findViewById(R.id.back_home);
        btnChangeStatus = findViewById(R.id.btnChangeStatus);
        int accountID = getIntent().getIntExtra("accountID", -1);
        account = accountAction.GetAccountByID(accountID);

        name.setText(account.Name);
        phone.setText(account.Phone);
        email.setText(account.Email);
        username.setText(account.Username);
        status.setText(account.Status == 1 ? "Block" : "Active");

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
                Account account_change = accountAction.GetAccountByID(accountID);
                int status_change = -1;
                if (account_change.Status == 0) {
                    status_change = 1;
                    accountAction.updateAccountStatus(accountID, status_change);
                } else if (account_change.Status == 1) {
                    status_change = 0;
                    accountAction.updateAccountStatus(accountID, status_change);
                }
                String message = "Status changed to " + (status_change == 1 ? "Block" : "Active");
                Toast.makeText(AccountDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                status.setText(status_change == 1 ? "Block" : "Active");
            }
        });
    }
}
