package com.creativedrewy.nativ

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NATIVApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(AnimatedImageDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir)
                    .maxSizePercent(0.5)
                    .build()
            }
            .build()
    }

}
