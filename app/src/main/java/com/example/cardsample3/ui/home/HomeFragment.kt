package com.example.cardsample3.ui.home

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.example.cardsample3.model.CardData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cardsample3.MainActivity
import com.example.cardsample3.R

import com.example.cardsample3.databinding.FragmentHomeBinding
import com.example.cardsample3.model.CardObject
import com.example.cardsample3.model.SelectedCard
import com.example.cardsample3.ui.card.CardDetailActivity
import com.example.cardsample3.ui.login.LoginActivity
import com.example.cardsample3.viewmodel.CardListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.RealmResults
import java.io.Serializable



class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: CardListViewModel by viewModels()        // Initialize ViewModel
    private var arrayAdapter: CardListAdapter? = null

   // private lateinit var observer: Observer<List<CardData>>


    override fun onStart() {
        super.onStart()
        // Set title
      //  (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.title_home))     // ActivityのonCreateよりonCreateViewの方が先に呼ばれるので、後で呼びだされるonStart内で実行している
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set observer for Live data
        observer = Observer<List<CardData>> {
            if (arrayAdapter == null) {
                // Set custom adaptor for ListVIew
                arrayAdapter = CardListAdapter(requireContext(), viewModel.listItems.value!!)    //　Setup custom adaptor　　listItemsはliveDataなので、中身を取り出して使う
                val listView = binding.CardListView
                listView.adapter = arrayAdapter

                // Did selected a row
                listView.setOnItemClickListener { adapterView, _, position, _ ->
                    //val cardData = adapterView.getItemAtPosition(position) as CardData

                    showCardDetailActivity(position)

                    //CardDeleteConfirmationDialogFragment().show( childFragmentManager, CardDeleteConfirmationDialogFragment.TAG)
                }
            }
            arrayAdapter!!.notifyDataSetChanged()     // called when listItems is changed
        }
        viewModel.listItems.observe(viewLifecycleOwner, observer)


        val button: FloatingActionButton = binding.updateButton  //findViewById(R.id.shutterButton)
        button.setOnClickListener {
            viewModel.updateData()      // ViewModelにデータ更新を指示
        }
        return root
    }

    fun showCardDetailActivity(position: Int) {

        val selectedCard = SelectedCard(position)

        launcher.launch(Intent(context, CardDetailActivity::class.java)
            .putExtra(CardDetailActivity.SELECTED_CARD_ID_KEY,
                selectedCard))     // intentにセット  "SELECTED_CARD_KEY"は遷移先のクラスでcompanion objectとして定義

        requireActivity().overridePendingTransition(R.anim.open_enter,
            R.anim.open_exit);       // 遷移アニメーション

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val res = result.data?.getSerializableExtra(CardDetailActivity.SELECTED_CARD_ID_KEY)
                if (res is SelectedCard) {
                    Toast.makeText(requireContext(), res.status, Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}

class CardDeleteConfirmationDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_confirmation))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm)) { _,_ ->
                Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show()
            }
            .create()

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Toast.makeText(requireContext(), R.string.canceled, Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val TAG = "CardDeleteConfirmationDialog"
    }
}

