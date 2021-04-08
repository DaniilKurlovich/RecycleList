package com.example.thirdtask

import android.os.Parcel
import android.os.Parcelable
import java.util.*

enum class TypePractice(val kind: String) {
    GOOD("good"),
    BAD("bad")
}


data class Practice(
    var name: String? = "",
    var description: String? = "",
    var priority: String? = "",
    var typePractice: String? = TypePractice.GOOD.kind,
    var period: Int = 1,
    var count: Int = 1,
    val uniq_id: String? = UUID.randomUUID().toString()
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