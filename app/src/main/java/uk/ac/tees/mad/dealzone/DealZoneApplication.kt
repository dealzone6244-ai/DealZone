package uk.ac.tees.mad.dealzone

import android.app.Application
import com.google.firebase.FirebaseApp
import uk.ac.tees.mad.dealzone.data.AuthRepository
import uk.ac.tees.mad.dealzone.data.RepoImpl
import uk.ac.tees.mad.dealzone.domain.Repo.Repo

class DealZoneApplication : Application() {
    lateinit var authRepository: AuthRepository
    lateinit var repo: Repo

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
        authRepository = AuthRepository()
        repo = RepoImpl()
    }
}