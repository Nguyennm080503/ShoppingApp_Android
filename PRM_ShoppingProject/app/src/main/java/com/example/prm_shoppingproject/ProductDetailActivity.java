package com.example.prm_shoppingproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductAction productAction;
    private int productID;
    ImageView imageProduct, backHome;
    TextView nameProduct, price, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productdetail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productAction = new ProductAction(ProductDetailActivity.this);
        Intent intent = getIntent();
        productID = intent.getIntExtra("productID", -1);
        Product product = productAction.GetProductByID(productID);

        imageProduct = findViewById(R.id.image_product);
        nameProduct = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        description = findViewById(R.id.description_product);
        backHome = findViewById(R.id.back_home);

        if (product != null) {
            nameProduct.setText(product.getName());
            price.setText(String.format("$%.2f", product.getPrice()));
            description.setText(product.getDescription());

            if (product.Image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(product.Image, 0, product.Image.length);
                imageProduct.setImageBitmap(bitmap);
            }
        }

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
