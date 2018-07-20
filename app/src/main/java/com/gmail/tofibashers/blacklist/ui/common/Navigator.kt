package com.gmail.tofibashers.blacklist.ui.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import com.gmail.tofibashers.blacklist.ui.select_contact.SelectContactActivity
import javax.inject.Inject
import javax.inject.Singleton
import com.gmail.tofibashers.blacklist.ui.blacklist_contact_options.BlacklistContactOptionsActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.BlacklistPhonenumberOptionsActivity
import com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options.SavedNumberAlreadyExistsDialogFragment
import com.gmail.tofibashers.blacklist.ui.time_settings.TimeSettingsActivity
import com.gmail.tofibashers.blacklist.utils.AndroidComponentKeys


/**
 * Created by TofiBashers on 17.07.2018.
 */
@Singleton
class Navigator
@Inject
constructor(){

    fun toSavedNumberAlreadyExists(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
                .add(SavedNumberAlreadyExistsDialogFragment(), AndroidComponentKeys.TAG_SAVED_NUMBER_ALREADY_EXISTS_DIALOGFRAGMENT)
                .commit()
    }

    fun toTimeSettings(activity: Activity) = toTimeSettingsWithResult(activity, -1)

    fun toTimeSettingsWithResult(activity: Activity, requestCode: Int) {
        val intent = Intent(activity, TimeSettingsActivity::class.java)
        activity.startActivityForResult(intent, requestCode)
    }

    fun toBlacklistPhoneOptionsWithResult(activity: Activity, requestCode: Int) {
        val optIntent = Intent(activity, BlacklistPhonenumberOptionsActivity::class.java)
        activity.startActivityForResult(optIntent, requestCode)
    }

    fun toBlacklistContactOptionsWithResult(activity: Activity, requestCode: Int) {
        val optIntent = Intent(activity, BlacklistContactOptionsActivity::class.java)
        activity.startActivityForResult(optIntent, requestCode)
    }

    fun toSelectContactWithResult(activity: Activity, requestCode: Int) {
        val optIntent = Intent(activity, SelectContactActivity::class.java)
        activity.startActivityForResult(optIntent, requestCode)
    }

    fun toParent(activity: Activity) = activity.finish()

    fun toParentWithResult(activity: Activity, resultCode: Int) {
        activity.setResult(resultCode)
        activity.finish()
    }

    @Throws(ThirdPartyAppForNavigationNotFoundException::class)
    fun toEditContact(activity: Activity, contactId: Long, contactKey: String) {
        val uri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
        val editIntent = Intent(Intent.ACTION_EDIT)
        editIntent.setDataAndType(uri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        editIntent.putExtra(SelectContactActivity.FINISH_ACTIVITY_ON_SAVE_COMPLETED_FLAG, true)
        startActivityInThirdPartyApp(activity, editIntent)
    }

    @Throws(ThirdPartyAppForNavigationNotFoundException::class)
    fun toDialingWithNumber(activity: Activity, number: String) {
        val uri = Uri.parse("tel: $number")
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = uri
        startActivityInThirdPartyApp(activity, dialIntent)
    }

    @Throws(ThirdPartyAppForNavigationNotFoundException::class)
    fun toCreateSmsWithNumber(activity: Activity, number: String) {
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.type = AndroidComponentKeys.TYPE_MMS_SMS
        smsIntent.putExtra(AndroidComponentKeys.EXTRA_ADDRESS, number)
        activity.startActivity(smsIntent)
        startActivityInThirdPartyApp(activity, smsIntent)
    }

    @Throws(ThirdPartyAppForNavigationNotFoundException::class)
    private fun startActivityInThirdPartyApp(activity: Activity, intent: Intent) {
        try {
            activity.startActivity(intent)
        }
        catch (e: ActivityNotFoundException){
            throw ThirdPartyAppForNavigationNotFoundException()
        }
    }
}