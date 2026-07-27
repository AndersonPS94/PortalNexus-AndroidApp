package com.example.portalnexus.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.databinding.ItemCharacterBinding;
import com.example.portalnexus.utils.ImageUtils;

import java.util.Objects;

public class CharacterAdapter extends ListAdapter<Character, CharacterAdapter.ViewHolder> {

    private final OnCharacterClickListener listener;

    public CharacterAdapter(OnCharacterClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Character> DIFF_CALLBACK = new DiffUtil.ItemCallback<Character>() {
        @Override
        public boolean areItemsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Character oldItem, @NonNull Character newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    Objects.equals(oldItem.getImage(), newItem.getImage());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCharacterBinding binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCharacterBinding binding;

        public ViewHolder(ItemCharacterBinding binding) {
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

            itemView.setOnClickListener(v -> listener.onCharacterClick(character));
        }
    }

    public interface OnCharacterClickListener {
        void onCharacterClick(Character character);
    }
}
