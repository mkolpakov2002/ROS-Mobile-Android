package ru.hse.miem.ros.ui.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import ru.hse.miem.ros.databinding.FragmentMainBinding
import ru.hse.miem.ros.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var observer: LifecycleObserver

    override fun onAttach(context: Context) {
        super.onAttach(context)
        observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                mViewModel = ViewModelProvider(this@MainFragment)[MainViewModel::class.java]
                activity?.let { mViewModel.init(it.application) }
                arguments?.getString("configName", "")?.let { configName ->
                    if (configName.isNotEmpty()) {
                        mViewModel.createFirstConfig(configName)
                    }
                }
//                mViewModel.getConfigTitle()
//                    .observe(viewLifecycleOwner) { newTitle: String? -> setTitle(newTitle) }
                activity?.lifecycle?.removeObserver(observer)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}