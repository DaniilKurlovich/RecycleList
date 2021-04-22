package com.example.thirdtask.Models

import android.os.Parcel
import android.os.Parcelable


data class Practice(
    var name: String?,
    var description: String?,
    var priority: String?,
    var typePractice: String?,
    var period: Int,
    var count: Int,
    val uniq_id: String?
    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(priority)
        parcel.writeString(typePractice)
        parcel.writeInt(period)
        parcel.writeInt(count)
    }

    fun setParcel(p: Practice) {
        name = p.name
        description = p.description
        typePractice = p.typePractice
        period = p.period
        count = p.count
        priority = p.priority
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Practice> {
        override fun createFromParcel(parcel: Parcel): Practice {
            return Practice(parcel)
        }

        override fun newArray(size: Int): Array<Practice?> {
            return arrayOfNulls(size)
        }
    }
}