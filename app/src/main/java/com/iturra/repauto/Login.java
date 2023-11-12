package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

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
                    finish();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    Toast.makeText(Login.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                }else{
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
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }

    }
}