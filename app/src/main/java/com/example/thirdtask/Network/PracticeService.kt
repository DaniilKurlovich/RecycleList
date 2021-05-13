package com.example.thirdtask.Network

import android.os.Parcel
import android.os.Parcelable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.*


data class HabitUID(val uid: String)
data class HabitDone(val uid: String, val date: Int)

data class Habit(val title: String, val description: String, val priority: Int, val type: Int,
                 val frequency: Int, val count: Int, var uid: String?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(priority)
        parcel.writeInt(type)
        parcel.writeInt(frequency)
        parcel.writeInt(count)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Habit> {
        override fun createFromParcel(parcel: Parcel): Habit {
            return Habit(parcel)
        }

        override fun newArray(size: Int): Array<Habit?> {
            return arrayOfNulls(size)
        }
    }
}


interface PracticeService {
    @GET("habit")
    suspend fun getPractices(): List<Habit>

    @PUT("habit")
    fun addHabit(@Body habit: Habit): Call<HabitUID>

    @DELETE("habit")
    suspend fun deleteHabit(@Body habit: HabitUID): Call<String>

    @POST("habit")
    suspend fun postDoneHabit(@Body habit: HabitDone): Call<String>
}


class AuthorizationInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }

    private fun Request.signedRequest(): Request {
        return newBuilder()
            .header("Authorization", "4c465727-6fc5-4e6b-b460-c95137b7289f")
            .build()
    }
}