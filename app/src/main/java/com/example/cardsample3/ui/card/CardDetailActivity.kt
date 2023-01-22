package com.example.cardsample3.ui.card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardsample3.R
import com.example.cardsample3.databinding.ActivityCardDetailBinding
import com.example.cardsample3.model.CardData
import com.example.cardsample3.model.SelectedCard

class CardDetailActivity : AppCompatActivity() {

    companion object {
        val SELECTED_CARD_ID_KEY = "selected_card"
    }

    private lateinit var selectedCardObject:SelectedCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCardDetailBinding.inflate(layoutInflater)     // ActivityのLayoutから生成されたクラス名を指定する
        setContentView(binding.root)

        //アクションバーに戻るボタンを追加する。
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var obj = intent.getSerializableExtra(SELECTED_CARD_ID_KEY)
        if(obj is SelectedCard) {
            selectedCardObject = obj
            Toast.makeText(this, selectedCardObject.selected_id.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // 戻るボタンを押したときの処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{    // これは「戻るボタン」のid
                val intentSUb = Intent()
                selectedCardObject.status = "SUCESS"
                intentSUb.putExtra(SELECTED_CARD_ID_KEY, selectedCardObject)

                setResult(Activity.RESULT_OK, intentSUb)

                finish()            //　これで呼び出し元へ戻る
                overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
            }
        }
        return super.onOptionsItemSelected(item)
    }
}