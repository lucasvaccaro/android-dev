package com.example.i857501.contactsapp.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.i857501.contactsapp.Contact;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ContactDAO {

    @Insert(onConflict = REPLACE)
    void insert(Contact c);

    @Delete
    void delete(Contact... c);

    @Query("SELECT * FROM contact ORDER BY name")
    List<Contact> list();
}
