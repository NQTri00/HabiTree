package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun TemplateContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(listOf<String>()) }
    val key =
        rememberSaveable {
            java.util.UUID
                .randomUUID()
                .toString()
        }
    GardenScreenIntro(AppText.text(TextKey.CopyChonViecVuaSuc895fff))
    Text(
        AppText.text(TextKey.CopyThemMotLanChinhSuaBatCuLucNao55b241),
        color = Muted,
        fontSize = AppTypography.Sp11,
        lineHeight = AppTypography.Sp16,
    )
    habitTemplates.forEach { template ->
        val added = template.id in state.snapshot.templates
        TemplateCard(template, template.id in selected, added) {
            selected = if (template.id in selected)selected - template.id else selected + template.id
        }
    }
    GardenPaintedButton(AppText.text(TextKey.CopyThem0ViecD0390c, selected.size), {
        onAct(EngagementAction.Templates(selected, key))
    }, Modifier.fillMaxWidth(), enabled = selected.isNotEmpty() && !state.busy)
}

@Composable
private fun TemplateCard(
    t: HabitTemplate,
    selected: Boolean,
    added: Boolean = false,
    onClick: () -> Unit,
) {
    GardenBotanicalFrame(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp48).clickable(enabled = !added, onClick = onClick),
        skin = if (selected)GardenPanelSkin.HINT else GardenPanelSkin.BADGE,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16), verticalAlignment = Alignment.CenterVertically) {
            GardenArtwork(if (t.config.verification == "TIMER")GardenArt.LANTERN else GardenArt.SPROUT, Modifier.size(AppDimensions.Dp24))
            Column(Modifier.weight(1f)) {
                Text(
                    t.config.title,
                    color = Deep,
                    fontSize = AppTypography.Sp15,
                    lineHeight = AppTypography.Sp21,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "${AppText.category(
                        t.config.category,
                    )} · ${if (t.config.verification == "TIMER") {
                        AppText.text(
                            TextKey.CopyBoDemC8def0,
                        )
                    } else {
                        AppText.text(TextKey.CopyCham36a8bd)
                    }} · ${t.effort}",
                    color = Muted,
                    fontSize = AppTypography.Sp12,
                    lineHeight = AppTypography.Sp17,
                )
                Text(
                    AppText.text(
                        TextKey.Copy01Xu8a2ae4,
                        if (t.config.frequency ==
                            "WEEKENDS"
                        ) {
                            AppText.text(TextKey.CopyCuoiTuanA0fb27)
                        } else {
                            AppText.text(TextKey.CopyMoiNgay8270cb)
                        },
                        t.config.reward,
                    ),
                    color = Green,
                    fontSize = AppTypography.Sp12,
                    lineHeight = AppTypography.Sp17,
                )
                if (t.config.startMinute >=
                    0
                ) {
                    Text(
                        AppText.text(TextKey.CopyKhungGio01C4abe6, clockLabel(t.config.startMinute), clockLabel(t.config.endMinute)),
                        color = Muted,
                        fontSize = AppTypography.Sp12,
                        lineHeight = AppTypography.Sp17,
                    )
                }
            }
            Checkbox(selected || added, onCheckedChange = null, enabled = !added)
        }
    }
}
