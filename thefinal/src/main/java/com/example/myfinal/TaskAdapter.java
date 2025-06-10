package com.example.myfinal;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    // Optional: Anda bisa menambahkan Listener jika ingin item bisa diklik atau checkbox diubah
    private OnItemClickListener listener;
    private OnTaskStatusChangeListener statusChangeListener;


    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    // Interface untuk klik item (opsional, tapi berguna untuk navigasi ke detail)
    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    // Interface untuk perubahan status checkbox (opsional, tapi penting untuk interaktivitas)
    public interface OnTaskStatusChangeListener {
        void onTaskStatusChanged(Task task, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnTaskStatusChangeListener(OnTaskStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Meng-inflate layout item_task.xml untuk setiap item di RecyclerView
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Mengisi data dari objek Task ke view di ViewHolder
        Task currentTask = taskList.get(position);
        holder.tvTaskTitle.setText(currentTask.getTitle());
        holder.checkBoxTask.setChecked(currentTask.isCompleted());

        // Terapkan efek coret jika tugas sudah selesai
        if (currentTask.isCompleted()) {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Handle klik checkbox
        holder.checkBoxTask.setOnCheckedChangeListener(null); // Penting untuk menghindari loop tak terbatas saat item di-recycle
        holder.checkBoxTask.setChecked(currentTask.isCompleted());
        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (statusChangeListener != null) {
                // Beri tahu Fragment/ViewModel bahwa status tugas berubah
                statusChangeListener.onTaskStatusChanged(currentTask, isChecked);
            }
        });


        // Handle klik seluruh item (misalnya untuk membuka detail tugas)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah total item dalam daftar
        return taskList.size();
    }

    // Method untuk memperbarui data di RecyclerView
    public void setTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged(); // Memberitahu RecyclerView bahwa data telah berubah
    }

    // Inner class ViewHolder: Menampung referensi ke View dari setiap item
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle;
        CheckBox checkBoxTask;

        TaskViewHolder(View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
        }
    }
}