package com.zpo.zong.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.zpo.zong.R
import kotlinx.android.synthetic.main.activity_lyric.*

class LyricActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyric)

//        var songName = intent.getStringExtra("songName").toString()
//        var singerName = intent.getStringExtra("singerName").toString()
        editSongTitle.visibility = View.VISIBLE
        editSingerName.visibility = View.VISIBLE
        btnSearch.visibility = View.VISIBLE

        btnSearch.setOnClickListener {
            editSongTitle.visibility = View.GONE
            editSingerName.visibility = View.GONE
            btnSearch.visibility = View.GONE
            var sName = findViewById<EditText>(R.id.editSongTitle).text.toString()
            var ssName = findViewById<EditText>(R.id.editSingerName).text.toString()
            songTil.visibility = View.VISIBLE
            songTil.text = editSongTitle.text.toString()
            val url = "https://api.lyrics.ovh/v1/$ssName/$sName"
            url.replace("","20%")
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    try {
                        textLyric.text = it.getString("lyrics")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(applicationContext, it.message,Toast.LENGTH_SHORT).show()
                }
            )
            requestQueue.add(jsonObjectRequest)
        }


    }

}