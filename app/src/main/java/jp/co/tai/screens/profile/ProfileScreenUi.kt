package jp.co.tai.screens.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import jp.co.tai.R
import jp.co.tai.screens.leaders.consumeThunderTouches
import jp.co.tai.screens.loading.Background
import jp.co.tai.screens.start.MainButton
import jp.co.tai.screens.start.SquareButton
import jp.co.tai.screens.start.UserProfileBox
import jp.co.tai.storage.SnowboundStorage
import jp.co.tai.ui.theme.Typography
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun ProfileScreenUi(
    storage: SnowboundStorage,
    back: () -> Unit
) {

    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current
    val windowInsetsController =
        remember { activity?.let { WindowInsetsControllerCompat(it.window, it.window.decorView) } }
    val userName by remember { mutableStateOf(storage.getName()) }
    var editName by remember { mutableStateOf("") }
    val filePhoto = remember { File(context.filesDir, "photo_image") }
    val profilePhotoUri = remember {
        when {
            filePhoto.exists() -> Uri.fromFile(filePhoto)
            storage.getPhoto().isNotEmpty() -> storage.getPhoto().toUri()
            else -> null
        }
    }
    var userPhoto by remember { mutableStateOf(profilePhotoUri) }
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }
    val gallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            userPhoto = it
            windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
    var pendingCameraPreview by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (pendingCameraPreview) {
                pendingCameraPreview = false
                showCamera = true
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }, 250L)
            pendingCameraPreview = false
        }
    }
    val scrollState = rememberScrollState()
    var isFocused by remember { mutableStateOf(false) }
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardOpen = imeBottom > 0

    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen) {
            delay(500)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeThunderTouches()
            .pointerInput(Unit) {
                detectTapGestures { }
            }
    ) {
        Background(bgRes = R.drawable.game_bg)

        Text(
            text = stringResource(R.string.profile).uppercase(),
            style = Typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1.4f)
                    .weight(0.55f)
            ) {
                Background(bgRes = R.drawable.privacy_bg)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                        .verticalScroll(scrollState)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        UserProfileBox(
                            userPhoto = userPhoto,
                            context = context
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userName,
                        style = Typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    NameEditField(
                        editName = editName,
                        onNameChange = { editName = it },
                        focusManager = focusManager,
                        windowInsetsController = windowInsetsController,
                        text = stringResource(R.string.change_name),
                        onFocused = {
                            isFocused = true
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    SquareButton(
                        btnRes = R.drawable.camera_btn,
                        btnMaxWidth = 0.2f
                    ){
                        focusManager.clearFocus()
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            showCamera = true
                        } else {
                            pendingCameraPreview = true
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }

                    SquareButton(
                        btnRes = R.drawable.gallery_btn,
                        btnMaxWidth = 0.2f
                    ){ gallery.launch(arrayOf("image/*")) }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.45f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 58.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding(),
                    verticalArrangement = Arrangement.Center
                ) {
                    MainButton(
                        buttonText = stringResource(R.string.save),
                        modifier = Modifier
                            .aspectRatio(4f)
                    ) {
                        if (editName.isNotBlank()) storage.setName(editName)
                        if (userPhoto != null && userPhoto?.scheme == "content") {
                            storage.setPhoto(userPhoto.toString())
                            if (filePhoto.exists()) {
                                filePhoto.delete()
                            }
                        }
                        if (!tempPhotoPath.isNullOrBlank()) {
                            storage.saveCameraPhoto(context, tempPhotoPath!!)
                        }
                        back()
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    MainButton(
                        buttonText = stringResource(R.string.clear_profile),
                        modifier = Modifier
                            .aspectRatio(4f)
                    ) {
                        storage.clearUserProfile()
                        if (filePhoto.exists()) {
                            filePhoto.delete()
                        }
                        back()
                    }
                }
            }
        }

        SquareButton(
            btnRes = R.drawable.back_btn,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 50.dp)
        ) { back() }

        if (showCamera) {
            Box(
                Modifier
                    .fillMaxSize()
                    .consumeThunderTouches()
                    .pointerInput(Unit) {
                        detectTapGestures { }
                    }
            ) {
                CameraPreview(
                    onImageCaptured = { uri ->
                        userPhoto = uri
                        tempPhotoPath = uri.path
                        showCamera = false
                    },
                    onError = {
                        Toast.makeText(context, "Camera error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                        showCamera = false
                    }
                )

                SquareButton(
                    btnRes = R.drawable.close_btn,
                    btnMaxWidth = 0.05f,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ){ showCamera = false }
            }
        }
    }
}