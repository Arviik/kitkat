package com.example.kitkat.model

import android.os.Parcel
import android.os.Parcelable

data class ConversationItem(val username: String, val lastMessage: String, val profilePicUrl: String) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(lastMessage)
        parcel.writeString(profilePicUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConversationItem> {
        override fun createFromParcel(parcel: Parcel): ConversationItem {
            return ConversationItem(parcel)
        }

        override fun newArray(size: Int): Array<ConversationItem?> {
            return arrayOfNulls(size)
        }
    }
}