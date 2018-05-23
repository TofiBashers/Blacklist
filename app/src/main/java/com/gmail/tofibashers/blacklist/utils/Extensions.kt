package com.gmail.tofibashers.blacklist.utils

import android.os.Build
import android.support.annotation.FloatRange
import android.widget.ImageView


/**
 * Created by TofiBashers on 23.05.2018.
 */

fun ImageView.setAlphaCompat(@FloatRange(from=0.0, to=1.0) imgAlpha: Float){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        imageAlpha = Math.round(255*imgAlpha)
    }
    else{
        alpha = imgAlpha
    }
}