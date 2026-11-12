package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import com.mamxanh.shared.model.*

private fun fixture()=LivingGardenUiState(
    snapshot=LivingGardenSnapshot(version=3,placements=listOf(GardenPlacement("g","ground_moss",1),GardenPlacement("a","accent_lantern",1),GardenPlacement("b","border_wood",2)),
        sections=(1..4).map{GardenSection(it,((it-1)*6+1..it*6).toList(),it==1,5400)},
        species=catalog.map{SpeciesRecord(it.id,1_767_225_600_000L,if(it.id=="cherry")8 else 0,1,1_767_225_600_000L,if(it.id=="cherry")1_794_484_800_000L else 0,living=1,tallestStage=if(it.id=="cherry")8 else 0,flourished=it.id=="cherry")},
        goals=listOf(CollectionGoal("collector","Trồng 3 loài",3,3,"",1),CollectionGoal("mature3","Nuôi 3 loài trưởng thành",1,3,"reward_sign"),CollectionGoal("bloom","Cây hoa đầu tiên nở rộ",1,1,"reward_petals",1),CollectionGoal("expansion","Mở khu vườn mới",0,1,"reward_path"),CollectionGoal("section","Một khu với 6 loài khác nhau",3,6,"reward_plaque")),
        owned=mapOf("ground_moss" to 1,"accent_lantern" to 1,"border_wood" to 1),coins=620),
    garden=Garden(trees=listOf(Tree(1,"cherry","Đào hồng",120,"2026-01-01",status=TreeStatus(state=TreeGrowthState.MATURE,stageCount=9,stageIndex=8,stageProgress=1f)),
        Tree(2,"pine","Thông xanh",30,"2026-01-01"),Tree(3,"snake","Lưỡi hổ",10,"2026-01-01")),
        plots=(1..6).map{GardenPlot(it,it<6,if(it<=3)it.toLong() else null)}),
    route="STUDIO",loading=false,selected="cherry")
@Composable private fun Document(content:@Composable ColumnScope.()->Unit) {
    MamXanhPreviewTheme {ProvideTextStyle(TextStyle(fontSize=12.sp,lineHeight=17.sp)){
        Column(Modifier.fillMaxWidth().padding(10.dp),verticalArrangement=Arrangement.spacedBy(6.dp),content=content)
    }}
}
@Preview(name="Bách thảo · complete content",widthDp=360,heightDp=900,device="spec:width=360dp,height=900dp,dpi=160")
@Composable fun EncyclopediaDocumentPreview(){Document{Encyclopedia(fixture()){_,_->}}}
@Preview(name="Species gallery · complete content",widthDp=320,heightDp=780,fontScale=1.3f,device="spec:width=320dp,height=780dp,dpi=160")
@Composable fun SpeciesDocumentPreview(){Document{SpeciesDetail(fixture(),{}, {})}}
@Preview(name="Appearance · complete content",widthDp=360,heightDp=620,device="spec:width=360dp,height=620dp,dpi=160")
@Composable fun AppearanceDocumentPreview(){Document{AppearanceContent(fixture(),false,{}, {}, {})}}
@Preview(name="Garden Studio · complete section",widthDp=360,heightDp=640,device="spec:width=360dp,height=640dp,dpi=160")
@Composable fun GardenStudioDocumentPreview(){
    CompactScreenPreview(fixture())
}
@Composable private fun CompactScreenPreview(state:LivingGardenUiState) {
    MamXanhPreviewTheme {LivingGardenScreen(state,{_,_->},{},{},{_,_->},{},{},{},{},{},{},{},true,{},false)}
}
@Preview(name="Arrange · compact full section",widthDp=360,heightDp=640,device="spec:width=360dp,height=640dp,dpi=160")
@Composable fun GardenArrangeDocumentPreview(){val s=fixture();CompactScreenPreview(s.copy(route="ARRANGE",selected="ground_moss",draft=s.snapshot.placements))}
@Preview(name="Decorations · complete catalog",widthDp=360,heightDp=720,device="spec:width=360dp,height=720dp,dpi=160")
@Composable fun CosmeticCatalogDocumentPreview(){CompactScreenPreview(fixture().copy(route="SHOP"))}
@Preview(name="Decoration detail · complete content",widthDp=320,heightDp=440,fontScale=1.3f,device="spec:width=320dp,height=440dp,dpi=160")
@Composable fun CosmeticDetailDocumentPreview(){Document{CosmeticDetail(fixture().copy(selected="accent_lantern"),{_,_->},{})}}
@Preview(name="Expansion · complete confirmation",widthDp=320,heightDp=360,fontScale=1.3f,device="spec:width=320dp,height=360dp,dpi=160")
@Composable fun ExpansionDocumentPreview(){CompactScreenPreview(fixture().copy(route="EXPANSION",selected="2"))}
