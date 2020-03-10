package android.ut3.snapito.view

import android.os.Bundle
import android.ut3.snapito.R
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import org.koin.android.ext.android.inject

//on injecte à la main les dépendances car c'est une classe qui n'est pas injectable par la suite
class ShowPictureActivity(
) : AppCompatActivity() {

    //injection de la dépendance
    private val firebaseStorageViewModel: FirebaseStorageViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture)
        var imageView = findViewById<ImageView>(R.id.showPictureImgView)
        Glide.with(this).using(FirebaseImageLoader()).load(firebaseStorageViewModel.getImageReference()).into(imageView)
    }
}
