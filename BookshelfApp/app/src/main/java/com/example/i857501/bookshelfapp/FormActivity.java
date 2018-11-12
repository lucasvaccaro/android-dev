package com.example.i857501.bookshelfapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormActivity extends AppCompatActivity {

    public static final String BUNDLE_BOOK = "book";

    Book book;
    BookService bookService;
    EditText title, author, nrOfPages, pubYear;
    Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        this.title = findViewById(R.id.title);
        this.author = findViewById(R.id.author);
        this.nrOfPages = findViewById(R.id.nr_of_pages);
        this.pubYear = findViewById(R.id.pub_year);
        this.category = findViewById(R.id.category);

        this.setupRetrofit();

        Intent intent = getIntent();
        if (intent.hasExtra(BUNDLE_BOOK)) {
            this.book = (Book) intent.getSerializableExtra(BUNDLE_BOOK);
            this.loadBook();
        } else {
            this.book = new Book();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.save:
                this.save();
                return true;
            case R.id.delete:
                this.delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_menu, menu);

        // Hide delete btn when adding book
        if (this.book.getId() == null) {
            menu.removeItem(R.id.delete);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bookapitt.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.bookService = retrofit.create(BookService.class);
    }

    private void loadBook() {
        this.title.setText(this.book.getTitle());
        this.author.setText(this.book.getAuthor());
        this.nrOfPages.setText(String.valueOf(this.book.getNumberOfPages()));
        this.pubYear.setText(String.valueOf(this.book.getPublicationYear()));

        int position = ((ArrayAdapter)this.category.getAdapter()).getPosition(book.getCategory());
        this.category.setSelection(position);
    }

    private void save() {
        String title = this.title.getText().toString();
        String author = this.author.getText().toString();
        String category = this.category.getSelectedItem().toString();
        int nrOfPages = Integer.parseInt(this.nrOfPages.getText().toString());
        int pubYear = Integer.parseInt(this.pubYear.getText().toString());

        this.book.setTitle(title);
        this.book.setAuthor(author);
        this.book.setCategory(category);
        this.book.setNumberOfPages(nrOfPages);
        this.book.setPublicationYear(pubYear);

        if (this.book.getId() != null) {
            this.update();
        } else {
            this.insert();
        }
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.do_you_conf)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookService.delete(book.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    // Response code is 200...299
                                    Toast.makeText(FormActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    // In case of server error
                                    Log.e("Retrofit", response.message());
                                    Toast.makeText(FormActivity.this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("Retrofit", t.getMessage());
                                Toast.makeText(FormActivity.this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .create()
                .show();
    }

    private void insert() {
        bookService.insert(this.book).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Response code is 200...299
                    Toast.makeText(FormActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // In case of server error
                    Log.e("Retrofit", response.message());
                    Toast.makeText(FormActivity.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Retrofit", t.getMessage());
                Toast.makeText(FormActivity.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update() {
        this.bookService.update(book.getId(), book).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Response code is 200...299
                    Toast.makeText(FormActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // In case of server error
                    Log.e("Retrofit", response.message());
                    Toast.makeText(FormActivity.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Retrofit", t.getMessage());
                Toast.makeText(FormActivity.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
