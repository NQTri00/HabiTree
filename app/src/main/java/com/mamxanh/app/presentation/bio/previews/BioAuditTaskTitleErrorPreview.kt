package com.mamxanh.app.presentation.bio
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(name="audit-task-title-error",widthDp=375,heightDp=812,fontScale=1f,device="spec:width=375dp,height=812dp,dpi=160")
@Composable fun BioAuditTaskTitleErrorPreview(){BioReferenceSurface("task-new",referenceFields=mapOf("task-error" to "title"))}
