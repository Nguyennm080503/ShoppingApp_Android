package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.TypeAction;
import com.example.prm_shoppingproject.Interface.Account.AccountCallback;
import com.example.prm_shoppingproject.Model.Account;

public class MainActivity extends AppCompatActivity {

    private AccountAction accountAction;
    private TypeAction typeAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        accountAction = new AccountAction(MainActivity.this);
        typeAction = new TypeAction(MainActivity.this);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    accountAction.GetAccountIDLogin(username, password, new AccountCallback() {
                        @Override
                        public void onSuccess(Account account) {
                            SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (account.AccountID != 0 && account.RoleID == 1 && account.Status == 0) {
                                Toast.makeText(MainActivity.this, "Login to account successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                editor.putInt("accountID", account.AccountID);
                                editor.apply();
                                startActivity(intent);
                            } else if (account.AccountID != 0 && account.RoleID == 0 && account.Status == 0) {
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                                editor.putInt("accountID", account.AccountID);
                                editor.apply();
                                startActivity(intent);
                            } else if (account.Status == 1) {
                                Toast.makeText(MainActivity.this, "Your account is blocked!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
