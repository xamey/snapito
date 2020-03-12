package android.ut3.snapito.view

import android.os.Bundle
import android.ut3.snapito.R
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import org.koin.android.ext.android.inject

class ShowPictureActivity(
) : AppCompatActivity() {

    private val firebaseStorageViewModel: FirebaseStorageViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture)

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        var imageView = findViewById<ImageView>(R.id.showPictureImgView)
        Glide.with(this)
            .using(FirebaseImageLoader())
            .load(firebaseStorageViewModel.getImageReference())
            .placeholder(circularProgressDrawable)
            .into(imageView)
    }
}
