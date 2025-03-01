package com.example.kitkat.model

import android.os.Parcel
import android.os.Parcelable

enum class MessageSender {
    ME,
    THEM,
    INFO
}

data class MessageItem(val message: String, var sender: MessageSender): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        MessageSender.ME
    ) {
        this.sender = MessageSender.valueOf(parcel.readString().toString())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(this.sender.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageItem> {
        override fun createFromParcel(parcel: Parcel): MessageItem {
            return MessageItem(parcel)
        }

        override fun newArray(size: Int): Array<MessageItem?> {
            return arrayOfNulls(size)
        }
    }
}