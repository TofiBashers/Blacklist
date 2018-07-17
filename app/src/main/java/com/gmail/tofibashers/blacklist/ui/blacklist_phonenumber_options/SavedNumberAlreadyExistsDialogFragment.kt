package com.gmail.tofibashers.blacklist.ui.blacklist_phonenumber_options

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.gmail.tofibashers.blacklist.R


/**
 * Created by TofiBashers on 16.03.2018.
 */
class SavedNumberAlreadyExistsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(this.activity ?: this.context!!)
                .setMessage(getString(R.string.dialog_error_saved_number_exists_now))
                .setNegativeButton(android.R.string.ok, null)
                .create()
    }
}