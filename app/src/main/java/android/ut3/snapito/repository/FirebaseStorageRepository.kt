package android.ut3.snapito.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

//rien à faire vu que y'a pas de dépendance

class FirebaseStorageRepository {

    var storage = Firebase.storage
    var imagesStorage = storage.reference.child("images")
    fun getImageReference(title: String): StorageReference {
        return imagesStorage.child(title)
    }
}