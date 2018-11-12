package com.example.i857501.contactsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity {

    public static final String BUNDLE_CONTACT = "contact";

    public static final int RESULT_SAVE = 1;
    public static final int RESULT_DELETE = 2;

    EditText name, email, phone;
    Contact contact;

    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        this.name = findViewById(R.id.name);
        this.email = findViewById(R.id.email);
        this.phone = findViewById(R.id.phone);

        Intent intent = getIntent();
        if (intent.hasExtra(BUNDLE_CONTACT)) {
            this.contact = (Contact) intent.getSerializableExtra(BUNDLE_CONTACT);
            this.loadContact();
            this.isEdit = true;
        } else {
            this.contact = new Contact();
        }
    }

    private void loadContact() {
        this.name.setText(this.contact.getName());
        this.email.setText(this.contact.getEmail());
        this.phone.setText(this.contact.getPhone());
    }

    public void save(View view) {
        this.contact.setName(this.name.getText().toString());
        this.contact.setEmail(this.email.getText().toString());
        this.contact.setPhone(this.phone.getText().toString());

        Intent result = new Intent();
        result.putExtra(BUNDLE_CONTACT, contact);

        setResult(RESULT_SAVE, result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // To resume the main act, not recreate it (same behavior as the back btn)
                onBackPressed();
                return true;
            case R.id.delete_contact:
                this.deleteContact();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.do_you_confirm)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_CONTACT, contact);
                        setResult(RESULT_DELETE, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.form_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
