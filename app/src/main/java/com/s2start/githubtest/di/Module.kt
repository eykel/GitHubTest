package com.s2start.githubtest.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.s2start.githubtest.service.repository.GitUserRepository
import com.s2start.githubtest.service.repository.local.GitUserDataBase
import com.s2start.githubtest.service.repository.local.UserDAO
import com.s2start.githubtest.service.repository.remote.GitHubApi
import com.s2start.githubtest.util.SecurityPreferences
import com.s2start.githubtest.view.viewmodel.FavoriteViewModel
import com.s2start.githubtest.view.viewmodel.GitHubSearchViewModel
import com.s2start.githubtest.view.viewmodel.GitUserViewModel
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val netModule = module {

    fun provideRetrofit(client: OkHttpClient, gson : Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    fun provideHttpClient(cache: Cache): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(cache)

        return okHttpClientBuilder.build()
    }

    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    fun provideGson(): Gson = GsonBuilder().create()

    single { provideCache(androidApplication()) }
    single { provideHttpClient(get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }
}

val apiModule = module {

    fun provideCharacterApi(retrofit: Retrofit): GitHubApi {
        return retrofit.create(GitHubApi::class.java)
    }

    single { provideCharacterApi(get()) }
}

val viewModule = module {
    single { SecurityPreferences(androidContext()) }
}

val viewModelModule = module {
    viewModel {
        GitHubSearchViewModel(get(), androidContext())
    }
    viewModel {
        GitUserViewModel(get(), androidContext())
    }
    viewModel {
        FavoriteViewModel(get(), androidContext())
    }
}

val repositoryModule = module {
    fun provideUserRepository(remote: GitHubApi, local : UserDAO ): GitUserRepository {
        return GitUserRepository(remote, local)
    }

    single { provideUserRepository(get(), get()) }
}

val databaseModule = module {

    fun provideDatabase(application: Application): GitUserDataBase {
        return Room.databaseBuilder(application, GitUserDataBase::class.java, "gitDB")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    fun provideDao(database: GitUserDataBase): UserDAO {
        return database.userDAO()
    }

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}