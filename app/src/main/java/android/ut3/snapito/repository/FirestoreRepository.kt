package android.ut3.snapito.repository

import android.ut3.snapito.model.firestore.StoredPhoto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

//rien à faire vu que y'a pas de dépendance
class FirestoreRepository {
    var firestoreDB = FirebaseFirestore.getInstance()


    fun getStoredPhoto(): CollectionReference {
        return firestoreDB.collection("images")
    }

    fun saveStoredPhoto(storedPhoto: StoredPhoto): Task<Void> {
        return firestoreDB.collection("images").document(storedPhoto.title).set(storedPhoto)
    }
}