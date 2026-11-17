package com.mamxanh.app.presentation.bio
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(name="audit-task-days-error",widthDp=375,heightDp=812,fontScale=1f,device="spec:width=375dp,height=812dp,dpi=160")
@Composable fun BioAuditTaskDaysErrorPreview(){BioReferenceSurface("task-new",referenceFields=mapOf("title" to "Đọc sách","frequency" to "CUSTOM","task-error" to "days"))}
