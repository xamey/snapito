package android.ut3.snapito.viewmodel

import android.ut3.snapito.dagger.DaggerAppComponent
import android.ut3.snapito.repository.FirebaseStorageRepository
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class FirebaseStorageViewModel @Inject constructor(): ViewModel() {

    var title: String = "0.jpg"
    @Inject
    lateinit var firebaseStorageRepository: FirebaseStorageRepository

    init {
        DaggerAppComponent.create().injectFirebaseStorageRepository(this)
    }

    fun getImageReference(): StorageReference {
        return firebaseStorageRepository.getImageReference(title)
    }
}