package com.example.mobil_proje;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Harita extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LinearLayout detailsLayout;
    private Button detailsButton;
    private Map<Marker, Foods> markerFoodsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harita);

        firebaseDatabase = FirebaseDatabase.getInstance("https://mobil-proje-65bc7-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("FoodInfo");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        detailsLayout = findViewById(R.id.details_layout);
        detailsButton = findViewById(R.id.details_button);

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Foods selectedFood = (Foods) v.getTag();
                if (selectedFood != null) {
                    DataHolder.getInstance().setSelectedFood(selectedFood);
                    Intent intent = new Intent(Harita.this, UrunAl.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Harita.this, "Lütfen bir ürünü seçin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Foods food = snapshot.getValue(Foods.class);
                    if (food != null && food.getKonum() != null) {
                        String[] latLng = food.getKonum().split(", ");
                        double latitude = Double.parseDouble(latLng[0]);
                        double longitude = Double.parseDouble(latLng[1]);
                        LatLng location = new LatLng(latitude, longitude);

                        Bitmap bitmap = decodeFromBase64(food.getResim());

                        Marker marker;
                        if (bitmap != null) {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(food.getGida_adi())
                                    .snippet(food.getAciklama())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        } else {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(food.getGida_adi())
                                    .snippet(food.getAciklama())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                        markerFoodsMap.put(marker, food);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    }
                }

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Foods selectedFood = markerFoodsMap.get(marker);
                        if (selectedFood != null) {
                            detailsButton.setTag(selectedFood);
                            detailsLayout.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata durumu
            }
        });
    }

    private Bitmap decodeFromBase64(String encodedImage) {
        try {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            int width = 100;
            int height = 100;
            return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
