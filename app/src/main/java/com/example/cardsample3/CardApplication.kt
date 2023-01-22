package com.example.cardsample3

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

// Realmの初期化

class CardApplication: Application(){

    override fun onCreate(){
        super.onCreate()

        //初期化
        Realm.init(this)

        //設定
        val config= RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            // .readOnly()
            .build()

        Realm.setDefaultConfiguration(config)

        /*
        val realm = Realm.getDefaultInstance()

        try {
            realm.executeTransactionAsync {
                realm.deleteAll()
            }
        }finally{
            realm.close()
        }
    */
    }

}