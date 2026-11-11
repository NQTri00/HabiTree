package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
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
import com.mamxanh.app.presentation.ui.*

@Composable
fun MintDialogAction(label: String,onClick:()->Unit,modifier:Modifier=Modifier,primary:Boolean=true,enabled:Boolean=true,destructive:Boolean=false) {
    val shape=RoundedCornerShape(14.dp)
    val tint=if(destructive)Color(0xFFA94036) else Green
    if(primary) Button(onClick,modifier.heightIn(min=48.dp),enabled=enabled,shape=shape,
        contentPadding=PaddingValues(horizontal=12.dp,vertical=9.dp),colors=ButtonDefaults.buttonColors(containerColor=tint)) {
        Text(label,fontSize=13.sp,lineHeight=17.sp,fontWeight=FontWeight.SemiBold)
    } else OutlinedButton(onClick,modifier.heightIn(min=48.dp),enabled=enabled,shape=shape,
        contentPadding=PaddingValues(horizontal=12.dp,vertical=9.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=Deep)) {
        Text(label,fontSize=13.sp,lineHeight=17.sp)
    }
}

/** One compact visual language for confirmations and forms, with reachable actions. */
@Composable
fun MintDialog(title:String,onDismiss:()->Unit,confirmLabel:String,onConfirm:()->Unit,
    cancelLabel:String="Để sau",confirmEnabled:Boolean=true,dismissEnabled:Boolean=true,destructive:Boolean=false,
    content:@Composable ColumnScope.()->Unit) {
    Dialog(onDismissRequest={if(dismissEnabled)onDismiss()},properties=DialogProperties(usePlatformDefaultWidth=false,decorFitsSystemWindows=false)) {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(12.dp),contentAlignment=Alignment.Center) {
            val short=maxHeight<180.dp
            Surface(Modifier.widthIn(max=420.dp).fillMaxWidth().heightIn(max=620.dp),
                shape=RoundedCornerShape(24.dp),color=Color(0xFFF8FCF9),shadowElevation=12.dp) {
                Column(Modifier.padding(if(short)8.dp else 16.dp),verticalArrangement=Arrangement.spacedBy(if(short)6.dp else 10.dp)) {
                    if(!short) Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                        Text(title,Modifier.weight(1f),color=Deep,fontSize=18.sp,lineHeight=22.sp,fontWeight=FontWeight.Bold)
                        IconButton(onClick=onDismiss,enabled=dismissEnabled,modifier=Modifier.size(48.dp)) {
                            Text("×",color=Muted,fontSize=22.sp)
                        }
                    }
                    if(!short)HorizontalDivider(color=Color(0xFFE1EBE4))
                    Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        if(short)Text(title,color=Deep,fontSize=15.sp,fontWeight=FontWeight.Bold)
                        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=12.sp,lineHeight=17.sp)) { content() }
                    }
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        MintDialogAction(cancelLabel,onDismiss,Modifier.weight(1f).fillMaxHeight(),primary=false,enabled=dismissEnabled)
                        MintDialogAction(confirmLabel,onConfirm,Modifier.weight(1f).fillMaxHeight(),enabled=confirmEnabled,destructive=destructive)
                    }
                }
            }
        }
    }
}
