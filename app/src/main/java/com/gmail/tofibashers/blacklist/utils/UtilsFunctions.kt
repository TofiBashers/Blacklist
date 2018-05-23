package com.gmail.tofibashers.blacklist.utils

import android.support.annotation.DrawableRes
import android.widget.ImageView
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.ui.common.CircleImageTranformation
import com.squareup.picasso.Picasso


/**
 * Created by TofiBashers on 27.04.2018.
 */

fun loadContactImageByUrlOrDefault(target: ImageView, imageUrl: String?) =
        loadContactImageByUrlOrPlaceholder(target, imageUrl, R.drawable.ic_contact_default_photo_48dp)

fun loadContactImageByUrlOrDisabledWithoutAlpha(target: ImageView, imageUrl: String?) =
        loadContactImageByUrlOrPlaceholder(target, imageUrl, R.drawable.ic_contact_disabled_photo_without_alpha_48dp)

private fun loadContactImageByUrlOrPlaceholder(target: ImageView, imageUrl: String?, @DrawableRes placeholderId: Int) {
    return Picasso.get()
            .load(imageUrl)
            .transform(CircleImageTranformation())
            .placeholder(placeholderId)
            .into(target)
}