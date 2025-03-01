package com.example.kitkat.network.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AzureUploadService {

    @PUT
    fun uploadVideoToAzure(
        @Url url: String,
        @Header("x-ms-blob-type") blobType: String = "BlockBlob",
        @Body file: RequestBody
    ): Call<ResponseBody>
}
