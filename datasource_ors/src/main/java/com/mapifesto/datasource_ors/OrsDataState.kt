package com.mapifesto.datasource_ors

sealed class OrsDataState<T> {

    data class Error<T>(
        val error: String
    ): OrsDataState<T>()

    data class OrsData<T>(
        val data: T
    ): OrsDataState<T>()

}
