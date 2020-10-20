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
import com.zpo.zong.activities.PlayActivity

open class AlbumDetailsAdapter(context: Context, mList: ArrayList<MusicFiles>) :
    RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder>() {

        val context = context

    companion object {
        @JvmStatic
        lateinit var albumFiles: ArrayList<MusicFiles>
    }

    init {
        albumFiles = mList
    }

    private val imageList = listOf<Int>(
        R.drawable.zong_bg,
        R.drawable.music,
        R.drawable.gitar
    )


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImage = itemView.findViewById<ImageView>(R.id.musicImg)
        val albumName = itemView.findViewById<TextView>(R.id.musicFileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return albumFiles.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.albumName.text = albumFiles[position].getTitle()
        val image = getAlbumArt(albumFiles[position].getPath())
        if (image != null) {
            Glide.with(context).asBitmap().load(image).centerCrop().into(holder.albumImage)
        } else {
            Glide.with(context).load(imageList.random()).centerCrop().into(holder.albumImage)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra("sender", "albumDetails")
            intent.putExtra("position", position)
            context.startActivity(intent)
        }

    }

    private fun getAlbumArt(uri: String?): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}