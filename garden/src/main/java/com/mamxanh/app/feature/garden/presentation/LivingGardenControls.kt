package com.mamxanh.app.feature.garden.presentation

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
    Text(text,modifier,color=Muted,fontSize=10.sp,lineHeight=14.sp)
}
@Composable internal fun GardenHeading(text:String,modifier:Modifier=Modifier) {
    Text(text,modifier,color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.Bold)
}
@Composable internal fun GardenPanel(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().mintDepth(RoundedCornerShape(14.dp),MintDepth.card,2.dp).padding(10.dp),
        verticalArrangement=Arrangement.spacedBy(6.dp),content=content)
}
/** Small visual control inside an accessible touch target; no default purple Material chips. */
@Composable internal fun GardenPill(label:String,selected:Boolean,onClick:()->Unit,modifier:Modifier=Modifier,
    enabled:Boolean=true,icon:ImageVector?=null) {
    Box(modifier.heightIn(min=48.dp).alpha(if(enabled)1f else .45f)
        .selectable(selected,enabled=enabled,role=Role.RadioButton,onClick=onClick),contentAlignment=Alignment.Center) {
        Row(Modifier.mintDepth(RoundedCornerShape(10.dp),if(selected)MintDepth.primary else MintDepth.secondary,if(selected)2.dp else 0.dp)
            .padding(horizontal=9.dp,vertical=6.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)) {
            if(icon!=null)Icon(icon,null,Modifier.size(13.dp),tint=if(selected)Color.White else Green)
            Text(label,color=if(selected)Color.White else Deep,fontSize=10.sp,lineHeight=13.sp,fontWeight=FontWeight.SemiBold,
                maxLines=1,overflow=TextOverflow.Ellipsis)
        }
    }
}
@Composable internal fun GardenCompactAction(label:String,onClick:()->Unit,modifier:Modifier=Modifier,
    primary:Boolean=false,enabled:Boolean=true,icon:ImageVector?=null) {
    Box(modifier.heightIn(min=48.dp).alpha(if(enabled)1f else .4f)
        .clickable(enabled=enabled,role=Role.Button,onClick=onClick),contentAlignment=Alignment.Center) {
        Row(Modifier.mintDepth(RoundedCornerShape(10.dp),if(primary)MintDepth.primary else MintDepth.secondary,if(primary)2.dp else 0.dp)
            .padding(horizontal=10.dp,vertical=7.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            if(icon!=null)Icon(icon,null,Modifier.size(14.dp),tint=if(primary)Color.White else Green)
            Text(label,color=if(primary)Color.White else Green,fontSize=11.sp,lineHeight=14.sp,fontWeight=FontWeight.SemiBold)
        }
    }
}
@Composable internal fun GardenFact(label:String,value:String,modifier:Modifier=Modifier) {
    Column(modifier,verticalArrangement=Arrangement.spacedBy(2.dp)) {
        GardenCaption(label)
        Text(value,color=Deep,fontSize=11.sp,lineHeight=15.sp,fontWeight=FontWeight.SemiBold)
    }
}
