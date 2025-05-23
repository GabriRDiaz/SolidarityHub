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

private lateinit var createTaskButton: Button
private lateinit var listTaskButton: Button


/**
 * A simple [Fragment] subclass.
 * Use the [volunteer_menu.newInstance] factory method to
 * create an instance of this fragment.
 */
class admin_menu : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_admin_menu, container, false)
        findComponents(rootView)
        setListeners()

        return rootView
    }

    private fun setListeners() {
        createTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminMenu_to_CrearTareas, null)
            findNavController().navigate(R.id.action_adminMenu_to_listaTareasFragment, null)
        }

        listTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminMenu_to_listaTareasFragment, null)
        }

    }

    private fun findComponents(rootView: View?) {
        if (rootView != null) {
            createTaskButton = rootView.findViewById(R.id.menuTaskCreationButton)
            listTaskButton = rootView.findViewById(R.id.menuTaskListButton)
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
            admin_menu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}