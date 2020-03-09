package android.ut3.snapito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.ut3.snapito.view.MapsActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var mapBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapBtn = findViewById(R.id.mapsBtn);
        mapBtn.setOnClickListener {
                v ->
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }
}
