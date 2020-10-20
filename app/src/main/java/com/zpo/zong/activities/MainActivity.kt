package com.zpo.zong.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zpo.zong.adapters.CategoryAlbumAdapter
import com.zpo.zong.adapters.MusicAdapter
import com.zpo.zong.MusicFiles
import com.zpo.zong.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    companion object{
        private const val REQUEST_CODE = 1
        var musicFileList:ArrayList<MusicFiles>? = null
        var shuttleBoolean:Boolean = false
        var repeatBoolean:Boolean = false
        var albums:ArrayList<MusicFiles>? = null
        var musicAdapter: MusicAdapter? = null

    }
    private var MY_SORT_PREF = "SortOrder"



//    private var musicAdapter:MusicAdapter? = null
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permission()
        rvCategory.setHasFixedSize(true)
        recyclerView.setHasFixedSize(true)

        appName.text  = getString(R.string.app_name_custom)

        if((musicFileList?.size)!! >= 1){

            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val rvCategory = findViewById<RecyclerView>(R.id.rvCategory)
            musicAdapter =
                MusicAdapter(
                    this,
                    musicFileList!!
                )
            recyclerView.adapter =
                musicAdapter
            rvCategory.adapter = CategoryAlbumAdapter(
                this,
                musicFileList!!
            )
            recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
            rvCategory.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)

        }else{
            Log.e("Error","Music size not have")
        }

        findingSong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
//                notesAdapter.cancelTimer()

            }

            override fun afterTextChanged(editable: Editable) {

                val myFiles = ArrayList<MusicFiles>()
                for (song in 1 until musicFileList!!.size){
                    var oneSong = musicFileList!![song]
                    if(oneSong.getTitle()!!.toLowerCase()!!.contains(editable.toString().toLowerCase()))
                    {
                        myFiles!!.add(oneSong)
                    }
                }
                musicAdapter!!.updateList(myFiles)

            }
        })


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun permission(){
        if(ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }else{
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
            musicFileList = getAllAudios(this)


        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
                musicFileList = getAllAudios(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun getAllAudios(context: Context):ArrayList<MusicFiles>{
        val preferences = getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE)
        val sortOrder = preferences.getString("sorting","sortByName")
        var duplicate = ArrayList<String>()
        val tempAudioList:ArrayList<MusicFiles> = ArrayList()
        var order:String = ""
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when(sortOrder){
//            "sortByName"->{order = MediaStore.MediaColumns.DISPLAY_NAME + "ASC"}
//            "sortByDate"->{order = MediaStore.MediaColumns.DATE_ADDED +"ASC"}
//            "sortBySize"->{order = MediaStore.MediaColumns.SIZE +"DESC"}
        }
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        val cursor = context.contentResolver.query(uri, projection, null, null, order)

        if(cursor != null){
            while (cursor.moveToNext()){
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artists = cursor.getString(4)
                val id = cursor.getString(5)

                var musicFiles = MusicFiles(
                    path,
                    title,
                    artists,
                    album,
                    duration,
                    id
                )
//                Log.e("Data","path $path : title $title  artists $artists album  $album duration $duration" )
                tempAudioList.add(musicFiles)
                if(duplicate.contains(album)){
                    albums!!.add(musicFiles)
                    duplicate.add(album)
                }
            }
            cursor.close()
        }
        return  tempAudioList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search,menu)

        val menuItem = menu!!.findItem(R.id.searchOption)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {


        val userInput = newText?.toLowerCase()
        val myFiles = ArrayList<MusicFiles>()
        for (song in 1 until musicFileList!!.size){
            var oneSong = musicFileList!![song]
            if(oneSong.getTitle()!!.toLowerCase()!!.contains(userInput.toString()))
            {
                myFiles!!.add(oneSong)
            }
        }
        musicAdapter!!.updateList(myFiles)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor = getSharedPreferences(MY_SORT_PREF, Context.MODE_PRIVATE).edit()
        when(item.itemId){
            R.id.byName ->{
                editor.putString("sorting","sortByName")
                editor.apply()
                this.recreate()
            }
            R.id.byDate ->{
                editor.putString("sorting","sortByDate")
                editor.apply()
                this.recreate()
            }
            R.id.bySize ->{
                editor.putString("sorting","sortBySize")
                editor.apply()
                this.recreate()
            }

        }
        return super.onOptionsItemSelected(item)

    }



}



