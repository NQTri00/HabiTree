package com.mamxanh.app.feature.tasks.presentation.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.BioButton
import com.mamxanh.core.ui.R

@Composable
fun FocusDock(state: FocusDockState, onAction: (FocusDockAction) -> Unit) {
    if (state.hasSession) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            val toggleLabel = if (state.running) R.string.copy_tam_dung_676358 else R.string.copy_tiep_tuc_87a7ae
            BioButton(
                stringResource(toggleLabel),
                { onAction(FocusDockAction.Toggle) },
                Modifier.weight(1f),
                if (state.running) "pause" else "play",
                enabled = !state.busy,
            )
            BioButton(
                stringResource(R.string.copy_ket_thuc_351849),
                { onAction(FocusDockAction.End) },
                Modifier.weight(1f),
                secondary = true,
                enabled = !state.busy,
            )
        }
    } else {
        BioButton(
            stringResource(R.string.copy_bat_dau_b3cacc),
            { onAction(FocusDockAction.Start) },
            Modifier.fillMaxWidth(),
            "play",
            enabled = state.canStart && !state.busy,
        )
    }
}

@Preview(name = "Start a focus session", widthDp = 335, showBackground = true)
@Composable
private fun FocusDockPreview() {
    FocusDock(FocusDockState(hasSession = false, running = false, canStart = true, busy = false)) {}
}

@Preview(name = "Active focus session", widthDp = 335, showBackground = true)
@Composable
private fun FocusDockRunningPreview() {
    FocusDock(FocusDockState(hasSession = true, running = true, canStart = false, busy = false)) {}
}
