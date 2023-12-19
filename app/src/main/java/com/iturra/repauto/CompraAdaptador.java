package com.iturra.repauto;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CompraAdaptador extends FirestoreRecyclerAdapter<Compra, CompraAdaptador.ViewHolder> {
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    public CompraAdaptador(@NonNull FirestoreRecyclerOptions<Compra> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Compra compra) {
        DocumentSnapshot dSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = dSnapshot.getId();

        Date fechaCompra = compra.getFechaCompra();
        Date fechaRetiro = compra.getFechaRetiro();

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        String fechaC = "";
        String fechaR = "";

        if (fechaCompra != null) {
            fechaC = formatoFecha.format(fechaCompra);
        }

        if (fechaRetiro != null) {
            fechaR = formatoFecha.format(fechaRetiro);
        }

        holder.cantidad.setText("Cantidad: " + String.valueOf(compra.getCantidadProducto()));
        holder.estado.setText("Estado: " + String.valueOf(compra.getEstadoProducto()));
        holder.fechaC.setText(fechaC);
        holder.fechaE.setText(fechaR);
        holder.nombre.setText(compra.getNombreProducto());
        holder.precio.setText("$" + String.valueOf(compra.getPrecioProducto()));
        try {
            String imagen = compra.getImagenProducto();
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

    // Resto del código, elementos, etc.

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_compra, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, precio, cantidad, estado, fechaC, fechaE;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreC);
            precio = itemView.findViewById(R.id.precioC);
            cantidad = itemView.findViewById(R.id.cantidadC);
            estado = itemView.findViewById(R.id.estadoC);
            fechaC = itemView.findViewById(R.id.fechaC);
            fechaE = itemView.findViewById(R.id.fechaRC);
            imagen = itemView.findViewById(R.id.imagenC);
        }

    }
}
