package com.abhishek.drivefilemanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.services.drive.model.File

class GlobalStateViewModel:ViewModel() {
    val toolbarState: MutableLiveData<ToolbarState> by lazy {
        MutableLiveData<ToolbarState>(ToolbarState.HOME)
    }
    val showBackButton: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
     val filePath: MutableLiveData<ArrayList<File>> by lazy {
        MutableLiveData(arrayListOf<File>())
    }
    val model= MutableLiveData<FileDataModel>(FileDataModel("root",true))

    fun addPath(file: File) {
        filePath.value?.add(file)
        filePath.value=filePath.value
    }
    fun  topPath():File?{
        filePath.value?.let {
            if(it.size>0){
                return  it[it.size-1]
            }

        }
        return null
    }
    fun popPath(){
        filePath.value?.let {
            if(it.size>0){
                it.removeAt(it.size-1)
            }

        }
        //update livedata
        filePath.value=filePath.value
    }
}