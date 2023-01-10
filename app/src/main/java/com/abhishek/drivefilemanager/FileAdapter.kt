package com.abhishek.drivefilemanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.drivefilemanager.databinding.FileItemBinding

class FileAdapter(private val data: ArrayList<FileDataModel>) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {
    private  var clickListener:((model:FileDataModel,position:Int)->Unit)?=null
    inner class ViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
           binding.root.setOnClickListener{
               clickListener?.let {
                   val position = adapterPosition
                   if (position != RecyclerView.NO_POSITION) {
                      it(data[position],position)
                   }
               }
           }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=FileItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val model=data[position]
            tvName.text=model.name
            val imgId=if(model.isDirectory)R.drawable.ic_folder else R.drawable.ic_file
            imgViewDirOrFile.setImageResource(imgId)
        }
    }
    override fun getItemCount(): Int {
        return  data.size
    }
    fun setOnItemClickListener(f:(model:FileDataModel,position:Int)->Unit) {
        clickListener=f
    }
}