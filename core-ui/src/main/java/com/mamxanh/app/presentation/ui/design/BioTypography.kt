package com.mamxanh.app.presentation.ui.design

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import java.io.DataInputStream

data class BioMonoFont(val family:FontFamily,val exact:Boolean)

val LocalBioMonoFont=staticCompositionLocalOf{BioMonoFont(FontFamily.Monospace,false)}

private fun hasFontData(context:Context,id:Int):Boolean {
    if(id==0)return false
    return runCatching{context.resources.openRawResource(id).use{
        val signature=DataInputStream(it).readInt()
        signature==0x00010000||signature==0x4F54544F
    }}.getOrDefault(false)
}

@Composable fun rememberBioMonoFont():BioMonoFont {
    val context=LocalContext.current
    return remember(context){
        val regular=context.resources.getIdentifier("consolas_regular","font",context.packageName)
        val bold=context.resources.getIdentifier("consolas_bold","font",context.packageName)
        if(hasFontData(context,regular)&&hasFontData(context,bold))BioMonoFont(FontFamily(Font(regular,FontWeight.Normal),Font(bold,FontWeight.Bold)),true)
        else BioMonoFont(FontFamily.Monospace,false)
    }
}
