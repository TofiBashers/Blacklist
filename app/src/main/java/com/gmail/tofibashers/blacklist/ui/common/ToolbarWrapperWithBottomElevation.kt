package com.gmail.tofibashers.blacklist.ui.common

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.gmail.tofibashers.blacklist.R


/**
 * Created by TofiBashers on 30.03.2018.
 */
class ToolbarWrapperWithBottomElevation : RelativeLayout {

    constructor(context: Context) : super(context) { initCard() }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initCard() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initCard() }

    private fun initCard(){
        val cardWrapperToolbar = LayoutInflater.from(context)
                .inflate(R.layout.partial_card_wrapper_toolbar, this, false) as CardView
        var defaultCardParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        defaultCardParams.addRule(ALIGN_PARENT_LEFT)
        defaultCardParams.addRule(ALIGN_PARENT_TOP)
        cardWrapperToolbar.layoutParams = defaultCardParams
        cardWrapperToolbar.post {
            var postDrawCardLayoutParams = cardWrapperToolbar.layoutParams as RelativeLayout.LayoutParams
            postDrawCardLayoutParams.topMargin -= cardWrapperToolbar.paddingTop - cardWrapperToolbar.contentPaddingTop
            postDrawCardLayoutParams.leftMargin -= cardWrapperToolbar.paddingLeft - cardWrapperToolbar.contentPaddingLeft
            postDrawCardLayoutParams.rightMargin -= cardWrapperToolbar.paddingRight - cardWrapperToolbar.contentPaddingRight
            cardWrapperToolbar.requestLayout()
        }
        addView(cardWrapperToolbar)
    }
}