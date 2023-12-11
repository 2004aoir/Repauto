package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.subtyping.qual.Bottom;

public class DetalleProducto extends AppCompatActivity {

    TextView Nombre,Precio,Stock,ModelosC,Descripcion;
    ImageView Imagen;
    EditText Cantidad;
    Button Carrito;

    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        mFirestore = FirebaseFirestore.getInstance();

        Nombre = findViewById(R.id.txt_dp_nombre);
        Precio = findViewById(R.id.txt_dp_precio);
        Stock = findViewById(R.id.txt_dp_stock);
        ModelosC = findViewById(R.id.txt_dp_modelos_compatibles);
        Descripcion = findViewById(R.id.txt_dp_descripcion);
        Cantidad = findViewById(R.id.txt_c_dp_cantidad);
        Imagen = findViewById(R.id.img_dp_imagen);
        Carrito = findViewById(R.id.btn_dp_agregarPC);

        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId");

        // Obtener la información del producto desde Firebase utilizando la ID del documento
        mFirestore.collection("productos").document(productId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            String nombre = documentSnapshot.getString("nombre");
                            int precio = documentSnapshot.getLong("precio").intValue();
                            int stock = documentSnapshot.getLong("stock").intValue();
                            String modelosCompatibles = documentSnapshot.getString("modelosCompatibles");
                            String descripcion = documentSnapshot.getString("descripcion");
                            String imagen = documentSnapshot.getString("imageUrl");

                            // Mostrar los datos en la interfaz de usuario (por ejemplo, en TextViews e ImageView)
                            Nombre.setText(nombre);
                            Precio.setText("Valor: $" + String.valueOf(precio));
                            Stock.setText("Cant. Disponible: " + String.valueOf(stock));
                            ModelosC.setText("Modelos Compatibles: " + modelosCompatibles);
                            Descripcion.setText("Descripcion: " + descripcion);

                            try {
                                if (imagen != null && !imagen.isEmpty()) {
                                    Picasso.get().load(imagen)
                                            .error(R.drawable.imagen_vacia) // Maneja el error con una imagen de error
                                            .into(Imagen);
                                } else {
                                    // Si la URL de la imagen es nula o vacía, muestra un marcador de posición o una imagen de error
                                    Imagen.setImageResource(R.drawable.imagen_vacia);
                                }
                            } catch (Exception e) {
                                Log.d("Exception", "Error al cargar la imagen: " + e);
                                // Si hay un error, muestra un marcador de posición o una imagen de error
                                Imagen.setImageResource(R.drawable.imagen_vacia);
                            }


                        } else {
                            // El documento no existe
                            // Manejar el caso en que el documento no se encuentre
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores al obtener datos desde Firebase
                    }
                });
    }
}