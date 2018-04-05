package com.gmail.tofibashers.blacklist.ui.common

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.CheckBox

import com.gmail.tofibashers.blacklist.R


/**
 * Created by TofiBashers on 29.03.2018.
 */
class StyledCheckBox : AppCompatCheckBox {
    constructor(context: Context) : super(ContextThemeWrapper(context, R.style.AppTheme_CheckBox)) {
        setStyle()
    }

    constructor(context: Context, attrs: AttributeSet) : super(ContextThemeWrapper(context, R.style.AppTheme_CheckBox), attrs) {
        setStyle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ContextThemeWrapper(context, R.style.AppTheme_CheckBox), attrs, defStyleAttr) {
        setStyle()
    }

    private fun setStyle() {
        isSaveEnabled = false
        setBackgroundColor(resources.getColor(android.R.color.transparent))
    }
}
