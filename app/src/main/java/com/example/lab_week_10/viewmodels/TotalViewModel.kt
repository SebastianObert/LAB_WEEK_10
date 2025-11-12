package com.example.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab_week_10.database.TotalObject

class TotalViewModel : ViewModel() {

    private val _total = MutableLiveData<TotalObject>()
    val total: LiveData<TotalObject> = _total

    init {
        _total.postValue(TotalObject(0, "N/A"))
    }

    fun incrementTotal() {
        val currentValue = _total.value?.value ?: 0
        val currentDate = _total.value?.date ?: "N/A"
        _total.postValue(TotalObject(currentValue + 1, currentDate))
    }

    fun setTotal(newTotal: TotalObject) {
        _total.postValue(newTotal)
    }
}