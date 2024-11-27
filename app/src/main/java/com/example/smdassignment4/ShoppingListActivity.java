package com.example.smdassignment4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private FloatingActionButton fab, logoutFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Initialize FirebaseAuth instance
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();  // Get current user's ID

        // Reference to the user's items in Firebase Realtime Database
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("items");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup FirebaseRecyclerOptions with query to retrieve only this user's items
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(itemsRef, Item.class)
                .build();

        // Create and set the adapter
        adapter = new ShoppingListAdapter(options);
        recyclerView.setAdapter(adapter);

        // Setup Floating Action Button to navigate to AddItemActivity
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ShoppingListActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        // Setup Logout FAB to log the user out and redirect to the Login Activity
        logoutFab = findViewById(R.id.logoutFab);
        logoutFab.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();  // Log the user out
            Toast.makeText(ShoppingListActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to the Login Activity
            Intent intent = new Intent(ShoppingListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
            startActivity(intent);
            finish();  // Close the current activity
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening for data changes
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening for data changes when the activity is stopped
        adapter.stopListening();
    }
}
