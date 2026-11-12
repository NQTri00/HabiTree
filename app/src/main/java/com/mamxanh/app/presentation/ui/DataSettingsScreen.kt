package com.mamxanh.app.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.BuildConfig
import com.mamxanh.app.R
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DataSettingsScreen(storage:GameStorage,onBack:()->Unit,onReplaced:()->Unit,modifier:Modifier=Modifier) {
    val context=LocalContext.current
    val service=remember(context,storage){BackupService(context.applicationContext,storage,BuildConfig.VERSION_NAME)}
    val scope=rememberCoroutineScope()
    var busy by remember{mutableStateOf(false)}
    androidx.activity.compose.BackHandler(enabled=busy) {}
    var pending by remember{mutableStateOf<GameBackup?>(null)}
    var confirmation by remember{mutableIntStateOf(0)}
    var message by remember{mutableStateOf("")}
    var bytes by remember{mutableLongStateOf(0)}
    var safety by remember{mutableStateOf(false)}
    val success=stringResource(R.string.data_success)
    val error=stringResource(R.string.data_error)
    fun run(action:()->Unit,replace:Boolean=false) {
        if(busy)return
        busy=true;message=""
        scope.launch {
            try {
                val restored=withContext(Dispatchers.IO){action();if(replace)storage.backup(BuildConfig.VERSION_NAME,System.currentTimeMillis()) else null}
                pending=null;confirmation=0;message=success
                if(restored!=null)message+="\n"+context.getString(R.string.data_counts,restored.count("tasks"),restored.count("trees"),restored.count("occurrences"))
                if(replace)onReplaced()
            } catch(_:Exception){message=error}
            finally {
                val info=withContext(Dispatchers.IO){service.sizeBytes() to service.hasSafety()}
                bytes=info.first;safety=info.second;busy=false
            }
        }
    }
    LaunchedEffect(service){val info=withContext(Dispatchers.IO){service.sizeBytes() to service.hasSafety()};bytes=info.first;safety=info.second}
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")){uri->if(uri!=null)run({service.export(uri)})}
    val diagnostics=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")){uri->if(uri!=null)run({service.diagnostics(uri)})}
    val import=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->
        if(uri!=null && !busy) {
            busy=true;message=""
            scope.launch {
                try{pending=withContext(Dispatchers.IO){service.inspect(uri)};confirmation=1}
                catch(_:Exception){message=error}
                finally{busy=false}
            }
        }
    }
    DataSettingsContent(bytes,safety,busy,message,pending,confirmation,
        onBack=onBack,onExport={export.launch("MamXanh-${System.currentTimeMillis()}.mamxanh")},
        onImport={import.launch(arrayOf("application/octet-stream","text/plain","application/zip"))},
        onRecover={confirmation=2},onClear={confirmation=3},
        onConfirm={
            when(confirmation) {
                1->pending?.let{backup->run({service.restore(backup)},true)}
                2->run({service.recover()},true)
                3->confirmation=4
                4->run({service.clear()},true)
            }
        },onCancel={pending=null;confirmation=0},
        onDiagnostics={diagnostics.launch("MamXanh-diagnostics.txt")},modifier=modifier)
}

@Composable
internal fun DataSettingsContent(bytes:Long,safety:Boolean,busy:Boolean,message:String,pending:GameBackup?,confirmation:Int,
    onBack:()->Unit,onExport:()->Unit,onImport:()->Unit,onRecover:()->Unit,onClear:()->Unit,onConfirm:()->Unit,onCancel:()->Unit,
    onDiagnostics:()->Unit,modifier:Modifier=Modifier) {
    val context=LocalContext.current
    var showLicense by remember { mutableStateOf(false) }
    var license by remember { mutableStateOf("") }
    LaunchedEffect(showLicense) {
        if(showLicense && license.isEmpty())license=withContext(Dispatchers.IO) {
            context.resources.openRawResource(R.raw.apache_license).bufferedReader().use { it.readText() }
        }
    }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                IconButton(onBack,enabled=!busy){Icon(Icons.AutoMirrored.Rounded.ArrowBack,stringResource(R.string.data_back),Modifier.size(18.dp),tint=Green)}
                Text(stringResource(R.string.data_title),fontSize=14.sp,fontWeight=FontWeight.Bold,color=Deep)
            }
            val scroll=rememberScrollState()
            Column(Modifier.weight(1f).fillMaxWidth().mintScrollEdges(scroll.canScrollBackward,scroll.canScrollForward)
                .verticalScroll(scroll).padding(horizontal=10.dp,vertical=4.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
                if(message.isNotBlank())Text(message,color=Green,fontSize=11.sp,lineHeight=15.sp)
                if(confirmation>0)DataPanel {
                    Label(if(confirmation>=3)R.string.data_clear else R.string.data_replace)
                    Copy(if(confirmation>=3)R.string.data_clear_warning else if(confirmation==2)R.string.data_safety_note else R.string.data_replace_warning)
                    pending?.let{backup->
                        Text(stringResource(R.string.data_counts,backup.count("tasks"),backup.count("trees"),backup.count("occurrences")),fontSize=11.sp,color=Deep)
                        Text(stringResource(R.string.data_created,java.text.SimpleDateFormat("dd/MM/yyyy HH:mm",java.util.Locale.getDefault()).format(java.util.Date(backup.createdAt)),backup.appVersion),fontSize=10.sp,color=Muted)
                    }
                    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        Action(if(confirmation==4)R.string.data_clear_again else R.string.data_confirm,onConfirm,!busy,Modifier.weight(1f),true)
                        Action(R.string.data_cancel,onCancel,!busy,Modifier.weight(1f))
                    }
                }
                DataPanel {
                    Label(R.string.data_backup);Copy(R.string.data_warning)
                    Text(stringResource(R.string.data_size,(bytes/1024).toString()),fontSize=10.sp,color=Muted)
                    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        Action(R.string.data_export,onExport,!busy,Modifier.weight(1f))
                        Action(R.string.data_import,onImport,!busy,Modifier.weight(1f))
                    }
                    if(safety)Action(R.string.data_recover,onRecover,!busy)
                    Action(R.string.data_clear,onClear,!busy)
                }
                DataPanel {Label(R.string.data_about);Copy(R.string.data_privacy);Copy(R.string.data_policy);Copy(R.string.data_accessibility)
                    Text(stringResource(R.string.data_version,BuildConfig.VERSION_NAME,BuildConfig.VERSION_CODE,GameSchema.VERSION),fontSize=10.sp,color=Muted)
                    Text("Kotlin · AndroidX · Koin · Ktor · SQLDelight\nApache 2.0",fontSize=10.sp,lineHeight=14.sp,color=Muted)
                    Action(R.string.data_licenses,{showLicense=!showLicense},!busy)
                    if(showLicense)Text(license,fontSize=10.sp,lineHeight=14.sp,color=Deep)
                }
                DataPanel {Copy(R.string.data_diagnostics_note);Action(R.string.data_diagnostics,onDiagnostics,!busy)}
            }
        }
    }
}
@Composable private fun DataPanel(content:@Composable ColumnScope.()->Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(),contentPadding=PaddingValues(10.dp),cornerSize=14.dp) {
        Column(verticalArrangement=Arrangement.spacedBy(6.dp),content=content)
    }
}
@Composable private fun Label(id:Int){Text(stringResource(id),fontSize=12.sp,lineHeight=16.sp,color=Deep,fontWeight=FontWeight.Bold)}
@Composable private fun Copy(id:Int){Text(stringResource(id),fontSize=11.sp,lineHeight=15.sp,color=Muted)}
@Composable private fun Action(id:Int,onClick:()->Unit,enabled:Boolean,modifier:Modifier=Modifier,primary:Boolean=false) {
    Box(modifier.heightIn(min=48.dp),contentAlignment=Alignment.Center) {
        MintDialogAction(stringResource(id),onClick,Modifier.fillMaxWidth(),primary=primary,enabled=enabled)
    }
}
@Preview(name="Data settings · full document",locale="vi",widthDp=360,heightDp=1000,device="spec:width=360dp,height=1000dp,dpi=160")
@Composable fun DataSettingsPreview(){MamXanhPreviewTheme{DataSettingsContent(24000,true,false,"",null,0,{},{},{},{},{},{},{},{})}}
@Preview(name="Restore confirmation · large text",locale="vi",widthDp=320,heightDp=1500,fontScale=1.3f,device="spec:width=320dp,height=1500dp,dpi=160")
@Composable fun DataRestorePreview(){MamXanhPreviewTheme{DataSettingsContent(24000,true,false,"",GameBackup(7,"0.1.0",1794486579000,emptyList()),1,{},{},{},{},{},{},{},{})}}
