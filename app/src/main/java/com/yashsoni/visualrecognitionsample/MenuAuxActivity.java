package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuAuxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_aux);
        CircleImageView img = findViewById(R.id.imgAddPat);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(HomeIntent);
            }
        });
        CircleImageView addPhoto = findViewById(R.id.imgUpPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(),PhotoActivity.class);
                startActivity(HomeIntent);
            }
        });
        CircleImageView imgResultado = findViewById(R.id.imgUpPhoto);
        imgResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(HomeIntent);
            }
        });
    }
}
