package com.example.i857501.counterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int value;

    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.value = 0;
        this.number = findViewById(R.id.number);
        this.updateNumberField();
    }

    private void updateValue() {
        String number = this.number.getText().toString();
        try {
            this.value = Integer.parseInt(number);
        } catch (Exception e) {
            this.value = 0;
        }
    }

    private void updateNumberField() {
        this.number.setText(String.format("%s", this.value));
    }

    public void increment(View view) {
        this.updateValue();
        this.value++;
        updateNumberField();
    }

    public void decrement(View view) {
        this.updateValue();
        this.value--;
        updateNumberField();
    }

    public void showToast(View view) {
        this.updateValue();
        Toast.makeText(this, String.format("Value is %s", this.value), Toast.LENGTH_LONG).show();
    }
}
