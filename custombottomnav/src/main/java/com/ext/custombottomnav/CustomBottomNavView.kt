package com.ext.custombottomnav

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat

class CustomBottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private var tabRadiusTop = 0f
    private var tabRadiusBottom = 0f
    private var tabTextSize = 12f
    private var iconSize = 24
    private var activeScale = 1.2f
    private var duration = 200L

    private var tabFont: Typeface? = null
    private var tabBgColor = Color.WHITE
    private var indicatorColor = Color.BLUE

    private var navPaddingStart = 5
    private var navPaddingTop = 5
    private var navPaddingEnd = 5
    private var navPaddingBottom = 5


    private val tabs = mutableListOf<BottomNavItem>()
    private var onTabSelected: ((Int) -> Unit)? = null

    private var selectedIndex = 0

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        if (!isInEditMode) {
            clipChildren = false
            clipToPadding = false

            val a = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomNavView)
            tabRadiusTop = a.getDimension(R.styleable.CustomBottomNavView_tabRadiusTop, 0f)
            tabRadiusBottom = a.getDimension(R.styleable.CustomBottomNavView_tabRadiusBottom, 0f)
            tabTextSize = a.getDimension(R.styleable.CustomBottomNavView_tabTextSize, 12f)
            iconSize = a.getDimensionPixelSize(R.styleable.CustomBottomNavView_iconSize, 24)
            activeScale = a.getFloat(R.styleable.CustomBottomNavView_activeTabScale, 1.2f)
            tabBgColor = a.getColor(R.styleable.CustomBottomNavView_tabBackgroundColor, Color.WHITE)
            indicatorColor = a.getColor(R.styleable.CustomBottomNavView_indicatorColor, Color.BLUE)
            duration = a.getInt(R.styleable.CustomBottomNavView_animationDuration, 200).toLong()

            val fontId = a.getResourceId(R.styleable.CustomBottomNavView_tabFontFamily, -1)
            if (fontId != -1) {
                tabFont = ResourcesCompat.getFont(context, fontId)
            }
            a.recycle()
            applyNavBarBackground()
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeightPx = (58 * resources.displayMetrics.density).toInt() // 58dp → px

        val originalHeight = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val finalHeight = maxOf(originalHeight, minHeightPx)

        val newHeightSpec = MeasureSpec.makeMeasureSpec(finalHeight, heightMode)

        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    fun setTabs(tabList: List<BottomNavItem>) {
        require(tabList.size in 2..6) { "Tabs must be between 2 and 6" }
        tabs.clear()
        tabs.addAll(tabList)
        createTabs()
    }

    fun setNavPadding(startDp: Int, topDp: Int, endDp: Int, bottomDp: Int) {
        navPaddingStart = startDp
        navPaddingTop = topDp
        navPaddingEnd = endDp
        navPaddingBottom = bottomDp
        applyNavBarBackground()
    }

    fun setOnTabSelectedListener(listener: (Int) -> Unit) {
        onTabSelected = listener
    }

    private fun createTabs() {
        removeAllViews()

        tabs.forEachIndexed { index, item ->

            val view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_nav_item, this, false)

            val icon = view.findViewById<ImageView>(R.id.icon)
            val title = view.findViewById<TextView>(R.id.title)
            val badge = view.findViewById<TextView>(R.id.badge)
            val indicator = view.findViewById<View>(R.id.indicator)
            val container = view.findViewById<LinearLayout>(R.id.tabContainer)

            container.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)

            icon.setImageDrawable(item.icon)
            icon.layoutParams.height = iconSize
            icon.layoutParams.width = iconSize

            title.text = item.title
            title.textSize = tabTextSize / resources.displayMetrics.density

            tabFont?.let { title.typeface = it }

            if (item.badgeCount > 0) {
                badge.text = item.badgeCount.toString()
                badge.visibility = VISIBLE
            }

            view.setOnClickListener {
                selectTab(index)
                onTabSelected?.invoke(index)
            }

            // First tab selected
            if (index == selectedIndex) {
                applySelected(icon, title, indicator, container, item)
            } else {
                applyUnSelected(icon, title, indicator, container, item)
            }

            addView(view)
        }
    }

    private fun selectTab(index: Int) {
        selectedIndex = index

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val icon = child.findViewById<ImageView>(R.id.icon)
            val title = child.findViewById<TextView>(R.id.title)
            val indicator = child.findViewById<View>(R.id.indicator)
            val container = child.findViewById<LinearLayout>(R.id.tabContainer)

            if (i == index) {
                applySelected(icon, title, indicator, container, tabs[i])
            } else {
                applyUnSelected(icon, title, indicator, container, tabs[i])
            }
        }
    }

    private fun applySelected(
        icon: ImageView,
        title: TextView,
        indicator: View,
        container: LinearLayout,
        item: BottomNavItem
    ) {
        icon.setColorFilter(item.selectedIconColor)
        title.setTextColor(item.selectedTextColor)

        indicator.setBackgroundColor(indicatorColor)
        indicator.visibility = VISIBLE

        animateScale(container, activeScale)
    }

    private fun applyUnSelected(
        icon: ImageView,
        title: TextView,
        indicator: View,
        container: LinearLayout,
        item: BottomNavItem
    ) {
        icon.setColorFilter(item.unSelectedIconColor)
        title.setTextColor(item.unSelectedTextColor)

        indicator.visibility = INVISIBLE
        animateScale(container, 1f)
    }

    private fun animateScale(view: LinearLayout, scale: Float) {
        ObjectAnimator.ofFloat(view, "scaleX", scale).setDuration(duration).start()
        ObjectAnimator.ofFloat(view, "scaleY", scale).setDuration(duration).start()
    }

    private fun applyNavBarBackground() {
        val bg = GradientDrawable()
        bg.cornerRadii = floatArrayOf(
            tabRadiusTop, tabRadiusTop,        // Top Left
            tabRadiusTop, tabRadiusTop,        // Top Right
            tabRadiusBottom, tabRadiusBottom,  // Bottom Right
            tabRadiusBottom, tabRadiusBottom   // Bottom Left
        )
        bg.setColor(tabBgColor)
        background = bg

        elevation = 10f
        clipToPadding = false
        val start = (navPaddingStart * resources.displayMetrics.density).toInt()
        val top = (navPaddingTop * resources.displayMetrics.density).toInt()
        val end = (navPaddingEnd * resources.displayMetrics.density).toInt()
        val bottom = (navPaddingBottom * resources.displayMetrics.density).toInt()

        setPadding(start, top, end, bottom)
    }

    fun setNavBarGradient(colors: IntArray, orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT) {
        val gradient = GradientDrawable(orientation, colors)
        gradient.cornerRadii = floatArrayOf(
            tabRadiusTop, tabRadiusTop,
            tabRadiusTop, tabRadiusTop,
            tabRadiusBottom, tabRadiusBottom,
            tabRadiusBottom, tabRadiusBottom
        )
        background = gradient
    }


}
