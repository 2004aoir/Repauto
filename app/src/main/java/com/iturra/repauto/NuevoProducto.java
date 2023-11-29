package com.iturra.repauto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class NuevoProducto extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri;
    private StorageReference storageReference;

    EditText nombreP,modelos_compatiblesP,decripcionP,precioP,stockP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        // Inicialización de los elementos de la interfaz
        nombreP = findViewById(R.id.txt_np_nombre);
        modelos_compatiblesP = findViewById(R.id.txt_np_modelos_compatibles);
        decripcionP = findViewById(R.id.txt_np_descripcion);
        precioP = findViewById(R.id.txt_np_precio);
        stockP  = findViewById(R.id.txt_np_stock);
        Button btnAddImage = findViewById(R.id.btn_np_añadir_imagen);
        Button btnDeleteImage = findViewById(R.id.btn_np_borrar_imagen);
        Button btnAddProduct = findViewById(R.id.btn_np_añadir_producto);
        imageView = findViewById(R.id.img_np_imagen);

        storageReference = FirebaseStorage.getInstance().getReference();

        // Acción al presionar el botón "Añadir"
        btnAddImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1);
        });

        // Acción al presionar el botón "Eliminar"
        btnDeleteImage.setOnClickListener(view -> {
            // Verificar si hay una imagen seleccionada
            if (imageUri != null) {
                // Eliminar la imagen de la interfaz y restablecer las variables relacionadas
                imageView.setImageResource(R.drawable.imagen_vacia); // Establece la imagen por defecto
                imageUri = null; // Elimina la referencia a la URI de la imagen seleccionada
                Toast.makeText(NuevoProducto.this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NuevoProducto.this, "No hay ninguna imagen para eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción al presionar el botón "Añadir producto"
        btnAddProduct.setOnClickListener(view -> {
            // Aquí recopila los datos del formulario (nombre, modelos compatibles, descripción, imagen URL)

            // Ejemplo de recopilación de datos (sustituye esto con tu lógica para obtener los datos del formulario)
            String nombreProducto = nombreP.getText().toString().trim();
            String modelosCompatibles = modelos_compatiblesP.getText().toString().trim();
            String descripcion = decripcionP.getText().toString().trim();
            String precioString = precioP.getText().toString().trim();
            String stockString = stockP.getText().toString().trim();

            if (nombreProducto.isEmpty()) {
                // Mostrar Toast para el campo de nombre vacío
                Toast.makeText(NuevoProducto.this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modelosCompatibles.isEmpty()) {
                // Mostrar Toast para el campo de modelos compatibles vacío
                Toast.makeText(NuevoProducto.this, "Por favor, ingresa modelos compatibles", Toast.LENGTH_SHORT).show();
                return;
            }

            if (descripcion.isEmpty()) {
                // Mostrar Toast para el campo de descripción vacío
                Toast.makeText(NuevoProducto.this, "Por favor, ingresa al menos una breve descripción", Toast.LENGTH_SHORT).show();
                return;
            }

            if (precioString.isEmpty()) {
                // Mostrar Toast para el campo de precio vacío
                Toast.makeText(NuevoProducto.this, "Por favor, ingresa un precio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stockString.isEmpty()) {
                // Mostrar Toast para el campo de stock vacío
                Toast.makeText(NuevoProducto.this, "Por favor, ingresa el stock disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            int precio = Integer.parseInt(precioString);
            int stock = Integer.parseInt(stockString);

            // Verificar si hay una imagen seleccionada
            if (imageUri != null) {
                // Subir la imagen al Storage
                StorageReference fileRef = storageReference.child("img_productos/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Obtener la URL de la imagen y mostrarla en imageView usando Picasso
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Guardar la URL de la imagen
                                String imageUrl = uri.toString();

                                // Crear un mapa para almacenar los datos del producto incluyendo la URL de la imagen
                                Map<String, Object> productData = new HashMap<>();
                                productData.put("nombre", nombreProducto);
                                productData.put("modelosCompatibles", modelosCompatibles);
                                productData.put("descripcion", descripcion);
                                productData.put("stock", stock);
                                productData.put("precio", precio);
                                productData.put("imageUrl", imageUrl);

                                // Guardar los datos como un documento en la colección "productos" de Firestore
                                FirebaseFirestore.getInstance().collection("productos")
                                        .add(productData)
                                        .addOnSuccessListener(documentReference -> {
                                            // Manejar la adición exitosa del documento
                                            Toast.makeText(NuevoProducto.this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
                                            mostrarAlertDialog();
                                            // Limpiar los campos del formulario después de agregar el producto
                                            nombreP.setText("");
                                            modelos_compatiblesP.setText("");
                                            decripcionP.setText("");
                                            precioP.setText("");
                                            stockP.setText("");
                                            imageView.setImageResource(R.drawable.imagen_vacia);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Manejar errores en la adición del documento
                                            Toast.makeText(NuevoProducto.this, "Error al agregar el producto", Toast.LENGTH_SHORT).show();
                                        });
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Manejar errores en la carga de la imagen
                            Toast.makeText(NuevoProducto.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // No se ha seleccionado ninguna imagen
                Toast.makeText(NuevoProducto.this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Manejar el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // No subas la imagen aquí, deja esa lógica para el botón "Añadir producto"
            // Esto evita cargar la imagen dos veces
            Picasso.get().load(imageUri).into(imageView);
            // imageUrl = uri.toString(); // No necesitas guardar la URL aquí
        }
    }


    // Método para obtener la extensión del archivo de la imagen
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void mostrarAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Producto agregado correctamente");
        builder.setMessage("¿Qué deseas hacer?");
        builder.setPositiveButton("Subir otro producto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Finalizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cambia a otra Activity y finaliza la Activity actual
                Intent intent = new Intent(NuevoProducto.this, ControlAdmin.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
