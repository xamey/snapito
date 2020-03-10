package android.ut3.snapito.view


import android.os.Bundle
import android.ut3.snapito.R
import android.ut3.snapito.adapter.GalleryImageAdapter
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject


class ShowMultiplePicturesActivity : AppCompatActivity() {

    private val firebaseStorageViewModel: FirebaseStorageViewModel by inject()

    private val SPAN_COUNT = 1
    lateinit var galleryAdapter: GalleryImageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_multiple_pictures)

        galleryAdapter = GalleryImageAdapter(firebaseStorageViewModel.getImagesReference())
        val rvStoredPhoto = findViewById(R.id.recyclerView) as RecyclerView
        rvStoredPhoto.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        rvStoredPhoto.adapter = galleryAdapter
    }
}
