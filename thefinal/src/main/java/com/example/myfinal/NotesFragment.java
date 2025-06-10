package com.example.myfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private Button btnToggleTheme;
    private TextView tvHeaderNotes; // Untuk header notes
    private RecyclerView notesRecyclerView;
    private NoteAdapter noteAdapter;
    private NoteViewModel noteViewModel;
    private FloatingActionButton fabAddNote;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        tvHeaderNotes = view.findViewById(R.id.tvHeaderNotes); // Inisialisasi header

        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new NoteAdapter(); // Inisialisasi tanpa list awal
        notesRecyclerView.setAdapter(noteAdapter);

        // Inisialisasi NoteViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Observasi LiveData dari ViewModel
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            noteAdapter.setNotes(notes); // Perbarui RecyclerView setiap kali data catatan berubah
        });

        // Setup listener untuk klik item catatan (untuk edit/lihat detail)
        noteAdapter.setOnItemClickListener(note -> {
            showAddEditNoteDialog(note); // Buka dialog untuk edit catatan
        });

        // Inisialisasi tombol ganti tema
        btnToggleTheme = view.findViewById(R.id.btnToggleTheme);
        btnToggleTheme.setOnClickListener(v -> toggleTheme());

        // Inisialisasi FAB untuk menambah catatan
        fabAddNote = view.findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(v -> showAddEditNoteDialog(null)); // null berarti menambah catatan baru

        return view;
    }

    private void toggleTheme() {
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        String message;
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO);
            message = "Tema Terang Diaktifkan";
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES);
            message = "Tema Gelap Diaktifkan";
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        requireActivity().recreate();
    }

    private void saveThemePreference(int mode) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("theme_mode", mode);
        editor.apply();
    }

    public static void loadThemePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        int savedTheme = preferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }

    // Metode untuk menampilkan dialog tambah/edit catatan
    private void showAddEditNoteDialog(@Nullable final Note noteToEdit) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_note, null); // Akan kita buat layout ini
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.tvDialogTitle); // Tambahkan ID ini di dialog_add_note.xml
        final EditText etNoteTitle = dialogView.findViewById(R.id.etNoteTitle);
        final EditText etNoteContent = dialogView.findViewById(R.id.etNoteContent);
        Button btnSaveNote = dialogView.findViewById(R.id.btnSaveNote);
        Button btnDeleteNote = dialogView.findViewById(R.id.btnDeleteNote); // Untuk opsi hapus

        final android.app.AlertDialog dialog = builder.create();

        if (noteToEdit != null) {
            dialogTitle.setText("Edit Catatan");
            etNoteTitle.setText(noteToEdit.getTitle());
            etNoteContent.setText(noteToEdit.getContent());
            btnDeleteNote.setVisibility(View.VISIBLE); // Tampilkan tombol hapus jika mengedit
        } else {
            dialogTitle.setText("Tambah Catatan Baru");
            btnDeleteNote.setVisibility(View.GONE); // Sembunyikan tombol hapus jika menambah baru
        }

        btnSaveNote.setOnClickListener(v -> {
            String title = etNoteTitle.getText().toString().trim();
            String content = etNoteContent.getText().toString().trim();

            if (!title.isEmpty() || !content.isEmpty()) {
                if (noteToEdit != null) {
                    // Update catatan yang sudah ada
                    noteToEdit.setTitle(title);
                    noteToEdit.setContent(content);
                    noteToEdit.setTimestamp(System.currentTimeMillis());
                    noteViewModel.update(noteToEdit);
                } else {
                    // Tambah catatan baru
                    Note newNote = new Note(title, content, System.currentTimeMillis());
                    noteViewModel.insert(newNote);
                }
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Judul atau isi catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteNote.setOnClickListener(v -> {
            if (noteToEdit != null) {
                noteViewModel.delete(noteToEdit);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}