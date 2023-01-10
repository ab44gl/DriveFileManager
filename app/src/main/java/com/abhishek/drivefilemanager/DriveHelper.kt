package com.abhishek.drivefilemanager

import android.util.Log
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class DriveHelper(private val drive: Drive) {
    private var parent= ""
    init {
        Utils.logD("drive is $drive")
    }
    fun setParent(p:String){
        parent=p
    }
    fun clearParent(){
        parent=""
    }
    fun createFolder(name: String = "folder-" + Random().nextInt(Int.MAX_VALUE)): File {
        val metadata: File = File()
            .setMimeType("application/vnd.google-apps.folder")
            .setName(name)
        if(parent.isNotEmpty()){
            metadata.parents= arrayListOf(parent)
        }
        val res = drive.files().create(metadata).execute()
            ?: throw IOException("drive Null result when requesting file creation.")
        Utils.logD("created  folder $res")
        return res
    }


    fun createTextFile(
        name: String = "text-file-" + Random().nextInt(Int.MAX_VALUE),
        data: String="Nothing"
    ): File {
        val metadata: File = File()
            .setMimeType("text/plain")
            .setName(name)
        if(parent.isNotEmpty()){
            metadata.parents= arrayListOf(parent)
        }
        val res = drive.files().create(metadata, ByteArrayContent.fromString(null, data)).execute()
            ?: throw IOException("drive Null result when requesting file creation.")
        Utils.logD("created  textFile $res")
        return res
    }

    fun list(): FileList {
        val res = drive.files().list().setSpaces("drive").execute()
            ?: throw IOException("drive Null result when list file")
        Utils.logD("${res.files.size} " + res)
        return res
    }
    fun query(q:String): FileList {
        val res = drive.files().list().setQ(q).setSpaces("drive").execute()
            ?: throw IOException("drive Null result when list file")
        Utils.logD("${res.files.size} " + res)
        return res
    }

    fun deleteAll() {
        val fileList = list()
        for (file in fileList.files) {
            try {
                drive.files().delete(file.id).execute()
                Utils.logD("deleted ${file.name}")
            } catch (e: IOException) {
                Utils.logD( "can't delete file $file", e)
            }

        }
    }

    fun readFile(id: String): Pair<String, String> {
        val metadata: File = drive.files().get(id).execute()
        val name = metadata.name
        val stringBuilder = StringBuilder()
        drive.files().get(id).executeMediaAsInputStream().use { `is` ->
            BufferedReader(InputStreamReader(`is`)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            }
        }
        val contents = stringBuilder.toString()
        Utils.logD("file $name \n data $contents")
        return Pair(name, contents)
    }

    fun updateFile(id: String, name: String? = null, data: String): File {
        val file = File()
        name?.let { file.setName(name) }
        val contentStream = ByteArrayContent.fromString("text/plain", data)
        val res = drive.files().update(id, file, contentStream).execute()
            ?: throw IOException("drive Null result when list file")
        Utils.logD("updated ${file.name}")
        return file
    }
}