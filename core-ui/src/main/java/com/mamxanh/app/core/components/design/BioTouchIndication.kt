package com.mamxanh.app.core.components.design

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DrawModifierNode
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppInteraction
import kotlinx.coroutines.launch

/** Touch feedback without a ripple/state-layer rectangle behind rounded controls. */
data class BioTouchIndication(
    val focusColor: Color,
    // Initial states let real Compose previews inspect the same drawing code deterministically.
    val initiallyPressed: Boolean = false,
    val initiallyFocused: Boolean = false,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): Modifier.Node =
        TouchNode(interactionSource, focusColor, initiallyPressed, initiallyFocused)
}

private class TouchNode(
    private val source: InteractionSource,
    private val focusColor: Color,
    private val initiallyPressed: Boolean,
    private val initiallyFocused: Boolean,
) : Modifier.Node(),
    DrawModifierNode {
    private val presses = mutableSetOf<PressInteraction.Press>()
    private val focuses = mutableSetOf<FocusInteraction.Focus>()
    private var pressed by mutableStateOf(initiallyPressed)
    private var focused by mutableStateOf(initiallyFocused)

    override fun onAttach() {
        coroutineScope.launch {
            source.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> presses.add(interaction)
                    is PressInteraction.Release -> presses.remove(interaction.press)
                    is PressInteraction.Cancel -> presses.remove(interaction.press)
                    is FocusInteraction.Focus -> focuses.add(interaction)
                    is FocusInteraction.Unfocus -> focuses.remove(interaction.focus)
                }
                pressed = presses.isNotEmpty()
                focused = focuses.isNotEmpty()
            }
        }
    }

    override fun onDetach() {
        presses.clear()
        focuses.clear()
        pressed = initiallyPressed
        focused = initiallyFocused
    }

    override fun ContentDrawScope.draw() {
        val factor = if (pressed) AppInteraction.PRESSED_SCALE else AppInteraction.RESTING_SCALE
        scale(factor, factor) { this@draw.drawContent() }
        if (focused) {
            val stroke = AppDimensions.Dp2.toPx()
            val inset = AppDimensions.Dp12.toPx().coerceAtMost(size.width / 2)
            val y = (size.height - AppDimensions.Dp3.toPx()).coerceAtLeast(0f)
            drawLine(
                color = focusColor,
                start = Offset(inset, y),
                end = Offset(size.width - inset, y),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}
