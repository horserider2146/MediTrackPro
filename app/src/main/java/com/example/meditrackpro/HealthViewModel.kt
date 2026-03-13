package com.example.meditrackpro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = MedicineDatabase.getDatabase(application).healthRecordDao()
    val allRecords: LiveData<List<HealthRecord>> = dao.getAllRecords()

    fun insert(record: HealthRecord) = viewModelScope.launch {
        dao.insertRecord(record)
    }

    fun delete(record: HealthRecord) = viewModelScope.launch {
        dao.deleteRecord(record)
    }
}