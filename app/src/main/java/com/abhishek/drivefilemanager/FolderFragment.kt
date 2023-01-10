package com.abhishek.drivefilemanager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.abhishek.drivefilemanager.databinding.FragmentFolderBinding
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderFragment(val file:File?=null) : Fragment() {
    private lateinit var binding: FragmentFolderBinding
    lateinit var globalStateViewModel: GlobalStateViewModel
    lateinit var adapter: FileAdapter
    var adapterDataModel= arrayListOf<FileDataModel>()
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            globalStateViewModel.popPath()
            parentFragmentManager.popBackStack()
        }
        Utils.logD("onCreate folder fragment")
    }
    // This callback will only be called when MyFragment is at least Started.


    override fun onResume() {
        super.onResume()
        Utils.logD("onResume fragment Folder ")
        if(file==null){
            globalStateViewModel.toolbarState.value=ToolbarState.HOME
            globalStateViewModel.showBackButton.value=false

        }else{
            globalStateViewModel.toolbarState.value=ToolbarState.FOLDER
            globalStateViewModel.showBackButton.value=true
        }

        Utils.logD("path ${globalStateViewModel.filePath.value}")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFolderBinding.inflate(inflater, container, false)
        globalStateViewModel= ViewModelProvider(requireActivity())[GlobalStateViewModel::class.java]
        //
        binding.apply {
            fbuttonAdd.setOnClickListener {
               showBottomSheet()
            }
        }

        val linearLayout=binding.includeFolderLayout.root
        linearLayout.setLayoutVisible(0)

        //recycleView
        val recycleView=binding.includeFolderLayout.dataLayout.recycleView
        adapter =FileAdapter(adapterDataModel)

        recycleView.layoutManager=LinearLayoutManager(requireContext())
        recycleView.adapter=adapter
        recycleView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener{model, position ->
            if(model.isDirectory){
                //move to directory
                Utils.logD("moving to model $model")
                gotoFolderFragment(model.file)
            }

        }
       /* globalStateViewModel.model.observe(viewLifecycleOwner){
            Utils.logD("model change $it")
        }*/
        //drive
        updateFileFromServer()
        return binding.root
    }

    private fun showBottomSheet() {
        val sheet=FolderModelBottomSheet()
        sheet.setClickListener {
            showInputDialog(it)
        }
        sheet.show(parentFragmentManager,"Sheet")
    }

    private fun driveHelper(): DriveHelper {
        return (requireActivity() as MainActivity).driveHelper
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFileFromServer() {

        lifecycleScope.launch(Dispatchers.IO){
            try {
                val top=globalStateViewModel.topPath()
                val  fileList=if(top==null){
                    Utils.logD("root-------")
                    driveHelper().query("'root' in parents ")
                }else{
                    Utils.logD("id-------")
                     driveHelper().query("'${top.id}' in parents ")
                }


                val data=FileDataModel.fromFileList(fileList)

                withContext(Dispatchers.Main){
                    //update data
                    adapterDataModel.clear()
                    adapterDataModel.addAll(data)
                    Utils.logD(data.size)
                    adapter.notifyDataSetChanged()
                }
            }catch (e:Exception){
                Utils.logD("list  ",e)
            }

        }
    }

    private fun gotoFolderFragment(file: File?) {
        if (file != null) {
            globalStateViewModel.addPath(file)
        }
        val t = parentFragmentManager.beginTransaction()
        t.setCustomAnimations(
            R.anim.slide_in, R.anim.slide_out,
            R.anim.slide_in_left, R.anim.slide_out_left
        )
        t.replace(R.id.fragmentContainerView, FolderFragment(file))
        t.setReorderingAllowed(true)
        t.addToBackStack(null)
        t.commit()



    }
    //dialog
    private fun showInputDialog(type:String) {

        val dialog=InputDialog(
            if(type=="file") "File" else "Folder",
            if(type=="file") "file name" else "folder name"
        )
        dialog.setOnClickListener { text, isOk ->
            Utils.logD("$text    $isOk")
            //lets create a folder
            lifecycleScope.launch(Dispatchers.IO){
                if(text.isNotEmpty()) {
                    try {
                        globalStateViewModel.topPath()?.let {
                            driveHelper().setParent(it.id)
                        }

                        val res=if(type=="file")driveHelper().createTextFile(text)else driveHelper().createFolder(text)
                        driveHelper().clearParent()
                        Utils.showMessageToast("file created $res",requireContext())
                        updateFileFromServer()

                    }catch (e:Exception){
                        Utils.showMessageToast("file not created",requireContext())
                        Utils.logD("error",e)
                    }

                }
            }
        }
        dialog.show(parentFragmentManager,"Dialog")


    }

}