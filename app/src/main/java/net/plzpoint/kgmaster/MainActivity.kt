package net.plzpoint.kgmaster

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    object Instance {
        var instance: MainActivity? = null
    }

    val meal_fragment = MealFragment().instance()
    val schedule_fragment = ScheduleFragment().instance()
    val notice_fragment = NoticeFragment().instance()
    val news_fragment = NewsFragment().instance()

    var fragmentTransaction: FragmentTransaction? = null
    var main_title: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Instance.instance = this

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        main_title = findViewById(R.id.main_title) as TextView

        val fm: FragmentManager = fragmentManager
        fragmentTransaction = fm.beginTransaction()
        fragmentTransaction!!.replace(R.id.content_main, meal_fragment)
        fragmentTransaction!!.commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.nav_meal -> {
                val fm: FragmentManager = fragmentManager
                fragmentTransaction = fm.beginTransaction()
                fragmentTransaction!!.replace(R.id.content_main, meal_fragment)
                fragmentTransaction!!.commit()
            }
            R.id.nav_schedule -> {
                val fm: FragmentManager = fragmentManager
                fragmentTransaction = fm.beginTransaction()
                fragmentTransaction!!.replace(R.id.content_main, schedule_fragment)
                fragmentTransaction!!.commit()
            }
            R.id.nav_notice -> {
                val fm: FragmentManager = fragmentManager
                fragmentTransaction = fm.beginTransaction()
                fragmentTransaction!!.replace(R.id.content_main, notice_fragment)
                fragmentTransaction!!.commit()
            }
            R.id.nav_news -> {
                val fm: FragmentManager = fragmentManager
                fragmentTransaction = fm.beginTransaction()
                fragmentTransaction!!.replace(R.id.content_main, news_fragment)
                fragmentTransaction!!.commit()
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
