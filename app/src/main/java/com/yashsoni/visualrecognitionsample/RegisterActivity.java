package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnRegister = (Button) findViewById(R.id.btnGuardar);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent= new Intent(getApplicationContext(),MenuAuxActivity.class);
                startActivity(HomeIntent);
            }
        });


    }
}
