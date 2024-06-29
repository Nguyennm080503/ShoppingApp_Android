package com.example.prm_shoppingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.ProductAction;
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
        productList = productAction.getAllProducts();


        productAdapter = new ProductAdapter(this, productList, this);
        recyclerViewProducts.setAdapter(productAdapter);

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
