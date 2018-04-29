package com.gmail.tofibashers.blacklist.ui.select_contact

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.gmail.tofibashers.blacklist.R
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.common.BaseStateableViewActivity
import kotterknife.bindView
import javax.inject.Inject

class SelectContactActivity : BaseStateableViewActivity<View, RecyclerView>() {

    override val loadingView: View by bindView(R.id.progressbar_view)
    override val dataView: RecyclerView by bindView(R.id.recycler_contacts)
    private val toolbar: Toolbar by bindView(R.id.toolbar)

    @Inject
    lateinit var selectContactAdapter: SelectContactAdaper

    @Inject
    lateinit var viewModel: SelectContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, layoutManager.orientation)
        @Suppress("DEPRECATION")
        dividerDecoration.setDrawable(resources.getDrawable(R.drawable.divider_select_contact))
        dataView.layoutManager = layoutManager
        dataView.addItemDecoration(dividerDecoration)
        dataView.adapter = selectContactAdapter

        if(savedInstanceState == null) viewModel.onInitGetList()

        viewModel.navigateSingleData.observe(this, Observer {
            when(it){
                is SelectContactNavData.EditContactRoute -> startEditContactView(it)
                is SelectContactNavData.BlacklistContactOptionsRoute -> startBlacklistContactOptionsViewWithFinish()
                is SelectContactNavData.ParentRoute -> finish()
            }
        })

        viewModel.viewStateData.observe(this, Observer {
            when(it){
                is SelectContactViewState.LoadingViewState -> showDefaultLoading()
                is SelectContactViewState.DataViewState -> showData(it)
            }
        })
    }

    override fun onBackPressed() {
        viewModel.onInitCancel()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp() : Boolean {
        onBackPressed()
        return true
    }

    private fun showData(dataViewState: SelectContactViewState.DataViewState){
        selectContactAdapter.set(dataViewState.state)
    }

    private fun startEditContactView(editRoute: SelectContactNavData.EditContactRoute){
        val editIntent = Intent(Intent.ACTION_EDIT)
        val uri = ContactsContract.Contacts.getLookupUri(editRoute.contactId, editRoute.contactKey)
        intent.setDataAndType(uri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        editIntent.putExtra(FINISH_ACTIVITY_ON_SAVE_COMPLETED_FLAG, true)
        startActivity(editIntent)
    }

    private fun startBlacklistContactOptionsViewWithFinish(){
        val editIntent = Intent(this, BlacklistContactOptionsActivity::class.java)
        startActivity(editIntent)
        finish()
    }

    val contactClickListener = object : SelectContactViewHolder.ClickListener{

        override fun onSelectClick(position: Int) = viewModel.onInitSelectContact(position)

        override fun onChangeClick(position: Int) = viewModel.onInitChangeContact(position)

    }

    companion object {

        const val FINISH_ACTIVITY_ON_SAVE_COMPLETED_FLAG = "finishActivityOnSaveCompleted"
    }
}