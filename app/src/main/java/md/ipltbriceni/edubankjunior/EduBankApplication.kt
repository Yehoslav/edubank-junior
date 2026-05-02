package md.ipltbriceni.edubankjunior

import android.app.Application
import com.google.firebase.FirebaseApp

class EduBankApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inițiază toate serviciile Firebase specificate în google.json
        FirebaseApp.initializeApp(this)
    }
}