package com.example.cardsample3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cardsample3.model.CardData
import com.example.cardsample3.model.CardObject
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.delete
import io.realm.kotlin.where
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList


class CardListViewModel : ViewModel() {
    private var _listItems = MutableLiveData<List<CardData>>()
    private lateinit var realm: Realm

    // サンプルデータ
    val planeData = arrayListOf(
        CardData(1, "Watchbase", "Hideji", "Kitamura", "09042251234","watchabse@gmail.com",null),
        CardData(2, "Cofame","Hitoshi","Kawanabe", "0801245678","hitoshi@cofame.com",null)
    )

    val listItems: LiveData<List<CardData>>     //このオブジェクトを監視対象として指定しているのでLiveDataの形式のママにしておく必要がある

    get() = _listItems

    init {
        //_listItems.value = planeData

        if (reaData() == 0)  {
            insertData(planeData)   // サンプルデータを登録
            reaData()
        }
    }


    fun updateData() {
       // planeData += CardData(3,"BONA","Mai", "Kit")
     //   _listItems.value = planeData

    }

    fun reaData():Int {
        //Realmインスタンスの取得
        realm = Realm.getDefaultInstance()
        val results:RealmResults<CardObject> = realm.where(CardObject::class.java).findAll()    //.sort(CardObject.created)


        var tempArray = mutableListOf<CardData>()

        for (obj in results) {
            var dt:ZonedDateTime? = null
            val tm = obj.created?.let {
                 // Date object
                dt = ZonedDateTime.ofInstant(
                    it.toInstant(),
                    ZoneId.systemDefault()
                )
            }
            tempArray.add(CardData(obj.id, obj.companyName, obj.firstName, obj.lastName, obj.tel, obj.email, dt))

        }

        _listItems.value = tempArray

        realm.close()

        return tempArray.size
    }

    fun insertData(data:List<CardData>) {
        //Realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        realm.executeTransactionAsync({ realm ->
            val id = realm.where<CardObject>().max("id")
            var nextId = (id?.toLong() ?: 0)

            for (obj in data) {
                nextId += 1
                val realmObject = realm.createObject<CardObject>(nextId)
                realmObject.companyName = obj.companyName
                realmObject.firstName = obj.firstName
                realmObject.lastName = obj.lastName
                realmObject.tel = obj.tel
                realmObject.email = obj.email

                lateinit var dt:ZonedDateTime

                if (obj.created == null) {
                    val localDateTime = LocalDateTime.now()                   // 現在の日時
                    dt = ZonedDateTime.of(localDateTime, ZoneId.of("UTC)"))     // UTCへ変換
                }else{
                    dt = obj.created!!
                }
                realmObject.created = Date(dt.toInstant().toEpochMilli());   // RealmではKotlinのLocalDateTimeやZOnedDateTimeはサポートされていないので、Dateへ変換する
            }
        }, {
            realm.close()
        }, { error ->
            realm.close()
        })
    }
}

