package com.example.notlar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

import com.example.notlar.R
import com.example.notlar.databinding.FragmentToDoDialogBinding
import com.example.notlar.utils.model.NotData
import com.google.android.material.textfield.TextInputEditText

class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var toDoData: NotData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(notId: String, not: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("NotId", notId)
                    putString("Not", not)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentToDoDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){

            toDoData = NotData(arguments?.getString("notId").toString() ,arguments?.getString("not").toString())
            binding.todoEt.setText(toDoData?.not)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val notum = binding.todoEt.text.toString()
            if (notum.isNotEmpty()){
                if (toDoData == null){
                    listener?.saveTask(notum , binding.todoEt)
                }else{
                    toDoData!!.not = notum
                    listener?.updateTask(toDoData!!, binding.todoEt)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask:String , todoEdit: TextInputEditText)
        fun updateTask(toDoData: NotData , todoEdit: TextInputEditText)
    }

}