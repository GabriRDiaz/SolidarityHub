package com.upv.solidarityHub.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var reqButton: Button
private lateinit var missingButton: Button
private lateinit var myHelReqsButton: Button


/**
 * A simple [Fragment] subclass.
 * Use the [volunteer_menu.newInstance] factory method to
 * create an instance of this fragment.
 */
class affected_menu : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_affected_menu, container, false)
        findComponents(rootView)
        setListeners()
        return rootView    }

    private fun findComponents(rootView: View?) {
        if (rootView != null) {
            reqButton = rootView.findViewById(R.id.menuhelpReqButton)
            missingButton = rootView.findViewById(R.id.menuMissingButton)
            myHelReqsButton = rootView.findViewById(R.id.menumyHelpReqsButton)
        }

    }

    private fun setListeners(){
        reqButton.setOnClickListener {
            findNavController().navigate(R.id.action_affectedmenu_to_createreq)
        }

        missingButton.setOnClickListener {
            findNavController().navigate(R.id.action_affectedmenu_to_registrarDesaparecido)
        }

        myHelReqsButton.setOnClickListener {
            findNavController().navigate(R.id.action_affectedmenu_to_myHelpReqs)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment volunteer_menu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            affected_menu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}