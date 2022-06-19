package com.mapifesto.openrouteservice

import com.mapifesto.datasource_ors.OrsIntermediary
import com.mapifesto.datasource_ors.OrsIntermediaryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Singleton
    @Provides
    fun provideOsmIntermediary(

    ) : OrsIntermediary {
        return OrsIntermediaryImpl()
    }

}