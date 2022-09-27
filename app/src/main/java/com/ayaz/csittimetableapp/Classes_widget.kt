package com.ayaz.csittimetableapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Implementation of App Widget functionality.
 */
class Classes_widget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
//fun getArrayList(key: String?): ArrayList<class_name_dataType> {
//    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
//    val prefs2 = android.preference.PreferenceManager.getDefaultSharedPreferences()
//
//    val gson = Gson()
//    val json: String = prefs.getString(key, null).toString()
//    val type: Type = object : TypeToken<ArrayList<class_name_dataType>?>() {}.getType()
//    return gson.fromJson(json, type)
//}
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.classes_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}