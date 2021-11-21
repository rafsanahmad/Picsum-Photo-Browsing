package com.rafsan.picsumphotoapp.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.alertDialog.DialogListener
import com.rafsan.picsumphotoapp.alertDialog.ShowAlert
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ActivityDetailBinding
import com.rafsan.picsumphotoapp.di.GlideApp

class FullScreenActivity : BaseActivity<ActivityDetailBinding>() {

    private val REQUEST_CODE_ASK_PERMISSIONS = 1010
    private val fullScreenViewModel: FullScreenViewModel by viewModels()

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    //used to check if fab menu are opened or closed
    private var menuClosed = false

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            val item = bundle.getSerializable("item")
            setupUI(item as ImageListItem)
        }
    }

    override fun setBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

    private fun setupUI(item: ImageListItem) {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 80f
        circularProgressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.colorPrimaryDark,
                BlendModeCompat.SRC_ATOP
            )
        circularProgressDrawable.start()

        with(binding) {
            GlideApp.with(this@FullScreenActivity)
                .load(item.download_url)
                .placeholder(circularProgressDrawable)
                .into(imageFullScreen)

            fabOptions.setOnClickListener {
                onOptionButtonClick()
            }

            //share fab button on click
            fabShare.setOnClickListener {

            }
            //download fab button on click
            fabDownload.setOnClickListener {
                handleDownload()
            }
        }
    }

    private fun handleDownload() {
        ShowAlert().alertDialog(this, "", getString(R.string.download_image),
            getString(R.string.yes), getString(R.string.no), object : DialogListener {
                override fun onYesClicked(obj: Any?) {
                    checkPermission()
                }

                override fun onNoClicked(error: String?) {

                }

            })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fullScreenViewModel.downloadImage()
        } else {
            val hasWriteStoragePermission =
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ShowAlert().alertDialog(
                        this,
                        "",
                        "Storage permission is required to save image to phone. Allow permission?",
                        getString(R.string.yes),
                        getString(R.string.no),
                        object : DialogListener {
                            override fun onYesClicked(obj: Any?) {
                                requestPermission(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    REQUEST_CODE_ASK_PERMISSIONS
                                )
                            }

                            override fun onNoClicked(error: String?) {
                            }

                        }
                    )
                } else {
                    //Request permission
                    requestPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        REQUEST_CODE_ASK_PERMISSIONS
                    )
                }
            }
            //Permission granted
            fullScreenViewModel.downloadImage()
        }
    }

    private fun onOptionButtonClick() {
        setVisibility(menuClosed)
        setAnimation(menuClosed)
        menuClosed = !menuClosed;
    }

    // A Function used to set the Animation effect
    private fun setAnimation(menuClosed: Boolean) {
        with(binding) {
            if (!menuClosed) {
                fabDownload.startAnimation(fromBottom)
                fabShare.startAnimation(fromBottom)
                fabOptions.startAnimation(rotateOpen)
            } else {
                fabDownload.startAnimation(toBottom)
                fabShare.startAnimation(toBottom)
                fabOptions.startAnimation(rotateClose)
            }
        }
    }

    // used to set visibility to VISIBLE / INVISIBLE
    private fun setVisibility(menuClosed: Boolean) {
        with(binding) {
            if (!menuClosed) {
                fabDownload.visibility = View.VISIBLE
                fabShare.visibility = View.VISIBLE
            } else {
                fabDownload.visibility = View.INVISIBLE
                fabShare.visibility = View.INVISIBLE
            }
        }
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionName), permissionRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fullScreenViewModel.downloadImage()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Respond to the action bar's Up/Home button
        if (item.itemId == R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}