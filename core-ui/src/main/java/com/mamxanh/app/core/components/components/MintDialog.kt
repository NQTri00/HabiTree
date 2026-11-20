package com.mamxanh.app.core.components.components

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.core.components.*

@Composable
fun MintDialogAction(label: String,onClick:()->Unit,modifier:Modifier=Modifier,primary:Boolean=true,enabled:Boolean=true,destructive:Boolean=false) {
    val shape=RoundedCornerShape(AppDimensions.Dp15)
    val tint=if(destructive)AppColors.SwatchFFA94036 else Green
    Box(modifier.heightIn(min=AppDimensions.Dp48)
        .clickable(enabled=enabled,role=androidx.compose.ui.semantics.Role.Button,onClick=onClick),
        contentAlignment=Alignment.Center) {
        Surface(Modifier.fillMaxWidth(),shape=shape,
            color=if(primary) tint.copy(alpha=if(enabled)1f else .2f) else if(destructive)AppColors.SwatchFFF7E5E2 else AppColors.Transparent,
            border=if(primary||destructive)null else androidx.compose.foundation.BorderStroke(AppDimensions.Dp1,Deep.copy(alpha=if(enabled).25f else .1f))) {
            Text(label,Modifier.padding(horizontal=AppDimensions.Dp14,vertical=AppDimensions.Dp13),
                color=if(!enabled)Muted else if(primary)AppColors.White else if(destructive)tint else Deep,
                fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp20,fontWeight=FontWeight.SemiBold,
                textAlign=androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

/** One compact visual language for confirmations and forms, with reachable actions. */
@Composable
fun MintDialog(title:String,onDismiss:()->Unit,confirmLabel:String,onConfirm:()->Unit,
    cancelLabel:String=AppText.text(TextKey.CopyDeSau9be0a7),confirmEnabled:Boolean=true,dismissEnabled:Boolean=true,destructive:Boolean=false,
    content:@Composable ColumnScope.()->Unit) {
    val body: @Composable () -> Unit = {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(AppDimensions.Dp12),contentAlignment=Alignment.Center) {
            val short=maxHeight<AppDimensions.Dp180
            Surface(Modifier.widthIn(max=AppDimensions.Dp440).fillMaxWidth().heightIn(max=AppDimensions.Dp620),
                shape=RoundedCornerShape(AppDimensions.Dp28),color=Cream,shadowElevation=AppDimensions.Dp12) {
                Column(Modifier.padding(if(short)AppDimensions.Dp8 else AppDimensions.Dp20),verticalArrangement=Arrangement.spacedBy(if(short)AppDimensions.Dp8 else AppDimensions.Dp20)) {
                    if(!short) Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                        Text(title,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp21,lineHeight=AppTypography.Sp27,fontWeight=FontWeight.Bold)
                        IconButton(onClick=onDismiss,enabled=dismissEnabled,modifier=Modifier.size(AppDimensions.Dp48)) {
                            Text("×",color=Muted,fontSize=AppTypography.Sp22)
                        }
                    }
                    if(!short)HorizontalDivider(color=AppColors.SwatchFFE1EBE4)
                    Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                        if(short)Text(title,color=Deep,fontSize=AppTypography.Sp15,fontWeight=FontWeight.Bold)
                        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)) { content() }
                    }
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                        MintDialogAction(cancelLabel,onDismiss,Modifier.weight(1f).fillMaxHeight(),primary=false,enabled=dismissEnabled)
                        MintDialogAction(confirmLabel,onConfirm,Modifier.weight(1f).fillMaxHeight(),enabled=confirmEnabled,destructive=destructive)
                    }
                }
            }
        }
    }
    // Layoutlib does not include a separate dialog window in a Compose preview.
    // Render the exact same dialog surface in the preview's window.
    if(androidx.compose.ui.platform.LocalInspectionMode.current) body()
    else Dialog(onDismissRequest={if(dismissEnabled)onDismiss()},properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) { body() }
}
