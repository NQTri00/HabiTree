package com.mamxanh.app.presentation.ui.design

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppTypography


import com.mamxanh.shared.localization.AppText

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.*
import java.util.Calendar
import java.util.Locale

@Composable private fun BioPickerField(label:String,shown:String,placeholder:String,icon:String,modifier:Modifier,onClick:()->Unit){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){
        if(label.isNotBlank())Bt(label,size=AppTypography.Size12)
        Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).background(bp.surface,RoundedCornerShape(AppDimensions.Dp12)).border(AppDimensions.Dp1,bp.control,RoundedCornerShape(AppDimensions.Dp12)).clickable(role=Role.Button,onClick=onClick).semantics(mergeDescendants=true){contentDescription=label}.padding(AppDimensions.Dp12),verticalAlignment=Alignment.CenterVertically){
            Bt(shown.ifBlank{placeholder},Modifier.weight(1f),size=AppTypography.Size14,tint=if(shown.isBlank())bp.muted else bp.ink)
            Bi(icon,size=AppIcons.Size15,tint=bp.ink)
        }
    }
}

@Composable fun BioDateField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,futureOnly:Boolean=false){
    val context=LocalContext.current;val inspection=LocalInspectionMode.current
    val iso=remember{bioIsoDateFormat()}
    val display=remember(AppText.language){bioDateFormat()}
    val shown=remember(value,AppText.language){runCatching{display.format(iso.parse(value)!!)}.getOrDefault(value)}
    val clearLabel = stringResource(R.string.copy_xoa_57435a)
    BioPickerField(label,shown,stringResource(R.string.copy_chon_ngay_d724ec),"calendar",modifier){
            if(!inspection){
                val calendar=Calendar.getInstance();runCatching{calendar.time=iso.parse(value)!!}
                val picker=DatePickerDialog(context,{_,year,month,day->onChange(String.format(Locale.US,"%04d-%02d-%02d",year,month+1,day))},calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
                if(futureOnly){val tomorrow=Calendar.getInstance().apply{add(Calendar.DATE,1);set(Calendar.HOUR_OF_DAY,0);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)};picker.datePicker.minDate=tomorrow.timeInMillis}
                if(!futureOnly)picker.setButton(android.content.DialogInterface.BUTTON_NEUTRAL,clearLabel){_,_->onChange("")}
                picker.show()
            }
    }
}

@Composable fun BioTimeField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier){
    val context=LocalContext.current;val inspection=LocalInspectionMode.current
    val hour=value.substringBefore(':').toIntOrNull()?.coerceIn(0,23)?:9
    val minute=value.substringAfter(':',"0").toIntOrNull()?.coerceIn(0,59)?:0
    val shown=if(value.isBlank())""else String.format(Locale.US,"%02d:%02d",hour,minute)
    val clearLabel = stringResource(R.string.copy_xoa_57435a)
    BioPickerField(label,shown,stringResource(R.string.copy_chon_gio_b73bd9),"clock",modifier){
        if(!inspection){val picker=TimePickerDialog(context,{_,h,m->onChange(String.format(Locale.US,"%02d:%02d",h,m))},hour,minute,true);picker.setButton(android.content.DialogInterface.BUTTON_NEUTRAL,clearLabel){_,_->onChange("")};picker.show()}
    }
}
