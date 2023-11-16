package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {
    FirebaseAuth mAuth;

    EditText correoRecuperacion;
    Button botonRecuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        correoRecuperacion = findViewById(R.id.txt_rec_correo);
        botonRecuperar = findViewById(R.id.botonRecuperarContrasena);
        mAuth = FirebaseAuth.getInstance();

        botonRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = correoRecuperacion.getText().toString().trim();

                if (correo.isEmpty()) {
                    Toast.makeText(RecuperarContrasena.this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                } else {
                    enviarCorreoRecuperacion(correo);
                }
            }
        });
    }

    private void enviarCorreoRecuperacion(String correo) {
        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarContrasena.this, "Correo de recuperación enviado a " + correo, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RecuperarContrasena.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(RecuperarContrasena.this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}