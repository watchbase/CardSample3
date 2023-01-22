package com.example.cardsample3.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.cardsample3.databinding.CardListRowBinding
import com.example.cardsample3.model.CardData
import com.example.cardsample3.model.CardObject
import io.realm.RealmResults


//　Define Custom Adaptor
class CardListAdapter(private val context: Context, private var listData: List<CardData> ) : BaseAdapter() {

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): CardData {
        return listData.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val binding = if (convertView == null) {
            // Binding作成
            val inflater = LayoutInflater.from(context)
            CardListRowBinding.inflate(inflater, parent, false)     // CardListRowBindingは レイアウトを定義している　cart_list_row.xml から自動生成されたクラス
        } else {
            DataBindingUtil.getBinding(convertView) ?: throw IllegalStateException()
        }
        with(binding) {
            cardData = listData.get(position)
            // Bindingオブジェクトに即反映
            executePendingBindings()
        }

        return binding.root
    }
}
