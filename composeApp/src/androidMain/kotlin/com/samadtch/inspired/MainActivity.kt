package com.samadtch.inspired

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.IntentCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.ui.theme.InspiredTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO: Handle Re-Loading after Paused status
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    //Loading
    private val _loaded = MutableStateFlow(false) //TODO: Make true again
    private val loaded = _loaded.asStateFlow()

    //Data
    private val _assetFile = MutableStateFlow<AssetFile?>(null)
    private val assetFile = _assetFile.asStateFlow()

    //Package Info
    private lateinit var packageInfo: PackageInfo

    //Review
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    //Picker
    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        // Handle the returned Uri
        it?.let { sendImage(it) }
    }

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Handle Incoming Images
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val uri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
            uri?.let { sendImage(uri) }
        }

        //Splash Screen
        installSplashScreen().apply {
            setKeepOnScreenCondition { loaded.value }
        }

        //Initializations
        remoteConfig.fetchAndActivate()//Remote Config
        initReviewManager()//Review Manager

        //Get Package Info
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "onCreate: " + e.message)
        }

        //UI
        setContent {
            //------------------------------- Declarations
            val asset by assetFile.collectAsState()

            //------------------------------- UI
            Column(Modifier.fillMaxWidth()) {
                App(
                    onSplashScreenDone = { lifecycleScope.launch { _loaded.emit(false) } },
                    onFilePick = {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    assetFile = asset
                )
            }
        }
    }

    private fun initReviewManager() {
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }
    }

    private fun sendImage(uri: Uri) {
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
        lifecycleScope.launch {
            _assetFile.emit(
                AssetFile(
                    fileName = fileName!!,
                    bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        .asImageBitmap()
                )
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    InspiredTheme {
        Surface {
            App(
                onSplashScreenDone = {},
                onFilePick = {},
                assetFile = null
            )
        }
    }
}