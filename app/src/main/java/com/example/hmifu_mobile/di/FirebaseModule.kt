package com.example.hmifu_mobile.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Firebase dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): com.google.firebase.storage.FirebaseStorage = com.google.firebase.storage.FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): com.example.hmifu_mobile.util.NetworkMonitor {
        return com.example.hmifu_mobile.util.ConnectivityManagerNetworkMonitor(context)
    }
}
