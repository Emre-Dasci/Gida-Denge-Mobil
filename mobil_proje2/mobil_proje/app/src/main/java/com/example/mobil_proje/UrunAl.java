package com.example.mobil_proje;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UrunAl extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    List<Foods> foodsList = new ArrayList<>();
    ListView listView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_al);
        listView = findViewById(R.id.list_view);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance("https://mobil-proje-65bc7-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("FoodInfo");

        myAdapter = new MyAdapter(this, foodsList);
        listView.setAdapter(myAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Foods foods = snapshot.getValue(Foods.class);
                if (foods != null) {
                    foodsList.add(foods);
                    myAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Foods updatedFood = snapshot.getValue(Foods.class);
                if (updatedFood != null) {
                    for (int i = 0; i < foodsList.size(); i++) {
                        if (foodsList.get(i).getUserid().equals(updatedFood.getUserid())) {
                            foodsList.set(i, updatedFood);
                            myAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Foods removedFood = snapshot.getValue(Foods.class);
                if (removedFood != null) {
                    foodsList.remove(removedFood);
                    myAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UrunAl.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class MyAdapter extends BaseAdapter {
        Context context;
        List<Foods> stringList;
        int selectedPosition = -1;

        public MyAdapter(Context context, List<Foods> stringList) {
            this.context = context;
            this.stringList = stringList;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int position) {
            return stringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.foods_layout, parent, false);
                holder = new ViewHolder();
                holder.txtFood = convertView.findViewById(R.id.txtFood);
                holder.imgFood = convertView.findViewById(R.id.imgFood);
                holder.btnContact = convertView.findViewById(R.id.btnContact);
                holder.btnDelete = convertView.findViewById(R.id.btnDelete); // Silme butonu
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Foods food = stringList.get(position);
            holder.txtFood.setText("Gıda Hakkında:" + food.getGida_adi() +
                    "\n Gönderen Adı: " + food.getIsim() +
                    "\n Gönderen Tel No: " + food.getTel_no() +
                    "\n Açıklama: " + food.getAciklama());

            if (food.getResim() != null && !food.getResim().isEmpty()) {
                byte[] imageAsByte = Base64.decode(food.getResim(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsByte, 0, imageAsByte.length);
                holder.imgFood.setImageBitmap(bitmap);
            } else {
                holder.imgFood.setImageBitmap(null);
            }

            holder.btnContact.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
            holder.btnDelete.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedPosition == position) {
                        selectedPosition = -1; // Deselect if clicked again
                    } else {
                        selectedPosition = position; // Select new item
                    }
                    notifyDataSetChanged(); // Update visibility
                }
            });

            holder.btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String telNo = food.getTel_no();
                    if (telNo != null && !telNo.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + telNo));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Telefon numarası bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Yalnızca kullanıcıya ait olan öğeler için silme butonu görünür
            if (food.getUserid() != null && food.getUserid().equals(currentUser.getUid())) {
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Ürün Siliniyor");
                        builder.setMessage("Bu ürünü silmek istediğinize emin misiniz?");
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Evete tıklandığında yapılacak işlemler
                                databaseReference.child(food.getIsim()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    foodsList.remove(position); // Listeden de kaldır
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context, "Ürün başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Ürün silinirken hata oluştu", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Hayıra tıklandığında yapılacak işlemler
                                // İşlem yapma, sadece dialogu kapat
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            } else {
                holder.btnDelete.setVisibility(View.GONE);
            }


            return convertView;
        }

        private class ViewHolder {
            TextView txtFood;
            ImageView imgFood;
            Button btnContact;
            Button btnDelete;
        }
    }
}
