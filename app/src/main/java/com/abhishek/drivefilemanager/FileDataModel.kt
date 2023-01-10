package com.abhishek.drivefilemanager

import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList

data class FileDataModel(
    var name:String,
    var isDirectory:Boolean,
    var file:File?=null
) {
    companion object{
        fun randomModels(): ArrayList<FileDataModel> {
            return arrayListOf<FileDataModel>().apply {
                repeat(20){
                    add(FileDataModel("Year $it",it%2==0))
                }
            }
        }
        fun fromFileList(fileList: FileList): ArrayList<FileDataModel> {
            return arrayListOf<FileDataModel>().apply {
               fileList.files.forEach {
                   add(
                       FileDataModel(
                           it.name,
                           it.mimeType=="application/vnd.google-apps.folder",
                           it
                       )
                   )
               }
            }
        }

    }
}