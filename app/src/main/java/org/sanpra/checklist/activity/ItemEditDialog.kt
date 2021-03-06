/*
 * Copyright (C) 2011-2018 Sandeep Raghuraman (sandy.8925@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sanpra.checklist.activity

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import org.apache.commons.lang3.StringUtils
import org.sanpra.checklist.R
import org.sanpra.checklist.dbhelper.ItemsDatabase
import org.sanpra.checklist.dbhelper.ItemsDbThreadHelper

class ItemEditDialog : DialogFragment(), Observer<ChecklistItem> {
    override fun onChanged(result: ChecklistItem?) {
        result?: return
        checklistItem = result
        editText?.setText(checklistItem.description)
    }

    private val okClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        checklistItem.description = StringUtils.defaultString(editText?.text.toString())
        ItemsDbThreadHelper.dbOpsHandler.post { itemsDao.updateItem(checklistItem) }
    }

    companion object {
        const val EXTRA_KEY_ITEM_ID = "item_id"
        const val TAG : String = "ItemEditDialog"
        fun getArgs(itemId : Long) : Bundle {
            val args = Bundle()
            args.putLong(EXTRA_KEY_ITEM_ID, itemId)
            return args
        }
    }

    private val UNINIT_ITEM_ID : Long = -23
    private var itemId : Long = UNINIT_ITEM_ID
    private lateinit var checklistItem: ChecklistItem
    private var editText: EditText? = null
    private lateinit var itemsDao : ItemsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        args ?: return
        itemId = args.getLong(EXTRA_KEY_ITEM_ID, UNINIT_ITEM_ID)
        itemsDao = ItemsDatabase.getInstance(requireContext()).itemsDao()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.EditDialogTheme)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, null)
        val rootView = requireActivity().layoutInflater.inflate(R.layout.item_edit_dialog_layout, null)
        editText = rootView.findViewById(R.id.iedl_text) as EditText?
        builder.setView(rootView)
        itemsDao.fetchItem(itemId).observe(this, this)
        return builder.create()
    }
}

