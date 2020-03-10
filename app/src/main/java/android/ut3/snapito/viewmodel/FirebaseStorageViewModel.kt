package android.ut3.snapito.viewmodel

import android.ut3.snapito.repository.FirebaseStorageRepository
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference

//dans la signature de la classe, on ajoute private val nomDeLaDépendance: TypeDeLaDépendance et celle-ci
//sera ajoutée par Koin
class FirebaseStorageViewModel(private val firebaseStorageRepository: FirebaseStorageRepository): ViewModel() {

    lateinit var title: String

    fun getImageReference(): StorageReference {
        return firebaseStorageRepository.getImageReference(title)
    }
}