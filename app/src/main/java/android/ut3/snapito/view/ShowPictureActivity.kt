package android.ut3.snapito.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.ut3.snapito.R
import android.ut3.snapito.dagger.DaggerAppComponent
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import javax.inject.Inject

class ShowPictureActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseStorageViewModel: FirebaseStorageViewModel

    init {
        DaggerAppComponent.create().injectFirebaseStorageViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture)
        var imageView = findViewById<ImageView>(R.id.showPictureImgView)
        Glide.with(this).using(FirebaseImageLoader()).load(firebaseStorageViewModel.getImageReference()).into(imageView)
    }
}
