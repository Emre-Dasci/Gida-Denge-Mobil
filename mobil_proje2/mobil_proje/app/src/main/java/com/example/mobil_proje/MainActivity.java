package com.example.mobil_proje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputEditText loginEmail,loginParola;
    Button btnGiris, btnKayit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.txtLoginEmail);
        loginParola = findViewById(R.id.txtLoginParola);

        btnGiris = findViewById(R.id.btnLoginGiris);
        btnKayit = findViewById(R.id.btnLoginKayit);

        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KayitOl.class);
                startActivity(intent);
            }
        });

        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = loginEmail.getText().toString();
                String Parola = loginParola.getText().toString();

                if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Parola))
                    Toast.makeText(MainActivity.this, "Lütfen Boş Bırakmayınız!", Toast.LENGTH_SHORT).show();
                else
                    login();
            }
        });

    }
    private void login()
    {
        loginEmail = findViewById(R.id.txtLoginEmail);
        loginParola =findViewById(R.id.txtLoginParola);

        String Email = loginEmail.getText().toString();
        String Parola = loginParola.getText().toString();

        mAuth.signInWithEmailAndPassword(Email,Parola)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,AnaSayfa.class);
                            startActivity(intent);
                            finish();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "Giriş Bilgileri Hatalı! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}