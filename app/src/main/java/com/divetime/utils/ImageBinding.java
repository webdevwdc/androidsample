package com.divetime.utils;

import android.databinding.BindingAdapter;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.divetime.R;

/**
 * Created by android on 12/7/17.
 */

public class ImageBinding {

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {

        String full_img_url=Constants.site_image+imageUrl;

        if(view!=null && view.getId()==R.id.image_article) // for home screen article section
        {
            full_img_url=Constants.articles_thumb_image_url+imageUrl;
        }

        ConstraintLayout constraintLayout=(ConstraintLayout) view.getParent();
        final ProgressBar progressBar=(ProgressBar)constraintLayout.findViewById(R.id.progressBar2);

        Glide.with(view.getContext()).load(full_img_url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                if(progressBar!=null)
                    progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(view);

    }

}
