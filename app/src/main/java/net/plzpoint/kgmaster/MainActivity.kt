package net.plzpoint.kgmaster

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pawegio.kandroid.find
import net.plzpoint.kgmaster.Fragment.ChatFragment
import net.plzpoint.kgmaster.Fragment.MealFragment
import net.plzpoint.kgmaster.Fragment.NewsFragment
import net.plzpoint.kgmaster.Fragment.NotifiFragment
import net.plzpoint.kgmaster.Util.CircleTransform
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val fragMeal = MealFragment().newInstance()
    val fragChat = ChatFragment().newInstance()
    val fragNotifi = NotifiFragment().newInitialize()
    val fragNews = NewsFragment().newInitialize()

    var navigationView: NavigationView? = null
    var navigationMenu: Menu? = null
    var kgSettingNotification: Switch? = null
    var navigationHead: View? = null

    var profileImage: ImageView? = null
    var profileNick: TextView? = null
    var profileComment: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.kg_menu) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
        navigationMenu = navigationView!!.menu
        navigationHead = navigationView!!.getHeaderView(0)

        // ----------------------------------- Setting Buttons
        kgSettingNotification = navigationView!!.menu.getItem(4).actionView as Switch
        kgSettingNotification!!.setOnCheckedChangeListener { compoundButton, b -> }

        // ----------------------------------- Profile Image
        profileImage = navigationHead?.findViewById(R.id.kg_profile_image) as ImageView
        profileImage?.setImageResource(R.mipmap.bell)
        profileImage?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                imageSelection()
            }
        })
        // ----------------------------------- Profile ( Nick, Comment )
        profileNick = navigationHead?.findViewById(R.id.kg_profile_nick) as TextView
        profileComment = navigationHead?.findViewById(R.id.kg_profile_comment) as TextView

        replaceContentFragment(R.id.content_main, fragMeal)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.kg_meal) {
            replaceContentFragment(R.id.content_main, fragMeal)
            onBackPressed()
        } else if (id == R.id.kg_talk) {
            replaceContentFragment(R.id.content_main, fragChat)
            onBackPressed()
        } else if (id == R.id.kg_notification) {
            replaceContentFragment(R.id.content_main, fragNotifi)
            onBackPressed()
        } else if (id == R.id.kg_news) {
            replaceContentFragment(R.id.content_main, fragNews)
            onBackPressed()
        }
        return true
    }

    // Fragment 전환
    fun Activity.replaceContentFragment(@IdRes frameId: Int, fragment: android.app.Fragment) {
        fragmentManager.beginTransaction().replace(frameId, fragment).commit()
    }

    // ----------- ImageSelection
    val SELECT_PICTURE = 0
    val CROP_IMAGE = 1
    var tempImageUri: Uri? = null

    fun imageSelection() {
        val cameraListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                // 이미지 잘라내기 위한 크기
                intent.putExtra("crop", "true")
                intent.putExtra("aspectX", 0)
                intent.putExtra("aspectY", 0)
                intent.putExtra("outputX", 200)
                intent.putExtra("outputY", 150)
                intent.putExtra("output", tempImageUri)
                try {
                    //intent.putExtra("return-data", true)
                    startActivityForResult(intent, CROP_IMAGE)
                } catch (e: ActivityNotFoundException) {
                    // Do nothing for now
                }
            }
        }
        val albumListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val intent = Intent(Intent.ACTION_PICK);
                intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
                try {
                    startActivityForResult(Intent.createChooser(intent, "이미지 선택"), SELECT_PICTURE)
                } catch (e: ActivityNotFoundException) {
                }
            }
        }
        val cancelListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
            }
        }
        AlertDialog.Builder(this)
                .setTitle("프로필 이미지 선택")
                .setNeutralButton("취소", cancelListener)
                .setPositiveButton("앨범선택", albumListener)
                .setNegativeButton("사진촬영", cameraListener)
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SELECT_PICTURE -> {
                    tempImageUri = data!!.data

                    val intent = Intent("com.android.camera.action.CROP")
                    intent.setDataAndType(tempImageUri, "image/*")
                    intent.putExtra("outputX", 90)
                    intent.putExtra("outputY", 90)
                    intent.putExtra("aspectX", 1)
                    intent.putExtra("aspectY", 1)
                    intent.putExtra("scale", true)
                    intent.putExtra("return-data", true)
                    startActivityForResult(intent, CROP_IMAGE)
                }

                CROP_IMAGE -> {
                    val extras = data!!.extras
                    if (extras != null) {
                        val photo: Bitmap = extras.getParcelable("data")
                        profileImage!!.setImageBitmap(photo)
                    }
                    val f = File(tempImageUri!!.getPath())
                    if (f.exists()) {
                        f.delete()
                    }

                    //Glide.with(this)
                    //        .load(filePath)
                    //        .crossFade()
                    //        .thumbnail(0.5f)
                    //        .bitmapTransform(CircleTransform(this))
                    //        .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //        .into(profileImage)
                }
            }
        }
    }

    private fun storeImage(bitmap: Bitmap, filePath: String) {
        val copyFile = File(filePath)
        var out: BufferedOutputStream?
        try {
            copyFile.createNewFile()
            out = BufferedOutputStream(FileOutputStream(copyFile))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }
}
