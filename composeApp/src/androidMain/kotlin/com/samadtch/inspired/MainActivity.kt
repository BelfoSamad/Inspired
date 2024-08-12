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
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.IntentCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.samadtch.bilinguai.BuildKonfig
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.utilities.PKCEUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import okio.ByteString.Companion.encodeUtf8
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
    private val viewModel: AppViewModel by viewModels()

    //Loading
    private val _loaded = MutableStateFlow(true)
    private val loaded = _loaded.asStateFlow()

    //Authorization Code
    private val _code = MutableStateFlow<String?>(null)
    private val code = _code.asStateFlow()

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
        it?.let { sendImage(it) } // Handle the returned Uri
    }

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            PreComposeApp {
                //------------------------------- Declarations
                val context = LocalContext.current
                val receivedCode by code.collectAsState()
                val receivedAssetFile by assetFile.collectAsState()
                val appState by viewModel.initUiState.collectAsState()

                //------------------------------- UI
                App(
                    appState = appState,
                    onSplashScreenDone = { lifecycleScope.launch { _loaded.emit(false) } },
                    openWebPage = { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) },
                    launchReview = {
                        reviewManager = ReviewManagerFactory.create(this)
                        val request = reviewManager.requestReviewFlow()
                        request.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reviewInfo = task.result
                            }
                        }
                    },
                    authorize = {
                        //Request Authorization Code
                        val authParams = mapOf(
                            "code_challenge_method" to "S256",
                            "response_type" to "code",
                            "client_id" to BuildKonfig.CLIENT_ID,
                            "redirect_uri" to BuildKonfig.REDIRECT_URL,
                            "scope" to BuildKonfig.SCOPES.replace(" ", "%20"),
                            "code_challenge" to PKCEUtil.getCodeChallenge()
                        ).entries.joinToString(separator = "&", prefix = "?") { (k, v) ->
                            "${(k.encodeUtf8().utf8())}=${v.encodeUtf8().utf8()}"
                        }

                        //Make Call
                        CustomTabsIntent.Builder().build().launchUrl(
                            context, Uri.parse("${BuildKonfig.AUTH_URL}?$authParams")
                        )
                    },
                    authorizationCode = if (receivedCode == null) null else PKCEUtil.getCodeVerifier() to receivedCode!!,
                    onFilePick = {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    assetFile = receivedAssetFile,
                    logout = { viewModel.logout() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Handle Intent (Image Picking, Auth)
        handleIntent(intent)
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

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val uri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
            uri?.let { sendImage(uri) }
        } else if (intent.action == Intent.ACTION_VIEW) {
            lifecycleScope.launch {
                if (intent.data != null) _code.emit(intent.data!!.getQueryParameter("code"))
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