package com.zpo.zong

class MusicFiles(
    private var path: String,
    private var title: String,
    private var artists: String,
    private var album: String,
    private var duration: String,
    private var id: String
) {

    fun getPath() : String?{
        return  path
    }
    fun setSender(sender: String?){
        this.path = path!!
    }

    fun getTitle() : String?{
        return  title
    }
    fun setTitle(title: String?){
        this.title = title!!
    }


    fun getArtists() : String?{
        return  artists
    }
    fun setArtists(artists: String?){
        this.artists = artists!!
    }


    fun getAlbum() : String?{
        return  album
    }
    fun setAlbum(album: String?){
        this.album = album!!
    }

    fun getDuration() : String?{
        return  duration
    }
    fun setDuration(duration: String?){
        this.duration = duration!!
    }

    fun getId() : String?{
        return  id
    }
    fun setId(id: String?){
        this.id = id!!
    }
}