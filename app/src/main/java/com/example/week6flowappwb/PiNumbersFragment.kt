package com.example.week6flowappwb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.week6flowappwb.databinding.FragmentPiNumbersBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class PiNumbersFragment : Fragment() {

    private lateinit var binding: FragmentPiNumbersBinding
    private lateinit var job: Job
    private lateinit var scope: CoroutineScope
    var counter = 4 //расчет начинается с 4 символов числа ПИ (3.14 стоит в TextView по умолчанию)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPiNumbersBinding.inflate(inflater, container, false)
        binding.piTextView.text = "3.14"

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener("requestPiCharKey", requireActivity()) { requestPiCharKey, bundle ->

            val isRunning = bundle.getBoolean("PI_CHAR_KEY")

            if (isRunning){
                job = Job()
                scope = CoroutineScope(Dispatchers.Main + job)
            } else {
                job.cancel()
            }

            scope.launch(Dispatchers.IO) {
                while (isRunning) {
                    val piCharFlow: Flow<String> = flow {
                        val pi = PiCalculation.piSpigot(counter).toString()
                        val piChar = pi.substring(pi.length - 1, pi.length)
                        counter += 1
                        delay(10)
                        emit(piChar)
                    }
                    piCharFlow.collect {
                        withContext(Dispatchers.Main) {
                            binding.piTextView.append(it)
                            binding.scrollView.fullScroll(View.FOCUS_DOWN)
                        }
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener("requestResultKey", requireActivity()) { requestResultKey, bundle ->

            val isReset = bundle.getBoolean("RESET_KEY")
            lifecycleScope.launch(Dispatchers.Main) {
                if (isReset) {
                    binding.piTextView.text = "3.14"
                    counter = 4
                }

            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}