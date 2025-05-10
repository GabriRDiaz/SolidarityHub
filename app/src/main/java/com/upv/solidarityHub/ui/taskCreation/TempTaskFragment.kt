package com.upv.solidarityHub.ui.taskCreation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var UserAdapter: tempTaskFragment.userAdapter? = null

private lateinit var tempRecycler : RecyclerView
private lateinit var buttonOK: Button
private lateinit var textTask: TextView

private val db: SupabaseAPI = SupabaseAPI()

private var currentTask by Delegates.notNull<Int>()

/**
 * A simple [Fragment] subclass.
 * Use the [tempTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class tempTaskFragment : Fragment() {
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

    private lateinit var taskIDList: List<Int>
    private var currentSize by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_temp_task, container, false)

        taskIDList = arguments?.getSerializable("list") as? ArrayList<Int> ?: emptyList()

        currentTask = taskIDList.first()

        lifecycleScope.launch{
            try{
                setSize()
                findComponents(rootView)
                setText()
            }
            catch(e: Exception){
                println("Error loading users: ${e.message}")
            }
        }



        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment tempTaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            tempTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class userAdapter(
        private val context: Context,
        private val maxSize: Int,
        private val users: List<Usuario>,
        private val onItemSelected: (position: Int, isSelected: Boolean) -> Unit
    ) : RecyclerView.Adapter<userAdapter.UserViewHolder>(){

        private val selectedItems = mutableSetOf<Int>()

        fun toggleSelection(position: Int){
            if(selectedItems.contains(position)){
                selectedItems.remove(position)
            }
            else{
                if(selectedItems.size < maxSize){
                    selectedItems.add(position)
                }
                else{
                    Toast.makeText(context, "Número de usuarios máximos alcanzado", Toast.LENGTH_SHORT).show()
                }

            }
            notifyItemChanged(position)
            onItemSelected(position, selectedItems.contains(position))
        }

        fun getSelectedItems(): List<Usuario>{
            return selectedItems.map {users[it]}
        }

        fun clearSelections(){
            selectedItems.clear()
            notifyDataSetChanged()
        }

        class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val name: TextView = itemView.findViewById(R.id.nameText)
            val town: TextView = itemView.findViewById(R.id.townText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent,false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.name.text = user.nombre
            holder.town.text = user.municipio

            holder.itemView.isActivated = selectedItems.contains(position)

            holder.itemView.setOnClickListener{
                toggleSelection(position)
            }
        }



        override fun getItemCount() = users.size

    }

    private suspend fun findComponents(rootView: View){
        tempRecycler = rootView.findViewById(R.id.tempRecycler)
        tempRecycler.layoutManager = LinearLayoutManager(requireContext())
        val users = getAllUsers()

        UserAdapter = users?.let { userAdapter(requireContext(), currentSize, it) { position, isSelected ->
            buttonconditions()
        }
        }

        tempRecycler.adapter = UserAdapter
        buttonOK = rootView.findViewById(R.id.buttonAcceptTemp)

        buttonOK.setOnClickListener {
            lifecycleScope.launch {
                val selectedUsers = UserAdapter?.getSelectedItems() ?: emptyList()
                selectedUsers.forEach { user ->
                    db.getUsuarioByCorreo(user.correo)
                        ?.let { it1 -> db.createIsAssigned(currentTask, it1) }
                }
                taskIDList = taskIDList.drop(1)
                if(taskIDList.isNotEmpty()){
                    currentTask = taskIDList.first()
                    UserAdapter?.clearSelections()
                    setText()
                    setSize()

                    val users = getAllUsers() ?: emptyList()
                    UserAdapter = userAdapter(requireContext(), currentSize, users) { position, isSelected ->
                        buttonconditions()
                    }
                    tempRecycler.adapter = UserAdapter
                    buttonconditions()

                }
                else{
                    Toast.makeText(requireContext(), "Se han asignado usuarios a todas las tareas", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_tempTaskFragment_to_crearTareasFragment)
                }
            }
        }
         buttonOK.isEnabled = false

        textTask = rootView.findViewById(R.id.textTaskTemp)


    }

    private suspend fun getAllUsers():List<Usuario>?{
        return db.getAllUsers()
    }

    private suspend fun getCurrenTask():SupabaseAPI.taskDB?{
        return db.getTaskById(currentTask)
    }

    private suspend fun getCurrentReq():SupabaseAPI.reqDB?{
        var task = getCurrenTask()
        return task?.og_req?.let { db.getHelpReqById(it) }
    }
    private suspend fun setText(){
        var request = getCurrentReq()
        if (request != null) {
            textTask.setText("Tarea: ${request.titulo}")
        }

    }

    private suspend fun setSize(){
        var req = getCurrentReq()
        val size = req?.envergadura
        when(size){
            "Pequeña (5 voluntarios máx.)" -> currentSize = 5
            "Media (15 voluntarios máx.)" -> currentSize = 15
            "Grande (15+ voluntarios)" -> currentSize = 50
        }

    }

    private fun buttonconditions(){
        buttonOK.isEnabled = UserAdapter?.getSelectedItems()?.isNotEmpty() ?: false
    }

    }



