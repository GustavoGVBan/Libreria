package net.penguincoders.doit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.penguincoders.doit.Model.BookModel;
import net.penguincoders.doit.Utils.DatabaseHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AddNewBook extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    public static final int PICK_IMAGE = 1;

    private String uriImage;

    private EditText editTextTitulo;
    private EditText editTextAutor;
    private EditText editTextEditorial;
    private EditText editTextAño;
    private Button imageButton;
    private ImageView imageView;
    private Button saveButton;

    private DatabaseHandler db;

    public static AddNewBook newInstance(){
        return new AddNewBook();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_book, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextTitulo = Objects.requireNonNull(getView()).findViewById(R.id.newTitleText);
        editTextAutor = Objects.requireNonNull(getView()).findViewById(R.id.newAutorText);
        editTextEditorial = Objects.requireNonNull(getView()).findViewById(R.id.newEditorialText);
        editTextAño = Objects.requireNonNull(getView()).findViewById(R.id.newAñoText);
        imageButton = getView().findViewById(R.id.imageButton);
        imageView = getView().findViewById(R.id.imageView);
        saveButton = getView().findViewById(R.id.newButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String titulo = bundle.getString("titulo");
            String autor = bundle.getString("autor");
            String editorial = bundle.getString("editorial");
            String año = bundle.getString("año");
            String imagen = bundle.getString("imagen");

            editTextTitulo.setText(titulo);
            editTextAutor.setText(autor);
            editTextEditorial.setText(editorial);
            editTextAño.setText(año);

            assert titulo != null;
            if(titulo.length()>0)
                saveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        editTextTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    saveButton.setEnabled(false);
                    saveButton.setTextColor(Color.GRAY);
                }
                else{
                    saveButton.setEnabled(true);
                    saveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });



        final boolean finalIsUpdate = isUpdate;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = editTextTitulo.getText().toString();
                String autor = editTextAutor.getText().toString();
                String editorial = editTextEditorial.getText().toString();
                String año = editTextAño.getText().toString();


                if(finalIsUpdate){
                    db.updateBook(bundle.getInt("id"), titulo, autor, editorial, año, uriImage);
                }
                else {
                    BookModel person = new BookModel();
                    person.setTitulo(titulo);
                    person.setAutor(autor);
                    person.setEditorial(editorial);
                    person.setAño(año);
                    person.setImagen(uriImage);
                    db.insertBook(person);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                uriImage = String.valueOf(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
