package com.zerone.paymentcheckout

import GridAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zerone.paymentcheckout.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val titles = listOf("Generate Token","Create Order", "Transactions", "Refunds")

        val adapter = GridAdapter(titles) { title ->

            if (title == "Create Order" ) {
                val intent = Intent(requireActivity(), TransactionFormActivity::class.java)
                //intent.putExtra("key", "value")
                requireActivity().startActivity(intent)
            }
            else if (title == "Generate Token" ) {
            val intent = Intent(requireActivity(), GenerateAuthActivity::class.java)
           // intent.putExtra("key", "value")
            requireActivity().startActivity(intent)
        }else {
//                val intent = Intent(requireActivity(), TransactionStatusActivity::class.java)
//                // intent.putExtra("key", "value")
//                requireActivity().startActivity(intent)
                  Toast.makeText(activity, "Coming soon ..", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(activity, 2) // 2 columns
        recyclerView.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
