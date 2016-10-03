package net.plzpoint.kgshmaster.activity

import android.app.Fragment
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import kotlinx.android.synthetic.main.activity_kgshmaster_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.plzpoint.kgshmaster.Fragment.ChatFragment
import net.plzpoint.kgshmaster.Fragment.HomeFragment
import net.plzpoint.kgshmaster.Fragment.MealFragment
import net.plzpoint.kgshmaster.Fragment.NotifiFragment

import net.plzpoint.kgshmaster.R
import net.plzpoint.kgshmaster.other.CircleTransform

class MainActivity : AppCompatActivity() {
    // ------------------------------   All Values
    var navigationView: NavigationView? = null
    var drawer: DrawerLayout? = null
    var navHeader: View? = null
    //var imgNavHeaderBg: ImageView? = null
    //var imgProfile: ImageView? = null
    //var txtName: TextView? = null
    //var txtWebsite: TextView? = null
    // var toolbar: Toolbar? = null
    // val floatingBtn: FloatingActionButton? = null

    val url_NavHeaderBg: String = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    val url_Profile = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    var navItemIndex = 0

    // ------------------------------   All Tags
    val tag_home = "home"
    val tag_meal = "meal"
    val tag_chat = "chat"
    val tag_notifi = "notifi"
    val tag_setting = "setting"

    var tag_current = tag_home

    var activityTitles: Array<String>? = null

    var shouldLoadHomeFragOnBackPress = true
    var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kgshmaster_main)

        setSupportActionBar(toolbar)

        mHandler = Handler()

        drawer = drawer_layout
        navigationView = nav_view

        // Navigation view header
        navHeader = nav_view.getHeaderView(0)
        //txtName = name
        //txtWebsite = website
        //imgNavHeaderBg = img_header_bg

        activityTitles = applicationContext.getResources().getStringArray(R.array.nav_item_activity_titles)

        Log.i("ASD", "${activityTitles?.count()}")

        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        })

        loadNavHeader()

        setUpNavigationView()

        if (savedInstanceState == null) {
            navItemIndex = 0;
            tag_current = tag_home;

            // loadHomeFragment();
        }
    }

    fun loadNavHeader() {
        navHeader?.name?.setText("Point")
        navHeader?.name?.setTextColor(Color.WHITE)
        navHeader?.website?.setText("www.plzpoint.net")
        navHeader?.website?.setTextColor(Color.WHITE)

        Glide.with(this).load(url_NavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navHeader?.img_profile_background);

        Glide.with(this).load(url_Profile)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(navHeader?.img_profile)

        // showing dot next to notifications label
        nav_view.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    fun setUpNavigationView() {
        nav_view.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_home -> {
                        navItemIndex = 0; tag_current = tag_home
                    }
                    R.id.nav_photos -> {
                        navItemIndex = 1;tag_current = tag_meal
                    }
                    R.id.nav_movies -> {
                        navItemIndex = 2;tag_current = tag_chat
                    }
                    R.id.nav_settings -> {
                        navItemIndex = 3;tag_current = tag_setting
                    }
                    else -> {
                        navItemIndex = 0
                    }
                }

                if (item.isChecked) {
                    item.setChecked(false)
                } else
                    item.setChecked(true)

                item.setChecked(true)

                return true
            }
        })
    }

    fun setToolbarTitle() {
        supportActionBar?.setTitle(activityTitles?.get(navItemIndex))
    }

    fun selectNavMenu() {
        navigationView?.getMenu()?.getItem(navItemIndex)?.isChecked = true
    }

    fun loadHomeFragment() {
        selectNavMenu()

        setToolbarTitle()

        if (supportFragmentManager.findFragmentByTag(tag_current) != null) {
            drawer?.closeDrawers()

            toggleFab()
            return
        }


        //var mPendingRunnable: Runnable = object : Runnable {
        //   override fun run() {
        //       var fragment = getHomeFragment()
        //       var fragmentTransaction = supportFragmentManager.beginTransaction()
        //       fragmentTransaction?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        //       fragmentTransaction?.replace(R.id.frame, fragment, tag_current)
        //     fragmentTransaction?.commitAllowingStateLoss()
        //    }
        //}

        // val mPendingRunnable = Runnable() {
        //     // update the main content by replacing fragments
        //     var fragment = getHomeFragment()
        //     var fragmentTransaction = supportFragmentManager?.beginTransaction()
        //     fragmentTransaction?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        //     fragmentTransaction?.replace(R.id.frame, fragment, tag_current)
        //     fragmentTransaction?.commitAllowingStateLoss()
        // }

        //if (mPendingRunnable != null) {
        //    mHandler?.post(mPendingRunnable);
        //}

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer?.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    fun toggleFab() {
        if (navItemIndex === 0)
            fab.show()
        else
            fab.hide()
    }

    fun getHomeFragment(): Fragment {
        when (navItemIndex) {
        // Home
            0 -> {
                return HomeFragment()
            }
            1 -> {
                return MealFragment()
            }
            2 -> {
                return ChatFragment()
            }
            3 -> {
                return NotifiFragment()
            }
            else -> {
                return HomeFragment()
            }
        }
    }
}