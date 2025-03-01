package com.example.kitkat.network.dto

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class VideoDTO(
    val id: Int? = null,
    val title: String,
    val duration: Int,
    val authorId: Int,
    val videoUrl: String,
    val thumbnailUrl: String,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String,
    val isPublic: Boolean,
): Serializable

data class VideoWithAuthor(
    val first: VideoDTO,
    val second: UserWithoutPasswordDTO
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as VideoDTO,
        parcel.readSerializable() as UserWithoutPasswordDTO
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(first)
        parcel.writeSerializable(second)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoWithAuthor> {
        override fun createFromParcel(parcel: Parcel): VideoWithAuthor {
            return VideoWithAuthor(parcel)
        }

        override fun newArray(size: Int): Array<VideoWithAuthor?> {
            return arrayOfNulls(size)
        }
    }
}
