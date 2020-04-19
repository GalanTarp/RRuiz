package net.azarquiel.rruiz.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.databinding.ActivityDetailCanapeBinding
import net.azarquiel.rruiz.model.Canape

class DetailCanape : AppCompatActivity() {

    private lateinit var canape: Canape

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        canape = intent.getSerializableExtra("canape") as Canape
        var binding: ActivityDetailCanapeBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_canape)
        binding.canape = canape
    }
}
