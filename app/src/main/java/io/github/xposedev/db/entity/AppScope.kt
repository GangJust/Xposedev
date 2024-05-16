package io.github.xposedev.db.entity

import android.os.Parcel
import android.os.Parcelable

data class AppScope(
    val id: Int,
    val appName: String,
    val packageName: String,
    val applicationName: String,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(appName)
        parcel.writeString(packageName)
        parcel.writeString(applicationName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppScope> {
        override fun createFromParcel(parcel: Parcel): AppScope {
            return AppScope(parcel)
        }

        override fun newArray(size: Int): Array<AppScope?> {
            return arrayOfNulls(size)
        }
    }

}
