package android.ut3.snapito.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(){

    var storage = Firebase.storage
    var imagesStorage = storage.reference.child("images")
    fun getImageReference(title: String): StorageReference {
        return imagesStorage.child(title)
    }
}