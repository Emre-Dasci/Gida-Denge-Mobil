package com.example.mobil_proje;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UrunEkle extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private MediaPlayer plyArkaPlan;
    private EditText isim, gida_adi, tel_no, aciklama;
    private ImageView resim;
    Button btnEkle, btnSelectLocation;
    private String resimBase64;
    private LatLng selectedLatLng;
    private static final int LOCATION_REQUEST_CODE = 1001;
    private MaterialCardView cv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urun_ekle);

        firebaseDatabase = FirebaseDatabase.getInstance("https://mobil-proje-65bc7-default-rtdb.firebaseio.com/");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        isim = findViewById(R.id.isim);
        gida_adi = findViewById(R.id.gida_adi);
        tel_no = findViewById(R.id.tel_no);
        aciklama = findViewById(R.id.aciklama);
        resim = findViewById(R.id.resim);
        cv_image = findViewById(R.id.cv_image);
        btnEkle = findViewById(R.id.btnEkle);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UrunEkle.this, SelectLocationActivity.class);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);

                btnSelectLocation.setText("Konum Seçildi");
            }
        });

        cv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camPermission()) {
                    ActivityCompat.requestPermissions(
                            UrunEkle.this,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            1
                    );
                } else {
                    resimAl();
                }
            }
        });

        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!camPermission()) {
                    ActivityCompat.requestPermissions(
                            UrunEkle.this,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            1
                    );
                } else {
                    resimAl();
                }
            }
        });

        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kontrol()) {
                    Foods foods = new Foods();
                    foods.setIsim(isim.getText().toString());
                    foods.setGida_adi(gida_adi.getText().toString());
                    foods.setTel_no(tel_no.getText().toString());
                    foods.setAciklama(aciklama.getText().toString());
                    foods.setResim(resimBase64);
                    foods.setUserid(currentUser.getUid()); // Kullanıcı ID'sini ekle
                    System.out.println(currentUser.getUid()+"id:"+1);
                    if (selectedLatLng != null) {
                        foods.setKonum(selectedLatLng.latitude + ", " + selectedLatLng.longitude);
                    }
                    databaseReference = firebaseDatabase.getReference("FoodInfo").child(isim.getText().toString());
                    databaseReference.setValue(foods).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(UrunEkle.this, AnaSayfa.class);
                                startActivity(intent);
                                Toast.makeText(UrunEkle.this, "Ürününüz Başarılı Bir Şekilde Eklenmiştir", Toast.LENGTH_SHORT).show();
                                medyaArkaPlan();
                            } else {
                                Toast.makeText(UrunEkle.this, "Ürün ekleme başarısız", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    resim.setImageBitmap(bitmap);
                    resimBase64 = encodeToBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedLatLng = data.getParcelableExtra("selectedLatLng");
            }
        }
    }
    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private boolean kontrol() {
        String txt_isim = isim.getText().toString();
        String txt_gida_adi = gida_adi.getText().toString();
        String txt_tel_no = tel_no.getText().toString();
        String txt_aciklama = aciklama.getText().toString();

        if (TextUtils.isEmpty(txt_isim) || TextUtils.isEmpty(txt_gida_adi) || TextUtils.isEmpty(txt_tel_no) || TextUtils.isEmpty(txt_aciklama) || resimBase64 == null || selectedLatLng == null) {
            Toast.makeText(UrunEkle.this, "Lütfen Tüm Bilgileri Doldurunuz!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!txt_tel_no.matches("\\d{11}")) {
            Toast.makeText(UrunEkle.this, "Lütfen 11 Haneli Geçerli Bir Telefon Numarası Giriniz!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void medyaArkaPlan() {
        plyArkaPlan = MediaPlayer.create(UrunEkle.this, R.raw.bildirim_onay);
        plyArkaPlan.start();
    }

    public boolean camPermission() {
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED;
    }

    void resimAl() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(UrunEkle.this);
    }
}
