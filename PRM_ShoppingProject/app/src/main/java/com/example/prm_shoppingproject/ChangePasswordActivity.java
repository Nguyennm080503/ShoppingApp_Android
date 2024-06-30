package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Model.Account;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private AppCompatButton btnChangePassword;
    private ImageView back;
    private AccountAction accountAction;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        back = findViewById(R.id.back_home);
        accountAction = new AccountAction(ChangePasswordActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        int accountIDLogin = sharedPreferences.getInt("accountID", -1);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(accountAction, accountIDLogin);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changePassword(AccountAction accountAction, int accountID) {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        accountAction.getAccountProfile(accountID, new AccountCallback() {
            @Override
            public void onSuccess(Account accountLoad) {
                Toast.makeText(ChangePasswordActivity.this, "Account loaded successfully!", Toast.LENGTH_SHORT).show();
                account = accountLoad;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChangePasswordActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current Password is required");
            return;
        }

        if(!currentPassword.equals(account.Password)){
            etCurrentPassword.setError("Current Password is wrong!");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New Password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm Password is required");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }
        accountAction.updatePassword(accountID, newPassword, new MessageCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChangePasswordActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
