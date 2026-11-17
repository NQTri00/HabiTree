package com.mamxanh.app.feature.garden.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable internal fun GardenCaption(text:String,modifier:Modifier=Modifier) {
    Text(text,modifier,color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
}
@Composable internal fun GardenHeading(text:String,modifier:Modifier=Modifier) {
    Text(text,modifier,color=Deep,fontSize=AppTypography.Sp19,lineHeight=AppTypography.Sp25,fontWeight=FontWeight.Bold)
}
@Composable internal fun GardenPanel(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().mintDepth(RoundedCornerShape(AppDimensions.Dp22),MintDepth.card,AppDimensions.Dp6).padding(AppDimensions.Dp18),
        verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6),content=content)
}
/** Small visual control inside an accessible touch target; no default purple Material chips. */
@Composable internal fun GardenPill(label:String,selected:Boolean,onClick:()->Unit,modifier:Modifier=Modifier,
    enabled:Boolean=true,icon:ImageVector?=null) {
    Box(modifier.heightIn(min=AppDimensions.Dp48).alpha(if(enabled)AppOpacity.Value1 else AppOpacity.Value_45)
        .selectable(selected,enabled=enabled,role=Role.RadioButton,onClick=onClick),contentAlignment=Alignment.Center) {
        Row(Modifier.mintDepth(RoundedCornerShape(AppDimensions.Dp15),if(selected)MintDepth.primary else MintDepth.secondary,if(selected)AppDimensions.Dp2 else AppDimensions.Dp0)
            .padding(horizontal=AppDimensions.Dp14,vertical=AppDimensions.Dp12),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
            if(icon!=null)Icon(icon,null,Modifier.size(AppDimensions.Dp13),tint=if(selected)AppColors.White else Green)
            Text(label,color=if(selected)AppColors.White else Deep,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp15,fontWeight=FontWeight.SemiBold,
                maxLines=1,overflow=TextOverflow.Ellipsis)
        }
    }
}
@Composable internal fun GardenCompactAction(label:String,onClick:()->Unit,modifier:Modifier=Modifier,
    primary:Boolean=false,enabled:Boolean=true,icon:ImageVector?=null) {
    Box(modifier.heightIn(min=AppDimensions.Dp48).alpha(if(enabled)AppOpacity.Value1 else AppOpacity.Value_4)
        .clickable(enabled=enabled,role=Role.Button,onClick=onClick),contentAlignment=Alignment.Center) {
        Row(Modifier.mintDepth(RoundedCornerShape(AppDimensions.Dp15),if(primary)MintDepth.primary else MintDepth.secondary,if(primary)AppDimensions.Dp2 else AppDimensions.Dp0)
            .padding(horizontal=AppDimensions.Dp10,vertical=AppDimensions.Dp7),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)) {
            if(icon!=null)Icon(icon,null,Modifier.size(AppDimensions.Dp14),tint=if(primary)AppColors.White else Green)
            Text(label,color=if(primary)AppColors.White else Green,fontSize=AppTypography.Sp13,lineHeight=AppTypography.Sp19,fontWeight=FontWeight.SemiBold)
        }
    }
}
@Composable internal fun GardenFact(label:String,value:String,modifier:Modifier=Modifier) {
    Column(modifier,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp2)) {
        GardenCaption(label)
        Text(value,color=Deep,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp15,fontWeight=FontWeight.SemiBold)
    }
}
