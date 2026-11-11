package com.mamxanh.shared.time

expect fun treeDateAt(epochMillis: Long, zone: String): String
expect fun treeMidnightAfter(epochMillis: Long, zone: String): Long

fun followingDate(date: String): String {
    val p = date.split('-').map(String::toInt)
    var y=p[0];var m=p[1];var d=p[2]+1
    val leap=y%4==0 && (y%100!=0 || y%400==0)
    val length=listOf(31,if(leap)29 else 28,31,30,31,30,31,31,30,31,30,31)[m-1]
    if(d>length){d=1;m++;if(m>12){m=1;y++}}
    return "${y.toString().padStart(4,'0')}-${m.toString().padStart(2,'0')}-${d.toString().padStart(2,'0')}"
}
