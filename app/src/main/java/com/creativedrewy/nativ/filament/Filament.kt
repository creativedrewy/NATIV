/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.creativedrewy.nativ.filament

import android.content.Context
import com.google.android.filament.Engine
import com.google.android.filament.Scene
import com.google.android.filament.View
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.FilamentAsset
import com.google.android.filament.gltfio.ResourceLoader
import java.nio.Buffer
import java.nio.ByteBuffer

data class ProductScene(
    val engine: Engine,
    val scene: Scene,
    val asset: FilamentAsset
)

val scenes = mutableMapOf<String, ProductScene>()

fun loadModelGlb(
    assetLoader: AssetLoader,
    resourceLoader: ResourceLoader,
    buffer: Buffer
): FilamentAsset {
    val asset = assetLoader.createAssetFromBinary(buffer)
    asset?.apply {
        resourceLoader.loadResources(asset)
        asset.releaseSourceData()
    }

    return asset!!
}

fun readCompressedAsset(context: Context, assetName: String): ByteBuffer {
    val input = context.assets.open(assetName)
    val bytes = ByteArray(input.available())
    input.read(bytes)
    return ByteBuffer.wrap(bytes)
}

fun setupModelViewer(viewer: ModelViewer) {
    val options = viewer.view.dynamicResolutionOptions
    options.enabled = true
    viewer.view.dynamicResolutionOptions = options

    // viewer.view.ambientOcclusion = View.AmbientOcclusion.SSAO
    viewer.view.antiAliasing = View.AntiAliasing.FXAA
    viewer.view.sampleCount = 4

    val bloom = viewer.view.bloomOptions
    bloom.enabled = true
    viewer.view.bloomOptions = bloom
}
