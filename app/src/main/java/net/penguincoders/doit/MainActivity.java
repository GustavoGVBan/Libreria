package net.penguincoders.doit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.penguincoders.doit.Adapters.ToDoAdapter;
import net.penguincoders.doit.Model.BookModel;
import net.penguincoders.doit.Utils.DatabaseHandler;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private SearchView searchView;
    private RecyclerView booksRecyclerView;
    private ToDoAdapter bookAdapter;
    private FloatingActionButton fab;

    private List<BookModel> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        searchView = findViewById(R.id.search_view);
        booksRecyclerView = findViewById(R.id.bookRecyclerView);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new ToDoAdapter(db,MainActivity.this);
        booksRecyclerView.setAdapter(bookAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(bookAdapter));
        itemTouchHelper.attachToRecyclerView(booksRecyclerView);

        fab = findViewById(R.id.fab);

        bookList = db.getAllBooks();
        Collections.reverse(bookList);
        bookAdapter.setBook(bookList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewBook.newInstance().show(getSupportFragmentManager(), AddNewBook.TAG);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("well", " this worked");
                bookAdapter.filter(newText);
                return false;
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        bookList = db.getAllBooks();
        Collections.reverse(bookList);
        bookAdapter.setBook(bookList);
        bookAdapter.notifyDataSetChanged();
    }
}