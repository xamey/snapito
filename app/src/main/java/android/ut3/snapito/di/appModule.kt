package android.ut3.snapito.di

import android.ut3.snapito.repository.FirebaseStorageRepository
import android.ut3.snapito.repository.FirestoreRepository
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import org.koin.dsl.module

//il s'agit d'une dépendance dans laquelle il faut ajouter une autre dépendance
//le get() signifie que Koin va aller chercher lui même à injecter cette dépendance
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

//il s'agit d'une dépendance qui n'a pas besoin d'autres dépendances
//d'où le fait qu'il n'y a pas de get()
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

