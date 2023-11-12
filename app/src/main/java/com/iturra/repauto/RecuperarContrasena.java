package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RecuperarContrasena extends AppCompatActivity {
    EditText correoRec;

    Button bottonRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        correoRec = findViewById(R.id.txt_rec_correo);
        bottonRec = findViewById(R.id.botonRecuperarContrasena);

    }
}