package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Product.ProductListCallBack;
import com.example.prm_shoppingproject.Model.Product;

import java.util.List;

public class ProductManagementActivity extends AppCompatActivity implements ProductAdapter.OnNumberCartChangeListener{

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private ProductAction productAction;
    private List<Product> productList;
    private ImageView backHome;
    private Button addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        recyclerViewProducts = findViewById(R.id.recyclerViewProduct);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productAction = new ProductAction(ProductManagementActivity.this);
        backHome = findViewById(R.id.back_home);
        addProduct = findViewById(R.id.btnAddProduct);
        productAction.getAllProducts(new ProductListCallBack() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter = new ProductAdapter(ProductManagementActivity.this, productList, ProductManagementActivity.this::onNumberCartChanged);
                recyclerViewProducts.setAdapter(productAdapter);
                Toast.makeText(ProductManagementActivity.this, "Products loaded successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProductManagementActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductManagementActivity.this, CreateProductionActivity.class);
                startActivity(intent);
            }
        });
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductManagementActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onNumberCartChanged(int numberItemInCart){
    }

}
