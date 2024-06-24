package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Model.Account;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton, loginButton;
    private AccountAction accountAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.register_name);
        emailEditText = findViewById(R.id.register_email);
        phoneEditText = findViewById(R.id.register_phone);
        usernameEditText = findViewById(R.id.register_username);
        passwordEditText = findViewById(R.id.register_password);
        confirmPasswordEditText = findViewById(R.id.register_confirm_password);
        registerButton = findViewById(R.id.register_submit_button);
        loginButton = findViewById(R.id.login_button);
        accountAction = new AccountAction(RegisterActivity.this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                Account account = accountAction.GetUsernameExisted(username);
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                }
                else if(account == null){
                    Toast.makeText(RegisterActivity.this, "Userame is existed!", Toast.LENGTH_SHORT).show();
                    performRegistration(name, email, phone, username, password);
                }
                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                    performRegistration(name, email, phone, username, password);
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performRegistration(String name ,String email, String phone, String username, String password) {
        try{
            accountAction.addAccount(name, email, phone, username, password);
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
