package ru.hse.miem.ros.ui.fragments.config

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.R
import ru.hse.miem.ros.viewmodel.ConfigurationsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 10.01.20
 * @updated on 06.02.20
 * @modified by
 */
class ConfigurationsFragment() : Fragment() {
    private lateinit var mViewModel: ConfigurationsViewModel
    private lateinit var addConfigButton: Button
    private lateinit var lastOpenedRV: RecyclerView
    private lateinit var lastOpenedAdapter: ConfigListAdapter
    private lateinit var lastOpenedMoreButton: ImageButton
    private lateinit var titleText: TextView
    private lateinit var renameButton: ImageButton
    private lateinit var deleteButton: ImageButton
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configurations, container, false)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addConfigButton = view.findViewById(R.id.add_config_button)
        lastOpenedRV = view.findViewById(R.id.last_opened_recyclerview)
        lastOpenedMoreButton = view.findViewById(R.id.last_opened_more_button)
        titleText = view.findViewById(R.id.current_config_textview)
        renameButton = view.findViewById(R.id.current_config_rename_button)
        deleteButton = view.findViewById(R.id.current_config_delete_button)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this)[ConfigurationsViewModel::class.java]
        setUpRecyclerViews()
        addConfigButton.setOnClickListener { v: View? -> mViewModel.addConfig() }
        renameButton.setOnClickListener { v: View? -> showRenameDialog() }
        deleteButton.setOnClickListener { v: View? -> showDeleteDialog() }
        lastOpenedMoreButton.setOnClickListener { v: View? ->
            if (lastOpenedRV.visibility == View.GONE) {
                lastOpenedRV.visibility = View.VISIBLE
                lastOpenedMoreButton.setImageResource(R.drawable.ic_expand_less_white_24dp)
            } else {
                lastOpenedRV.visibility = View.GONE
                lastOpenedMoreButton.setImageResource(R.drawable.ic_expand_more_white_24dp)
            }
        }
        mViewModel.getLastOpenedConfigNames().observe(
            getViewLifecycleOwner(),
            Observer { configNames: List<String> -> lastOpenedAdapter.setConfigs(configNames) }
        )
        mViewModel.configTitle
            .observe(getViewLifecycleOwner(), Observer { configTitle: String? ->
                if (configTitle == null) {
                    titleText.setText(R.string.no_config)
                } else {
                    titleText.text = configTitle
                }
            })
    }

    private fun setUpRecyclerViews() {
        lastOpenedRV.setLayoutManager(CustomLinearLayoutManager(context))
        lastOpenedRV.setItemAnimator(DefaultItemAnimator())
        lastOpenedAdapter = ConfigListAdapter()
        lastOpenedRV.setAdapter(lastOpenedAdapter)
        lastOpenedRV.addOnItemTouchListener(
            CustomRVItemTouchListener(
                context, lastOpenedRV,
                object : RecyclerViewItemClickListener {
                    override fun onClick(parent: RecyclerView, view: View, position: Int) {
                        openConfig(
                            parent,
                            position
                        )
                    }
                }
            )
        )
    }

    private fun openConfig(parent: RecyclerView, position: Int) {
        val configName: String = lastOpenedAdapter.configNameList[position]
        mViewModel.chooseConfig(configName)
    }

    private fun showRenameDialog() {
        if (context == null) return

        // Set up the input
        val input: EditText = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val dialog: AlertDialog = AlertDialog.Builder(
            requireContext()
        )
            .setTitle(R.string.rename_config)
            .setView(input)
            .setPositiveButton(
                R.string.ok
            ) { view: DialogInterface?, _: Int ->
                mViewModel.renameConfig(input.text.toString())
            }
            .setNegativeButton(
                R.string.cancel
            ) { view: DialogInterface, _: Int -> view.cancel() }
            .create()
        dialog.show()
    }

    private fun showDeleteDialog() {
        if (context == null) return
        val dialog: AlertDialog = AlertDialog.Builder(
            requireContext()
        )
            .setTitle("Remove config")
            .setMessage(R.string.really_delete)
            .setPositiveButton(
                R.string.yes
            ) { view: DialogInterface?, _: Int -> mViewModel.deleteConfig() }
            .setNegativeButton(
                R.string.no
            ) { view: DialogInterface, _: Int -> view.cancel() }
            .create()
        val color: Int = resources.getColor(R.color.delete_red)
        dialog.setOnShowListener {
            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setTextColor(color)
        }
        dialog.show()
    }
}
