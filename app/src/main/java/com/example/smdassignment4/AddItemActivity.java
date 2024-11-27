package com.example.smdassignment4;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameField, quantityField, priceField;
    private Button addItemButton;

    // Firebase Database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize UI components
        itemNameField = findViewById(R.id.etItemName);
        quantityField = findViewById(R.id.etQuantity);
        priceField = findViewById(R.id.etPrice);
        addItemButton = findViewById(R.id.btnAddItem);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Add Item Button Click Listener
        addItemButton.setOnClickListener(view -> {
            String itemName = itemNameField.getText().toString().trim();
            String quantityStr = quantityField.getText().toString().trim();
            String priceStr = priceField.getText().toString().trim();

            if (TextUtils.isEmpty(itemName)) {
                itemNameField.setError("Item Name is required");
                return;
            }
            if (TextUtils.isEmpty(quantityStr)) {
                quantityField.setError("Quantity is required");
                return;
            }
            if (TextUtils.isEmpty(priceStr)) {
                priceField.setError("Price is required");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            // Generate a unique ID for the item (Firebase automatically generates unique keys)
            String itemId = databaseReference.push().getKey();

            // Create an Item object
            Item item = new Item(itemName, quantity, price);

            // Store the item in Firebase Realtime Database
            if (itemId != null) {
                databaseReference.child(itemId).setValue(item)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddItemActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close the activity
                            } else {
                                Toast.makeText(AddItemActivity.this, "Failed to Add Item", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
