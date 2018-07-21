package com.gmail.tofibashers.blacklist.ui.common

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.gmail.tofibashers.blacklist.R


/**
 * Created by TofiBashers on 02.07.2018.
 * See [R.styleable#CardWrapperWithBottomElevation] for Attributes.
 * @attr ref R.styleable#WrapperWithElevation_wrapLayout
 */
class CardWrapperWithBottomElevation : RelativeLayout {

    constructor(context: Context) : super(context) { initCard(null, 0) }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initCard(attrs, 0) }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initCard(attrs, defStyleAttr) }

    private fun initCard(attrs: AttributeSet?, defStyleAttr: Int){

        val wrapperAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.CardWrapperWithBottomElevation,
                defStyleAttr,
                0)
        try {
            val layoutId = wrapperAttrs.getResourceId(R.styleable.CardWrapperWithBottomElevation_cardViewLayout, -1)
            if(layoutId == -1){
                throw RuntimeException("attribute 'layout' required")
            }

            val cardWrapperToolbar = LayoutInflater.from(context)
                    .inflate(layoutId, this, false) as? CardView ?:
                    throw RuntimeException("attribute 'cardViewLayout' must have a CardView parent")

            var defaultCardParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            defaultCardParams.addRule(ALIGN_PARENT_LEFT)
            defaultCardParams.addRule(ALIGN_PARENT_TOP)
            defaultCardParams.addRule(ALIGN_PARENT_RIGHT)
            cardWrapperToolbar.layoutParams = defaultCardParams
            post {
                val postDrawCardLayoutParams = cardWrapperToolbar.layoutParams as RelativeLayout.LayoutParams
                if(parent is AppBarLayout && (parent as AppBarLayout).getChildAt(0).id == id) {
                    val currentWrapperLayoutParams = layoutParams as MarginLayoutParams
                    currentWrapperLayoutParams.topMargin -= cardWrapperToolbar.paddingTop - cardWrapperToolbar.contentPaddingTop
                }
                else {
                    postDrawCardLayoutParams.topMargin -= cardWrapperToolbar.paddingTop - cardWrapperToolbar.contentPaddingTop
                }
                postDrawCardLayoutParams.leftMargin -= cardWrapperToolbar.paddingLeft - cardWrapperToolbar.contentPaddingLeft
                postDrawCardLayoutParams.rightMargin -= cardWrapperToolbar.paddingRight - cardWrapperToolbar.contentPaddingRight
                cardWrapperToolbar.requestLayout()
            }
            addView(cardWrapperToolbar)
        } finally {
            wrapperAttrs.recycle()
        }
    }
}