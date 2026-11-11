import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.util.ArrayDeque

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
// Keep the supplied growth sequence as build input; generated sprites share one canvas.
val cherryGrowthResources = layout.buildDirectory.dir("generated/cherryGrowth/res")
val generateCherryGrowthArtwork = tasks.register("generateCherryGrowthArtwork") {
    inputs.file(rootProject.layout.projectDirectory.file("mics/tree growth.gif"))
    outputs.dir(cherryGrowthResources)
    doLast {
        val output = outputs.files.singleFile.resolve("drawable-nodpi").apply { mkdirs() }
        javax.imageio.ImageIO.createImageInputStream(inputs.files.singleFile).use { stream ->
            val reader = javax.imageio.ImageIO.getImageReadersByFormatName("gif").next()
            try {
                reader.input = stream
                val count = reader.getNumImages(true)
                require(count == 12) { "Review the growth mapping before changing the supplied sequence." }
                val metadata = reader.streamMetadata.getAsTree("javax_imageio_gif_stream_1.0")
                val screen = (0 until metadata.childNodes.length)
                    .map { metadata.childNodes.item(it) }.first { it.nodeName == "LogicalScreenDescriptor" }
                val width = screen.attributes.getNamedItem("logicalScreenWidth").nodeValue.toInt()
                val height = screen.attributes.getNamedItem("logicalScreenHeight").nodeValue.toInt()
                val canvas = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                var previousDisposal = "none"
                var previousBounds = Rectangle()
                var previousCanvas: BufferedImage? = null
                var background = Color.WHITE
                var artworkBounds: Rectangle? = null
                repeat(count) { index ->
                    val graphics = canvas.createGraphics()
                    try {
                        when (previousDisposal) {
                            "restoreToBackgroundColor" -> {
                                graphics.color = background
                                graphics.fill(previousBounds)
                            }
                            "restoreToPrevious" -> previousCanvas?.let {
                                graphics.composite = AlphaComposite.Src
                                graphics.drawImage(it, 0, 0, null)
                                graphics.composite = AlphaComposite.SrcOver
                            }
                        }
                        val frameMetadata = reader.getImageMetadata(index)
                            .getAsTree("javax_imageio_gif_image_1.0")
                        val nodes = (0 until frameMetadata.childNodes.length).map { frameMetadata.childNodes.item(it) }
                        val descriptor = nodes.first { it.nodeName == "ImageDescriptor" }.attributes
                        val x = descriptor.getNamedItem("imageLeftPosition").nodeValue.toInt()
                        val y = descriptor.getNamedItem("imageTopPosition").nodeValue.toInt()
                        val frame = reader.read(index)
                        val disposal = nodes.first { it.nodeName == "GraphicControlExtension" }
                            .attributes.getNamedItem("disposalMethod").nodeValue
                        previousCanvas = if (disposal == "restoreToPrevious") {
                            BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also {
                                val copy = it.createGraphics()
                                try { copy.drawImage(canvas, 0, 0, null) } finally { copy.dispose() }
                            }
                        } else null
                        graphics.drawImage(frame, x, y, null)
                        if (index == 0) background = Color(canvas.getRGB(0, 0), true)
                        previousDisposal = disposal
                        previousBounds = Rectangle(x, y, frame.width, frame.height)
                    } finally { graphics.dispose() }

                    // Remove only near-white pixels connected to the export background.
                    // Interior petal highlights stay opaque, and no per-frame cropping moves the root.
                    val pixels = canvas.getRGB(0, 0, width, height, null, 0, width)
                    val pending = ArrayDeque<Int>()
                    val visited = BooleanArray(pixels.size)
                    fun enqueue(position: Int) {
                        if (visited[position]) return
                        visited[position] = true
                        val color = pixels[position]
                        val red = color shr 16 and 255
                        val green = color shr 8 and 255
                        val blue = color and 255
                        if (color ushr 24 == 0 || minOf(red, green, blue) >= 245) pending.add(position)
                    }
                    repeat(width) { enqueue(it); enqueue((height - 1) * width + it) }
                    repeat(height) { enqueue(it * width); enqueue(it * width + width - 1) }
                    while (pending.isNotEmpty()) {
                        val position = pending.removeFirst()
                        pixels[position] = 0
                        val x = position % width
                        val y = position / width
                        if (x > 0) enqueue(position - 1)
                        if (x + 1 < width) enqueue(position + 1)
                        if (y > 0) enqueue(position - width)
                        if (y + 1 < height) enqueue(position + width)
                    }
                    pixels.forEachIndexed { position, color ->
                        if (color ushr 24 != 0) {
                            val pixel = Rectangle(position % width, position / width, 1, 1)
                            artworkBounds = artworkBounds?.union(pixel) ?: pixel
                        }
                    }
                    val sprite = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                    sprite.setRGB(0, 0, width, height, pixels, 0, width)
                    javax.imageio.ImageIO.write(sprite, "png", output.resolve("cherry_growth_" + index.toString().padStart(2, '0') + ".png"))
                }
                // One union for every stage removes export whitespace without changing growth scale/root.
                val bounds = requireNotNull(artworkBounds)
                val values = output.parentFile.resolve("values").apply { mkdirs() }
                values.resolve("cherry_growth_bounds.xml").writeText(
                    """<resources><integer-array name="cherry_growth_bounds"><item>${bounds.x}</item><item>${bounds.y}</item><item>${bounds.width}</item><item>${bounds.height}</item></integer-array></resources>"""
                )
            } finally { reader.dispose() }
        }
    }
}

android {
    namespace = "com.mamxanh.core.ui"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    buildFeatures { compose = true }
    sourceSets.getByName("main").res.srcDir(cherryGrowthResources)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
tasks.named("preBuild").configure { dependsOn(generateCherryGrowthArtwork) }

kotlin {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
}
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
