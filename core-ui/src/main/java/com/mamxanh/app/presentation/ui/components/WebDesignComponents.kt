package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*

@Composable
fun GardenScreenIntro(title:String,eyebrow:String="",subtitle:String="",modifier:Modifier=Modifier){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)){
        if(eyebrow.isNotBlank())Text(eyebrow,color=Muted,fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp15,letterSpacing=AppTypography.Sp2)
        Text(title,color=Deep,fontSize=AppTypography.Sp26,lineHeight=AppTypography.Sp34,fontWeight=FontWeight.Bold)
        if(subtitle.isNotBlank())Text(subtitle,color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
    }
}

@Composable
fun GardenNavigationCard(title:String,onClick:()->Unit,subtitle:String="",modifier:Modifier=Modifier,art:GardenArt=GardenArt.SPROUT){
    GardenBotanicalFrame(modifier.fillMaxWidth().clickable(onClick=onClick),contentPadding=PaddingValues(AppDimensions.Dp16),cornerSize=AppDimensions.Dp18){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            Box(Modifier.size(AppDimensions.Dp40).background(Pale,RoundedCornerShape(AppDimensions.Dp12)),contentAlignment=Alignment.Center){GardenArtwork(art,Modifier.size(AppDimensions.Dp24))}
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
                Text(title,color=Deep,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.SemiBold)
                if(subtitle.isNotBlank())Text(subtitle,color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
            }
            Text("›",color=Green,fontSize=AppTypography.Sp24)
        }
    }
}

@Composable
fun GardenExpandableSection(title:String,modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){
    var expanded by rememberSaveable(title){mutableStateOf(false)}
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        androidx.compose.material3.HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
        Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).semantics{stateDescription=if(expanded)AppText.text(TextKey.CopyDaMo617b34) else AppText.text(TextKey.CopyDaThuGon9b8c90)}.clickable(role=Role.Button){expanded=!expanded},verticalAlignment=Alignment.CenterVertically){
            Text(title,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp14,fontWeight=FontWeight.SemiBold)
            Text(if(expanded)"−" else "+",color=Green,fontSize=AppTypography.Sp22)
        }
        if(expanded)Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12),content=content)
    }
}
