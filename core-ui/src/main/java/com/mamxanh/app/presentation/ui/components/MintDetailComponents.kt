package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*

@Composable
fun MintStatusPill(label: String, modifier: Modifier = Modifier) {
    Surface(modifier,shape=RoundedCornerShape(50),color=AppColors.SwatchFFE4F2EA,contentColor=Green) {
        Text(label,Modifier.padding(horizontal=AppDimensions.Dp6,vertical=AppDimensions.Dp3),fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp13,fontWeight=FontWeight.Medium)
    }
}

@Composable
fun MintTreeStatus(stage: String,state: String,water: String) {
    FlowRow(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
        MintStatusPill(stage); MintStatusPill(state); MintStatusPill(water)
    }
}

@Composable
fun MintMetricStrip(metrics: List<Pair<String,String>>, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
        metrics.forEach { (label,value) ->
            GardenBotanicalFrame(Modifier.weight(1f).fillMaxHeight(), contentPadding=PaddingValues(AppDimensions.Dp12),cornerSize=AppDimensions.Dp18) {
                Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
                    Text(value,color=Deep,fontSize=AppTypography.Sp21,lineHeight=AppTypography.Sp26,fontWeight=FontWeight.Bold,textAlign=TextAlign.Center)
                    Text(label,color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16,textAlign=TextAlign.Center)
                }
            }
        }
    }
}

/** Fixed artwork and label slots keep the entire grid aligned at every font scale. */
@Composable
fun MintCatalogCard(name: String,value: String,onClick: (() -> Unit)?=null,detail:String="",artwork: @Composable (Modifier)->Unit) {
    val nameHeight=with(LocalDensity.current){AppTypography.Sp40.toDp()}
    GardenBotanicalFrame(Modifier.fillMaxWidth().then(if(onClick!=null)
        Modifier.clickable(role=Role.Button,onClickLabel=AppText.text(TextKey.CopyChon085cbcf,name),onClick=onClick) else Modifier),
        contentPadding=PaddingValues(AppDimensions.Dp12),cornerSize=AppDimensions.Dp20) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
            Box(Modifier.fillMaxWidth().height(AppDimensions.Dp100).background(AppColors.SwatchFFF0F4E8,RoundedCornerShape(AppDimensions.Dp14))){artwork(Modifier.fillMaxSize())}
            Box(Modifier.fillMaxWidth().height(nameHeight),contentAlignment=Alignment.CenterStart) {
                Text(name,color=Deep,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp15,fontWeight=FontWeight.Bold,
                    textAlign=TextAlign.Start,maxLines=2,overflow=TextOverflow.Ellipsis)
            }
            Text(value,Modifier.fillMaxWidth(),color=Green,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp15,fontWeight=FontWeight.Bold,maxLines=1,
                overflow=TextOverflow.Ellipsis,textAlign=TextAlign.Start)
            if(detail.isNotBlank())Text(detail,Modifier.fillMaxWidth(),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
        }
    }
}
