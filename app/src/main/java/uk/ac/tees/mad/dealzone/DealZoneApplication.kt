package uk.ac.tees.mad.dealzone

import android.app.Application
import com.google.firebase.FirebaseApp
import uk.ac.tees.mad.dealzone.data.AuthRepository

class DealZoneApplication : Application() {
    lateinit var authRepository: AuthRepository

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
        authRepository = AuthRepository()
    }
}