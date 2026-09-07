package com.anahit.mediaplayer.viewmodel.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.anahit.mediaplayer.model.MediaFileModel
import com.anahit.mediaplayer.room.MediaDatabase
import com.anahit.mediaplayer.room.entity.MediaEntity

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    // good
    private val providerHandler = ContentProviderHandler(getApplication())

    var musicLiveData = MutableLiveData<ArrayList<MediaFileModel>>()
    var videoLiveData = MutableLiveData<ArrayList<MediaFileModel>>()
    var favoriteMusicsLiveData = MutableLiveData<List<MediaEntity>>()


    fun getAllMusics() {
        musicLiveData = MutableLiveData<ArrayList<MediaFileModel>>()

        providerHandler.getAllMusics {
            it?.let {
                musicLiveData.postValue(it)
            }
        }
    }

    fun getAllVideos() {
        videoLiveData = MutableLiveData<ArrayList<MediaFileModel>>()

        providerHandler.getAllVideos {
            it?.let {
                videoLiveData.postValue(it)
            }
        }
    }




    fun insertMedia(mediaModel: MediaFileModel){
        Thread{
            val mediaEntity = MediaEntity(mediaModel.id, mediaModel.title, mediaModel.path, mediaModel.duration, mediaModel.byteArray, mediaModel.artist)
            MediaDatabase
                .getInstance(getApplication())
                .mediaDao()
                .insert(mediaEntity)
        }.start()
    }


    fun deleteMedia(mediaId: String){
        Thread{
            MediaDatabase
                .getInstance(getApplication())
                .mediaDao()
                .deleteMediaById(mediaId)
        }.start()
    }


    fun getAll() {
        Thread {
            favoriteMusicsLiveData.postValue(
                MediaDatabase
                    .getInstance(getApplication())
                    .mediaDao()
                    .getAll()
            )
        }.start()
    }
}