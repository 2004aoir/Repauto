package com.iturra.repauto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    Button botonRegistrar;
    EditText nombre_usuario,correo,contrasena,confi_contrasena;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre_usuario = findViewById(R.id.txt_reg_nombre_usuario);
        correo = findViewById(R.id.txt_reg_correo);
        contrasena = findViewById(R.id.txt_reg_contrasena);
        confi_contrasena = findViewById(R.id.txt_reg_confi_contrasena);

        botonRegistrar = findViewById(R.id.botonRegistrar);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreUser = nombre_usuario.getText().toString();
                String correoUser = correo.getText().toString().trim();
                String contraseñaUser = contrasena.getText().toString();
                String confiContraseñaUser = confi_contrasena.getText().toString();

                if(nombreUser.isEmpty() || correoUser.isEmpty() || contraseñaUser.isEmpty() || confiContraseñaUser.isEmpty()){
                    Toast.makeText(RegistroUsuario.this,"Ingrese todos los datos",Toast.LENGTH_SHORT).show();
                }else if(!contraseñaUser.equals(confiContraseñaUser)){
                    Toast.makeText(RegistroUsuario.this,"Las contraseñas no son iguales",Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegistroUsuario.this,"Verifique la informacion",Toast.LENGTH_SHORT).show();
                }else{
                    registarUser(nombreUser,correoUser,contraseñaUser);
                }
            }
        });

    }
    private void registarUser(String nombreUser,String correoUser, String contraseñaUser){
        mAuth.createUserWithEmailAndPassword(correoUser,contraseñaUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                String rol = "usuario";

                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("nombre",nombreUser);
                map.put("correo",correoUser);
                map.put("rol",rol);

                mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(RegistroUsuario.this, Login.class));
                        Toast.makeText(RegistroUsuario.this,"Usuario Registrado",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroUsuario.this,"Error al guardar",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroUsuario.this,"Error Al Registrar",Toast.LENGTH_SHORT).show();
            }
        });

    }
}