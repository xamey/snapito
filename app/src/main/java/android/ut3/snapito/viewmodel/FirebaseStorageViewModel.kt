package android.ut3.snapito.viewmodel

import android.ut3.snapito.model.photos.TakenPhoto
import android.ut3.snapito.repository.FirebaseStorageRepository
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference

class FirebaseStorageViewModel(private val firebaseStorageRepository: FirebaseStorageRepository): ViewModel() {

    lateinit var title: String
    lateinit var listTitle: List<String>

    fun getImageReference(): StorageReference {
        return firebaseStorageRepository.getImageReference(title)
    }

    fun getImagesReference(): List<StorageReference> {
        return firebaseStorageRepository.getImagesReference(listTitle)
    }

    fun saveImage(takenPhoto: TakenPhoto) {
        return firebaseStorageRepository.saveImage(takenPhoto)
    }
}