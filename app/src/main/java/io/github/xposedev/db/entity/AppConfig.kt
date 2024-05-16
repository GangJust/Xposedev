package io.github.xposedev.db.entity

import android.os.Parcel
import android.os.Parcelable

data class AppConfig(
    val port: String
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(port)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppConfig> {
        override fun createFromParcel(parcel: Parcel): AppConfig {
            return AppConfig(parcel)
        }

        override fun newArray(size: Int): Array<AppConfig?> {
            return arrayOfNulls(size)
        }
    }
}