package com.gmail.tofibashers.blacklist.ui.blacklist_contact_options

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.gmail.tofibashers.blacklist.R


/**
 * Provides divider decoration, that provides divider type depends on RecyclerView child view tag.
 * Created by TofiBashers on 19.05.2018.
 */
class BlacklistContactOptionsVerticalDividersByTagDecoration
/**
 * Creates a vertical dividers [RecyclerView.ItemDecoration] that can be used with a
 * [LinearLayoutManager].
 *
 * @param context Current context, it will be used to access resources.
 */
(context: Context) : RecyclerView.ItemDecoration() {

    private val gradientDivider: Drawable = ContextCompat.getDrawable(context,
            R.drawable.divider_blacklist_contact_options_gradient)!!
    private val solidLineDivider: Drawable = ContextCompat.getDrawable(context,
            R.drawable.divider_blacklist_contact_options_solid_line)!!

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.layoutManager == null) {
            return
        }
        drawDividers(c, parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val dividerType = view.getTag(R.id.blacklist_contact_options_divider_key) as DividerType?
        outRect.set(0, 0, 0, when(dividerType){
            DividerType.GRADIENT -> gradientDivider.intrinsicHeight
            DividerType.SOLID_LINE -> solidLineDivider.intrinsicHeight
            else -> throw RuntimeException("Unknown divider type")
        })
    }

    private fun drawDividers(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val dividerType = child.getTag(R.id.blacklist_contact_options_divider_key) as DividerType?
            drawChildDivider(canvas, parent, left, right, child, when(dividerType){
                DividerType.GRADIENT -> gradientDivider
                DividerType.SOLID_LINE -> solidLineDivider
                else -> throw RuntimeException("Unknown divider type")
            })
        }
        canvas.restore()
    }

    private fun drawChildDivider(canvas: Canvas,
                                 parent: RecyclerView,
                                 left: Int,
                                 right: Int,
                                 child: View,
                                 dividerDrawable: Drawable) {
        parent.getDecoratedBoundsWithMargins(child, mBounds)
        val bottom = mBounds.bottom + Math.round(child.translationY)
        val top = bottom - dividerDrawable.intrinsicHeight
        dividerDrawable.setBounds(left, top, right, bottom)
        dividerDrawable.draw(canvas)
    }
}
