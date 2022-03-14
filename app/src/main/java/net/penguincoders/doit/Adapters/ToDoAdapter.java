package net.penguincoders.doit.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import net.penguincoders.doit.AddNewBook;
import net.penguincoders.doit.MainActivity;
import net.penguincoders.doit.Model.BookModel;
import net.penguincoders.doit.R;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<BookModel> originalPersonList;
    private List<BookModel> personList;
    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final BookModel item = personList.get(position);
        holder.row.setText(item.getTitulo());
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setBook(List<BookModel> todoList) {
        this.originalPersonList = todoList;
        this.personList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        BookModel item = personList.get(position);
        db.deleteBookk(item.getId());
        personList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        BookModel item = personList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("titulo", item.getTitulo());
        bundle.putString("autor", item.getAutor());
        bundle.putString("editorial", item.getEditorial());
        bundle.putString("año", item.getAño());
        bundle.putString("imagen", item.getImagen());
        AddNewBook fragment = new AddNewBook();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewBook.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView row;

        ViewHolder(View view) {
            super(view);
            row = view.findViewById(R.id.textView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(final String stringSearch){
        if(stringSearch.length() == 0) {
            personList.clear();
            personList.addAll(originalPersonList);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<BookModel> collect = personList.stream()
                        .filter(i->i.getTitulo().toLowerCase().contains(stringSearch))
                        .collect(Collectors.<BookModel>toList());
                personList.clear();
                personList.addAll(collect);
            }else{
                personList.clear();
                for (BookModel i : personList) {
                    if (i.getTitulo().toLowerCase().contains(stringSearch)) {
                        personList.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
