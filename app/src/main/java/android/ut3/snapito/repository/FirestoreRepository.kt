package android.ut3.snapito.repository

import android.ut3.snapito.model.firestore.StoredPhoto
import android.ut3.snapito.model.photos.TakenPhoto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    var firestoreDB = FirebaseFirestore.getInstance()


    fun getStoredPhoto(): CollectionReference {
        return firestoreDB.collection("images")
    }

    fun saveStoredPhoto(takenPhoto: TakenPhoto): Task<Void> {
        val title = takenPhoto.uri.lastPathSegment!!
        val storedPhoto = StoredPhoto(title, takenPhoto.lat, takenPhoto.long)
        return firestoreDB.collection("images").document(title!!).set(storedPhoto)
    }

    fun getCollectionReference(): CollectionReference {
        return firestoreDB.collection("images")
    }
}