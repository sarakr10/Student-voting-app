package com.example.decideit;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DecideActivity extends AppCompatActivity implements View.OnClickListener {

    Button by;
    Button bn;
    Button ba;

    Button selectedButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_decide);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        by = findViewById(R.id.yes);
        bn = findViewById(R.id.no);
        ba = findViewById(R.id.abstrain);

        by.setOnClickListener(this);
        bn.setOnClickListener(this);
        ba.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int green = ContextCompat.getColor(this, R.color.green);
        int charcoal = ContextCompat.getColor(this, R.color.charcoal);
        if(selectedButton!=null){
            selectedButton.setBackgroundColor(charcoal);
        }
        selectedButton = (Button) v;
        selectedButton.setBackgroundColor(green);
    }
}