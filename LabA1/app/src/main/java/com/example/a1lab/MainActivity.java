package com.example.a1lab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v) {
        TextView tv = (TextView)findViewById(R.id.textView);

        EditText txt = (EditText) findViewById(R.id.editTextTextPersonName);
        String newText = txt.getText().toString();

        String text = tv.getText() + "\n" + newText;
        tv.setText(text);
    }
}