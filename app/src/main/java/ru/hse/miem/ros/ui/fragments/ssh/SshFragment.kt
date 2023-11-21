package ru.hse.miem.ros.ui.fragments.ssh

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.SSHEntity
import ru.hse.miem.ros.databinding.FragmentSshBinding
import ru.hse.miem.ros.viewmodel.SshViewModel
import java.util.Arrays

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 18.03.20
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 */
class SshFragment() : Fragment(), OnEditorActionListener {
    private lateinit var mViewModel: SshViewModel
    private lateinit var binding: FragmentSshBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SshRecyclerViewAdapter
    private lateinit var terminalEditText: AutoCompleteTextView
    private lateinit var connectButton: Button
    private lateinit var sendButton: FloatingActionButton
    private lateinit var abortButton: FloatingActionButton
    private var connected: Boolean = false
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSshBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    public override fun onDestroyView() {
        super.onDestroyView()
        setConnectionData()
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Use a linear layout manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)

        // Specify an adapter
        mAdapter = SshRecyclerViewAdapter()

        // Define the Recycler View
        recyclerView = view.findViewById(R.id.outputRV)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setAdapter(mAdapter)

        // Get the handles
        terminalEditText = view.findViewById(R.id.terminal_editText)
        connectButton = view.findViewById(R.id.sshConnectButton)
        sendButton = view.findViewById(R.id.sshSendButton)
        abortButton = view.findViewById(R.id.sshAbortButton)

        // Define autocompletion
        val autocompletion: Array<String> = resources.getStringArray(R.array.ssh_autocmpletion)
        Arrays.sort(autocompletion)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, autocompletion)
        terminalEditText.setAdapter(adapter)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this)[SshViewModel::class.java]

        // Define ViewModel Connection ------
        mViewModel.sSH
            .observe(getViewLifecycleOwner()) { ssh: SSHEntity? ->
                if (ssh == null) return@observe
                binding.ipAddressEditText.setText(ssh.ip)
                binding.portEditText.setText(ssh.port.toString())
                binding.usernameEditText.setText(ssh.username)
                binding.passwordEditText.setText(ssh.password)
            }

        // Connect Buttons
        connectButton.setOnClickListener { v: View? ->
            if (connected) {
                mViewModel.stopSsh()
            } else {
                setConnectionData()
                connectSsh()
            }
        }
        sendButton.setOnClickListener { v: View? ->
            val message: String = terminalEditText.text.toString()
            mViewModel.sendViaSSH(message)
            terminalEditText.setText("")
            hideSoftKeyboard()
        }
        abortButton.setOnClickListener { v: View? -> mViewModel.abortAction() }
        mViewModel.outputData.observe(getViewLifecycleOwner()) { s: String ->
            mAdapter.addItem(s)
            recyclerView.scrollToPosition(mAdapter.itemCount - 1)
        }
        mViewModel.isConnected
            .observe(getViewLifecycleOwner()) { connectionFlag: Boolean ->
                connected = connectionFlag
                if (connectionFlag) {
                    connectButton.text = "Disconnect"
                } else {
                    connected = false
                    connectButton.text = "Connect"
                }
            }

        // User Input
        binding.ipAddressEditText.setOnEditorActionListener(this)
        binding.portEditText.setOnEditorActionListener(this)
        binding.usernameEditText.setOnEditorActionListener(this)
        binding.passwordEditText.setOnEditorActionListener(this)
    }

    private fun connectSsh() {
        mViewModel.connectViaSSH()
    }

    private fun hideSoftKeyboard() {
        val imm: InputMethodManager = activity
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    public override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            setConnectionData()
            view.clearFocus()
            hideSoftKeyboard()
            return true
        }
        return false
    }

    fun setConnectionData() {
        val sshIp: Editable? = binding.ipAddressEditText.getText()
        if (sshIp != null) {
            mViewModel.setSshIp(sshIp.toString())
        }
        val sshPort: Editable? = binding.portEditText.getText()
        if (sshPort != null) {
            mViewModel.setSshPort(sshPort.toString())
        }
        val sshPassword: Editable? = binding.passwordEditText.getText()
        if (sshPassword != null) {
            mViewModel.setSshPassword(sshPassword.toString())
        }
        val sshUsername: Editable? = binding.usernameEditText.getText()
        if (sshUsername != null) {
            mViewModel.setSshUsername(sshUsername.toString())
        }
    }

    companion object {
        val TAG: String? = SshFragment::class.java.canonicalName
        fun newInstance(): SshFragment {
            return SshFragment()
        }
    }
}
