package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tree.presentation.components.DecorationPicker
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable
internal fun TreeDetailTabs(selected: Int, onSelect: (Int)->Unit) {
    Row(Modifier.fillMaxWidth().selectableGroup()
        .background(Color(0xFFEAF3ED),RoundedCornerShape(12.dp)).padding(3.dp),
        horizontalArrangement=Arrangement.spacedBy(3.dp)) {
        listOf("Chăm sóc","Trang trí","Thông tin").forEachIndexed { index,label ->
            Box(Modifier.weight(1f).heightIn(min=48.dp)
                .selectable(selected==index,role=Role.Tab,onClick={onSelect(index)}),
                contentAlignment=Alignment.Center) {
                Surface(Modifier.fillMaxWidth(),shape=RoundedCornerShape(9.dp),
                    color=if(selected==index)Color.White else Color.Transparent,
                    shadowElevation=if(selected==index)2.dp else 0.dp) {
                    Column(Modifier.padding(vertical=6.dp),horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        Text(label,color=if(selected==index)Green else Muted,fontSize=11.sp,lineHeight=14.sp,
                            fontWeight=if(selected==index)FontWeight.Bold else FontWeight.Medium,textAlign=TextAlign.Center)
                        Box(Modifier.width(16.dp).height(2.dp)
                            .background(if(selected==index)Green else Color.Transparent,RoundedCornerShape(50)))
                    }
                }
            }
        }
    }
}

/** Content has natural height: runtime scrolls it; previews show every row. */
@Composable
internal fun TreeDetailSectionContent(tree: Tree, selected: Int, inventory: Map<String,Int>,
    busy: Boolean, onIntent: (TreeIntent)->Unit, onOpenDecorations:()->Unit={}) {
    val status=tree.status
    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        when(selected) {
            0 -> {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    GardenArtwork(GardenArt.WATERING_CAN,Modifier.size(24.dp))
                    Column(Modifier.weight(1f)) {
                        Text(status.waterLabel,color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.SemiBold)
                        Text(status.cue.label,color=Muted,fontSize=11.sp,lineHeight=14.sp)
                    }
                    MintStatusPill(status.stateLabel)
                }
                if(status.fertilizerLabel.isNotBlank())MintStatusPill(status.fertilizerLabel)
                if(status.state==TreeGrowthState.GROWING) {
                    HorizontalDivider(color=Color(0xFFE2ECE5))
                    Text("Dinh dưỡng",color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.Bold)
                    Text(if(!status.waterActive)"Tưới cây trước khi bón phân." else "Chọn một loại phân trong túi.",
                        color=Muted,fontSize=11.sp,lineHeight=15.sp)
                    Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        FertilizerTier.entries.forEach { tier ->
                            val count=inventory[tier.itemId] ?: 0
                            val available=status.canFertilize && count>0 && !busy
                            Surface(Modifier.fillMaxWidth().heightIn(min=48.dp)
                                .clickable(enabled=available,role=Role.Button,onClick={
                                    onIntent(TreeIntent.Fertilize(tree.id,tier.itemId))
                                }),shape=RoundedCornerShape(10.dp),color=Color(0xFFF0F6F2),
                                border=BorderStroke(1.dp,Color(0xFFE2ECE5))) {
                                Row(Modifier.padding(horizontal=8.dp,vertical=5.dp),verticalAlignment=Alignment.CenterVertically,
                                    horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                    GardenArtwork(GardenArt.FERTILIZER,Modifier.size(24.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(tier.label,color=Deep,fontSize=11.sp,lineHeight=14.sp,fontWeight=FontWeight.SemiBold)
                                        Text("Trong túi: $count",color=Muted,fontSize=10.sp,lineHeight=13.sp)
                                    }
                                    Text("${tier.rate/1000.0}×",color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold)
                                    Surface(shape=RoundedCornerShape(8.dp),color=if(available)Green else Color(0xFFE1EBE5)) {
                                        Text(if(available)"Bón" else if(count==0)"Hết" else "Chờ",
                                            Modifier.padding(horizontal=8.dp,vertical=4.dp),
                                            color=if(available)Color.White else Muted,fontSize=10.sp,lineHeight=13.sp)
                                    }
                                }
                            }
                        }
                    }
                    TextButton(onClick={onIntent(TreeIntent.OpenFertilizerShop)},enabled=!busy,
                        contentPadding=PaddingValues(horizontal=4.dp)) {
                        Text("Bổ sung vật phẩm  ›",color=Green,fontSize=11.sp,lineHeight=14.sp)
                    }
                } else Text(if(status.alive)"Cây đã trưởng thành. Tiếp tục tưới để giữ cây khỏe mạnh."
                    else "Cây đã khô héo và không thể hồi phục.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
            }
            1 -> {
                Text("Điểm tô cho cây",color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.Bold)
                Text("Phụ kiện thuộc túi đồ; di chuyển hoặc cất lại không mất xu.",color=Muted,fontSize=11.sp,lineHeight=15.sp)
                if(status.alive)GardenPaintedButton("Sắp xếp phụ kiện",onOpenDecorations,Modifier.fillMaxWidth(),primary=false,enabled=!busy)
                else Text("Không thể trang trí cây đã khô héo.",color=Muted,fontSize=11.sp,lineHeight=15.sp)
            }
            else -> {
                Text("Hồ sơ cây",color=Deep,fontSize=12.sp,lineHeight=16.sp,fontWeight=FontWeight.Bold)
                val species=catalog.first { it.id==tree.speciesId }
                Column(Modifier.fillMaxWidth().background(Color(0xFFF0F6F2),RoundedCornerShape(10.dp))
                    .padding(horizontal=10.dp)) {
                    listOf("Loài" to species.name,"Giai đoạn" to "${status.stageIndex+1} / ${status.stageCount}",
                        "Phát triển" to "${status.percentage}%","Ngày trồng" to tree.planted).forEachIndexed { index,(label,value) ->
                        if(index>0)HorizontalDivider(color=Color(0xFFE2ECE5))
                        Row(Modifier.fillMaxWidth().padding(vertical=8.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                            Text(label,Modifier.weight(.4f),color=Muted,fontSize=11.sp,lineHeight=15.sp)
                            Text(value,Modifier.weight(.6f),color=Deep,fontSize=11.sp,lineHeight=15.sp,
                                fontWeight=FontWeight.Medium,textAlign=TextAlign.End)
                        }
                    }
                }
                Text(species.description,color=Muted,fontSize=11.sp,lineHeight=16.sp)
            }
        }
        HorizontalDivider(color=Color(0xFFE2ECE5))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            if(status.state==TreeGrowthState.MATURE)TextButton(onClick={onIntent(TreeIntent.AskDisposition(tree.id,true))},
                enabled=!busy,contentPadding=PaddingValues(horizontal=4.dp)) {
                Text("Bán cây",color=Green,fontSize=11.sp,lineHeight=14.sp)
            } else Spacer(Modifier.weight(1f))
            TextButton(onClick={onIntent(TreeIntent.AskDisposition(tree.id,false))},enabled=!busy,
                contentPadding=PaddingValues(horizontal=4.dp)) {
                Text("Dọn cây",color=Muted,fontSize=11.sp,lineHeight=14.sp)
            }
        }
    }
}

private fun sectionPreviewTree()=Tree(1,"cherry","Đào hồng xuân",45,"2026-11-01","FLOWERS",
    status=TreeStatus(stageIndex=3,stageCount=9,stageProgress=.46f,wateredUntil=100,sampledAt=1,cue=CareCue.HEALTHY))

@Composable
private fun SectionDocument(selected: Int) {
    MamXanhPreviewTheme {
        Column(Modifier.fillMaxWidth().background(Color(0xFFF7FCF8)).padding(12.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)) {
            TreeDetailTabs(selected,{})
            TreeDetailSectionContent(sectionPreviewTree(),selected,mapOf("fertilizer" to 2,"fertilizer_rich" to 1),false,{})
        }
    }
}

@Preview(name="Care · complete content",widthDp=360)
@Composable fun TreeCareFullContentPreview(){SectionDocument(0)}
@Preview(name="Decoration · complete content",widthDp=360)
@Composable fun TreeDecorationFullContentPreview(){SectionDocument(1)}
@Preview(name="Information · complete content",widthDp=360)
@Composable fun TreeInformationFullContentPreview(){SectionDocument(2)}
@Preview(name="Care · narrow, large text, complete content",widthDp=320,fontScale=1.3f)
@Composable fun TreeCareNarrowContentPreview(){SectionDocument(0)}
