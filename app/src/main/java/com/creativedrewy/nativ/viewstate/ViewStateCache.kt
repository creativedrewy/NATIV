package com.creativedrewy.nativ.viewstate

import com.creativedrewy.nativ.viewmodel.NftViewProps
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewStateCache @Inject constructor() {

    private var props: List<NftViewProps> = listOf()
    private var cachedMediaBytes: MutableMap<String, ByteArray> = mutableMapOf()

    val hasCache: Boolean
        get() = props.isNotEmpty()

    val cachedProps: List<NftViewProps>
        get() = props

    val mediaCache: Map<String, ByteArray>
        get() = cachedMediaBytes

    fun clearCache() {
        props = listOf()
    }

    fun updateCache(propsList: List<NftViewProps>) {
        props = propsList
    }

    fun cacheMediaItem(id: String, mediaBytes: ByteArray) {
        cachedMediaBytes[id] = mediaBytes
    }
}
