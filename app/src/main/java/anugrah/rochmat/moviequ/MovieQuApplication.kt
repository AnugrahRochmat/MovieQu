package anugrah.rochmat.moviequ

import android.app.Application
import anugrah.rochmat.moviequ.di.coreModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieQuApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MovieQuApplication)
            modules(coreModules)
        }
    }

    // Test dummy PR CHeck
}