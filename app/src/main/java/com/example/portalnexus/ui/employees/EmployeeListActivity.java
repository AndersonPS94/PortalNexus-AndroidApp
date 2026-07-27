package com.example.portalnexus.ui.employees;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.portalnexus.adapter.EmployeeAdapter;
import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.databinding.ActivityEmployeeListBinding;
import com.example.portalnexus.utils.DialogHelper;
import com.example.portalnexus.utils.SnackbarHelper;
import com.example.portalnexus.viewmodel.EmployeeViewModel;

public class EmployeeListActivity extends AppCompatActivity {

    private ActivityEmployeeListBinding binding;
    private EmployeeViewModel viewModel;
    private EmployeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityEmployeeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            binding.fabAdd.setTranslationY(-insets.bottom);
            return windowInsets;
        });

        viewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();
        
        viewModel.loadEmployees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadEmployees();
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAdapter(new EmployeeAdapter.OnEmployeeActionListener() {
            @Override
            public void onEdit(Employee employee) {
                Intent intent = new Intent(EmployeeListActivity.this, EmployeeFormActivity.class);
                intent.putExtra("employee", employee);
                startActivity(intent);
            }

            @Override
            public void onDelete(int id) {
                DialogHelper.showDeleteConfirmation(EmployeeListActivity.this, 
                        "Excluir Funcionário", 
                        "Deseja realmente remover este colaborador do nexo?", 
                        () -> viewModel.deleteEmployee(id));
            }
        });

        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEmployees.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.employees.observe(this, employees -> {
            adapter.submitList(employees);
            binding.swipeRefresh.setRefreshing(false);
        });

        viewModel.isLoading.observe(this, isLoading -> {
            if (!binding.swipeRefresh.isRefreshing()) {
                if (isLoading && adapter.getItemCount() == 0) {
                    DialogHelper.showLoading(this, "Carregando funcionários...");
                } else {
                    DialogHelper.hideLoading();
                }
            }
        });

        viewModel.error.observe(this, error -> {
            binding.swipeRefresh.setRefreshing(false);
            if (error != null) {
                SnackbarHelper.show(binding.getRoot(), error, SnackbarHelper.Type.ERROR);
            }
        });
        
        viewModel.operationSuccess.observe(this, success -> {
            if (success != null && success) {
                viewModel.resetOperationSuccess();
                viewModel.loadEmployees();
                SnackbarHelper.show(binding.getRoot(), "Operação realizada com sucesso!", SnackbarHelper.Type.SUCCESS);
            }
        });
    }

    private void setupListeners() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadEmployees());

        binding.fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(EmployeeListActivity.this, EmployeeFormActivity.class));
        });
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }
}
