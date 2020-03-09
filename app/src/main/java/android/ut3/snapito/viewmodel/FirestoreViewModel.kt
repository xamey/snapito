package android.ut3.snapito.viewmodel

import android.content.ContentValues
import android.ut3.snapito.dagger.DaggerAppComponent
import android.ut3.snapito.model.firestore.StoredPhoto
import android.ut3.snapito.repository.FirestoreRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirestoreViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var firestoreRepository: FirestoreRepository

    var storedPhotos: MutableLiveData<List<StoredPhoto>> = MutableLiveData()
    init {
        DaggerAppComponent.create().injectFirestoreRepository(this)
    }

    fun saveStoredPhoto(storedPhoto: StoredPhoto) {
        firestoreRepository.saveStoredPhoto(storedPhoto).addOnFailureListener{
            Log.e(ContentValues.TAG, "Failed to save photo")
        }
    }

    fun getStoredPhotos(): LiveData<List<StoredPhoto>> {
        firestoreRepository.getStoredPhoto().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e!=null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                storedPhotos.value = null
                return@EventListener
            }
            var savedStoredPhotos: MutableList<StoredPhoto> = mutableListOf()
            for (doc in value!!) {
                var storedPhoto = doc.toObject(StoredPhoto::class.java)
                savedStoredPhotos.add(storedPhoto)
            }
            storedPhotos.value = savedStoredPhotos
        })
        return storedPhotos
    }
}