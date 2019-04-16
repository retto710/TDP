package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

public class MenuAuxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_aux);

        Button btnRegister = (Button) findViewById(R.id.btnPaciente);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(HomeIntent);
            }
        });

        Button btnFoto = (Button) findViewById(R.id.btnFoto);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(),PhotoActivity.class);
                startActivity(HomeIntent);
            }
        });

        Button btnResultado = findViewById(R.id.btnResultado);
        btnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(HomeIntent);
            }
        });
    }
}
