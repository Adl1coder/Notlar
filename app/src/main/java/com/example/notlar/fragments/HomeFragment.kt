package com.example.notlar.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notlar.R
import com.example.notlar.databinding.FragmentHomeBinding
import com.example.notlar.utils.adapter.NotAdapter
import com.example.notlar.utils.model.NotData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    NotAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var taskAdapter: NotAdapter
    private lateinit var toDoItemList: MutableList<NotData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //get data from firebase
        getTaskFromFirebase()


        binding.addTaskBtn.setOnClickListener {

            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
            )

        }
    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val notumuz =
                        taskSnapshot.key?.let { NotData(it, taskSnapshot.value.toString()) }

                    if (notumuz != null) {
                        toDoItemList.add(notumuz)
                    }

                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("Tasks")
            .child(authId)


        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = NotAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }

    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {

        database
            .push().setValue(todoTask)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Not eklendi", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun updateTask(toDoData: NotData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.notId] = toDoData.not
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Not güncellendi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: NotData, position: Int) {
        database.child(toDoData.notId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Not silindi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: NotData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.notId, toDoData.not)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

}