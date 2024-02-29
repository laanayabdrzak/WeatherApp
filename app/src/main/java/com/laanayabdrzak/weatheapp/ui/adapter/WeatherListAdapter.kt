package com.laanayabdrzak.weatheapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.laanayabdrzak.weatheapp.data.model.WeatherData
import com.laanayabdrzak.weatheapp.databinding.CustomListItemBinding

class WeatherListAdapter(
    context: Context,
    private var data: List<WeatherData.WeatherDataDetails.Timeline.Interval> = emptyList()
) : ArrayAdapter<WeatherData.WeatherDataDetails.Timeline.Interval>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBinding: CustomListItemBinding
        val view = if (convertView == null) {
            itemBinding = CustomListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            itemBinding.root
        } else {
            itemBinding = CustomListItemBinding.bind(convertView)
            convertView
        }

        val interval = getItem(position)
        itemBinding.textTemperature.text = "Température: ${interval?.values?.temperature}"
        itemBinding.textTemperatureApparent.text = "Température apparent: ${interval?.values?.temperatureApparent}"
        itemBinding.textWindSpeed.text = "Vitesse du vent: ${interval?.values?.windSpeed}"

        return view
    }

    fun setData(newData: List<WeatherData.WeatherDataDetails.Timeline.Interval>) {
        data = newData
        notifyDataSetChanged()
    }
}
