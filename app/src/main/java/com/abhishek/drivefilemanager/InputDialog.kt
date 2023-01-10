package com.abhishek.drivefilemanager

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.abhishek.drivefilemanager.databinding.InputDialogBinding


class InputDialog(
    private val title:String="New Folder",
    private val editTextText:String="New Folder",
) : DialogFragment() {
    private lateinit var binding: InputDialogBinding
    private  var clickListener:((text:String,isOk:Boolean)->Unit)?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InputDialogBinding.inflate(inflater, container, false)
        binding.apply {
            tvTitle.text=title
            edittext.setText(editTextText)
            edittext.selectAll()
            buttonOk.setOnClickListener {
                buttonClick(true)
            }
            buttonCancel.setOnClickListener {
                buttonClick(false)
            }
        }
        return binding.root
    }
    fun setOnClickListener(f:(text:String,isOk:Boolean)->Unit){
        clickListener=f
    }
    private fun buttonClick(isOk:Boolean){
        requireDialog().dismiss()
        clickListener?.invoke(binding.edittext.text.toString(),isOk)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        requireDialog().window?.setLayout((6 * width)/7,ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.edittext.requestFocus()
        requireDialog().window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
    }
}