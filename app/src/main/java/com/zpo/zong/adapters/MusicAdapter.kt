package com.zpo.zong.adapters

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.zpo.zong.MusicFiles
import com.zpo.zong.R
import com.zpo.zong.activities.PlayActivity

import java.io.File

class MusicAdapter (context: Context, mList:ArrayList<MusicFiles>): RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {


    companion object{
        var list = ArrayList<MusicFiles>()
    }
    init {
        list = mList
    }

    val context = context
    val imageList = listOf<Int>(
        R.drawable.zong_bg,
        R.drawable.music,
        R.drawable.gitar
    )




    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val fileName: TextView = itemView.findViewById<TextView>(R.id.musicFileName)
        val tvDuration: TextView = itemView.findViewById<TextView>(R.id.tvDuration)
        val album_art: ImageView = itemView.findViewById<ImageView>(R.id.musicImg)
        val menuMore = itemView.findViewById<ImageView>(R.id.menuMore)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_items,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        fun formattedTime(mCurrentPosition:Int):String{
            var totalOut = ""
            var totalNew = ""
            val seconds = (mCurrentPosition % 60).toString()
            var minutes = (mCurrentPosition / 60).toString()
            totalOut = "$minutes : $seconds"
            totalNew = "$minutes : 0$seconds"

            if(seconds.length == 1){
                return  totalNew
            }else{
                return totalOut
            }

        }
        var timeTotal = (list[position].getDuration()!!.toInt()/1000)
        holder.fileName.text = list[position].getTitle()
        holder.tvDuration.text = formattedTime(timeTotal)
        val image = getAlbumArt(list[position].getPath())
        if(image != null){
            Glide.with(context).asBitmap().load(image).centerCrop().into(holder.album_art)
        }else{
            Glide.with(context).load(imageList.random()).centerCrop().into(holder.album_art)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra("position",position)
            context.startActivity(intent)
        }
        holder.menuMore.setOnClickListener {
            val popupMenu = PopupMenu(context,it)
            popupMenu.menuInflater.inflate(R.menu.popup,popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when(item?.itemId){
                    R.id.delete -> {
                        Toast.makeText(context,"Delete Clicked", Toast.LENGTH_SHORT).show()
                        deleteFile(position,it)
                    }


                }
                true
            }
        }
    }

    private fun deleteFile(position:Int,v:View){
        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            list[position].getId()!!.toLong())
        val file = File(list[position].getPath())
        val deleted = file.delete()
        if(deleted){
            context.contentResolver.delete(contentUri,null,null)
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,
                list.size)
            Snackbar.make(v,"File Deleted",Snackbar.LENGTH_LONG)
                .show()
        }else{
            Snackbar.make(v,"Can't be file Deleted",Snackbar.LENGTH_LONG)
                .show()
        }

    }

    private fun getAlbumArt(uri:String?):ByteArray?{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

    fun updateList(musicFilesArrayList:ArrayList<MusicFiles>){
        list = ArrayList()
        list.addAll(musicFilesArrayList)
        notifyDataSetChanged()

    }


}