package com.ext.custombottomnavigation

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ext.custombottomnav.BottomNavItem
import com.ext.custombottomnav.CustomBottomNavView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<CustomBottomNavView>(R.id.bottomNav)

        val tabs = listOf(

            BottomNavItem(
                id = 1,
                icon = getDrawable(R.drawable.ic_home)!!,
                title = "Home",
                selectedIconColor = Color.BLUE,
                unSelectedIconColor = Color.GRAY,
                selectedTextColor = Color.BLUE,
                unSelectedTextColor = Color.GRAY,
                badgeCount = 2
            ),

            BottomNavItem(
                id = 2,
                icon = getDrawable(R.drawable.ic_search)!!,
                title = "Search",
                selectedIconColor = Color.RED,
                unSelectedIconColor = Color.GRAY,
                selectedTextColor = Color.RED,
                unSelectedTextColor = Color.GRAY
            ),

            BottomNavItem(
                id = 3,
                icon = getDrawable(R.drawable.ic_profile)!!,
                title = "Profile",
                selectedIconColor = Color.GREEN,
                unSelectedIconColor = Color.GRAY,
                selectedTextColor = Color.GREEN,
                unSelectedTextColor = Color.GRAY
            )

        )

        bottomNav.setTabs(tabs)

        bottomNav.setOnTabSelectedListener { index ->
            Toast.makeText(this, "Tab $index Selected", Toast.LENGTH_SHORT).show()
        }
    }
}
