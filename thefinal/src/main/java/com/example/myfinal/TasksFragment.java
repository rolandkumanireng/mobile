package com.example.myfinal;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksFragment extends Fragment {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private RecyclerView tasksRecyclerView;
    private FloatingActionButton fabAddTask;

    private TextView tvQuote;
    private TextView tvQuoteAuthor;
    private Button btnRefreshQuote;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(new ArrayList<>());
        tasksRecyclerView.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTasks(tasks);
        });

        taskAdapter.setOnTaskStatusChangeListener((task, isChecked) -> {
            task.setCompleted(isChecked);
            taskViewModel.update(task);
        });

        taskAdapter.setOnItemClickListener(task -> {
            // TODO: Implementasi Intent untuk membuka TaskDetailActivity atau dialog edit
        });

        fabAddTask = view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tombol Tambah ditekan!", Toast.LENGTH_SHORT).show();
            showAddTaskDialog();
        });

        // Inisialisasi komponen API
        tvQuote = view.findViewById(R.id.tvQuote);
        tvQuoteAuthor = view.findViewById(R.id.tvQuoteAuthor);
        btnRefreshQuote = view.findViewById(R.id.btnRefreshQuote);

        fetchRandomQuote();

        if (btnRefreshQuote != null) {
            btnRefreshQuote.setOnClickListener(v -> fetchRandomQuote());
        }
    }

    private void showAddTaskDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        final EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        Button btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        final android.app.AlertDialog dialog = builder.create();

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String description = etTaskDescription.getText().toString().trim();

            if (!title.isEmpty()) {
                Task newTask = new Task(title, description, false);
                taskViewModel.insert(newTask);
                dialog.dismiss();
            } else {
                etTaskTitle.setError("Judul tidak boleh kosong!");
            }
        });

        dialog.show();
    }

    private void fetchRandomQuote() {
        if (!isAdded()) {
            return;
        }

        tvQuote.setText("Memuat Kutipan...");
        tvQuoteAuthor.setText("");
        if (btnRefreshQuote != null) btnRefreshQuote.setVisibility(View.GONE);

        QuoteApiService apiService = RetrofitClient.getApiService();
        Call<Quote> call = apiService.getRandomQuote();

        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(@NonNull Call<Quote> call, @NonNull Response<Quote> response) {
                if (isAdded()) { // Pastikan fragment masih melekat
                    if (response.isSuccessful() && response.body() != null) {
                        Quote quote = response.body();
                        tvQuote.setText(quote.getContent());
                        tvQuoteAuthor.setText("- " + quote.getAuthor());
                        if (btnRefreshQuote != null) btnRefreshQuote.setVisibility(View.GONE);
                    } else {
                        tvQuote.setText("Gagal memuat kutipan.");
                        tvQuoteAuthor.setText("");
                        if (btnRefreshQuote != null) btnRefreshQuote.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Quote> call, @NonNull Throwable t) {
                if (isAdded()) { // Pastikan fragment masih melekat
                    tvQuote.setText("Gagal memuat kutipan. Periksa koneksi.");
                    tvQuoteAuthor.setText("");
                    Toast.makeText(getContext(), "Error API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    if (btnRefreshQuote != null) btnRefreshQuote.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}