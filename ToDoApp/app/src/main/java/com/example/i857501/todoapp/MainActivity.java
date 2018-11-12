package com.example.i857501.todoapp;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText taskDescription;
    ListView taskList;
    ArrayAdapter<Task> adapter;
    List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.taskDescription = findViewById(R.id.description);
        this.taskList = findViewById(R.id.tasks_list);

        this.setupList();

        registerForContextMenu(this.taskList);
    }

    private void setupList() {
        this.tasks = new ArrayList<>();
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        this.taskList.setAdapter(adapter);

        this.taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) parent.getItemAtPosition(position);
                task.setDone(true);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void addTask(View view) {
        String description = this.taskDescription.getText().toString();

        if (!description.isEmpty()) {
            Task task = new Task(description);
            this.tasks.add(task);
            this.adapter.notifyDataSetChanged();
            this.taskDescription.setText("");
        }
    }

    private void deleteAllTasks() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.do_you_confirm)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tasks.clear();
                    adapter.notifyDataSetChanged();
                }
            })
            .create()
            .show();
    }

    private void deleteAllTasksDone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tasks.removeIf(Task::isDone);
        } else {
            for (Iterator<Task> iterator = this.tasks.iterator(); iterator.hasNext(); ) {
                Task t = iterator.next();
                if (t.isDone()) {
                    iterator.remove();
                }
            }
        }

        this.adapter.notifyDataSetChanged();
    }

    private void deleteTask(final int position) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.do_you_confirm)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasks.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                this.deleteAllTasks();
                return true;
            case R.id.delete_all_done:
                this.deleteAllTasksDone();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {
            case R.id.tasks_list:
                getMenuInflater().inflate(R.menu.task_menu, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position;
        switch (item.getItemId()) {
            case R.id.delete_task:
                position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                this.deleteTask(position);
                return true;
            case R.id.check_uncheck:
                position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                this.tasks.get(position).switchDone();
                this.adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }
}
