package android.ut3.snapito

import android.content.Intent
import android.os.Bundle
import android.ut3.snapito.di.firebaseStorageRepository
import android.ut3.snapito.di.firebaseStorageViewModel
import android.ut3.snapito.di.firestoreRepository
import android.ut3.snapito.di.firestoreViewModel
import android.ut3.snapito.notif.NotificationHelper
import android.ut3.snapito.view.MapsActivity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    private lateinit var mapBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //on start Koin en instanciant les dépendances
        startKoin {
            modules(
                listOf(
                    firebaseStorageViewModel,
                    firestoreViewModel,
                    firebaseStorageRepository,
                    firestoreRepository
                )
            )
        }

        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")

        setContentView(R.layout.activity_main)

        mapBtn = findViewById(R.id.mapsBtn);
        mapBtn.setOnClickListener { v ->
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}
