package com.mamxanh.app.core.components.design

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview

@Composable fun BioSelect(label:String,options:List<Pair<String,String>>,value:String,modifier:Modifier=Modifier,onSelect:(String)->Unit){
    var open by remember{mutableStateOf(false)}
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){
        if(label.isNotBlank())Bt(label,size=AppTypography.Size12)
        Box{
            val disclosureState = if(open)stringResource(R.string.copy_da_mo_617b34)else stringResource(R.string.copy_da_dong_6b9194)
        Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).background(bp.surface,RoundedCornerShape(AppDimensions.Dp12)).border(AppDimensions.Dp1,bp.control,RoundedCornerShape(AppDimensions.Dp12)).semantics{if(label.isNotBlank())contentDescription=label;stateDescription=disclosureState}.clickable(role=Role.DropdownList){open=true}.padding(AppDimensions.Dp12),verticalAlignment=Alignment.CenterVertically){Bt(options.firstOrNull{it.first==value}?.second?:options.firstOrNull()?.second.orEmpty(),Modifier.weight(1f),size=AppTypography.Size14);Bi("next",Modifier.rotate(90f),tint=bp.ink,size=AppIcons.Size15)}
            DropdownMenu(open,{open=false},containerColor=bp.surface){options.forEach{(id,title)->DropdownMenuItem(text={Bt(title)},onClick={onSelect(id);open=false})}}
        }
    }
}
@Composable fun BioToggle(label:String,value:Boolean,onChange:(Boolean)->Unit){
    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp55).padding(end=AppDimensions.Dp3).toggleable(value=value,role=Role.Switch,onValueChange=onChange),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        Bt(label,Modifier.weight(1f),size=AppTypography.Size13)
        Box(Modifier.size(AppDimensions.Dp42,AppDimensions.Dp25).background(if(value)bp.deep else AppColors.SwatchFFCFD6C8,RoundedCornerShape(AppDimensions.Dp20)).padding(AppDimensions.Dp3)){Box(Modifier.align(if(value)Alignment.CenterEnd else Alignment.CenterStart).size(AppDimensions.Dp19).background(AppColors.White,RoundedCornerShape(50)))}
    }
}

@Preview(name = "Language selector", widthDp = 335, showBackground = true)
@Composable
private fun BioSelectPreview() {
    BioSelect(
        stringResource(R.string.app_language_title),
        listOf(
            "vi" to stringResource(R.string.app_language_vi),
            "en" to stringResource(R.string.app_language_en),
        ),
        "en",
        onSelect = {},
    )
}

@Preview(name = "Reminder toggle states", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioTogglePreview() {
    Column {
        BioToggle(stringResource(R.string.copy_nhac_khi_cay_can_tuoi_246e3f), true) {}
        BioToggle(stringResource(R.string.copy_giam_chuyen_dong_ff3295), false) {}
    }
}
