package com.mamxanh.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mamxanh.app.presentation.ui.MamXanhApp
import com.mamxanh.app.presentation.viewmodel.GardenViewModel
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{val vm:GardenViewModel=viewModel(factory=GardenViewModel.factory(applicationContext));MamXanhApp(vm)}}}
