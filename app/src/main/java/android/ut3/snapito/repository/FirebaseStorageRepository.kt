package android.ut3.snapito.repository

import android.ut3.snapito.model.photos.TakenPhoto
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class FirebaseStorageRepository {

    var storage = Firebase.storage
    var imagesStorage = storage.reference.child("images")
    fun getImageReference(title: String): StorageReference {
        return imagesStorage.child(title)
    }

    fun getImagesReference(listTitle: List<String>): List<StorageReference> {
        return listTitle.map { imagesStorage.child(it)}
    }

    fun saveImage(takenPhoto: TakenPhoto) {
        val newImageStorage = imagesStorage.child(takenPhoto.uri.lastPathSegment)
        newImageStorage.putFile(takenPhoto.uri)
    }
}