package com.example.anothertestproject.di

import androidx.room.Room
import com.example.anothertestproject.data.room.Database
import com.example.anothertestproject.domain.usecases.GetCurrenciesUseCase
import com.example.anothertestproject.domain.usecases.ManageUserUseCase
import com.example.anothertestproject.domain.usecases.OnExchangeUseCase
import com.example.anothertestproject.domain.viewnodels.MainActivityViewModel
import com.example.anothertestproject.repositories.cyrrency.CurrencyApi
import com.example.anothertestproject.repositories.cyrrency.CurrencyApiRepository
import com.example.anothertestproject.repositories.cyrrency.CurrencyApiRepositoryImpl
import com.example.anothertestproject.repositories.user.RoomUserRepositoryImpl
import com.example.anothertestproject.repositories.user.UserRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    fun provideUseApi(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }

    single { provideUseApi(get()) }
}

val retrofitModule = module {

    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(factory: Gson, client: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl("https://api.apilayer.com/")
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
            .create(CurrencyApi::class.java)
    }

    single { provideGson() }
    single { provideHttpClient() }
    single { provideRetrofit(get(), get()) }
}

val repositoriesModule = module {
    single<CurrencyApiRepository> { CurrencyApiRepositoryImpl(get()) }
    single<UserRepository> { RoomUserRepositoryImpl(get()) }
}
val roomModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            Database::class.java,
            "RoomUserDatabase"
        ).build()
    }
    single {
        val db = get<Database>()
        db.getLibraryDao()
    }
}
val useCases = module {
    factory { GetCurrenciesUseCase(get()) }
    factory { ManageUserUseCase(get()) }
    factory { OnExchangeUseCase() }
}

val viewModelModule = module {
    factory { MainActivityViewModel(get(), get(), get()) }
}