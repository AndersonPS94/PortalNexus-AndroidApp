package com.example.portalnexus.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.databinding.ItemCharacterBinding;
import com.example.portalnexus.databinding.ItemLoadingFooterBinding;
import com.example.portalnexus.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharacterAdapter extends ListAdapter<Character, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private final OnCharacterClickListener listener;
    private boolean isLoading = false;

    public CharacterAdapter(OnCharacterClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Character> DIFF_CALLBACK = new DiffUtil.ItemCallback<Character>() {
        @Override
        public boolean areItemsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
            if (oldItem.getId() == -1 || newItem.getId() == -1) return false;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    Objects.equals(oldItem.getImage(), newItem.getImage());
        }
    };

    @Override
    public int getItemViewType(int position) {
        if (isLoading && position == getItemCount() - 1) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            ItemLoadingFooterBinding loadingBinding = ItemLoadingFooterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(loadingBinding);
        }
        ItemCharacterBinding binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(getItem(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        return isLoading ? count + 1 : count;
    }

    public void setLoading(boolean loading) {
        if (this.isLoading != loading) {
            this.isLoading = loading;
            if (loading) {
                notifyItemInserted(super.getItemCount());
            } else {
                notifyItemRemoved(super.getItemCount());
            }
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemCharacterBinding binding;

        public ItemViewHolder(ItemCharacterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Character character, OnCharacterClickListener listener) {
            binding.txtName.setText(character.getName() != null ? character.getName() : "");
            binding.txtStatus.setText(String.format("%s - %s - %s", 
                    character.getStatus() != null ? character.getStatus() : "Unknown", 
                    character.getSpecies() != null ? character.getSpecies() : "Unknown", 
                    character.getGender() != null ? character.getGender() : "Unknown"));
            
            String locationName = (character.getLocation() != null && character.getLocation().getName() != null) 
                    ? character.getLocation().getName() : "Unknown";
            binding.txtLastLocation.setText(locationName);
            
            ImageUtils.loadImage(binding.imgCharacter, character.getImage());

            int color;
            String status = character.getStatus() != null ? character.getStatus().toLowerCase() : "";
            switch (status) {
                case "alive": color = Color.GREEN; break;
                case "dead": color = Color.RED; break;
                default: color = Color.GRAY; break;
            }
            GradientDrawable background = (GradientDrawable) binding.statusIndicator.getBackground();
            background.setColor(color);

            binding.imgCharacter.setTransitionName("hero_" + character.getId());
            binding.imgCharacter.setContentDescription("Foto de " + character.getName() + " - Status: " + character.getStatus());

            itemView.setOnClickListener(v -> listener.onCharacterClick(character, binding.imgCharacter));
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(ItemLoadingFooterBinding binding) {
            super(binding.getRoot());
        }
    }

    public interface OnCharacterClickListener {
        void onCharacterClick(Character character, View sharedView);
    }
}
