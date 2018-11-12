package com.example.i857501.contactsapp;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.i857501.contactsapp.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "app_database";

    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private AppDatabase appDatabase;

    ListView contactsList;
    ArrayAdapter<Contact> adapter;
    List<Contact> contacts;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.contactsList = findViewById(R.id.contact_list);
        this.setupContactsList();
        this.initDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ListAsyncTask().execute();
    }

    private void initDatabase() {
        this.appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    private void setupContactsList() {
        this.contacts = new ArrayList<>();
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.contacts);
        this.contactsList.setAdapter(adapter);

        this.contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                intent.putExtra(FormActivity.BUNDLE_CONTACT, contact);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        });

        this.contactsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        this.contactsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            List<Contact> selectedContacts;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Contact contact = contacts.get(position);
                if (checked) {
                    selectedContacts.add(contact);
                } else {
                    selectedContacts.remove(contact);
                }

                int count = selectedContacts.size();
                mode.setTitle(String.format("%d selected", count, getString(R.string.selected)));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                selectedContacts = new ArrayList<>();

                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, contacts);
                contactsList.setAdapter(adapter);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_contacts:
                        deleteContacts(selectedContacts);
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, contacts);
                contactsList.setAdapter(adapter);
            }
        });
    }

    private void deleteContacts(final List<Contact> selectedContacts) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.do_you_confirm)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteAsyncTask().execute(selectedContacts.toArray(new Contact[0]));
                    }
                })
                .create()
                .show();
    }

    public void addContact(View view) {
        Intent intent = new Intent(MainActivity.this, FormActivity.class);
        startActivityForResult(intent, REQUEST_ADD);
    }

    private Contact findContact(Contact contact) {
        for (Contact c : this.contacts) {
            if (c.getId().equals(contact.getId())) {
                return c;
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Contact contact = null;
        if (data != null && data.hasExtra(FormActivity.BUNDLE_CONTACT)) {
            contact = (Contact) data.getSerializableExtra(FormActivity.BUNDLE_CONTACT);
        }

        switch (resultCode) {
            case FormActivity.RESULT_SAVE:
                // insert will update if PK already exists
                new InsertAsyncTask().execute(contact);
                break;
            case FormActivity.RESULT_DELETE:
                new DeleteAsyncTask().execute(contact);
                break;
        }

        this.adapter.notifyDataSetChanged();
    }

    class InsertAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            Contact contact = contacts[0];
            appDatabase.contactDAO().insert(contact);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
        }
    }

    class DeleteAsyncTask extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            appDatabase.contactDAO().delete(contacts);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ListAsyncTask().execute();
            Toast.makeText(MainActivity.this, getString(R.string.contacts_deleted), Toast.LENGTH_SHORT).show();
            actionMode.finish();
        }
    }

    class ListAsyncTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            return appDatabase.contactDAO().list();
        }

        @Override
        protected void onPostExecute(List<Contact> data) {
            super.onPostExecute(data);
            contacts.clear();
            contacts.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }

}
