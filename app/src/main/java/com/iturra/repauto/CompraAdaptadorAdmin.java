package com.iturra.repauto;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CompraAdaptadorAdmin extends FirestoreRecyclerAdapter<Compra, CompraAdaptadorAdmin.ViewHolder> {
    FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    Activity activity;
    String id = "";

    public CompraAdaptadorAdmin(@NonNull FirestoreRecyclerOptions<Compra> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Compra compra) {
        DocumentSnapshot dSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        id = dSnapshot.getId();

        String idComprador = compra.getIdUsuario();
        FirebaseFirestore.getInstance().collection("user").document(idComprador)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Aquí obtienes el nombre del usuario
                        String nombreUsuario = documentSnapshot.getString("nombre");
                        holder.NombreUsuario.setText(nombreUsuario);
                    } else {
                        // El documento no existe
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener el documento
                });

        Date fechaCompra = compra.getFechaCompra();
        Date fechaRetiro = compra.getFechaRetiro();

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        String fechaC = "Error al mostrar fecha compra";
        String fechaR = "Fecha no disponible";

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptador_compra_admin, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView NombreUsuario,nombre, precio, cantidad, estado, fechaC, fechaE;
        ImageView imagen;
        Button GuardarCambios,EliminarCompra;
        EditText Dia,Mes,Anio;
        Spinner estadoConfi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            NombreUsuario = itemView.findViewById(R.id.nombreUserCadmin);
            nombre = itemView.findViewById(R.id.nombreCadmin);
            precio = itemView.findViewById(R.id.precioCadmin);
            cantidad = itemView.findViewById(R.id.cantidadCadmin);
            estado = itemView.findViewById(R.id.estadoCadmin);
            fechaC = itemView.findViewById(R.id.fechaCadmin);
            fechaE = itemView.findViewById(R.id.fechaRCadmin);
            imagen = itemView.findViewById(R.id.imagenCadmin);
            Dia = itemView.findViewById(R.id.DiaCadmin);
            Mes = itemView.findViewById(R.id.MesCadmin);
            Anio = itemView.findViewById(R.id.AnioCadmin);
            estadoConfi = itemView.findViewById(R.id.estadoConfiCadmin);
            GuardarCambios = itemView.findViewById(R.id.guardarCadmin);
            EliminarCompra = itemView.findViewById(R.id.eliminarCadmin);

            String[] opcionesEstado = {"Pendiente", "Listo para Retiro", "Retirado"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, opcionesEstado);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            estadoConfi.setAdapter(adapter);

            GuardarCambios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String dia = Dia.getText().toString();
                    String mes = Mes.getText().toString();
                    String anio = Anio.getText().toString();

                    if (!dia.isEmpty() && !mes.isEmpty() && !anio.isEmpty()) {
                        int intDia = Integer.parseInt(dia);
                        int intMes = Integer.parseInt(mes);
                        int intAnio = Integer.parseInt(anio);

                        if (intDia >= 1 && intDia <= 31 && intMes >= 1 && intMes <= 12 && anio.length() <= 4) {
                            Date fecha = new Date(intAnio - 1900, intMes - 1, intDia);
                            Timestamp timestamp = new Timestamp(fecha);

                            // Obtener el estado seleccionado en el Spinner
                            String estadoSeleccionado = estadoConfi.getSelectedItem().toString();

                            FirebaseFirestore.getInstance()
                                    .collection("compra")
                                    .document(id)
                                    .update("fechaRetiro", timestamp, "estadoProducto", estadoSeleccionado)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(itemView.getContext(), "Cambios guardados", Toast.LENGTH_SHORT).show();
                                        // Limpiar los campos después de guardar los cambios
                                        Dia.setText("");
                                        Mes.setText("");
                                        Anio.setText("");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(itemView.getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(itemView.getContext(), "Error en día, mes o año", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "Campos incompletos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
