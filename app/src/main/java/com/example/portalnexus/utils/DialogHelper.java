package com.example.portalnexus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.portalnexus.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogHelper {

    private static AlertDialog loadingDialog;

    public static void showLoading(Context context, String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) return;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView txtMessage = view.findViewById(R.id.txtLoadingMessage);
        txtMessage.setText(message);

        loadingDialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .create();
        loadingDialog.show();
    }

    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public static void showError(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showConfirmation(Context context, String title, String message, Runnable onConfirm) {
        showCustomDialog(context, android.R.drawable.ic_dialog_alert, title, message, "Sim", onConfirm, context.getColor(R.color.primary));
    }

    public static void showDeleteConfirmation(Context context, String title, String message, Runnable onConfirm) {
        showCustomDialog(context, android.R.drawable.ic_delete, title, "⚠️ " + message, "Excluir", onConfirm, context.getColor(R.color.error));
    }

    public static void showLogoutConfirmation(Context context, Runnable onConfirm) {
        showCustomDialog(context, android.R.drawable.ic_lock_power_off, "Encerrar Sessão", "Deseja realmente sair do Portal Nexus?", "Sair", onConfirm, context.getColor(R.color.warning));
    }

    public static void showPermissionSettingsDialog(Context context, String title, String message) {
        showCustomDialog(context, R.drawable.iconapp, title, message, "Configurações", () -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        });
    }

    public static void showCustomDialog(Context context, int iconRes, String title, String message, String confirmLabel, Runnable onConfirm) {
        showCustomDialog(context, iconRes, title, message, confirmLabel, onConfirm, context.getColor(R.color.primary));
    }

    private static void showCustomDialog(Context context, int iconRes, String title, String message, String confirmLabel, Runnable onConfirm, int accentColor) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null);
        
        ImageView icon = view.findViewById(R.id.dialogIcon);
        TextView txtTitle = view.findViewById(R.id.dialogTitle);
        TextView txtMsg = view.findViewById(R.id.dialogMessage);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirm);

        icon.setImageResource(iconRes);
        txtTitle.setText(title);
        txtMsg.setText(message);
        btnConfirm.setText(confirmLabel);

        icon.setImageTintList(ColorStateList.valueOf(accentColor));
        btnConfirm.setBackgroundColor(accentColor);
        btnConfirm.setTextColor(context.getColor(R.color.white));
        txtTitle.setTextColor(accentColor);

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            onConfirm.run();
            dialog.dismiss();
        });

        dialog.show();
    }
}
