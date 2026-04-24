package md.ipltbriceni.edubankjunior

import android.app.Application
import com.google.firebase.FirebaseApp

class EduBankApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // This initializes all Firebase services linked via google-services.json
        FirebaseApp.initializeApp(this)
    }
}