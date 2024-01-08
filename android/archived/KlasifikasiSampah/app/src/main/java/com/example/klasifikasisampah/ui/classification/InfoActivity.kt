package com.example.klasifikasisampah.ui.classification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.klasifikasisampah.R
import com.example.klasifikasisampah.databinding.ActivityInfoBinding
import com.example.klasifikasisampah.ml.ScrapComposeClassifier
import com.example.klasifikasisampah.ui.main.MainActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlin.math.floor

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private lateinit var adapter: InfoAdapter
    private var listScrap = ArrayList<ScrapComposeClassifier.Classification>()

    private var nama: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get image
        val img = intent.getStringExtra(EXTRA_IMAGE)
        Glide.with(this)
            .load(img)
            .into(binding.imageResult)

        // get data and visualize pie chart
        val type = intent.getStringExtra(EXTRA_TYPE)
        Log.d("INFO_TYPE", "type: $type")
        val confident = intent.getFloatExtra(EXTRA_CONFIDENT, 0F)
        Log.d("INFO_CONFIDENT", "confident: $confident")

        val value1 = floor(confident * 100)
        val value2 = 100 - value1

        val pieMap: MutableMap<String, Float> = when (type) {
            "organik" -> mutableMapOf("Organik" to value1, "Anorganik" to value2)
            else -> mutableMapOf("Organik" to value2, "Anorganik" to value1)
        }

        val entries = ArrayList<PieEntry>()
        pieMap.filter { (key, value) ->
            entries.add(PieEntry(value, key))
        }

        val pieColors = ArrayList<Int>()
        pieColors.add(Color.parseColor("#54B086"))
        pieColors.add(Color.parseColor("#E9BC99"))

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = pieColors
        pieDataSet.sliceSpace = 1f
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(0f)

        binding.pieChart.apply {
            data = pieData
            animateY(1400, Easing.EaseInOutQuad)
            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.isWordWrapEnabled = true
            setDrawEntryLabels(false)
            invalidate()
        }

        //rv identifikasi sampah
        listScrap = intent.getParcelableArrayListExtra(EXTRA_IDENTIFY)!!
        adapter = InfoAdapter(listScrap, this)
        binding.apply {
            rvSampah.setHasFixedSize(true)
            rvSampah.layoutManager = LinearLayoutManager(this@InfoActivity)
            rvSampah.adapter = adapter
        }

        //adapter onclick
        adapter.setOnItemClickCallback(object : InfoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ScrapComposeClassifier.Classification) {
                nama = data.kategori
            }
        })

        val appBarBtnBack = binding.appBarResult.findViewById<Button>(R.id.btn_back)
        appBarBtnBack.setOnClickListener {
            val backIntent = Intent(this@InfoActivity, MainActivity::class.java)
            startActivity(backIntent)
            finish()
        }

        binding.btnMoreTake.setOnClickListener {
            val cameraIntent = Intent(this@InfoActivity, CameraActivity::class.java)
            startActivity(cameraIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_CONFIDENT = "extra_confident"
        const val EXTRA_IDENTIFY = "extra_identify"
        const val EXTRA_IMAGE = "image"
    }
}