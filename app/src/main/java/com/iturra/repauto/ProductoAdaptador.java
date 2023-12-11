package com.iturra.repauto;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductoAdaptador extends FirestoreRecyclerAdapter<Producto, ProductoAdaptador.ViewHolder> {
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Activity activity;

    public ProductoAdaptador(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Producto producto) {
        DocumentSnapshot dSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String productId = dSnapshot.getId();

        holder.nombre.setText("Producto: " + producto.getNombre());
        holder.precio.setText( "Valor: $" + String.valueOf(producto.getPrecio()));
        holder.stock.setText("Stock: " + String.valueOf(producto.getStock()));
        try {
            String imagen = producto.getImageUrl();
            if (imagen != null && !imagen.isEmpty()) {
                Picasso.get().load(imagen)
                        .error(R.drawable.imagen_vacia) // Maneja el error con una imagen de error
                        .into(holder.imagen);
            } else {
                // Si la URL de la imagen es nula o vacía, muestra un marcador de posición o una imagen de error
                holder.imagen.setImageResource(R.drawable.imagen_vacia);
            }
        } catch (Exception e) {
            Log.d("Exception", "Error al cargar la imagen: " + e);
            // Si hay un error, muestra un marcador de posición o una imagen de error
            holder.imagen.setImageResource(R.drawable.imagen_vacia);
        }

        // Si tienes una imagen para mostrar, aquí puedes manejarla en la interfaz

        holder.agregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAlCarrito(productId, producto);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la ID del documento del producto
                String productId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();

                // Crear un Intent para abrir el nuevo Activity
                Intent intent = new Intent(activity, DetalleProducto.class);

                // Poner la ID del documento en el Intent
                intent.putExtra("productId", productId);

                // Iniciar el nuevo Activity
                activity.startActivity(intent);
            }
        });
    }

    private void agregarAlCarrito(String productId, Producto producto) {
        String userId = mAuth.getCurrentUser().getUid();
        Integer cantidadP = 1;
        String estadoProducto = "Pendiente";

        Map<String, Object> carritoInfo = new HashMap<>();
        carritoInfo.put("idProducto", productId);
        carritoInfo.put("idUsuario", userId);
        carritoInfo.put("nombreProducto", producto.getNombre());
        carritoInfo.put("precioProducto", producto.getPrecio());
        carritoInfo.put("imagenProducto", producto.getImageUrl());
        carritoInfo.put("fechaRetiro","");
        carritoInfo.put("estadoProducto",estadoProducto);
        carritoInfo.put("CantidadProducto", cantidadP);

        mFirestore.collection("carrito").add(carritoInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(activity, "Producto agregado al carrito.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error al agregar el producto al carrito.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Resto del código para onCreateViewHolder y ViewHolder sigue igual
    @NonNull
    @Override
    public ProductoAdaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_productos, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, stock;
        ImageView imagen;
        Button agregarCarrito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreP);
            precio = itemView.findViewById(R.id.precioP);
            stock = itemView.findViewById(R.id.stockP);
            imagen = itemView.findViewById(R.id.imagenP);
            agregarCarrito = itemView.findViewById(R.id.agregarPC);
        }
    }

}

