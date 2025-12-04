package com.ext.custombottomnav

import android.graphics.drawable.Drawable

data class BottomNavItem(
    val id: Int,
    val icon: Drawable,
    val title: String,
    val selectedIconColor: Int,
    val unSelectedIconColor: Int,
    val selectedTextColor: Int,
    val unSelectedTextColor: Int,
    val badgeCount: Int = 0
)
