package com.gmail.tofibashers.blacklist.utils

import android.widget.ImageView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.ui.common.CircleImageTranformation
import com.squareup.picasso.Picasso


/**
 * Created by TofiBashers on 27.04.2018.
 */

fun loadContactImageByUrlOrDefault(target: ImageView, imageUrl: String?) {
    return Picasso.get()
            .load(imageUrl)
            .transform(CircleImageTranformation())
            .placeholder(R.drawable.ic_contact_default_photo_48dp)
            .into(target)
}