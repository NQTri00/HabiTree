package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.core.components.design.*

@Composable
fun BioHomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit,
    tree: (@Composable () -> Unit)?,
    nextTask: (@Composable () -> Unit)?,
    reward: (@Composable () -> Unit)?,
) {
    val narrow = LocalConfiguration.current.screenWidthDp < AppThemeEffects.CompactWidth
    val landscape=LocalConfiguration.current.screenHeightDp<AppThemeEffects.CompactHeight
    HomeGreeting(state.date, narrow, landscape)
    val sectionSpacing = if (tree != null && !narrow && !landscape) AppDimensions.Dp10 else AppDimensions.Dp18
    Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
        if (tree != null) tree() else BioEmpty(stringResource(R.string.copy_bat_dau_bang_mot_mam_xanh_cd3b5c),stringResource(R.string.copy_mot_hat_nho_mot_chut_moi_ngay_bcf756),action=stringResource(R.string.copy_gieo_cay_dau_tien_afd3c1),onClick={onAction(HomeScreenAction.PickSeed)})
        BioSection(stringResource(R.string.copy_viec_tiep_theo_ea34c3),stringResource(R.string.copy_xem_tat_ca_44aeb8)){onAction(HomeScreenAction.ViewTasks)}
        if (nextTask != null) nextTask() else
            BioLink(stringResource(R.string.copy_mot_viec_nho_hom_nay_3c953f), "plus", { onAction(HomeScreenAction.AddTask) })
    }
    BioMetrics(listOf(
        stringResource(R.string.copy_viec_hom_nay_22a0a4) to "${state.completed}/${state.required}",
        stringResource(R.string.copy_xu_hom_nay_0c44ed) to "${state.earned}/50",
        stringResource(R.string.copy_ngay_deu_dan_31b8f2) to state.streak.toString(),
    ))
    reward?.invoke()
    HomeQuickActions(onAction)
    HomeReviewLink { onAction(HomeScreenAction.Review) }
}

@Composable
private fun HomeReviewLink(onClick: () -> Unit) {
    BioLink(
        stringResource(R.string.copy_nhin_lai_tuan_ff3f0c),
        "calendar",
        onClick,
        stringResource(R.string.copy_nhung_dieu_nho_da_lon_len_882792),
    )
}

@Composable
fun HomeReward(claimed: Boolean, canClaim: Boolean, onClaim: () -> Unit) {
    BioCard(Modifier.fillMaxWidth(), AppColors.RewardSurface, padding = AppSpacing.Value14, radius = AppRadii.Value18) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            Bi("gift", tint = bp.ink)
            Column(Modifier.weight(1f)) {
                Bt(stringResource(R.string.copy_thuong_hom_nay_c015c6), size = AppTypography.Size12, semi = true)
                Bt(stringResource(R.string.copy_8_xu_3a7892), size = AppTypography.Size11)
            }
            val label = if (claimed) R.string.copy_da_nhan_1d62ad else R.string.copy_nhan_8518f2
            BioButton(stringResource(label), onClaim, enabled = canClaim && !claimed)
        }
    }
}

@Composable
private fun HomeGreeting(date: String, narrow: Boolean, landscape: Boolean) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
        Column(Modifier.weight(1f)){if(!landscape){Bt(stringResource(R.string.copy_mot_nhip_xanh_4e8208),size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7))};Bt(stringResource(R.string.copy_ngay_moi_mam_moi_236a07),size=if(narrow||landscape)AppTypography.Size22 else AppTypography.HomeGreetingSize,line=if(narrow||landscape)AppTypography.HomeGreetingCompactLineHeight else AppTypography.HomeGreetingLineHeight,bold=true,spacing=AppTypography.TrackingMinus1)}
        Box(Modifier.padding(top=if(landscape)AppDimensions.Dp0 else AppDimensions.Dp19).background(AppColors.White.copy(alpha=AppOpacity.Value_25),RoundedCornerShape(AppDimensions.Dp8)).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)).padding(horizontal=AppDimensions.Dp10,vertical=AppDimensions.Dp9)){Bt(date,size=AppTypography.Size11,tint=bp.muted,mono=true,line=AppTypography.LineHeight14_3)}
    }
}

@Composable
private fun HomeQuickActions(onAction: (HomeScreenAction) -> Unit) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        listOf(Triple("plus",stringResource(R.string.copy_them_viec_c371c2),HomeScreenAction.AddTask),Triple("garden",stringResource(R.string.copy_gieo_cay_31adde),HomeScreenAction.BrowseSeeds),Triple("spark",stringResource(R.string.copy_phu_kien_0852d7),HomeScreenAction.BrowseAccessories)).forEach{(icon,title,action)->Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp76).background(AppColors.White.copy(alpha=AppOpacity.Value_25),bioShape(AppRadii.Value14)).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value14)).clickable{onAction(action)}.padding(AppDimensions.Dp10),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){Bi(icon);Bt(title,size=AppTypography.Size11,tint=bp.deep,align=TextAlign.Center)}}
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioHomeScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioHomeScreen(HomeScreenState("17-11", 0, 4, 0, 0), {}, null, null, null)
    }
}

@Preview(name = "Home greeting", widthDp = 335, showBackground = true)
@Composable
private fun HomeGreetingPreview() {
    HomeGreeting("17-11", narrow = false, landscape = false)
}

@Preview(name = "Home shortcuts", widthDp = 335, showBackground = true)
@Composable
private fun HomeQuickActionsPreview() {
    HomeQuickActions {}
}

@Preview(name = "Home reward", widthDp = 335, showBackground = true)
@Composable
private fun HomeRewardPreview() {
    HomeReward(claimed = false, canClaim = true) {}
}

@Preview(name = "Home weekly review link", widthDp = 335, showBackground = true)
@Composable
private fun HomeReviewLinkPreview() {
    HomeReviewLink {}
}
