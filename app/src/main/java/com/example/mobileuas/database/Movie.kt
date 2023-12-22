package com.example.mobileuas.database

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val producer: String = "",
    val rating: String = "",
    val description: String = ""
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeString(producer)
        parcel.writeString(rating)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}
//Parcelable{
//
//    constructor(parcel: Parcel) : this(
//    parcel.readString() ?: "",
//    parcel.readString() ?: "",
//    parcel.readString() ?: "",
//    parcel.readString() ?: "",
//    parcel.readString() ?: "",
//    parcel.readString() ?: ""
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(id)
//        parcel.writeString(imageUrl)
//        parcel.writeString(title)
//        parcel.writeString(producer)
//        parcel.writeString(rating)
//        parcel.writeString(description)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Movie> {
//        override fun createFromParcel(parcel: Parcel): Movie {
//            return Movie(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Movie?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

