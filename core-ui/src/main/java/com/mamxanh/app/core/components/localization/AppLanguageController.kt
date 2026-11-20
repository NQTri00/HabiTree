package com.mamxanh.app.core.components.localization

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import java.util.Locale

/** Persisted per-app language. Existing installations retain Vietnamese until changed. */
object AppLanguageController {
    private const val preferences="app_language"
    private const val selectedLanguage="selected_language"
    private const val frameworkInitialized="framework_initialized"

    fun current(context:Context):AppLanguage {
        val systemChoice=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
            context.getSystemService(LocaleManager::class.java)?.applicationLocales?.let{if(it.isEmpty)null else it[0].language}
        else null
        val prefs=context.getSharedPreferences(preferences,Context.MODE_PRIVATE)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU&&systemChoice==null&&prefs.getBoolean(frameworkInitialized,false))
            return AppLanguage.fromTag(android.content.res.Resources.getSystem().configuration.locales[0].language)
        val saved=prefs.getString(selectedLanguage,AppLanguage.Vietnamese.tag).orEmpty()
        return AppLanguage.fromTag(systemChoice?:saved)
    }

    fun initialize(context:Context){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.TIRAMISU)return
        val prefs=context.getSharedPreferences(preferences,Context.MODE_PRIVATE)
        if(prefs.getBoolean(frameworkInitialized,false))return
        val manager=context.getSystemService(LocaleManager::class.java)
        val saved=AppLanguage.fromTag(prefs.getString(selectedLanguage,AppLanguage.Vietnamese.tag).orEmpty())
        prefs.edit().putBoolean(frameworkInitialized,true).apply()
        if(manager.applicationLocales.isEmpty)manager.applicationLocales=LocaleList.forLanguageTags(saved.tag)
    }

    fun wrap(context:Context):Context {
        val language=current(context)
        val locale=Locale.forLanguageTag(language.tag)
        AppText.language=language
        Locale.setDefault(locale)
        val configuration=Configuration(context.resources.configuration).apply{setLocale(locale);setLayoutDirection(locale)}
        return context.createConfigurationContext(configuration)
    }

    fun select(context:Context,tag:String){
        val language=AppLanguage.fromTag(tag)
        if(current(context)==language)return
        context.getSharedPreferences(preferences,Context.MODE_PRIVATE).edit().putString(selectedLanguage,language.tag).putBoolean(frameworkInitialized,true).apply()
        AppText.language=language
        Locale.setDefault(Locale.forLanguageTag(language.tag))
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            context.getSystemService(LocaleManager::class.java).applicationLocales=LocaleList.forLanguageTags(language.tag)
        }else activity(context)?.recreate()
    }

    private tailrec fun activity(context:Context):Activity?=when(context){
        is Activity->context
        is ContextWrapper->activity(context.baseContext)
        else->null
    }
}
