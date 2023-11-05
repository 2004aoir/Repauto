package com.iturra.repauto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler bypass = new Handler();
        bypass.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hayConexion(Splash.this)){
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }else{
                    //Toast.makeText(MainSplash.this,"NO HAY CONEXION",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Splash.this);
                    alertBuilder.setTitle("Internet desconectado :(");
                    alertBuilder.setMessage("Nesesita conexión a internet para usar la App ");
                    alertBuilder.setPositiveButton("OK (Cerrar App)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog dialog = alertBuilder.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                }
            }
        },1500);
    }
    public boolean hayConexion(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnectedOrConnecting()){
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(mobile!= null && mobile.isConnectedOrConnecting() ||
                    wifi != null && wifi.isConnectedOrConnecting()){
                return true;

            }else{
                return false;
            }

        }else{
            return false;
        }
    }
}