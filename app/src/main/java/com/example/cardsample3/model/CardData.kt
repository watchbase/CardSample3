package com.example.cardsample3.model


import android.icu.text.SimpleDateFormat
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//　ローカルdbへの保存用
open class CardObject: RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var companyName: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var tel: String = ""
    var email: String = ""
    var created: Date? = null

}

// アクティビティ間でのデータ受け渡し用（Serializableである必要があるため,Realmとは別に定義）
class CardData(
    var id:Int = 0,
    var companyName:String = "",
    var firstName:String = "",
    var lastName:String = "",
    var tel:String = "",
    var email:String = "",
    var created: ZonedDateTime? = null,
    var cardObject:CardObject? = null
):Serializable {

    init {
        if (created == null) {
            val localDateTime = LocalDateTime.now()
            created = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
        }
    }

    val createdString: String
        get() = created!!.format(DateTimeFormatter.ISO_LOCAL_DATE)     // 2020-12-04　形式へ変換

    var personName:String = ""
        get() = lastName + " " + firstName

    fun save() {
        //Realmインスタンスの取得
        val realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync {

        }
    }
}

