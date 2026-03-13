package com.example.meditrackpro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = MedicineDatabase.getDatabase(application).medicineDao()
    val allMedicines: LiveData<List<Medicine>> = dao.getAllMedicines()

    fun insert(medicine: Medicine) = viewModelScope.launch {
        dao.insertMedicine(medicine)
    }

    fun delete(medicine: Medicine) = viewModelScope.launch {
        dao.deleteMedicine(medicine)
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        dao.deleteMedicineById(id)
    }
}