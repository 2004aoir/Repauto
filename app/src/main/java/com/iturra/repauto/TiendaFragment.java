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


public class TiendaFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProductoAdaptador ProductoAdaptador;
    private FirebaseFirestore mfirestore;

    public TiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        mfirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_tienda);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = mfirestore.collection("productos");

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();

        ProductoAdaptador = new ProductoAdaptador(firestoreRecyclerOptions, getActivity());
        recyclerView.setAdapter(ProductoAdaptador);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ProductoAdaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ProductoAdaptador.stopListening();
    }
}
