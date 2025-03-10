package com.nhatvm.toptop.data.video


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.nhatvm.toptop.data.video.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class VideoDetailViewModel @Inject constructor(
    val videoPlayer: ExoPlayer,
    val videoRepository: VideoRepository,
): ViewModel() {

    private var _uiState =
        MutableStateFlow<VideoDetailUiState>(VideoDetailUiState.Default) // mutableLivedata
    val uiState: StateFlow<VideoDetailUiState> // livedata
        get() = _uiState

    init {
        videoPlayer.repeatMode = REPEAT_MODE_ALL
        videoPlayer.playWhenReady = true
        videoPlayer.prepare()
    }

    fun handleAction(action: VideoDetailAction) {
        when (action) {
            is VideoDetailAction.LoadData -> {
                val videoId = action.id
                loadVideo(videoId = videoId)
            }

            is VideoDetailAction.ToggleVideo -> {
                toggleVideo()
            }

        }
    }

    private fun loadVideo(videoId: Int) {
        _uiState.value = VideoDetailUiState.Loading
        viewModelScope.launch {
            delay(10L)
            Log.d("VIDEOID", "$videoId")
            var videoUrl = videoRepository.getVideoObject()[videoId].urlVideo
            startplayVideo(url = videoUrl)

            _uiState.value = VideoDetailUiState.Success
        }
    }

    fun pauseVideo() {
        videoPlayer.pause()
    }
    fun playVideo() {
        videoPlayer.play()
    }

    private fun startplayVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        videoPlayer.setMediaItem(mediaItem)
        videoPlayer.play()
    }

    private fun toggleVideo() {
        if (videoPlayer.isLoading) {
        } else {
            if (videoPlayer.isPlaying) videoPlayer.pause()
            else videoPlayer.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        videoPlayer.release()
    }

}

// MVVM MVI
sealed interface VideoDetailUiState {
    object Default: VideoDetailUiState
    object Loading: VideoDetailUiState
    object Success: VideoDetailUiState
}

sealed class VideoDetailAction {
    data class LoadData(val id: Int): VideoDetailAction()
    object ToggleVideo: VideoDetailAction()
}