package android.ut3.snapito.repository

import android.ut3.snapito.model.firestore.StoredPhoto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepository @Inject constructor(){
    var firestoreDB = FirebaseFirestore.getInstance()


    fun getStoredPhoto(): CollectionReference {
        return firestoreDB.collection("images")
    }

    fun saveStoredPhoto(storedPhoto: StoredPhoto): Task<Void> {
        return firestoreDB.collection("images").document(storedPhoto.title).set(storedPhoto)
    }
}