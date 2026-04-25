package uk.ac.tees.mad.dealzone

import android.app.Application
import com.google.firebase.FirebaseApp
import uk.ac.tees.mad.dealzone.data.AuthRepository
import uk.ac.tees.mad.dealzone.data.RepoImpl
import uk.ac.tees.mad.dealzone.data.local.AppDatabase
import uk.ac.tees.mad.dealzone.util.ConnectivityObserver
import uk.ac.tees.mad.dealzone.util.NetworkConnectivityObserver
import uk.ac.tees.mad.dealzone.domain.Repo.Repo

class DealZoneApplication : Application() {
    lateinit var authRepository: AuthRepository
    lateinit var repo: Repo
    lateinit var database: AppDatabase
    lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        authRepository = AuthRepository()
        repo = RepoImpl(database.productDao())
        connectivityObserver = NetworkConnectivityObserver(this)
    }
}