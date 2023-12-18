package com.iturra.repauto;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CarritoFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CarritoAdaptador carritoAdaptador;
    private FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_carrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userId = mAuth.getCurrentUser().getUid();

        Query query = mFirestore.collection("carrito").whereEqualTo("idUsuario", userId);

        FirestoreRecyclerOptions<Carrito> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Carrito>().setQuery(query, Carrito.class).build();

        carritoAdaptador = new CarritoAdaptador(firestoreRecyclerOptions, getActivity());
        recyclerView.setAdapter(carritoAdaptador);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        carritoAdaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        carritoAdaptador.stopListening();
    }
}
