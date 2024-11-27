package com.example.smdassignment4;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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

        // Initialize FirebaseAuth instance to get the current user's ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();  // Get current user's ID

        // Initialize Firebase Database reference specific to this user's shopping list
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("items");

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

            // Store the item under the authenticated user's node in Firebase Realtime Database
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
