package com.upv.solidarityHub.ui.taskCreation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var UserAdapter: tempTaskFragment.UserAdapter? = null

private lateinit var tempRecycler : RecyclerView
private lateinit var buttonOK: Button
private lateinit var textTask: TextView

private val db: SupabaseAPI = SupabaseAPI()


/**
 * A simple [Fragment] subclass.
 * Use the [tempTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class tempTaskFragment : Fragment() {
    private lateinit var userList: List<Usuario>
    private lateinit var listAllUsers: List<Usuario>
    private var currentMaxSize by Delegates.notNull<Int>()
    private var currentMinSize by Delegates.notNull<Int>()
    private var taskId by Delegates.notNull<Int>()
    private lateinit var userAdapter: UserAdapter

    private var currentTask: SupabaseAPI.taskDB? = null
    private var currentHelpReq: SupabaseAPI.reqDB? = null

    private val requiredHabilityList = listOf(
        "Reconstrucción",
        "Primeros auxilios",
        "Asistencia a mayores",
        "Asistencia a discapacitados",
        "Transporte"
    )

    private val viewModel: TempTaskViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_temp_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userList = arguments?.getSerializable("list") as? ArrayList<Usuario> ?: emptyList()
        taskId = arguments?.getInt("taskID") ?: -1


        lifecycleScope.launch {
            try {
                currentTask = getCurrentTask()
                currentHelpReq = getCurrentReq()
                initializeViews(view)
                listAllUsers = viewModel.getAllAvailableUsers()
                setupRecyclerView()
                setupButton()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
                Log.e("TempTaskFragment", "Error: ${e.message}")
            }
        }
    }


    private suspend fun initializeViews(view: View) {
        currentMaxSize = when (currentHelpReq?.envergadura) {
            "Pequeña (5 voluntarios máx.)" -> 5
            "Media (15 voluntarios máx.)" -> 15
            "Grande (15+ voluntarios)" -> 50
            else -> 0
        }

        currentMinSize = when (currentHelpReq?.envergadura) {
            "Pequeña (5 voluntarios máx.)" -> 1
            "Media (15 voluntarios máx.)" -> 6
            "Grande (15+ voluntarios)" -> 16
            else -> 0
        }

        view.findViewById<TextView>(R.id.textTaskTemp).text = "Tarea: ${currentHelpReq?.titulo}"
    }


    private suspend fun getCurrentTask(): SupabaseAPI.taskDB? {
        return SupabaseAPI().getTaskById(taskId)
    }

    private suspend fun getCurrentReq(): SupabaseAPI.reqDB? {
        return currentTask?.og_req?.let { SupabaseAPI().getHelpReqById(it) }
    }

    private fun setupRecyclerView() {
        val requiresAbility = viewModel.taskRequiresAbility()
        userAdapter = currentHelpReq?.let {
            UserAdapter(
                context = requireContext(),
                maxSize = currentMaxSize,
                minSize = currentMinSize,
                users = listAllUsers,
                requiresAbility = requiresAbility,
                taskCat = it.categoria,
                onSelectionChanged = { buttonconditions() },
                coroutineScope = viewLifecycleOwner.lifecycleScope
            )
        }!!

        view?.findViewById<RecyclerView>(R.id.tempRecycler)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            post {
                (adapter as? UserAdapter)?.setInitialSelections(userList)
                buttonconditions()
            }
        }
    }

    private fun setupButton() {
        view?.findViewById<Button>(R.id.buttonAcceptTemp)?.apply {
            isEnabled = false
            setOnClickListener {
                lifecycleScope.launch {
                    currentTask?.id?.let { it1 -> viewModel.assignSelectedUsers(it1) }
                    navigateBackWithResult()
                }
            }
        }
    }


    private fun navigateBackWithResult() {
        setFragmentResult("task_edit_complete", Bundle())
        findNavController().popBackStack()
    }

    private fun buttonconditions() {
        view?.findViewById<Button>(R.id.buttonAcceptTemp)?.isEnabled =
            userAdapter.getSelectedItems()
                .isNotEmpty() && userAdapter.getSelectedItems().size >= currentMinSize
    }

    class UserAdapter(
        private val context: Context,
        private val maxSize: Int,
        private val minSize: Int,
        private val users: List<Usuario>,
        private val requiresAbility: Boolean,
        private val taskCat: String,
        private val onSelectionChanged: () -> Unit,
        private val coroutineScope: CoroutineScope
    ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        private val selectedItems = mutableSetOf<Int>()

        fun getSelectedItems(): List<Usuario> = selectedItems.map { users[it] }

        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else if (selectedItems.size < maxSize) {
                selectedItems.add(position)
            } else {
                Toast.makeText(context, "Número de usuarios máximos alcanzado", Toast.LENGTH_SHORT)
                    .show()
            }
            notifyItemChanged(position)
            onSelectionChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val layout = if (requiresAbility) R.layout.user_layoutability else R.layout.user_layout
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

            return UserViewHolder(
                view,
                { position -> toggleSelection(position) },
                requiresAbility,
                taskCat,
                coroutineScope
            )
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.bind(user, selectedItems.contains(position))
        }

        override fun getItemCount(): Int = users.size

        fun setInitialSelections(assignedUsers: List<Usuario>) {
            selectedItems.clear()
            val assignedEmails = assignedUsers.map { it.correo }.toSet()
            users.forEachIndexed { index, user ->
                if (assignedEmails.contains(user.correo)) {
                    selectedItems.add(index)
                }
            }
            notifyDataSetChanged()
            onSelectionChanged()
        }

        inner class UserViewHolder(
            itemView: View,
            private val onItemClick: (Int) -> Unit,
            private val requiresAbility: Boolean,
            private val taskCat: String,
            private val coroutineScope: CoroutineScope
        ) : RecyclerView.ViewHolder(itemView) {
            private val name: TextView = itemView.findViewById(R.id.nameText)
            private val town: TextView = itemView.findViewById(R.id.townText)
            private val ability: TextView? =
                if (requiresAbility) itemView.findViewById(R.id.userAbilityText) else null
            private var currentUser: Usuario? = null

            init {
                itemView.setOnClickListener { onItemClick(adapterPosition) }
            }

            fun bind(user: Usuario, isSelected: Boolean) {
                currentUser = user
                name.text = user.nombre
                town.text = user.municipio
                itemView.isActivated = isSelected

                if (requiresAbility) {
                    ability?.text = "Cargando..."
                    loadUserAbility()
                }
            }

            private fun loadUserAbility() {
                coroutineScope.launch {
                    try {
                        val hasAbility = checkUserAbility(currentUser?.correo)
                        ability?.text = if (hasAbility) taskCat else "N/A"
                    } catch (e: Exception) {
                        ability?.text = "Error"
                    }
                }
            }

            private suspend fun checkUserAbility(email: String?): Boolean {
                if (email == null) return false
                val usersWithAbility = SupabaseAPI().getUsersWithAbility(taskCat)
                return usersWithAbility?.contains(email) ?: false
            }
        }
    }

    companion object {
        fun newInstance(userList: ArrayList<Usuario>, taskId: Int): tempTaskFragment {
            return tempTaskFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("list", userList)
                    putInt("taskID", taskId)
                }
            }
        }
    }

}



