package com.example.travelmantics.utilities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.travelmantics.ProfileActivity;
import com.example.travelmantics.R;

import org.w3c.dom.Text;

public class TextGetterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_getter);

        Button submit = findViewById(R.id.submit);
        EditText editText = findViewById(R.id.name_getter);

        submit.setOnClickListener(view -> submit(editText));
    }

    private void submit(EditText editText) {
        String name = editText.getText().toString();
        if(name.equals("@string/enter_your_name")) {
            name = "";
        }

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, name);
        startActivity(intent);
    }
}