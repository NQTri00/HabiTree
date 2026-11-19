package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText

/** Native icon foreground/background resources under representative launcher masks. */
@Composable internal fun LauncherMarks(round:Boolean) {
    val context=LocalContext.current
    AppText.language=AppLanguage.fromTag(LocalConfiguration.current.locales[0].language)
    val world=BioWorld.reference(context)
    BioTheme(world,"1") {
        Column(Modifier.fillMaxSize().background(bp.bg).padding(AppDimensions.Dp20),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
            Bt("HabiTree",size=AppTypography.Size27,bold=true)
            Bt(if(round)"Circular launcher mask"else "Rounded-square launcher mask")
            world.species.chunked(3).forEach { row ->
                Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                    row.forEach { species ->
                        val id=species.s("id")
                        val palette=world.data.o("palettes").o(id)
                        val drawable=context.resources.getIdentifier("ic_habitree_foreground_$id","drawable",context.packageName)
                        Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                            Box(Modifier.size(AppDimensions.Dp88).clip(if(round)CircleShape else RoundedCornerShape(AppDimensions.Dp20)).background(color(palette.s("accent"))),contentAlignment=Alignment.Center) {
                                Image(painterResource(drawable),null,Modifier.fillMaxSize())
                            }
                            Bt(species.s("name"),size=AppTypography.Size11)
                        }
                    }
                    repeat(3-row.size){Spacer(Modifier.weight(1f))}
                }
            }
        }
    }
}
