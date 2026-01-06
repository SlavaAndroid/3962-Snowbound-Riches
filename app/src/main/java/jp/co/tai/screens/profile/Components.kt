package jp.co.tai.screens.profile

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import jp.co.tai.R
import jp.co.tai.ui.theme.BlueDark
import jp.co.tai.ui.theme.OrangeGradient
import java.io.File

@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    val imageCapture = remember { ImageCapture.Builder().build() }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraX", "Binding failed", e)
                }

            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp),
            onClick = {
                val fileToSave = File(context.cacheDir, "testImage${System.currentTimeMillis()}")
                if (fileToSave.exists().not()) {
                    fileToSave.createNewFile()
                }
                val options = ImageCapture.OutputFileOptions.Builder(fileToSave).build()
                imageCapture.takePicture(
                    options,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val uri = Uri.fromFile(fileToSave)
                            onImageCaptured(uri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            onError(exception)
                        }

                    }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueDark,
                contentColor = Color.White
            )
        ) {
            Text(text = "Take a photo")
        }
    }
}

@Composable
fun NameEditField(
    modifier: Modifier = Modifier,
    editName: String,
    onNameChange: (String) -> Unit,
    focusManager: FocusManager,
    windowInsetsController: WindowInsetsControllerCompat?,
    text: String,
    onFocused: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(36.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                brush = OrangeGradient,
                shape = RoundedCornerShape(30.dp)
            )
    ) {
        BasicTextField(
            value = editName,
            onValueChange = {
                if (it.length <= 8) {
                    onNameChange(it)
                }
            },
            modifier = modifier
                .fillMaxSize()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocused()
                },
            textStyle = TextStyle(
                color = BlueDark,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(BlueDark),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    if (editName.isEmpty() && !isFocused) {
                        Text(
                            text = text,
                            color = BlueDark,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    innerTextField()
                }
            }
        )

        Image(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = "Edit",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
                .fillMaxWidth(0.1f)
                .aspectRatio(1f),
            contentScale = ContentScale.FillBounds
        )
    }
}