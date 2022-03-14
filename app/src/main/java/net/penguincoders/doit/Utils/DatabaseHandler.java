package net.penguincoders.doit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.penguincoders.doit.Model.BookModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TITULO = "titulo";
    private static final String AUTOR = "autor";
    private static final String EDITORIAL = "editorial";
    private static final String PRECIO = "precio";
    private static final String CATEGORIA = "categoria";
    private static final String AÑO = "año";
    private static final String IMAGEN = "imagen";
    private static final String STATUS = "status";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITULO + " TEXT, " +
            AUTOR + " TEXT, " +
            EDITORIAL + " TEXT, " +
            PRECIO + " TEXT, " +
            CATEGORIA + " TEXT, " +
            IMAGEN + " TEXT, " +
            AÑO + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertBook(BookModel person){
        ContentValues cv = new ContentValues();
        cv.put(TITULO, person.getTitulo());
        cv.put(AUTOR, person.getAutor());
        cv.put(EDITORIAL, person.getEditorial());
        cv.put(PRECIO, person.getEditorial());
        cv.put(CATEGORIA, person.getEditorial());
        cv.put(AÑO, person.getAño());
        cv.put(IMAGEN, person.getImagen());
        db.insert(TODO_TABLE, null, cv);
    }

    public List<BookModel> getAllBooks(){
        List<BookModel> bookList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        BookModel book = new BookModel();
                        book.setId(cur.getInt(cur.getColumnIndex(ID)));
                        book.setTitulo(cur.getString(cur.getColumnIndex(TITULO)));
                        book.setAutor(cur.getString(cur.getColumnIndex(AUTOR)));
                        book.setEditorial(cur.getString(cur.getColumnIndex(EDITORIAL)));
                        book.setEditorial(cur.getString(cur.getColumnIndex(PRECIO)));
                        book.setEditorial(cur.getString(cur.getColumnIndex(CATEGORIA)));
                        book.setAño(cur.getString(cur.getColumnIndex(AÑO)));
                        book.setImagen(cur.getString(cur.getColumnIndex(IMAGEN)));
                        bookList.add(book);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return bookList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateBook(int id, String titulo, String autor, String editorial, String año, String uriImage) {
        ContentValues cv = new ContentValues();
        cv.put(TITULO,titulo);
        cv.put(AUTOR, autor);
        cv.put(EDITORIAL, editorial);
        cv.put(PRECIO, editorial);
        cv.put(CATEGORIA, editorial);
        cv.put(AÑO, año);
        cv.put(IMAGEN, uriImage);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteBookk(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}




