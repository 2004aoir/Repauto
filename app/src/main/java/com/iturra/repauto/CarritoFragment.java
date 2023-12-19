package com.iturra.repauto;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Map;

public class CarritoFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CarritoAdaptador carritoAdaptador;
    private FirebaseFirestore mFirestore;
    Button Pagar;
    TextView MontoTotal;
    private int montoTotal = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        MontoTotal = view.findViewById(R.id.txt_fcc_total);
        Pagar = view.findViewById(R.id.btn_fcc_Pagar);

        Pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacion();
            }
        });


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

        // Agregar un SnapshotListener para escuchar cambios en los documentos
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Manejar el error, si lo hay
                return;
            }

            montoTotal = 0; // Reiniciar el monto total al recalcularlo

            if (value != null) {
                for (Carrito carrito : value.toObjects(Carrito.class)) {
                    try {
                        montoTotal += carrito.getPrecioProducto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                // Mostrar el monto total en el TextView
                MontoTotal.setText("$" + String.valueOf(montoTotal));
            }
        });

        return view;
    }

    // Método para mostrar el diálogo de confirmación
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar compra");
        builder.setMessage("¿Está seguro que desea comprar estos artículos?");

        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            // Migrar documentos a la colección "compra"
            migrarDocumentos();
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {
            // No hacer nada, simplemente cerrar el diálogo
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para migrar los documentos a la colección "compra"
    private void migrarDocumentos() {
        String userId = mAuth.getCurrentUser().getUid();
        long currentTimeStamp = System.currentTimeMillis();

        mFirestore.collection("carrito").whereEqualTo("idUsuario", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Obtener los datos del documento
                    Map<String, Object> data = document.getData();

                    // Agregar la fecha de compra actual al mapa de datos
                    data.put("fechaCompra", new Date(currentTimeStamp));

                    mFirestore.collection("compra").add(data)
                            .addOnSuccessListener(documentReference -> {
                                // Eliminar el documento de la colección "carrito" después de migrarlo
                                mFirestore.collection("carrito").document(document.getId()).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Documento eliminado exitosamente
                                            Toast.makeText(getContext(), "Productos comprados y pagados exitosamente", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            mostrarError("Error al eliminar documento de carrito: " + e.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> {
                                mostrarError("Error al migrar documento a compra: " + e.getMessage());
                            });
                }
            } else {
                mostrarError("Error al obtener documentos de carrito: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> {
            mostrarError("Error general en la consulta de carrito: " + e.getMessage());
        });
    }


    private void mostrarError(String mensaje) {
        // Muestra un Toast con el mensaje de error
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
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
