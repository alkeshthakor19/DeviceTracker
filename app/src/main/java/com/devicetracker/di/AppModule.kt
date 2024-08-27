package com.devicetracker.di

import com.devicetracker.data.repository.AuthRepositoryImpl
import com.devicetracker.data.repository.MemberRepositoryImpl
import com.devicetracker.domain.repository.AuthRepository
import com.devicetracker.domain.repository.MemberRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(
        auth = Firebase.auth
    )

    @Provides
    fun provideMemberRepository(): MemberRepository = MemberRepositoryImpl(
        db = Firebase.firestore,
        storageReference = FirebaseStorage.getInstance().reference
    )
}