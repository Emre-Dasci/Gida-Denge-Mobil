package com.example.mobil_proje;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class KayitOl extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText Email, KAdi, Parola, ParolaTekrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);
        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.txtRegisterEmail);
        KAdi =findViewById(R.id.txtRegisterKAdi);
        Parola =findViewById(R.id.txtRegisterParola);
        ParolaTekrar =findViewById(R.id.txtRegisterParolaTekrar);

        Button btnKayit = (Button)findViewById(R.id.btnKayit);

        btnKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void registerUser() {
        String txtEmail = Email.getText().toString();
        String txtKAdi = KAdi.getText().toString();
        String txtParola = Parola.getText().toString();
        String txtParolaTekrar = ParolaTekrar.getText().toString();

        if (txtParola.length() < 6) {
            Toast.makeText(this, "Şifreniz en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show();
            return; // İşlemi sonlandır
        }

        if (!txtParola.equals(txtParolaTekrar)) {
            Toast.makeText(this, "Şifreler eşleşmiyor. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
            return; // İşlemi sonlandır
        }

        if (!txtEmail.isEmpty() && !txtParola.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(txtEmail, txtParola)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(KayitOl.this, "Kayıt Başarılı Bir Şekilde Tamamlanmıştır.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(KayitOl.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            Log.w(TAG, "createUserWithEmail:failure", exception);
                            Toast.makeText(getApplicationContext(), "Kayıt Tamamlanamadı.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Bosluklari doldurunuz...", Toast.LENGTH_SHORT).show();
        }
    }

}