package com.example.i857501.bookshelfapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.Normalizer;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Book> items;

    public BookAdapter(Context context, List<Book> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        return new ItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Book book = this.items.get(i);

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.publicationYear.setText(String.format("%d", book.getPublicationYear()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(FormActivity.BUNDLE_BOOK, book);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, publicationYear;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.author = itemView.findViewById(R.id.author);
            this.publicationYear = itemView.findViewById(R.id.publication_year);
        }
    }
}
