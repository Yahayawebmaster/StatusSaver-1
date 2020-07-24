package com.collabcreation.statussaver.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collabcreation.statussaver.Modal.Node;
import com.collabcreation.statussaver.R;
import com.hbb20.CountryCodePicker;

import java.net.MalformedURLException;
import java.net.URL;

public class DirectMessage extends AppCompatActivity {
    Toolbar toolbar;
    CountryCodePicker countryCodePicker;
    EditText message, number;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Direct Message");

        countryCodePicker = findViewById(R.id.ccp);
        number = findViewById(R.id.number);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        Bitmap bitmap;

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countryCodePicker.getSelectedCountryCode() != null && !number.getText().toString().trim().isEmpty() && number.getText().length() == 10 && !message.getText().toString().trim().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl(number.getText().toString().trim(), countryCodePicker.getSelectedCountryCode(), message.getText().toString())));
                    intent.setPackage("com.whatsapp");
                    startActivity(intent);

                }
            }
        });
    }

    private String whatsappUrl(String number, String countryCode, String message) {

        return "https://api.whatsapp.com/send"
                + "?phone=" + countryCode + number
                + "&text=" + message;
    }

    public URL buildUrl(Uri myUri) {

        URL finalUrl = null;
        try {
            finalUrl = new URL(myUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        return finalUrl;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
