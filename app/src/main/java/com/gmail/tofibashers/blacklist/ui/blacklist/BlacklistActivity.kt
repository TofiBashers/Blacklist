package com.gmail.tofibashers.blacklist.ui.blacklist


import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.*
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.entity.GetBlacklistResult
import com.gmail.tofibashers.blacklist.entity.SystemVerWarningType
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import com.gmail.tofibashers.blacklist.ui.options.OptionsActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys
import kotterknife.bindView
import javax.inject.Inject

class BlacklistActivity : BaseStateableViewActivity<Group, Group>(),
        View.OnClickListener {

    override val loadingView: Group by bindView(R.id.group_progress)
    override val dataView: Group by bindView(R.id.group_numlist_with_settings)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val cardWrapperAddNumWithSettings: CardView by bindView(R.id.cardview_add_num_with_settings)
    private val addButton: Button by bindView(R.id.button_add)
    private val ignoreHiddenCheckBox: AppCompatCheckBox by bindView(R.id.checkbox_ignore_hidden_numbers)
    private val list: RecyclerView by bindView(R.id.num_list)

    @Inject
    lateinit var viewModel: BlacklistViewModel

    @Inject
    lateinit var blacklistAdapter: BlacklistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist)
        setSupportActionBar(toolbar)

        cardWrapperAddNumWithSettings.post {
            var layoutParams = cardWrapperAddNumWithSettings.layoutParams as RelativeLayout.LayoutParams
            layoutParams.leftMargin -= cardWrapperAddNumWithSettings.paddingLeft - cardWrapperAddNumWithSettings.contentPaddingLeft
            layoutParams.rightMargin -= cardWrapperAddNumWithSettings.paddingRight - cardWrapperAddNumWithSettings.contentPaddingRight
            cardWrapperAddNumWithSettings.requestLayout()
        }

        addButton.setOnClickListener(this)
        ignoreHiddenCheckBox.setOnCheckedChangeListener(ignoreHiddenChangeListener)

        val layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, layoutManager.orientation)
        @Suppress("DEPRECATION")
        dividerDecoration.setDrawable(resources.getDrawable(R.drawable.divider_blacklist))
        list.layoutManager = layoutManager
        list.addItemDecoration(dividerDecoration)
        list.adapter = blacklistAdapter

        if (savedInstanceState == null) viewModel.onInitGetList()

        viewModel.viewStateData.observe(this, Observer{
            when(it){
                is BlacklistViewState.ListViewState -> showListWithIgnoreHidden(it)
                is BlacklistViewState.LoadingViewState -> showDefaultLoading()
            }
        })
        viewModel.navigateSingleData.observe(this, Observer {
            when(it){
                BlacklistNavRoute.OPTIONS -> navigateToPhoneOptions()
                BlacklistNavRoute.BLACKLIST_CONTACT_OPTIONS -> navigateToContactOptions()
                BlacklistNavRoute.SELECT_CONTACT -> navigateToSelectContact()
            }
        })
        viewModel.warningMessageData.observe(this, Observer { showWarningDialog(it!!) })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_add -> {
                Log.w(null, "TryToAddNew")
                viewModel.onInitCreateItem()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_CANCELED){
            viewModel.onAdditionOrCreationCancelled()
        }
    }

    private fun showListWithIgnoreHidden(listViewState: BlacklistViewState.ListViewState) {
        val resultList: GetBlacklistResult.ListWithIgnoreResult = listViewState.blackListWithIgnoreHidden
        setViewState(ViewState.DATA)
        ignoreHiddenCheckBox.setOnCheckedChangeListener(null)
        ignoreHiddenCheckBox.isChecked = resultList.ignoreHidden
        ignoreHiddenCheckBox.setOnCheckedChangeListener(ignoreHiddenChangeListener)
        blacklistAdapter.addAll(resultList)
    }

    private fun showWarningDialog(warning: GetBlacklistResult.SystemVerWarning){
        AlertDialog.Builder(this)
                .setMessage(warningTypeToResId(warning.warningType))
                .setNegativeButton(android.R.string.ok, null)
                .create()
                .show()
    }

    private fun navigateToPhoneOptions() {
        val optIntent = Intent(this, OptionsActivity::class.java)
        startActivityForResult(optIntent, AndroidComponentKeys.REQUEST_CODE_OPTIONS_VIEW)
    }

    private fun navigateToContactOptions() {
        val optIntent = Intent(this, BlacklistContactOptionsActivity::class.java)
        startActivityForResult(optIntent, AndroidComponentKeys.REQUEST_CODE_BLACKLIST_CONTACT_OPTIONS_VIEW)
    }

    private fun navigateToSelectContact() {
        val optIntent = Intent(this, SelectContactActivity::class.java)
        startActivityForResult(optIntent, AndroidComponentKeys.REQUEST_CODE_SELECT_CONTACT_VIEW)
    }

    private fun warningTypeToResId(warningType: SystemVerWarningType): Int = when(warningType){
        SystemVerWarningType.MAY_UPDATE_TO_INCOMPATIBLE_VER -> R.string.blacklist_warning_update_to_incompatible_android_version
        SystemVerWarningType.INCOMPATIBLE_VER -> R.string.blacklist_warning_update_to_incompatible_android_version
    }

    val contactClickListener = object : BlacklistContactItemWithIgnoredInfoHolder.ClickListener{

        override fun onChangeClick(position: Int) = viewModel.onInitContactItemChange(position)

        override fun onDeleteClick(position: Int) = viewModel.onInitContactItemDelete(position)
    }

    val phoneNumberClickListener = object : BlacklistPhoneNumberItemHolder.ClickListener{

        override fun onDeleteClick(position: Int) = viewModel.onInitPhoneNumberItemDelete(position)

        override fun onChangeClick(position: Int) = viewModel.onInitPhoneNumberItemChange(position)
    }

    private val ignoreHiddenChangeListener = CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
        if(buttonView.id == ignoreHiddenCheckBox.id){
            viewModel.onIgnoreHiddenStateChanged(isChecked)
        }
    }

    companion object {

        private val LOG_TAG = BlacklistActivity::class.simpleName
        private val BROADCAST_ACTION = "com.example.blacklist"
    }

}
