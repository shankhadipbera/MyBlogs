package com.shankha.myblogs.model

data class Usersdata(
    val name:String="",
    val email :String="",
    val password :String="",
    val profileImage :String =""
){
    constructor():this("","","")
}
