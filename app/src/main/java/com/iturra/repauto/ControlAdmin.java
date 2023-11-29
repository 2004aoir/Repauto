package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class ControlAdmin extends AppCompatActivity {

    LinearLayout anadirProducto;
    private FirebaseAuth mAuth;
    Button botonCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_admin);
        mAuth = FirebaseAuth.getInstance();

        anadirProducto = findViewById(R.id.lay_ca_anadir_producto);
        botonCerrarSesion = findViewById(R.id.btn_admin_cerrar_sesion);

        anadirProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlAdmin.this,NuevoProducto.class));
            }
        });
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