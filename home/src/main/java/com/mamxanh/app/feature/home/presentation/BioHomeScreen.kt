package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class HomeScreenState(
    val date: String,
    val completed: Int,
    val required: Int,
    val earned: Int,
    val streak: Int,
)

enum class HomeScreenAction { PickSeed, ViewTasks, AddTask, BrowseSeeds, BrowseAccessories, Review }

@Composable
fun BioHomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit,
    tree: (@Composable () -> Unit)?,
    nextTask: (@Composable () -> Unit)?,
    reward: (@Composable () -> Unit)?,
) {
    val narrow = LocalConfiguration.current.screenWidthDp < 359
    val landscape=LocalConfiguration.current.screenHeightDp<500
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
        Column(Modifier.weight(1f)){if(!landscape){Bt(AppText.text(TextKey.CopyMotNhipXanh4e8208),size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7))};Bt(AppText.text(TextKey.CopyNgayMoiMamMoi236a07),size=if(narrow||landscape)22 else 25,line=if(narrow||landscape)28.6f else 32.5f,bold=true,spacing=AppTypography.TrackingMinus1)}
        Box(Modifier.padding(top=if(landscape)AppDimensions.Dp0 else AppDimensions.Dp19).background(AppColors.White.copy(alpha=AppOpacity.Value_25),RoundedCornerShape(AppDimensions.Dp8)).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)).padding(horizontal=AppDimensions.Dp10,vertical=AppDimensions.Dp9)){Bt(state.date,size=AppTypography.Size11,tint=bp.muted,mono=true,line=AppTypography.LineHeight14_3)}
    }
    if (tree != null) tree() else BioEmpty(AppText.text(TextKey.CopyBatDauBangMotMamXanhCd3b5c),AppText.text(TextKey.CopyMotHatNhoMotChutMoiNgayBcf756),action=AppText.text(TextKey.CopyGieoCayDauTienAfd3c1),onClick={onAction(HomeScreenAction.PickSeed)})
    BioSection(AppText.text(TextKey.CopyViecTiepTheoEa34c3),AppText.text(TextKey.CopyXemTatCa44aeb8)){onAction(HomeScreenAction.ViewTasks)}
    if (nextTask != null) nextTask() else
        BioLink(AppText.text(TextKey.CopyMotViecNhoHomNay3c953f), "plus", { onAction(HomeScreenAction.AddTask) })
    BioMetrics(listOf(
        AppText.text(TextKey.CopyViecHomNay22a0a4) to "${state.completed}/${state.required}",
        AppText.text(TextKey.CopyXuHomNay0c44ed) to "${state.earned}/50",
        AppText.text(TextKey.CopyNgayDeuDan31b8f2) to state.streak.toString(),
    ))
    reward?.invoke()
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        listOf(Triple("plus",AppText.text(TextKey.CopyThemViecC371c2),HomeScreenAction.AddTask),Triple("garden",AppText.text(TextKey.CopyGieoCay31adde),HomeScreenAction.BrowseSeeds),Triple("spark",AppText.text(TextKey.CopyPhuKien0852d7),HomeScreenAction.BrowseAccessories)).forEach{(icon,title,action)->Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp76).background(AppColors.White.copy(alpha=AppOpacity.Value_25),bioShape(AppRadii.Value14)).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value14)).clickable{onAction(action)}.padding(AppDimensions.Dp10),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){Bi(icon);Bt(title,size=AppTypography.Size11,tint=bp.deep,align=TextAlign.Center)}}
    }
    BioLink(AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),"calendar",{onAction(HomeScreenAction.Review)},AppText.text(TextKey.CopyNhungDieuNhoDaLonLen882792))
}
