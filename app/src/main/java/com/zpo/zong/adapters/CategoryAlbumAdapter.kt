package com.zpo.zong.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zpo.zong.MusicFiles
import com.zpo.zong.R
import com.zpo.zong.activities.AlbumDetailsActivity

class CategoryAlbumAdapter(context: Context, mList:ArrayList<MusicFiles>):RecyclerView.Adapter<CategoryAlbumAdapter.MyHolder>() {
    val context = context
    private val albumFiles = mList
    private val imageList = listOf<Int>(
        R.drawable.zong_bg,
        R.drawable.music,
        R.drawable.gitar
    )


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val albumImage = itemView.findViewById<ImageView>(R.id.albumImage)
        val albumName = itemView.findViewById<TextView>(R.id.albumName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.category_album_items,parent,false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return albumFiles.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.albumName.text = albumFiles[position].getAlbum()
        val image = getAlbumArt(albumFiles[position].getPath())
        if(image != null){
            Glide.with(context).asBitmap().load(image).centerCrop().into(holder.albumImage)
        }else{
            Glide.with(context).load(imageList.random()).centerCrop().into(holder.albumImage)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context,
                AlbumDetailsActivity::class.java)
            intent.putExtra("albumName",albumFiles[position].getAlbum())
            context.startActivity(intent)
        }
    }

    private fun getAlbumArt(uri:String?):ByteArray?{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}