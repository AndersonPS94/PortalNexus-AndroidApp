package com.example.portalnexus.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalnexus.R;
import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.databinding.ItemEmployeeBinding;
import com.example.portalnexus.utils.ImageUtils;

import java.util.Objects;

public class EmployeeAdapter extends ListAdapter<Employee, EmployeeAdapter.ViewHolder> {

    private final OnEmployeeActionListener listener;

    public EmployeeAdapter(OnEmployeeActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Employee> DIFF_CALLBACK = new DiffUtil.ItemCallback<Employee>() {
        @Override
        public boolean areItemsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getPosition(), newItem.getPosition()) &&
                    Objects.equals(oldItem.getEmail(), newItem.getEmail()) &&
                    Objects.equals(oldItem.getPhoto(), newItem.getPhoto()) &&
                    oldItem.isActive() == newItem.isActive();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeBinding binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmployeeBinding binding;

        public ViewHolder(ItemEmployeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Employee employee, OnEmployeeActionListener listener) {
            binding.txtName.setText(employee.getName() != null ? employee.getName() : "");
            binding.txtPosition.setText(employee.getPosition() != null ? employee.getPosition() : "");
            binding.txtEmail.setText(employee.getEmail() != null ? employee.getEmail() : "");

            if (employee.isActive()) {
                binding.txtStatus.setText(R.string.employee_active);
                binding.txtStatus.setTextColor(itemView.getContext().getColor(R.color.success));
                binding.statusBadge.setStrokeColor(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.success)));
            } else {
                binding.txtStatus.setText(R.string.employee_inactive);
                binding.txtStatus.setTextColor(itemView.getContext().getColor(R.color.onSurfaceVariant));
                binding.statusBadge.setStrokeColor(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(R.color.divider)));
            }

            ImageUtils.loadImage(binding.imgEmployee, employee.getPhoto());
            binding.imgEmployee.setContentDescription("Foto do funcionário " + employee.getName() + " - Cargo: " + employee.getPosition());

            itemView.setOnClickListener(v -> listener.onEdit(employee));
            binding.btnEdit.setOnClickListener(v -> listener.onEdit(employee));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(employee.getId()));
        }
    }

    public interface OnEmployeeActionListener {
        void onEdit(Employee employee);
        void onDelete(int id);
    }
}
