package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import com.mamxanh.app.presentation.ui.components.MintBackdrop
import com.mamxanh.shared.model.*

private fun previewEngagement()=EngagementUiState(loading=false,snapshot=EngagementSnapshot(
    focus=listOf(FocusSession("preview",0,"Đọc một điều bạn yêu thích","2026-11-12",1500,300,"PAUSED")),
    review=WeeklyReview("2026-11-09",15,10,null,45,40,0,3,0,"Học tập","Nếp sống",""),
    days=(1..12).map{ReviewDay("2026-11-${it.toString().padStart(2,'0')}",if(it%3==0)"REST" else if(it%2==0)"SUCCESS" else "MISSED")},
    tomorrow="2026-11-13",focusCompleted=2))
@Composable
private fun FullDocument(body:@Composable ColumnScope.()->Unit) {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=12.sp,lineHeight=16.sp)) {
        MintBackdrop {
            Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=body)
        }
        }
    }
}
@Preview(name="Phase3 templates · complete document",widthDp=360,heightDp=1200,device="spec:width=360dp,height=1200dp,dpi=160")
@Composable fun PhaseThreeTemplatesDocumentPreview(){FullDocument{TemplateContent(previewEngagement(),{})}}
@Preview(name="Phase3 weekly review · complete document",widthDp=360,heightDp=1000,device="spec:width=360dp,height=1000dp,dpi=160")
@Composable fun PhaseThreeReviewDocumentPreview(){FullDocument{ReviewContent(previewEngagement(),{})}}
@Preview(name="Phase3 focus · narrow large text",widthDp=320,heightDp=500,fontScale=1.3f,device="spec:width=320dp,height=500dp,dpi=160")
@Composable fun PhaseThreeFocusDocumentPreview(){FullDocument{FocusContent(previewEngagement(),{},{_,_->})}}
@Preview(name="Phase3 settings · complete document",widthDp=360,heightDp=720,device="spec:width=360dp,height=720dp,dpi=160")
@Composable fun PhaseThreeSettingsDocumentPreview(){FullDocument{SettingsContent(previewEngagement(),{},{})}}
