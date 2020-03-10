package android.ut3.snapito.di

import android.ut3.snapito.repository.FirebaseStorageRepository
import android.ut3.snapito.repository.FirestoreRepository
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import org.koin.dsl.module

val firebaseStorageViewModel = module {
    single {
        FirebaseStorageViewModel(get())
    }
}

val firestoreViewModel = module {
    single {
        FirestoreViewModel(get())
    }
}

val firebaseStorageRepository = module {
    single {
        FirebaseStorageRepository()
    }
}

val firestoreRepository = module {
    single {
        FirestoreRepository()
    }
}

