package com.example.i857501.bookshelfapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookService {

    @GET("books")
    Call<List<Book>> list();

    @POST("books")
    Call<Void> insert(@Body Book b);

    @PUT("books/{id}")
    Call<Void> update(@Path("id") String id, @Body Book b);

    @DELETE("books/{id}")
    Call<Void> delete(@Path("id") String id);
}
