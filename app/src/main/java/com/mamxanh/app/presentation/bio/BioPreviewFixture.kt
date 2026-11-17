package com.mamxanh.app.presentation.bio
import androidx.compose.runtime.*
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.tooling.preview.Preview

@Composable internal fun BioReferenceSurface(route:String,param:String="",modal:String="",id:String="",tab:String="",home:String="1",step:Int=0,expanded:Boolean=false,bottom:Boolean=false,sheetBottom:Boolean=false,referenceFields:Map<String,String> = emptyMap(),busy:Boolean=false){
 val context=LocalContext.current
 val requestedBusy=busy
 val initialFocus=remember{FocusRequester()}
 val keyboard=LocalSoftwareKeyboardController.current
 val touch=remember{object:InputModeManager{
  override val inputMode=InputMode.Touch
  override fun requestInputMode(inputMode:InputMode)=inputMode==InputMode.Touch
 }}
 val world=remember{BioWorld.reference(context).also{if(modal=="focus-end")it.state.put("focus",json("id" to "reference","title" to "M\u1ed9t kho\u1ea3ng cho ri\u00eang m\u00ecnh","state" to "RUNNING","remaining" to 1500))}}
 val ui=remember{BioController(route,param,modal).apply{modalId=id;homeTree=home;onboardingStep=step;this.busy=requestedBusy;fields.putAll(referenceFields);if(expanded)tabs["expanded"]="true";if(bottom)tabs["scrollBottom"]="true";if(sheetBottom)tabs["sheetBottom"]="true";if(tab.isNotBlank()){val parts=tab.split(':');tabs[parts[0]]=parts[1]}}}
 CompositionLocalProvider(LocalInputModeManager provides touch){
  Box(Modifier.fillMaxSize().focusRequester(initialFocus).focusable()){BioApplication(world,ui)}
 }
 LaunchedEffect(Unit){initialFocus.requestFocus();keyboard?.hide()}
}

