package com.example.cardsample3.ui.notifications


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cardsample3.MainActivity
import com.example.cardsample3.R
import com.example.cardsample3.databinding.FragmentNotificationsBinding


class NotificationsFragment : Fragment() {
    private val texts = arrayOf(
        "abc ", "bcd", "cde", "def", "efg",
        "fgh", "ghi", "hij", "ijk", "jkl",
        "klm", "abc ", "bcd", "cde", "def",
        "efg", "fgh", "ghi", "hij", "ijk"
    )
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()

      //  (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.title_notifications))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val arrayAdapter = NotificationListAdapter( requireContext(), this.texts)    //　Setup custom adaptor

        // Set custom adaptor for ListVIew
        val listView = binding.MessageListView
        listView.adapter = arrayAdapter

        return root
    }

    //　Define Custom Adaptor
    private inner class NotificationListAdapter(private val context: Context, private val data: Array<String>, ) : BaseAdapter() {

       private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return texts.size
        }

        override fun getItem(position: Int): String {
            return texts[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?
        ): View? {
            val textView1: TextView
            val textView2: TextView
            val textView3: TextView
            var rowView = convertView

            if (rowView == null) {
                 rowView = layoutInflater.inflate(R.layout.message_listview_row, parent, false)!!   // generate View object from Layout resource
            }

            val item: String = getItem(position)

            textView1 = rowView.findViewById<TextView>(R.id.textView1) as TextView
            textView2 = rowView.findViewById<TextView>(R.id.textView2) as TextView
            textView3 = rowView.findViewById<TextView>(R.id.textView3) as TextView
            textView1.text = item
            textView2.text = item
            textView3.text = item


            return rowView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}