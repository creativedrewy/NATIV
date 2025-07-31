package com.creativedrewy.imageslivewallpaper

import android.content.Context
import android.graphics.Color
import android.transition.Scene.getCurrentScene
import android.util.Log
import android.view.MotionEvent
import org.rajawali3d.Object3D
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.ATexture.TextureException
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.view.ISurface
import org.rajawali3d.wallpaper.Wallpaper


//val images = listOf(
//    "https://arweave.net/e7bJrc4k-dQtGVS1rxHlCWiHjdQIM82d4h8OdZ-ioVM?ext=png",
//    "https://arweave.net/cqI0lELJFasPoJjjg-dZoUG1LJDxAa3hq4s8IVvYVgo?ext=png",
//    "https://arweave.net/ghHzqkyF_gg6K3HuB8xTxi4ZRSZ7DPtpUmglWOLch9c?ext=png"
//)
//
//val imageLoader = ImageLoader.Builder(context)
//    .build()
//    .diskCache
//
//private fun loadImages() {
//    bitmaps.clear()
//    images.forEach { url ->
//        try {
//            val rawPath: Path? = imageLoader?.openEditor(url)?.data
//            rawPath?.let { path ->
//                val pathStrSource = path.name.replace(".tmp", "")
//                val cachedImage = context.cacheDir.toString() + "/" + pathStrSource
//
//                val bitmap = BitmapFactory.decodeFile(cachedImage)
//                bitmap?.let { bmp ->
//                    val scaledBitmap = Bitmap.createScaledBitmap(
//                        bmp,
//                        (squareSize.toInt()),
//                        (squareSize.toInt()),
//                        true
//                    )
//
//                    bitmaps.add(scaledBitmap)
//                    if (bmp != scaledBitmap) {
//                        bmp.recycle()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("ImagesWallpaper", "Error loading image: $url", e)
//        }
//    }
//}

class ImagesWallpaperService : Wallpaper() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine(applicationContext, BasicRenderer(applicationContext), ISurface.ANTI_ALIASING_CONFIG.NONE)
    }

}

class BasicRenderer(
    context: Context
) : Renderer(context) {
    private var mSphere: Object3D? = null

    override fun initScene() {
        try {
            val material = Material()
//            material.addTexture(
//                Texture(
//                    "earthColors",
//                    R.drawable.earthtruecolor_nasa_big
//                )
//            )

            material.color = Color.BLUE
            material.colorInfluence = 1f
            mSphere = Sphere(1f, 24, 24)
            mSphere?.setMaterial(material)

            currentScene.addChild(mSphere)
        } catch (e: TextureException) {
            e.printStackTrace()
        }

        currentCamera.enableLookAt()
        currentCamera.setLookAt(0.0, 0.0, 0.0)
        currentCamera.setZ(6.0)
        currentCamera.setOrientation(currentCamera.orientation.inverse())
    }

    override fun onRender(elapsedTime: Long, deltaTime: Double) {
        super.onRender(elapsedTime, deltaTime)
        mSphere!!.rotate(Vector3.Axis.Y, 1.0)
    }

    override fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int
    ) { }

    override fun onTouchEvent(event: MotionEvent?) { }
}