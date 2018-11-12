package com.example.i857501.bookshelfapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefresh;
    RecyclerView bookList;
    BookAdapter adapter;
    LinearLayoutManager manager;
    List<Book> books, allBooks;
    BookService bookService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.swipeRefresh = findViewById(R.id.swipe_refresh);
        this.bookList = findViewById(R.id.book_list);

        this.setupSwipeRefresh();
        this.setupList();
        this.setupRetrofit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.listBooks();
        this.swipeRefresh.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                this.openSearch();
                return true;
            case android.R.id.home:
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                search("");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(bookList.getApplicationWindowToken(), 0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSearch() {
        final EditText editText = new EditText(this);
        editText.setHint(getString(R.string.type_to_search));
        editText.setTextColor(getResources().getColor(android.R.color.white));
        editText.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        editText.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        editText.setSingleLine(true);
        editText.requestFocus();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(editText);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void search(String filter) {
        List<Book> filteredBooks = new ArrayList<>();

        for (Book b : this.allBooks) {
            if (b.getTitle().toLowerCase().contains(filter.toLowerCase())) {
                filteredBooks.add(b);
            }
        }

        this.books.clear();
        this.books.addAll(filteredBooks);
        this.adapter.notifyDataSetChanged();
    }

    private void setupSwipeRefresh() {
        this.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        this.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listBooks();
            }
        });
    }

    private void setupList() {
        this.books = new ArrayList<>();
        this.allBooks = new ArrayList<>();
        this.adapter = new BookAdapter(this, this.books);
        this.manager = new LinearLayoutManager(this);
        this.bookList.setAdapter(this.adapter);
        this.bookList.setLayoutManager(this.manager);
        this.bookList.setHasFixedSize(true); // for better performance

        DividerItemDecoration divider = new DividerItemDecoration(bookList.getContext(), manager.getOrientation());
        this.bookList.addItemDecoration(divider);

        this.adapter.notifyDataSetChanged();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://bookapitt.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.bookService = retrofit.create(BookService.class);
    }

    private void listBooks() {
        this.bookService.list().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                List<Book> data = response.body();
                books.clear();
                books.addAll(data);
                allBooks.clear();
                allBooks.addAll(data);
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("Retrofit", t.getMessage());
                Toast.makeText(MainActivity.this, getString(R.string.load_fail), Toast.LENGTH_SHORT);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void addBook(View view) {
        Intent intent = new Intent(MainActivity.this, FormActivity.class);
        startActivity(intent);
    }
}
