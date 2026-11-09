package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

private val sourceIcons = linkedMapOf(
    "🗓️" to GardenArt.CALENDAR, "🗓" to GardenArt.CALENDAR, "☀️" to GardenArt.CALENDAR,
    "📅" to GardenArt.CALENDAR, "📚" to GardenArt.CATEGORY, "📖" to GardenArt.JOURNAL,
    "✏️" to GardenArt.PENCIL, "🪙" to GardenArt.COIN, "⭐" to GardenArt.COIN,
    "🌱" to GardenArt.SPROUT, "🌿" to GardenArt.SPROUT, "🌰" to GardenArt.NUT,
    "💧" to GardenArt.DROP, "🌸" to GardenArt.FLOWERS, "🪴" to GardenArt.SPROUT,
    "🌲" to GardenArt.TREE, "🌳" to GardenArt.TREE, "🧺" to GardenArt.SEED,
    "🥾" to GardenArt.BOOTS, "🥛" to GardenArt.GLASS, "☑" to GardenArt.CHECKED_TASK
)

/** Live text with reference-art inline icons instead of platform emoji artwork. */
@Composable
fun GardenRichText(
    text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified, fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null, textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified, maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip, style: TextStyle = LocalTextStyle.current
) {
    val annotated = remember(text) {
        buildAnnotatedString {
            var index = 0
            while (index < text.length) {
                val match = sourceIcons.keys.firstOrNull { text.startsWith(it, index) }
                if (match != null) {
                    appendInlineContent(match, match)
                    index += match.length
                } else {
                    append(text[index])
                    index++
                }
            }
        }
    }
    val inline = remember {
        sourceIcons.mapValues { (_, art) ->
            InlineTextContent(Placeholder(1.1.em, 1.1.em, PlaceholderVerticalAlign.Center)) {
                GardenArtwork(art, Modifier.fillMaxSize())
            }
        }
    }
    Text(annotated, modifier, color = color, fontSize = fontSize, fontStyle = fontStyle,
        fontWeight = fontWeight, textAlign = textAlign, lineHeight = lineHeight,
        maxLines = maxLines, overflow = overflow, style = style, inlineContent = inline)
}

@Preview(name = "Live text with source icons")
@Composable
fun GardenRichTextPreview() { GardenRichText("💧 500 ml  ·  🪙 120 xu") }
