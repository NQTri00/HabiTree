package com.mamxanh.app.presentation.bio

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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable private fun BioPickerField(label:String,shown:String,placeholder:String,icon:String,modifier:Modifier,onClick:()->Unit){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(7.dp)){
        if(label.isNotBlank())Bt(label,size=12)
        Row(Modifier.fillMaxWidth().heightIn(min=48.dp).background(bp.surface,RoundedCornerShape(12.dp)).border(1.dp,bp.control,RoundedCornerShape(12.dp)).clickable(role=Role.Button,onClick=onClick).semantics(mergeDescendants=true){contentDescription=label}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){
            Bt(shown.ifBlank{placeholder},Modifier.weight(1f),size=14,tint=if(shown.isBlank())bp.muted else bp.ink)
            Bi(icon,size=15,tint=bp.ink)
        }
    }
}

@Composable internal fun BioDateField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,futureOnly:Boolean=false){
    val context=LocalContext.current;val inspection=LocalInspectionMode.current
    val iso=remember{SimpleDateFormat("yyyy-MM-dd",Locale.US).apply{isLenient=false}}
    val display=remember{SimpleDateFormat("dd/MM/yyyy",Locale("vi","VN"))}
    val shown=remember(value){runCatching{display.format(iso.parse(value)!!)}.getOrDefault(value)}
    BioPickerField(label,shown,"Chọn ngày","calendar",modifier){
            if(!inspection){
                val calendar=Calendar.getInstance();runCatching{calendar.time=iso.parse(value)!!}
                val picker=DatePickerDialog(context,{_,year,month,day->onChange(String.format(Locale.US,"%04d-%02d-%02d",year,month+1,day))},calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
                if(futureOnly){val tomorrow=Calendar.getInstance().apply{add(Calendar.DATE,1);set(Calendar.HOUR_OF_DAY,0);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)};picker.datePicker.minDate=tomorrow.timeInMillis}
                if(!futureOnly)picker.setButton(android.content.DialogInterface.BUTTON_NEUTRAL,"Xóa"){_,_->onChange("")}
                picker.show()
            }
    }
}

@Composable internal fun BioTimeField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier){
    val context=LocalContext.current;val inspection=LocalInspectionMode.current
    val hour=value.substringBefore(':').toIntOrNull()?.coerceIn(0,23)?:9
    val minute=value.substringAfter(':',"0").toIntOrNull()?.coerceIn(0,59)?:0
    val shown=if(value.isBlank())""else String.format(Locale.US,"%02d:%02d",hour,minute)
    BioPickerField(label,shown,"Chọn giờ","clock",modifier){
        if(!inspection){val picker=TimePickerDialog(context,{_,h,m->onChange(String.format(Locale.US,"%02d:%02d",h,m))},hour,minute,true);picker.setButton(android.content.DialogInterface.BUTTON_NEUTRAL,"Xóa"){_,_->onChange("")};picker.show()}
    }
}
