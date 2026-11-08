package com.mamxanh.app.feature.tree.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.cos
import kotlin.math.sin

/** A single continuous rig: every branch retains its attachment as growth changes. */
@Composable
fun KawaiiPlantIllustration(
    growth: Int,
    speciesId: String = "sprout",
    decoration: String = "NONE",
    modifier: Modifier = Modifier,
    growthOverride: Float? = null
) {
    val animatedGrowth by animateFloatAsState(
        targetValue = growth.coerceIn(0, 120).toFloat(),
        animationSpec = tween(1400),
        label = "tree-growth"
    )
    // Inspection follows the finger exactly; earned growth moves along the same rig.
    val progress = ((growthOverride ?: animatedGrowth) / 120f).coerceIn(0f, 1f)
    val inspecting = LocalInspectionMode.current || growthOverride != null
    val breeze = rememberInfiniteTransition(label = "garden-breeze")
    val phase by breeze.animateFloat(
        initialValue = 0f, targetValue = 6.283185f,
        animationSpec = infiniteRepeatable(tween(6400, easing = LinearEasing)),
        label = "tree-breeze-phase"
    )
    val wind = if (inspecting) 0f else sin(phase)
    Canvas(modifier.aspectRatio(0.95f)) {
        val w = size.width
        val h = size.height
        fun ease(start: Float, end: Float): Float {
            val t = ((progress - start) / (end - start)).coerceIn(0f, 1f)
            return t * t * (3f - 2f * t)
        }
        val base = Offset(w * .5f, h * .82f)
        drawOval(Color(0xFF436C46).copy(alpha = .18f), Offset(w * .12f, h * .78f), Size(w * .76f, h * .15f))
        drawOval(Color(0xFF90613E), Offset(w * .14f, h * .79f), Size(w * .72f, h * .11f))
        drawOval(Color(0xFFBF9569), Offset(w * .19f, h * .80f), Size(w * .62f, h * .075f))
        val shoot = ease(.02f, .74f)
        val wood = ease(.24f, 1f)
        val height = h * (.025f + .60f * shoot)
        // The seed remains rooted while the shoot straightens gradually.
        val bend = w * .13f * sin(progress * 3.14159f) * (1f - wood * .55f)
        fun trunk(t: Float): Offset = Offset(
            base.x + bend * sin(t * 3.14159f) + wind * w * .012f * shoot * t * t,
            base.y - height * t
        )
        val trunkPath = Path().apply {
            moveTo(base.x, base.y)
            for (i in 1..60) {
                val p = trunk(i / 60f)
                lineTo(p.x, p.y)
            }
        }
        val bark = Color(0xFF7C5338)
        val stem = androidx.compose.ui.graphics.lerp(Color(0xFF74A845), bark, wood)
        val thickness = w * (.009f + .039f * wood)
        drawPath(trunkPath, Color(0xFF4F4930), style = Stroke(thickness * 1.2f, cap = StrokeCap.Round))
        drawPath(trunkPath, stem, style = Stroke(thickness, cap = StrokeCap.Round))
        drawPath(trunkPath, Color(0xFFD2AD72).copy(alpha = wood * .5f), style = Stroke(thickness * .22f, cap = StrokeCap.Round))
        val seed = 1f - ease(.12f, .57f)
        drawOval(Color(0xFF754A2F), Offset(base.x - w * .044f * seed, base.y - h * .037f * seed), Size(w * .088f * seed, h * .067f * seed))
        // Stable branch topology. Length, angle and foliage unfold with smooth curves.
        for (index in 0..11) {
            val side = if (index % 2 == 0) -1f else 1f
            val level = index / 2
            val birth = .12f + level * .065f
            val unfolding = ease(birth, birth + .28f)
            val attachment = trunk(.34f + level * .105f)
            val length = w * (.27f - level * .026f) * unfolding
            val lift = h * (.04f + .10f * unfolding) * unfolding +
                wind * side * h * .007f * unfolding * (1f + level * .1f)
            val tip = Offset(attachment.x + side * length, attachment.y - lift)
            val branch = Path().apply {
                moveTo(attachment.x, attachment.y)
                cubicTo(attachment.x + side * length * .42f, attachment.y - lift * .1f,
                    tip.x - side * length * .18f, tip.y + lift * .26f, tip.x, tip.y)
            }
            drawPath(branch, stem, style = Stroke((thickness * (.48f - level * .045f)).coerceAtLeast(.1f), cap = StrokeCap.Round))
            for (leafIndex in 0..4) {
                val leafGrowth = ease(birth + leafIndex * .025f, birth + .25f + leafIndex * .025f)
                val radius = w * (.027f + .012f * wood) * leafGrowth
                val fraction = .38f + leafIndex * .155f
                val t = fraction
                val u = 1f - t
                val controlOne = Offset(attachment.x + side * length * .42f, attachment.y - lift * .1f)
                val controlTwo = Offset(tip.x - side * length * .18f, tip.y + lift * .26f)
                val center = attachment * (u*u*u) + controlOne * (3f*u*u*t) +
                    controlTwo * (3f*u*t*t) + tip * (t*t*t)
                val leaf = Path().apply {
                    moveTo(center.x, center.y)
                    cubicTo(center.x + side * radius * .5f, center.y - radius * 1.45f,
                        center.x + side * radius * 1.8f, center.y - radius * 1.3f,
                        center.x + side * radius * 1.65f, center.y - radius * .15f)
                    cubicTo(center.x + side * radius, center.y + radius * .3f,
                        center.x + side * radius * .3f, center.y + radius * .3f, center.x, center.y)
                }
                val foliage = if (speciesId == "cherry") Color(0xFF6E9B59) else Color(0xFF4F8958)
                drawPath(leaf, foliage)
                drawPath(leaf, Color(0xFF365F3C).copy(alpha = .45f), style = Stroke(w * .002f))
                drawLine(Color(0xFFB4CF83), center, Offset(center.x + side * radius * 1.3f, center.y - radius * .45f), w * .002f)
                val bloom = ease(.68f + leafIndex * .025f, .92f + leafIndex * .015f)
                if (speciesId == "cherry" || decoration == "FLOWERS") {
                    val flower = Offset(center.x + side * radius, center.y - radius * .5f)
                    val r = w * .018f * bloom
                    for (petal in 0..4) {
                        val a = petal * 1.256637f
                        drawCircle(Color(0xFFF2B4BB), r * .7f, flower + Offset(cos(a) * r, sin(a) * r))
                    }
                    drawCircle(Color(0xFFFFE4A0), r * .42f, flower)
                }
            }
        }
        if (decoration == "LANTERNS") {
            val tip = trunk(.6f)
            val r = w * .034f * wood
            drawLine(bark, tip, tip + Offset(w * .14f * wood, h * .08f * wood), w * .003f)
            drawOval(Color(0xFFECA653), tip + Offset(w * .12f * wood, h * .07f * wood), Size(r, r * 1.3f))
        }
        if (decoration == "RIBBON") {
            val center = trunk(.25f)
            val r = w * .055f * wood
            val ribbon = Path().apply {
                moveTo(center.x, center.y); lineTo(center.x-r, center.y-r*.6f)
                lineTo(center.x-r, center.y+r*.6f); lineTo(center.x+r, center.y-r*.6f)
                lineTo(center.x+r, center.y+r*.6f); close()
            }
            drawPath(ribbon, Color(0xFFD88895))
        }
    }
}

@Preview(name = "Continuous rig · seed", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeSeedRigPreview() { KawaiiPlantIllustration(0) }

@Preview(name = "Continuous rig · unfolding shoot", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeShootRigPreview() { KawaiiPlantIllustration(32, "cherry") }

@Preview(name = "Continuous rig · young tree", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeYoungRigPreview() { KawaiiPlantIllustration(72, "cherry") }

@Preview(name = "Continuous rig · flowering tree", widthDp = 320, heightDp = 360, showBackground = true)
@Composable
fun TreeMatureRigPreview() { KawaiiPlantIllustration(120, "cherry") }
