package com.iturra.repauto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class TiendaFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductoAdaptador ProductoAdaptador;
    private FirebaseFirestore mfirestore;

    EditText Encontrar;
    Button Buscar;

    public TiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        Encontrar = view.findViewById(R.id.txt_ft_Buscar);
        Buscar = view.findViewById(R.id.btn_ft_buscar);

        mfirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_tienda);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = mfirestore.collection("productos")
                .whereGreaterThan("stock", 0);

        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>().setQuery(query, Producto.class).build();

        ProductoAdaptador = new ProductoAdaptador(firestoreRecyclerOptions, getActivity());
        recyclerView.setAdapter(ProductoAdaptador);

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoBusqueda = Encontrar.getText().toString().trim();

                Query query = mfirestore.collection("productos")
                        .whereEqualTo("nombre", textoBusqueda)
                        .whereGreaterThan("stock", 0);

                FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                        new FirestoreRecyclerOptions.Builder<Producto>()
                                .setQuery(query, Producto.class)
                                .build();

                ProductoAdaptador = new ProductoAdaptador(firestoreRecyclerOptions, getActivity());
                recyclerView.setAdapter(ProductoAdaptador);
                ProductoAdaptador.startListening();
            }
        });


        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Recargar la lista completa de productos al jalar hacia abajo
                Query query = mfirestore.collection("productos")
                        .whereGreaterThan("stock", 0);
                FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                        new FirestoreRecyclerOptions.Builder<Producto>()
                                .setQuery(query, Producto.class)
                                .build();

                ProductoAdaptador = new ProductoAdaptador(firestoreRecyclerOptions, getActivity());
                recyclerView.setAdapter(ProductoAdaptador);
                ProductoAdaptador.startListening();

                swipeRefreshLayout.setRefreshing(false); // Detener la animación de recarga
            }
        });


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
