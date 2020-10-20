package com.zpo.zong.activities

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zpo.zong.adapters.AlbumDetailsAdapter
import com.zpo.zong.activities.MainActivity.Companion.musicFileList
import com.zpo.zong.MusicFiles
import com.zpo.zong.R
import kotlinx.android.synthetic.main.activity_album_details.*
import java.util.*

class AlbumDetailsActivity : AppCompatActivity() {

    private var albumName:String = ""
    var albumSongs = ArrayList<MusicFiles>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)

        albumName = intent.getStringExtra("albumName").toString()
        var j= 0

        for(i in 1 until musicFileList!!.size){
            if (albumName == musicFileList!![i].getAlbum()) {
                albumSongs.add(j, musicFileList!![i])
                j++
            }
        }

        val image = getAlbumArt(albumSongs[0].getPath())
        if(image != null){
            Glide.with(this)
                .load(image)
                .into(albumPhoto)
        }else{
            Glide.with(this)
                .load(R.drawable.music)
                .into(albumPhoto)
        }

        if(albumSongs.size > 1){

            recyclerViewAlbum.adapter =
                AlbumDetailsAdapter(this, albumSongs)
            recyclerViewAlbum.layoutManager =LinearLayoutManager(this,RecyclerView.VERTICAL,false)
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

