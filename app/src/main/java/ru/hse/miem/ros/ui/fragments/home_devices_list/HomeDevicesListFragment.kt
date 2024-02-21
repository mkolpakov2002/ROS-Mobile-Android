package ru.hse.miem.ros.ui.fragments.home_devices_list

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
import androidx.navigation.NavController
import ru.hse.miem.ros.databinding.FragmentHomeDevicesListBinding
import ru.hse.miem.ros.viewmodel.MainViewModel

class HomeDevicesListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDevicesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeDevicesListFragment().apply {
            }
    }

    lateinit var navController: NavController
    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: FragmentHomeDevicesListBinding
    private lateinit var observer: LifecycleObserver

    override fun onAttach(context: Context) {
        super.onAttach(context)
        observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                mViewModel = ViewModelProvider(this@HomeDevicesListFragment)[MainViewModel::class.java]
                activity?.let { mViewModel.init(it.application) }
                arguments?.getString("configName", "")?.let { configName ->
                    if (configName.isNotEmpty()) {
                        mViewModel.createFirstConfig(configName)
                    }
                }
                mViewModel.getConfigTitle()
                    .observe(viewLifecycleOwner) {
                        //TODO: формировать элемент списка RecycleView устройств
//                        newTitle: String? -> setTitle(newTitle)
                    }
                activity?.lifecycle?.removeObserver(observer)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.lifecycle?.addObserver(observer)

    }

}