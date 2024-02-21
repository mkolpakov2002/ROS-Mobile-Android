package ru.hse.miem.ros.ui.fragments.master

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.MasterEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.connection.ConnectionType
import ru.hse.miem.ros.databinding.FragmentMasterBinding
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.MasterViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.3.0
 * @created on 10.01.2020
 * @updated on 05.10.2020
 * @modified by Maxim Kolpakov
 * @updated on 16.11.2020
 * @modified by Tanya Rykova
 * @updated on 13.05.2021
 * @modified by Maxim Kolpakov
 */
class MasterFragment() : Fragment(), OnEditorActionListener {
    private lateinit var mViewModel: MasterViewModel
    private lateinit var binding: FragmentMasterBinding
    private lateinit var ipItemList: ArrayList<String?>
    private lateinit var ipArrayAdapter: ArrayAdapter<String?>
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMasterBinding.inflate(inflater, container, false)
        return binding.root
    }

    public override fun onDestroyView() {
        super.onDestroyView()
        updateMasterDetails()
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MasterViewModel::class.java]

        // Define Views --------------------------------------------------------------
        ipItemList = ArrayList()
        ipArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item, ipItemList
        )
        binding.ipAddessTextView.setAdapter(ipArrayAdapter)
        val firstDeviceIp: String = mViewModel.iPAddress
        binding.ipAddessTextView.setText(firstDeviceIp, false)
        binding.ipAddessTextView.setOnClickListener {
            updateIpSpinner()
            binding.ipAddessTextView.showDropDown()
        }
        binding.ipAddessLayout.setEndIconOnClickListener { v: View? ->
            binding.ipAddessTextView.requestFocus()
            binding.ipAddessTextView.callOnClick()
        }
        binding.ipAddessTextView.setOnItemClickListener {
                _: AdapterView<*>?, _: View?, _: Int, _: Long ->
            binding.ipAddessTextView.clearFocus()
        }

        // View model connection -------------------------------------------------------------------
        mViewModel.master
            .observe(getViewLifecycleOwner()) { master: MasterEntity? ->
                if (master == null) {
                    binding.masterIpEditText.getText()?.clear()
                    binding.masterPortEditText.getText()?.clear()
                    return@observe
                }
                binding.masterIpEditText.setText(master.ip)
                binding.masterPortEditText.setText(master.port.toString())
            }
        mViewModel.currentNetworkSSID.observe(
            getViewLifecycleOwner()
        ) { networkSSID: String? -> binding.NetworkSSIDText.setText(networkSSID) }
        mViewModel.rosConnection.observe(
            getViewLifecycleOwner()
        ) { connectionType: ConnectionType? -> setRosConnection(connectionType) }

        // User input ------------------------------------------------------------------------------
        binding.connectButton.setOnClickListener { v: View? ->
            updateMasterDetails()
            mViewModel.setMasterDeviceIp(binding.ipAddessTextView.text.toString())
            mViewModel.connectToMaster()
        }
        binding.disconnectButton.setOnClickListener { v: View? -> mViewModel.disconnectFromMaster() }
        binding.helpButton.setOnClickListener { v: View? -> showConnectionHelpDialog() }
        binding.masterIpEditText.setOnEditorActionListener(this)
        binding.masterPortEditText.setOnEditorActionListener(this)
    }

    private fun updateIpSpinner() {
        ipItemList = ArrayList()
        ipItemList = mViewModel.iPAddressList
        ipArrayAdapter.clear()
        ipArrayAdapter.addAll(ipItemList)
    }

    private fun showConnectionHelpDialog() {
        mViewModel.updateHelpDisplay()
        val items: Array<String> = resources.getStringArray(R.array.connection_checklist)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_checklist_title)
            .setItems(items, null)
            .show()
    }

    private fun setRosConnection(connectionType: ConnectionType?) {
        var connectVisibility: Int = View.INVISIBLE
        var disconnectVisibility: Int = View.INVISIBLE
        var pendingVisibility: Int = View.INVISIBLE
        var statustext: String? = requireContext().getString(R.string.connected)
        if ((connectionType == ConnectionType.DISCONNECTED
                    || connectionType == ConnectionType.FAILED)
        ) {
            connectVisibility = View.VISIBLE
            statustext = requireContext().getString(R.string.disconnected)
        } else if (connectionType == ConnectionType.CONNECTED) {
            disconnectVisibility = View.VISIBLE
        } else if (connectionType == ConnectionType.PENDING) {
            pendingVisibility = View.VISIBLE
            statustext = requireContext().getString(R.string.pending)
        }

        // Display connection help dialog if the connection failed and enough time has passed
        // since the last display.
        if (connectionType == ConnectionType.FAILED && mViewModel.shouldShowHelp()) {
            showConnectionHelpDialog()
        }
        binding.statusText.text = statustext
        binding.connectedImage.visibility = disconnectVisibility
        binding.disconnectedImage.visibility = connectVisibility
        binding.connectButton.visibility = connectVisibility
        binding.disconnectButton.visibility = disconnectVisibility
        binding.pendingBar.visibility = pendingVisibility
    }

    private fun updateMasterDetails() {
        // Update master IP
        val masterIp: Editable? = binding.masterIpEditText.getText()
        if (masterIp != null) {
            mViewModel.setMasterIp(masterIp.toString())
        }

        // Update master port
        val masterPort: Editable? = binding.masterPortEditText.getText()
        if (!masterPort.isNullOrEmpty()) {
            mViewModel.setMasterPort(masterPort.toString())
        }
    }

    public override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                updateMasterDetails()
                view.clearFocus()
                Utils.hideSoftKeyboard(view)
                return true
            }
        }
        return false
    }

    companion object {
        private val TAG: String = MasterFragment::class.java.simpleName
        private val MIN_HELP_TIMESPAM: Long = (10 * 1000).toLong()
        fun newInstance(): MasterFragment {
            Log.i(TAG, "New Master Fragment")
            return MasterFragment()
        }
    }
}
