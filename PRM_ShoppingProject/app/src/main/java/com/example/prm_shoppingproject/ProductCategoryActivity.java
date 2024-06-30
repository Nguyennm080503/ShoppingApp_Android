package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shoppingproject.Action.AccountAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Action.TypeAction;
import com.example.prm_shoppingproject.Model.Account;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Model.TypeProduct;

import java.util.List;

public class ProductCategoryActivity extends AppCompatActivity implements ProductAdapter.OnNumberCartChangeListener{
    private RecyclerView recyclerView;
    private ProductAction productAction;
    private TypeAction typeAction;
    private ProductAdapter productAdapter;
    private ImageView backHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_category), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        productAction = new ProductAction(ProductCategoryActivity.this);
        typeAction = new TypeAction(ProductCategoryActivity.this);
        int category = intent.getIntExtra("categoryID", -1);
        TypeProduct typeProduct = typeAction.GetTypeProductByID(category);
        List<Product> products = productAction.getAllProductsByCategory(category);
        TextView categoryName = findViewById(R.id.category_name);
        categoryName.setText(typeProduct.TypeName);
        recyclerView = findViewById(R.id.productView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        backHome = findViewById(R.id.back_home);

        productAdapter = new ProductAdapter(this, products, this);
        recyclerView.setAdapter(productAdapter);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductCategoryActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onNumberCartChanged(int numberItemInCart){
    }
}
