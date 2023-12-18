package com.iturra.repauto;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CarritoAdaptador extends FirestoreRecyclerAdapter<Carrito, CarritoAdaptador.ViewHolder> {
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Activity activity;

    public CarritoAdaptador(@NonNull FirestoreRecyclerOptions<Carrito> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Carrito carrito) {
        DocumentSnapshot dSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = dSnapshot.getId();
        holder.nombre.setText(carrito.getNombreProducto());
        holder.precio.setText("$" + String.valueOf(carrito.getPrecioProducto()));
        holder.cantidad.setText("Cantidad: " + String.valueOf(carrito.getCantidadProducto()));

        holder.botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCarrito(id);
            }
        });

        try {
            String imagen = carrito.getImagenProducto();
            if (imagen != null && !imagen.isEmpty()) {
                Picasso.get().load(imagen)
                        .error(R.drawable.imagen_vacia)
                        .into(holder.imagen);
            } else {
                holder.imagen.setImageResource(R.drawable.imagen_vacia);
            }
        } catch (Exception e) {
            Log.d("Exception", "Error al cargar la imagen: " + e);
            holder.imagen.setImageResource(R.drawable.imagen_vacia);
        }
    }

    private void deleteCarrito(String id) {
        DocumentReference carritoRef = mFirestore.collection("carrito").document(id);
        carritoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener la cantidadProducto y la ID del producto
                    int cantidadProducto = documentSnapshot.getLong("cantidadProducto").intValue();
                    String idProducto = documentSnapshot.getString("idProducto");

                    // Actualizar la cantidad en la colección 'producto'
                    DocumentReference productoRef = mFirestore.collection("productos").document(idProducto);
                    productoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                int stockActual = documentSnapshot.getLong("stock").intValue();

                                // Sumar la cantidadProducto al stock actual
                                int nuevaCantidadStock = stockActual + cantidadProducto;

                                // Actualizar el campo 'stock' con la nueva cantidad
                                productoRef.update("stock", nuevaCantidadStock).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Aquí puedes eliminar el documento del carrito si la actualización fue exitosa
                                        carritoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(activity, "Producto eliminado correctamente.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, "Error al eliminar producto.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "Error al actualizar cantidad en producto.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(activity, "El documento del producto no existe.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, "Error al obtener datos del producto.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(activity, "El documento del carrito no existe.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al obtener datos del carrito.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @NonNull
    @Override
    public CarritoAdaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_carrito, parent, false);
        return new CarritoAdaptador.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, cantidad;
        ImageView imagen;
        Button botonEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreCC);
            precio = itemView.findViewById(R.id.valorCC);
            cantidad = itemView.findViewById(R.id.cantidadCC);
            imagen = itemView.findViewById(R.id.imagenCC);
            botonEliminar = itemView.findViewById(R.id.eliminarCC);
        }
    }
}