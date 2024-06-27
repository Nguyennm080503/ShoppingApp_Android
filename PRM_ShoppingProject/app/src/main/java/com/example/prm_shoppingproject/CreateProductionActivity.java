package com.example.prm_shoppingproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.io.IOException;

public class CreateProductionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION = 100;
    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private EditText editTextProductDescription;
    private EditText editTextProductTypeID;
    private EditText editTextProductStatus;
    private ImageView imageViewProduct;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductTypeID = findViewById(R.id.editTextProductTypeID);
        editTextProductStatus = findViewById(R.id.editTextProductStatus);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonSaveProduct = findViewById(R.id.buttonSaveProduct);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        buttonSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageViewProduct.setImageBitmap(bitmap);
                imageBytes = ImageUtil.getBytesFromBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProduct() {
        String name = editTextProductName.getText().toString().trim();
        String priceString = editTextProductPrice.getText().toString().trim();
        String description = editTextProductDescription.getText().toString().trim();
        String typeIDString = editTextProductTypeID.getText().toString().trim();
        String statusString = editTextProductStatus.getText().toString().trim();

        if (name.isEmpty() || priceString.isEmpty() || description.isEmpty() || typeIDString.isEmpty() || statusString.isEmpty() || imageBytes == null) {
            Toast.makeText(this, "Please fill out all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);
        int typeID = Integer.parseInt(typeIDString);
        int status = Integer.parseInt(statusString);

        Product product = new Product(0, name, price, imageBytes, description, typeID, status);

        ProductAction productAction = ProductAction.getInstance(this);
        productAction.open();
        productAction.addProduct(product.getName(), product.getPrice(), product.getImage(), product.getDescription(), product.getTypeID());
        productAction.close();


        Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
