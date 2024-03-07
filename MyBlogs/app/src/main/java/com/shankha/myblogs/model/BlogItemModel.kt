package com.shankha.myblogs.model

import android.os.Parcel
import android.os.Parcelable

data class BlogItemModel(
    var heading: String? ="",
    val userName: String? ="",
    val datePublish: String? ="",
    val userId:String?="",
    var blogText: String? ="",
    var likeCount:Int=0,
    val imageUrl: String? ="",
    var isSaved: Boolean=false,
    var postId:String?="",
    val likedBy: MutableList<String>? =null


):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readByte() != 0.toByte(),
        parcel.readString()?:""

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(datePublish)
        parcel.writeString(userId)
        parcel.writeString(blogText)
        parcel.writeInt(likeCount)
        parcel.writeString(imageUrl)
        parcel.writeByte(if (isSaved) 1 else 0)
        parcel.writeString(postId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }
}