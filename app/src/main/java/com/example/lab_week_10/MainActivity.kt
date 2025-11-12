package com.example.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("LifecycleDebug", "onCreate dipanggil")

        prepareViewModel()

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifecycleDebug", "onResume dipanggil")

        initializeValueFromDatabase()

        viewModel.total.value?.let {
            Log.d("LifecycleDebug", "Mencoba menampilkan Toast dengan tanggal: ${it.date}")
            if (it.date != "N/A") {
                Toast.makeText(this, it.date, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        Log.d("LifecycleDebug", "onPause dipanggil")
        viewModel.total.value?.let { currentTotalObject ->
            val newTimestamp = Date().toString()
            val updatedTotalObject = TotalObject(currentTotalObject.value, newTimestamp)
            Log.d("LifecycleDebug", "Menyimpan ke DB: value=${updatedTotalObject.value}, date=${updatedTotalObject.date}")
            db.totalDao().update(Total(ID, updatedTotalObject))
        }
    }

    private fun initializeValueFromDatabase() {
        val totalFromDb = db.totalDao().getTotal(ID)
        if (totalFromDb.isEmpty()) {
            Log.d("LifecycleDebug", "Database kosong, membuat entri baru.")
            val initialObject = TotalObject(0, "N/A")
            db.totalDao().insert(Total(id = ID, total = initialObject))
            viewModel.setTotal(initialObject)
        } else {
            Log.d("LifecycleDebug", "Database ditemukan, memuat data: ${totalFromDb.first().total}")
            viewModel.setTotal(totalFromDb.first().total)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel(){
        viewModel.total.observe(this, { newTotalObject ->
            Log.d("LifecycleDebug", "LiveData terupdate, UI diubah ke: ${newTotalObject.value}")
            updateText(newTotalObject.value)
        })
    }

    companion object {
        const val ID: Long = 1
    }
}