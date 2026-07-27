package com.example.portalnexus.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.portalnexus.R;

public class ImageUtils {
    public static void loadImage(ImageView imageView, String url) {
        if (url == null || url.isEmpty()) {
            imageView.setImageResource(R.drawable.iconapp);
            return;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.iconapp)
                .error(R.drawable.iconapp)
                .into(imageView);
    }
}
