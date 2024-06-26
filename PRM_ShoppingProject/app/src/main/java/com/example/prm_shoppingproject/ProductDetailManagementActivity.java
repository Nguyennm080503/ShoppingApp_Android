package com.example.prm_shoppingproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_shoppingproject.Action.CategoryAction;
import com.example.prm_shoppingproject.Action.ProductAction;
import com.example.prm_shoppingproject.Interface.Account.MessageCallback;
import com.example.prm_shoppingproject.Interface.Product.ProductCallBack;
import com.example.prm_shoppingproject.Interface.Product.ProductListCallBack;
import com.example.prm_shoppingproject.Interface.TypeProduct.TypeProductListCallBack;
import com.example.prm_shoppingproject.Model.Product;
import com.example.prm_shoppingproject.Model.ProductUpdate;
import com.example.prm_shoppingproject.Model.TypeProduct;
import com.example.prm_shoppingproject.Util.ImageUtil;

import java.io.IOException;
import java.util.List;

public class ProductDetailManagementActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProductAction productAction;
    private CategoryAction typeProductionAction;
    private int productID;
    ImageView imageProduct, backHome;
    TextView nameProduct, price, description, categroyName;

    EditText editNameProduct, editPrice, editDescription;
    LinearLayout originalLayout, editLayout;
    Spinner spinnerCategory;
    AppCompatButton btnEdit;
    private Product product;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailManagement), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productAction = new ProductAction(ProductDetailManagementActivity.this);
        typeProductionAction = new CategoryAction(ProductDetailManagementActivity.this);
        Intent intent = getIntent();
        productID = intent.getIntExtra("productID", -1);
        productAction.GetProductByID(productID, new ProductCallBack() {
            @Override
            public void onSuccess(Product productLoad) {
                product = productLoad;
                if (product != null) {
                    nameProduct.setText(product.getName());
                    price.setText(String.format("$%.2f", product.getPrice()));
                    description.setText(product.getDescription());
                    categroyName.setText(product.getTypeName());

                    if (product.Image != null) {
                        Bitmap bitmap = ImageUtil.convertBase64ToBitmap(product.Image);
                        imageProduct.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onError(String error) {
            }
        });

        imageProduct = findViewById(R.id.image_product);
        nameProduct = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        description = findViewById(R.id.description_product);
        categroyName = findViewById(R.id.category_product);
        backHome = findViewById(R.id.back_home);

        editNameProduct = findViewById(R.id.edit_name_product);
        editPrice = findViewById(R.id.edit_price_product);
        editDescription = findViewById(R.id.edit_description_product);
        originalLayout = findViewById(R.id.original_layout);
        editLayout = findViewById(R.id.edit_layout);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnEdit = findViewById(R.id.btn_edit);


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editLayout.getVisibility() == View.GONE) {
                    nameProduct.setVisibility(View.GONE);
                    price.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    categroyName.setVisibility(View.GONE);

                    editNameProduct.setVisibility(View.VISIBLE);
                    editPrice.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.VISIBLE);

                    Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
                    buttonSelectImage.setVisibility(View.VISIBLE);

                    buttonSelectImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openImageChooser();
                        }
                    });



                    editNameProduct.setText(nameProduct.getText().toString());
                    editPrice.setText(price.getText().toString().substring(1)); // Remove the "$" sign
                    editDescription.setText(description.getText().toString());

                    typeProductionAction.getAllTypeProducts(new TypeProductListCallBack() {
                        @Override
                        public void onSuccess(List<TypeProduct> typeProducts) {
                            TypeProductAdapter adapter = new TypeProductAdapter(ProductDetailManagementActivity.this, typeProducts);
                            spinnerCategory.setAdapter(adapter);
                            spinnerCategory.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(ProductDetailManagementActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    editLayout.setVisibility(View.VISIBLE);
                    btnEdit.setText("Save");
                } else {
                    nameProduct.setText(editNameProduct.getText().toString());
                    price.setText(String.format("$%.2f", Float.parseFloat(editPrice.getText().toString())));
                    description.setText(editDescription.getText().toString());

                    nameProduct.setVisibility(View.VISIBLE);
                    price.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    categroyName.setVisibility(View.VISIBLE);

                    editNameProduct.setVisibility(View.GONE);
                    editPrice.setVisibility(View.GONE);
                    editDescription.setVisibility(View.GONE);
                    spinnerCategory.setVisibility(View.GONE);

                    editLayout.setVisibility(View.GONE);
                    btnEdit.setText("Edit");

                    // Here you can update the product in the database if needed
                    product.setName(editNameProduct.getText().toString());
                    product.setPrice(Float.parseFloat(editPrice.getText().toString()));
                    product.setDescription(editDescription.getText().toString());

                    TypeProduct selectedType = (TypeProduct) spinnerCategory.getSelectedItem();
                    Log.d("Selected Type", String.valueOf(selectedType.getTypeID()));

                    ProductUpdate productUpdate = new ProductUpdate();
                    productUpdate.ProductID = product.ProductID;
                    productUpdate.Description = product.Description;
                    productUpdate.Name = product.Name;
                    productUpdate.Price = product.Price;
                    productUpdate.TypeID = selectedType.TypeID;
                    productUpdate.Image = imageBytes;

                    productAction.UpdateProduct(productUpdate, new MessageCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(ProductDetailManagementActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(ProductDetailManagementActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }); // Assuming you have an update method in ProductAction
                }
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailManagementActivity.this, ProductManagementActivity.class);
                startActivity(intent);
            }
        });
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
                imageProduct.setImageBitmap(bitmap);
                imageBytes = ImageUtil.getBytesFromBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
