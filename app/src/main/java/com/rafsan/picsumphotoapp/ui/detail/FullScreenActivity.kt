package com.rafsan.picsumphotoapp.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.snackbar.Snackbar
import com.rafsan.picsumphotoapp.BuildConfig
import com.rafsan.picsumphotoapp.R
import com.rafsan.picsumphotoapp.alertDialog.DialogListener
import com.rafsan.picsumphotoapp.alertDialog.ShowAlert
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.databinding.ActivityDetailBinding
import com.rafsan.picsumphotoapp.di.GlideApp
import com.rafsan.picsumphotoapp.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FullScreenActivity : BaseActivity<ActivityDetailBinding>() {

    private val TAG = "FullScreenActivity"
    private val REQUEST_CODE_ASK_PERMISSIONS = 1010
    private val fullScreenViewModel: FullScreenViewModel by viewModels()
    private lateinit var imageItem: ImageListItem
    private var downloadedFileUri: Uri? = null
    private var shareImage = false

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
            imageItem = item as ImageListItem
            setupUI()
            setupObserver()
        }
    }

    override fun setBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

    private fun setupUI() {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 80f
        circularProgressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.colorPrimary,
                BlendModeCompat.SRC_ATOP
            )
        circularProgressDrawable.start()

        with(binding) {
            GlideApp.with(this@FullScreenActivity)
                .load(imageItem.download_url)
                .placeholder(circularProgressDrawable)
                .into(imageFullScreen)

            fabOptions.setOnClickListener {
                onOptionButtonClick()
            }

            //share fab button on click
            fabShare.setOnClickListener {
                handleShare()
            }
            //download fab button on click
            fabDownload.setOnClickListener {
                handleDownload()
            }
        }
    }

    private fun setupObserver() {
        fullScreenViewModel.imageUri.observe(this, Observer { uri ->
            if (!uri.path.isNullOrEmpty()) {
                downloadedFileUri = uri
                if (!shareImage) {
                    val snackBar =
                        Snackbar.make(
                            binding.rootLayout,
                            getString(R.string.image_download_success),
                            Snackbar.LENGTH_LONG
                        )
                    snackBar.show()
                    openImage(uri)
                } else {
                    shareImage()
                }
            }
        })

        fullScreenViewModel.errorToast.observe(this, Observer { value ->
            if (value.isNotEmpty()) {
                Toast.makeText(this@FullScreenActivity, value, Toast.LENGTH_LONG).show()
            } else {
                fullScreenViewModel.hideErrorToast()
            }
        })
    }

    private fun handleDownload() {
        ShowAlert().alertDialog(this, "", getString(R.string.download_image),
            getString(R.string.yes), getString(R.string.no), object : DialogListener {
                override fun onYesClicked(obj: Any?) {
                    checkPermission()
                }

                override fun onNoClicked(error: String?) {}
            })
    }

    private fun handleShare() {
        shareImage = true
        if (downloadedFileUri == null) {
            checkPermission()
        } else {
            shareImage()
        }
    }

    private fun shareImage() {
        var authority = "com.rafsan.picsumphotoapp.fileprovider"
        if (BuildConfig.DEBUG) {
            authority = "com.rafsan.picsumphotoapp.debug.fileprovider"
        }
        downloadedFileUri?.let {
            val file = File(it.path)
            val imageUri = FileProvider.getUriForFile(this, authority, file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            shareImage = false
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fullScreenViewModel.downloadImage(imageItem)
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
                        getString(R.string.rationale),
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
            } else {
                //Permission granted
                fullScreenViewModel.downloadImage(imageItem)
            }
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

    private fun openImage(uri: Uri) {
        var file = File(uri.path)
        val finalUri: Uri? = FileUtils().copyFileToDownloads(this, file)
        finalUri?.let {
            file = File(it.path)
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                // JPG file
                intent.setDataAndType(it, "image/jpeg")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
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
                fullScreenViewModel.downloadImage(imageItem)
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