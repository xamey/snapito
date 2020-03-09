package android.ut3.snapito.dagger

import android.ut3.snapito.view.MapsActivity
import android.ut3.snapito.view.ShowPictureActivity
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {
    fun injectFirestoreRepository(firestoreViewModel: FirestoreViewModel)
    fun injectFirestoreViewModel(mapsActivity: MapsActivity)
    fun injectFirebaseStorageRepository(firebaseStorageViewModel: FirebaseStorageViewModel)
    fun injectFirebaseStorageViewModel(showPictureActivity: ShowPictureActivity)
    fun injectFirebaseStorageViewModel(mapsActivity: MapsActivity)
}