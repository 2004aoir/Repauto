package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Login extends AppCompatActivity {

    TextView irRegistro,irRecuperacion;
    Button botonIniciarSesion;
    EditText correoL,contrasenaL;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        correoL = findViewById(R.id.txt_log_correo);
        contrasenaL = findViewById(R.id.txt_log_contrasena);

        irRecuperacion = findViewById(R.id.init_recuperacion);
        botonIniciarSesion = findViewById(R.id.botonIniciarSesion);
        irRegistro = findViewById(R.id.init_registro);
        irRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RegistroUsuario.class));
            }
        });

        irRecuperacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RecuperarContrasena.class));
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correoUser = correoL.getText().toString().trim();
                String contraseñaUser = contrasenaL.getText().toString().trim();


                if(correoUser.isEmpty() || contraseñaUser.isEmpty()){
                    Toast.makeText(Login.this,"Ingrese todos los datos",Toast.LENGTH_SHORT).show();
                }else{
                    IniciarSesion(correoUser,contraseñaUser);
                }
            }
        });

    }
    public void IniciarSesion(String correoUser, String contraseñaUser) {
        mAuth.signInWithEmailAndPassword(correoUser,contraseñaUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Acceder al documento del usuario en Firestore
                        DocumentReference userRef = mFirestore.collection("user").document(uid);
                        userRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String rol = documentSnapshot.getString("rol");

                                // Verificar el rol y redirigir a la actividad correspondiente
                                if (rol != null) {
                                    switch (rol) {
                                        case "usuario":
                                            // Redirigir a la actividad de usuario
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            break;
                                        case "admin":
                                            // Redirigir a la actividad de admin
                                            startActivity(new Intent(Login.this, ControlAdmin.class));
                                            break;
                                        case "empleado":
                                            // Redirigir a la actividad de empleado
                                            startActivity(new Intent(Login.this, ControlAccion.class));
                                            break;
                                        default:
                                            // Rol desconocido, manejar según sea necesario
                                            break;
                                    }
                                    finish(); // Finalizar la actividad actual
                                }
                            }
                        }).addOnFailureListener(e -> {
                            // Manejar errores al obtener el documento
                            Toast.makeText(Login.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Toast.makeText(Login.this,"Error al iniciar sesión",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,"Error al iniciar sesión",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verificando Credenciales...");
            progressDialog.setCancelable(false); // Evita que se pueda cancelar

            progressDialog.show(); // Mostrar el ProgressDialog antes de obtener los datos

            String uid = currentUser.getUid();
            DocumentReference userRef = mFirestore.collection("user").document(uid);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                progressDialog.dismiss(); // Ocultar el ProgressDialog una vez que se han obtenido los datos

                if (documentSnapshot.exists()) {
                    String rol = documentSnapshot.getString("rol");

                    if (rol != null) {
                        switch (rol) {
                            case "usuario":
                                startActivity(new Intent(Login.this, MainActivity.class));
                                break;
                            case "admin":
                                startActivity(new Intent(Login.this, ControlAdmin.class));
                                break;
                            case "empleado":
                                startActivity(new Intent(Login.this, ControlAccion.class));
                                break;
                            default:
                                // Manejar un rol desconocido si es necesario
                                break;
                        }
                        finish(); // Finalizar la actividad actual
                    }
                }
            }).addOnFailureListener(e -> {
                progressDialog.dismiss(); // Ocultar el ProgressDialog en caso de error
                // Manejar errores al obtener el documento
                Toast.makeText(Login.this, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show();
            });
        }
    }


}