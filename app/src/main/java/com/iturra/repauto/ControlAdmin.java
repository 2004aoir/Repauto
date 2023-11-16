package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ControlAdmin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button botonCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_admin);

        botonCerrarSesion = findViewById(R.id.btn_admin_cerrar_sesion);
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ControlAdmin.this,Login.class));

            }
        });


    }
}