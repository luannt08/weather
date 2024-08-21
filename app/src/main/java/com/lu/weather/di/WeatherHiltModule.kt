package com.lu.weather.di

import com.lu.weather.common.realm.RealmHelper
import com.lu.weather.common.retrofit.RetrofitHelper
import com.lu.weather.data.repository.weatherbit.IWeatherRepository
import com.lu.weather.data.repository.weatherbit.WeatherAPIService
import com.lu.weather.data.repository.weatherbit.WeatherRepository
import com.lu.weather.data.source.IRealmDataSource
import com.lu.weather.data.source.RealmDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [WeatherHiltModule.BindHelper::class])
@InstallIn(SingletonComponent::class)
class WeatherHiltModule {

    @Singleton
    @Provides
    fun providesWeatherApiService(retrofitHelper: RetrofitHelper): WeatherAPIService =
        retrofitHelper.provideWeatherbitRetrofit().create(WeatherAPIService::class.java)

    @Provides
    fun providesRealm(realmHelper: RealmHelper) = realmHelper.createRealm()

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindHelper {
        @Binds
        fun bindsWeatherRepository(weatherRepository: WeatherRepository): IWeatherRepository

        @Binds
        fun bindsRealmDataSource(realmDataSource: RealmDataSource): IRealmDataSource
    }
}