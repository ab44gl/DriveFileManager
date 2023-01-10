package com.abhishek.drivefilemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.abhishek.drivefilemanager.databinding.FragmentSearchBinding
class SearchFragment : Fragment() {
   private lateinit var binding: FragmentSearchBinding
   lateinit var globalStateViewModel: GlobalStateViewModel
   override fun onCreateView(
       inflater: LayoutInflater,
       container: ViewGroup?,
       savedInstanceState: Bundle?
   ): View {
       binding = FragmentSearchBinding.inflate(inflater, container, false)
       globalStateViewModel= ViewModelProvider(requireActivity())[GlobalStateViewModel::class.java]
       globalStateViewModel.toolbarState.value=ToolbarState.SEARCH
       return binding.root
   }
    override fun onResume() {
        super.onResume()
        Utils.logD("fragment Search")
        globalStateViewModel.toolbarState.value=ToolbarState.SEARCH
        globalStateViewModel.showBackButton.value=true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       super.onViewCreated(view, savedInstanceState)
   }
}