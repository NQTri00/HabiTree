package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.GardenFontFamily

@Composable fun BioField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,multiline:Boolean=false,placeholder:String="",icon:String="",number:Boolean=false,centered:Boolean=false,error:String="",accessibilityLabel:String=label){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){
        if(label.isNotBlank())Bt(label,size=AppTypography.Size12)
        val textColor=bp.ink;val muted=bp.muted
        BasicTextField(value,onChange,Modifier.fillMaxWidth().heightIn(min=if(multiline)AppDimensions.Dp90 else AppDimensions.Dp48).background(bp.surface,RoundedCornerShape(AppDimensions.Dp12)).border(AppDimensions.Dp1,if(error.isBlank())bp.control else AppColors.Danger,RoundedCornerShape(AppDimensions.Dp12)).semantics{if(accessibilityLabel.isNotBlank())contentDescription=accessibilityLabel;if(error.isNotBlank())error(error)}.padding(AppDimensions.Dp12),singleLine=!multiline,textStyle=TextStyle(textAlign=if(centered)TextAlign.Center else TextAlign.Start,color=textColor,fontSize=AppTypography.Sp14,lineHeight=AppTypography.Sp21,fontFamily=GardenFontFamily,platformStyle=androidx.compose.ui.text.PlatformTextStyle(includeFontPadding=false)),keyboardOptions=KeyboardOptions(keyboardType=if(number)KeyboardType.Number else KeyboardType.Text),cursorBrush=SolidColor(bp.deep),decorationBox={inner->Row(verticalAlignment=if(multiline)Alignment.Top else Alignment.CenterVertically){Box(Modifier.weight(1f)){if(value.isBlank()&&placeholder.isNotBlank())Bt(placeholder,size=AppTypography.Size14,tint=muted);inner()};if(icon.isNotBlank())Bi(icon,size=AppIcons.Size15,tint=textColor)}})
        BioError(error)
    }
}
@Composable fun BioError(text:String){if(text.isNotBlank())Bt(text,Modifier.semantics{liveRegion=LiveRegionMode.Polite},size=AppTypography.Size12,tint=AppColors.Danger)}

@Preview(name = "Empty field", widthDp = 335, showBackground = true)
@Composable
private fun BioFieldPreview() {
    BioField(label = "Plant name", value = "", onChange = {}, placeholder = "Give your plant a name")
}

@Preview(name = "Numeric field error", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioErrorPreview() {
    BioField(label = "Duration", value = "121", onChange = {}, number = true, error = "Enter 5–120 minutes")
}
