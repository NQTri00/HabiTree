package com.mamxanh.app.presentation.ui.components

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
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(6.dp)){
        if(eyebrow.isNotBlank())Text(eyebrow,color=Muted,fontSize=10.sp,lineHeight=15.sp,letterSpacing=2.sp)
        Text(title,color=Deep,fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold)
        if(subtitle.isNotBlank())Text(subtitle,color=Muted,fontSize=12.sp,lineHeight=18.sp)
    }
}

@Composable
fun GardenNavigationCard(title:String,onClick:()->Unit,subtitle:String="",modifier:Modifier=Modifier,art:GardenArt=GardenArt.SPROUT){
    GardenBotanicalFrame(modifier.fillMaxWidth().clickable(onClick=onClick),contentPadding=PaddingValues(16.dp),cornerSize=18.dp){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
            Box(Modifier.size(40.dp).background(Pale,RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){GardenArtwork(art,Modifier.size(24.dp))}
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)){
                Text(title,color=Deep,fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.SemiBold)
                if(subtitle.isNotBlank())Text(subtitle,color=Muted,fontSize=12.sp,lineHeight=18.sp)
            }
            Text("›",color=Green,fontSize=24.sp)
        }
    }
}

@Composable
fun GardenExpandableSection(title:String,modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){
    var expanded by rememberSaveable(title){mutableStateOf(false)}
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(12.dp)){
        androidx.compose.material3.HorizontalDivider(color=androidx.compose.ui.graphics.Color(0xFFE4E7DC))
        Row(Modifier.fillMaxWidth().heightIn(min=48.dp).semantics{stateDescription=if(expanded)"Đã mở" else "Đã thu gọn"}.clickable(role=Role.Button){expanded=!expanded},verticalAlignment=Alignment.CenterVertically){
            Text(title,Modifier.weight(1f),color=Deep,fontSize=14.sp,fontWeight=FontWeight.SemiBold)
            Text(if(expanded)"−" else "+",color=Green,fontSize=22.sp)
        }
        if(expanded)Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)
    }
}
