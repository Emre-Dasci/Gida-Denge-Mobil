package com.example.mobil_proje;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AnaSayfa extends AppCompatActivity {

    ImageButton btnUrunAl, btnUrunEkle, btnHarita, btnCikis;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        btnUrunAl = findViewById(R.id.btnUrunAl);
        btnUrunEkle = findViewById(R.id.btnUrunEkle);
        btnHarita = findViewById(R.id.btnHarita);
        btnCikis = findViewById(R.id.btnCikis);

        btnUrunAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent urunAl = new Intent(AnaSayfa.this, UrunAl.class);
                startActivity(urunAl);
            }
        });

        btnUrunEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent urunEkle = new Intent(AnaSayfa.this, UrunEkle.class);
                startActivity(urunEkle);
            }
        });

        btnCikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AnaSayfa.this)
                        .setTitle("Çıkış")
                        .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();

                                Intent cikis = new Intent(AnaSayfa.this, MainActivity.class);
                                cikis.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(cikis);
                                Toast.makeText(AnaSayfa.this, "Çıkış Başarılı!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("Hayır", null)
                        .show();
            }
        });

        btnHarita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent harita = new Intent(AnaSayfa.this, Harita.class);
                startActivity(harita);
            }
        });
    }
}
