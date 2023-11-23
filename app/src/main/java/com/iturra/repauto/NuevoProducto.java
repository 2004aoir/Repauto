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

    private String imageUrl;
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
            // Abre la galería para seleccionar una imagen
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1);
        });

        // Acción al presionar el botón "Eliminar"
        btnDeleteImage.setOnClickListener(view -> {
            // Verificar si hay una imagen para eliminar
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                // Eliminar la imagen del Storage
                StorageReference fileRef = storageReference.child("img_productos/" + fileName);
                fileRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Manejar la eliminación exitosa de la imagen
                            Toast.makeText(NuevoProducto.this, "Imagen eliminada exitosamente", Toast.LENGTH_SHORT).show();
                            // Establecer la imagen predeterminada en imageView
                            imageView.setImageResource(R.drawable.imagen_vacia);
                            // También podrías borrar la referencia a la imagen actual
                            imageUri = null;
                            imageUrl = null; // Limpiar la URL almacenada
                        })
                        .addOnFailureListener(e -> {
                            // Manejar errores en la eliminación de la imagen
                            Toast.makeText(NuevoProducto.this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // No hay URL de imagen almacenada
                Toast.makeText(NuevoProducto.this, "No hay imagen para eliminar", Toast.LENGTH_SHORT).show();
            }
        });



        // Acción al presionar el botón "Añadir producto"
        btnAddProduct.setOnClickListener(view -> {
            // Aquí recopila los datos del formulario (nombre, modelos compatibles, descripción, imagen URL)

            // Ejemplo de recopilación de datos (sustituye esto con tu lógica para obtener los datos del formulario)
            String nombreProducto = nombreP.getText().toString();
            String modelosCompatibles = modelos_compatiblesP.getText().toString();
            String descripcion = decripcionP.getText().toString();
            Integer precio = Integer.valueOf(precioP.getText().toString());
            Integer stock = Integer.valueOf(stockP.getText().toString());

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

            // Subir la imagen al Storage
            StorageReference fileRef = storageReference.child("img_productos/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de la imagen y mostrarla en imageView usando Picasso
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.get().load(uri).into(imageView);
                            // Guardar la URL de la imagen en alguna variable si necesitas usarla posteriormente
                            String imageUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Manejar errores en la carga de la imagen
                        Toast.makeText(NuevoProducto.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
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
