package android.ut3.snapito.viewmodel

import android.ut3.snapito.repository.FirebaseStorageRepository
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference

class FirebaseStorageViewModel(private val firebaseStorageRepository: FirebaseStorageRepository): ViewModel() {

    lateinit var title: String

    fun getImageReference(): StorageReference {
        return firebaseStorageRepository.getImageReference(title)
    }
}