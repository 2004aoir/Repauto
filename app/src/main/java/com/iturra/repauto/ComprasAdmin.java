package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ComprasAdmin extends AppCompatActivity {
    RecyclerView recyclerView;
    CompraAdaptadorAdmin CompraAdaptadorAdmin;
    FirebaseFirestore mfirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras_admin);
        mfirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_compra_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = mfirestore.collection("compra");

        FirestoreRecyclerOptions<Compra> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Compra>().setQuery(query,Compra.class).build();
        CompraAdaptadorAdmin = new CompraAdaptadorAdmin(firestoreRecyclerOptions,this);
        CompraAdaptadorAdmin.notifyDataSetChanged();
        recyclerView.setAdapter(CompraAdaptadorAdmin);

    }

    @Override
    protected void onStart() {
        super.onStart();
        CompraAdaptadorAdmin.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CompraAdaptadorAdmin.startListening();
    }
}