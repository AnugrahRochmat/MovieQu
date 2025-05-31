package anugrah.rochmat.moviequ.di

import androidx.room.Room
import anugrah.rochmat.moviequ.data.datasource.local.AppDatabase
import anugrah.rochmat.moviequ.data.datasource.remote.TMDBApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val networkModule = module {
    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.TMDB_BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single<TMDBApiService> {
        get<Retrofit>().create(TMDBApiService::class.java)
    }
}

val databaseModule = module {
    // Database
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "moviequ_database"
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().movieGenreDao() }
}

val coreModules = listOf(
    networkModule,
    databaseModule
)