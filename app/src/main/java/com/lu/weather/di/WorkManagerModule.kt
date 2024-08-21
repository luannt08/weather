//package com.lu.weather.di
//
//import android.content.Context
//import android.util.Log
//import androidx.hilt.work.HiltWorkerFactory
//import androidx.startup.Initializer
//import androidx.work.Configuration
//import androidx.work.WorkManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.EntryPoint
//import dagger.hilt.InstallIn
//import dagger.hilt.android.EntryPointAccessors
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object WorkManagerModule : Initializer<WorkManager> {
//
//    private var isInitialized = false
//
//    @Provides
//    @Singleton
//    override fun create(@ApplicationContext context: Context): WorkManager {
//        if (!isInitialized) { // just in case this gets called twice
//            val entryPoint = EntryPointAccessors.fromApplication(
//                context, HiltWorkerFactoryEntryPoint::class.java
//            )
//
//            val configuration = Configuration.Builder().setWorkerFactory(entryPoint.workerFactory())
//                .setMinimumLoggingLevel(Log.VERBOSE).build()
//
//            WorkManager.initialize(context, configuration)
//
//            isInitialized = true
//        }
//
//        return WorkManager.getInstance(context)
//    }
//
//    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
//        return mutableListOf()
//    }
//
//    @EntryPoint
//    @InstallIn(SingletonComponent::class)
//    interface HiltWorkerFactoryEntryPoint {
//        fun workerFactory(): HiltWorkerFactory
//    }
//}