package com.abhishek.drivefilemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishek.drivefilemanager.databinding.FragmentFolderModelBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FolderModelBottomSheet : BottomSheetDialogFragment() {
   private lateinit var binding: FragmentFolderModelBottomSheetBinding
   private var clickListener:((type:String)->Unit)?=null
   override fun onCreateView(
       inflater: LayoutInflater,
       container: ViewGroup?,
       savedInstanceState: Bundle?
   ): View? {
       binding = FragmentFolderModelBottomSheetBinding.inflate(inflater, container, false)
       binding.buttonFolder.setOnClickListener {
           click("folder")
       }
       binding.buttonFile.setOnClickListener {
           click("file")
       }
       return binding.root
   }
    private fun click(type: String){
        clickListener?.invoke(type)
        dismiss()
    }
    fun setClickListener(f:(type:String)->Unit){
        clickListener=f
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       super.onViewCreated(view, savedInstanceState)
   }
}