package com.example.klasifikasisampah.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.klasifikasisampah.databinding.FragmentAccountBinding
import com.example.klasifikasisampah.ui.SplashScreenActivity

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountBinding.bind(view)

        binding.tvTitle.text = "Mahasiswa"
        binding.tvFullname.text = "Agus Syahril Mubarok"
        binding.tvEmail.text = "gusrylmubarok@gmail.com"

        binding.btnLogout.setOnClickListener {
            val backIntent = Intent(activity, SplashScreenActivity::class.java)
            startActivity(backIntent)

        }
    }

}


