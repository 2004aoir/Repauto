package com.iturra.repauto;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UsuarioFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db; // Asegúrate de tener la instancia de FirebaseFirestore
    Button botonCerrarSesion;
    TextView Nombre, Correo, Registro, UltimoRegistro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Obtén la instancia de FirebaseFirestore

        String userId = mAuth.getCurrentUser().getUid(); // Obtener el ID del usuario actual

        botonCerrarSesion = view.findViewById(R.id.btn_user_cerrar_sesion);

        Nombre = view.findViewById(R.id.txt_ui_nombre);
        Correo = view.findViewById(R.id.txt_ui_correo);
        Registro = view.findViewById(R.id.txt_ui_f_registro);
        UltimoRegistro = view.findViewById(R.id.txt_ui_ultimo_ingreso);

        // Verificar si el usuario está autenticado
        if (mAuth.getCurrentUser() != null) {
            // Obtener el usuario actual
            FirebaseUser currentUser = mAuth.getCurrentUser();

            // Mostrar el correo electrónico
            String email = currentUser.getEmail();
            Correo.setText(email);

            // Mostrar la fecha de registro
            Date fechaRegistro = new Date(currentUser.getMetadata().getCreationTimestamp());
            String fechaRegistroString = SimpleDateFormat.getDateInstance().format(fechaRegistro);
            Registro.setText(fechaRegistroString);

            // Mostrar la última fecha de ingreso
            Date ultimoIngreso = new Date(currentUser.getMetadata().getLastSignInTimestamp());
            String ultimoIngresoString = SimpleDateFormat.getDateTimeInstance().format(ultimoIngreso);
            UltimoRegistro.setText(ultimoIngresoString);

            // Obtener el nombre del usuario desde Firestore
            DocumentReference userRef = db.collection("user").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Verificar si la subcolección "nombre" existe y obtener el nombre
                        if (documentSnapshot.contains("nombre")) {
                            String nombreUsuario = documentSnapshot.getString("nombre");
                            Nombre.setText("Bienvenido "+nombreUsuario);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar errores al obtener el nombre desde Firestore
                }
            });
        }

        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
