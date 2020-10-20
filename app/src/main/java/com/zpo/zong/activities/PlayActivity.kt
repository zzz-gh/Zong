package com.zpo.zong.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.zpo.zong.adapters.AlbumDetailsAdapter.Companion.albumFiles

import com.zpo.zong.activities.MainActivity.Companion.repeatBoolean
import com.zpo.zong.activities.MainActivity.Companion.shuttleBoolean
import com.zpo.zong.adapters.MusicAdapter.Companion.list
import com.zpo.zong.MusicFiles
import com.zpo.zong.R
import kotlinx.android.synthetic.main.activity_play.*
import java.util.*

class PlayActivity : AppCompatActivity(),MediaPlayer.OnCompletionListener{
    companion object{
        private var listSongs:ArrayList<MusicFiles>? = null
        private var uri:Uri? = null
        private lateinit var mediaPlayer: MediaPlayer
    }
    private var position = -1
    private var handler: Handler = Handler()

    private lateinit var playThread:Thread
    private lateinit var prevThread:Thread
    private lateinit var nextThread:Thread



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)


        getIntentMethod()
        songName.text = listSongs!![position].getTitle()
        songArtist.text = listSongs!![position].getArtists()
        mediaPlayer.setOnCompletionListener(this)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something

            }
        })


        handler.post(object  :Runnable{
            override fun run() {
                if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        val duration = mediaPlayer.duration/1000
                        seekBar.progress = mCurrentPosition
                        durationPlayed.text = formattedTime(mCurrentPosition)
                        durationTotal.text = formattedTime(duration)

                }
                handler.postDelayed(this,1000)
            }

        })

        idShuffle.setOnClickListener {
            if(shuttleBoolean){
                shuttleBoolean = false
                idShuffle.setImageResource(R.drawable.shuffle_off)
            }else{
                shuttleBoolean = true
                idShuffle.setImageResource(R.drawable.shuffle_on)
            }
        }

        idRepeat.setOnClickListener {
            if(repeatBoolean){
                repeatBoolean = false
                idRepeat.setImageResource(R.drawable.repeat_off)
            }else{
                repeatBoolean = true
                idRepeat.setImageResource(R.drawable.repeat_on)
            }
        }



        lyricSearch.setOnClickListener {
            val intent = Intent(this, LyricActivity::class.java)
            intent.putExtra("songName",songName.text.toString())
            intent.putExtra("singerName",songArtist.text.toString())
            startActivity(intent)
        }

    }

    override fun onResume() {
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    private fun playThreadBtn(){
        playThread = Thread()
        {
                playPauseBtn.setOnClickListener {
                    playPauseBtnClicked()
                }
        }
        playThread.start()
    }

    private fun playPauseBtnClicked(){
        if (mediaPlayer.isPlaying){
            playPauseBtn.setImageResource(R.drawable.ic_play)
            mediaPlayer.pause()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition


                    }
                    handler.postDelayed(this,1000)
                }

            })
        }else{
            playPauseBtn.setImageResource(R.drawable.ic_pause)
            mediaPlayer.start()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition
                    }
                    handler.postDelayed(this,1000)
                }

            })
        }
    }

    private fun nextThreadBtn(){
        nextThread = Thread()
        {
            idNext.setOnClickListener {
                nextBtnClicked()
            }
        }
        nextThread.start()
    }

    private fun nextBtnClicked(){

        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.release()
            if(shuttleBoolean && !repeatBoolean){
                position = getRandom(listSongs!!.size - 1)
            }else if(!shuttleBoolean && !repeatBoolean){
                position = (position+1)% listSongs!!.size
            }

            uri = Uri.parse(
                listSongs!![position].getPath()!!)
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            metaData(uri)
            songName.text = listSongs!![position].getTitle()
            songArtist.text = listSongs!![position].getArtists()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition


                    }
                    handler.postDelayed(this,1000)
                }

            })
            mediaPlayer.setOnCompletionListener(this)
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause)
            mediaPlayer.start()
        }else{
            mediaPlayer.stop()
            mediaPlayer.release()
            if(shuttleBoolean && !repeatBoolean){
                position = getRandom(listSongs!!.size - 1)
            }else if(!shuttleBoolean && !repeatBoolean){
                position = (position+1)% listSongs!!.size
            }

            val urii = Uri.parse(listSongs!![position].getPath()!!)
            mediaPlayer = MediaPlayer.create(applicationContext,urii)
            metaData(urii)
            songName.text = listSongs!![position].getTitle()
            songArtist.text = listSongs!![position].getArtists()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition


                    }
                    handler.postDelayed(this,1000)
                }

            })
            mediaPlayer.setOnCompletionListener(this)
            playPauseBtn.setBackgroundResource(R.drawable.ic_play)
        }

    }

    private fun getRandom(i:Int):Int{
        val random = Random()
        return random.nextInt(i+1)
    }

    private fun prevThreadBtn(){
        prevThread = Thread()
        {
            idPrev.setOnClickListener {
                prevBtnClicked()
            }
        }
        prevThread.start()
    }

    private fun prevBtnClicked(){
        if(mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.release()
            if(shuttleBoolean && !repeatBoolean){
                position = getRandom(listSongs!!.size - 1)
            }else if(!shuttleBoolean && !repeatBoolean){
                position = if (position - 1 < 0) listSongs!!.size - 1 else position - 1
            }
            uri = Uri.parse(
                listSongs!![position].getPath()!!)
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            metaData(uri)
            songName.text = listSongs!![position].getTitle()
            songArtist.text = listSongs!![position].getArtists()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition


                    }
                    handler.postDelayed(this,1000)
                }

            })
            mediaPlayer.setOnCompletionListener(this)
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause)
            mediaPlayer.start()
        }else{
            mediaPlayer.stop()
            mediaPlayer.release()
            if(shuttleBoolean && !repeatBoolean){
                position = getRandom(listSongs!!.size - 1)
            }else if(!shuttleBoolean && !repeatBoolean){
                position = if (position - 1 < 0) listSongs!!.size - 1 else position - 1
            }
            val urii = Uri.parse(listSongs!![position].getPath()!!)
            mediaPlayer = MediaPlayer.create(applicationContext,urii)
            metaData(urii)
            songName.text = listSongs!![position].getTitle()
            songArtist.text = listSongs!![position].getArtists()
            seekBar.max = mediaPlayer.duration/1000
            handler.post(object  :Runnable{
                override fun run() {
                    if (mediaPlayer != null) {

                        val mCurrentPosition = (mediaPlayer.currentPosition)/1000
                        seekBar.progress = mCurrentPosition


                    }
                    handler.postDelayed(this,1000)
                }

            })
            mediaPlayer.setOnCompletionListener(this)
            playPauseBtn.setBackgroundResource(R.drawable.ic_play)
        }
    }

    private fun formattedTime(mCurrentPosition:Int):String{
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

    private fun getIntentMethod(){
        mediaPlayer = MediaPlayer()
        position = intent.getIntExtra("position",-1)
        val sender = intent.getStringExtra("sender")
        listSongs = if(sender != null && sender == "albumDetails"){
            albumFiles
        }else{
            list
        }

        if(listSongs != null){
            playPauseBtn.setImageResource(R.drawable.ic_pause)
            uri = Uri.parse(
                listSongs!![position].getPath())
        }

        if(mediaPlayer.isPlaying){

                mediaPlayer.stop()
                mediaPlayer.release()

                mediaPlayer = MediaPlayer.create(applicationContext,
                    uri
                )
                mediaPlayer.start()


        }else{
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            mediaPlayer.start()
        }
        seekBar.max = mediaPlayer.duration/1000
        metaData(uri)
    }

    private fun metaData(uri: Uri?){
        var retriever:MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())

        val art = retriever.embeddedPicture
        var bitmap:Bitmap
        if(art != null){
            bitmap = BitmapFactory.decodeByteArray(art,0,art.size)
            imageAnimation(this,coverArt,bitmap)
            Palette.from(bitmap).generate { palette ->
                val swatch = palette!!.dominantSwatch
                if(swatch != null){
                    imageViewGradient.setBackgroundResource(R.drawable.gradient_bg)
                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(swatch.rgb, 0x00000000)
                    )
                    imageViewGradient.background = gradientDrawable
//                    layoutTopBottom.background = gradientDrawable
                    val gradientDrawableBg = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(swatch.rgb, swatch.rgb)
                    )
                    mContainer.background = gradientDrawableBg

                    songName.setTextColor(swatch.titleTextColor)
                    songArtist.setTextColor(swatch.bodyTextColor)
                }else{

                    imageViewGradient.setBackgroundResource(R.drawable.gradient_bg)
                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(0xff000000.toInt(), 0x00000000)
                    )
                    imageViewGradient.background = gradientDrawable

                    val gradientDrawableBg = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(0xff000000.toInt(), 0xff000000.toInt())
                    )
                    mContainer.background = gradientDrawableBg
//                    layoutTopBottom.background = gradientDrawableBg
                    songName.setTextColor(Color.WHITE)
                    songArtist.setTextColor(Color.DKGRAY)
                }
            }
        }else{
            Glide.with(this).load(R.drawable.music).centerCrop().into(coverArt)
            imageViewGradient.setBackgroundResource(R.drawable.gradient_bg)
//            layoutTopBottom.setBackgroundResource(R.drawable.gradient_bg)
            songName.setTextColor(Color.WHITE)
            songArtist.setTextColor(Color.DKGRAY)

        }
    }

    private fun imageAnimation(context: Context,imageView:ImageView,bitmap: Bitmap){
        val animOut = AnimationUtils.loadAnimation(context,android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(context,android.R.anim.fade_in)
        animOut.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object :Animation.AnimationListener{
                    override fun onAnimationRepeat(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {

                    }

                    override fun onAnimationStart(p0: Animation?) {

                    }
                })
                imageView.startAnimation(animIn)
            }

            override fun onAnimationStart(p0: Animation?) {

            }

        })

        imageView.startAnimation(animOut)

    }

    override fun onCompletion(p0: MediaPlayer?) {
        nextBtnClicked()
        if(mediaPlayer != null){
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(this)
        }
    }


}


