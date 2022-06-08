package com.example.week6flowappwb

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.week6flowappwb.databinding.FragmentButtonsBinding
import java.util.*

class ButtonsFragment : Fragment() {
    private var isThreadRunning = false
    private lateinit var binding: FragmentButtonsBinding
    private var pauseOffset: Long = 0
    private var chronometer20secDelimiter = 1
    private var currentChronometerTime: Long = 0L
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentButtonsBinding.inflate(inflater, container, false)

        binding.playButton.setOnClickListener { startFlowCalculation() }
        binding.pauseButton.setOnClickListener { pauseFlowCalculation() }
        binding.resetButton.setOnClickListener { resetFlowCalculation() }

        binding.flowChronometer.setOnChronometerTickListener {
            currentChronometerTime = SystemClock.elapsedRealtime() - binding.flowChronometer.base

            if (currentChronometerTime / chronometer20secDelimiter > 20000) {
                chronometer20secDelimiter++
                val randomColor: Int =
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
                binding.layout.setBackgroundColor(randomColor)
            }
        }

        return binding.root
    }

    private fun startFlowCalculation() {
        isThreadRunning = true
        binding.playButton.isEnabled = false
        binding.resetButton.isEnabled = false

        if (isThreadRunning) {
            binding.flowChronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            binding.flowChronometer.start()
        }

        parentFragmentManager.setFragmentResult(
            "requestPiCharKey",
            bundleOf("PI_CHAR_KEY" to isThreadRunning)
        )
    }


    private fun pauseFlowCalculation() {
        isThreadRunning = false
        binding.playButton.isEnabled = true
        binding.resetButton.isEnabled = true

        if (!isThreadRunning) {
            binding.flowChronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - binding.flowChronometer.base
        }

        parentFragmentManager.setFragmentResult(
            "requestPiCharKey",
            bundleOf("PI_CHAR_KEY" to isThreadRunning)
        )
    }

    private fun resetFlowCalculation() {
        isThreadRunning = false

        if (!isThreadRunning) {
            binding.flowChronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            currentChronometerTime = 0L
            chronometer20secDelimiter = 1
            binding.flowChronometer.stop()
        }

        binding.playButton.isEnabled = true
        val isReset = true
        parentFragmentManager.setFragmentResult(
            "requestResultKey",
            bundleOf("RESET_KEY" to isReset)
        )
    }
}