package com.debugcc.myliferpg.Models;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.debugcc.myliferpg.R;

public class User {

    public static final String FACEBOOK_PROVIDER = "Facebook";
    public static final String GOOGLE_PROVIDER = "Google";

    private String id;
    private String name;
    private String email;
    private String urlProfilePicture;
    private String provider;

    public User() {
        id = "";
        name = "";
        email = "";
        urlProfilePicture = "";
        provider = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        //notifyPropertyChanged(com.debugcc.myliferpg.BR.id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlProfilePicture() {
        return urlProfilePicture;
    }

    public void setUrlProfilePicture(String urlProfilePicture) {
        this.urlProfilePicture = urlProfilePicture;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Log.e("modelo usuario", "loadImage: "+imageUrl );
        Glide.with(view.getContext())
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.img_placeholder_dark)
                .centerCrop()
                .error(R.drawable.img_placeholder_dark)
                .into(new BitmapImageViewTarget(view) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(view.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", urlProfilePicture='" + urlProfilePicture + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
