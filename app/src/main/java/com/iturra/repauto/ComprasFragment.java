package com.iturra.repauto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ComprasFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CompraAdaptador compraAdaptador;
    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compras, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_compras);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userId = mAuth.getCurrentUser().getUid();

        Query query = mFirestore.collection("compra").whereEqualTo("idUsuario", userId);

        FirestoreRecyclerOptions<Compra> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Compra>().setQuery(query, Compra.class).build();

        compraAdaptador = new CompraAdaptador(firestoreRecyclerOptions, getActivity());
        recyclerView.setAdapter(compraAdaptador);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        compraAdaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        compraAdaptador.stopListening();
    }
}
