/*
 * Copyright (C) 2011-2015 Sandeep Raghuraman (sandy.8925@gmail.com)

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

package org.sanpra.checklist.activity;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import org.sanpra.checklist.dbhelper.ItemsDatabase;
import org.sanpra.checklist.dbhelper.ItemsDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChecklistItemsCursorLoader extends AsyncTaskLoader<List<ChecklistItem>> {

    @NonNull private final ItemsDbHelper itemsDbHelper;
    @NonNull private final ItemsDao itemsDao;

    ChecklistItemsCursorLoader(@NonNull final Context context) {
        super(context);
        final Context appContext = context.getApplicationContext();
        itemsDbHelper = ItemsDbHelper.getInstance(appContext);
        itemsDao = ItemsDatabase.getInstance(appContext).itemsDao();
    }

    @Override
    public List<ChecklistItem> loadInBackground() {
        return itemsDao.fetchAllItems();
    }

    private static ChecklistItem createItemFromCursor(@NonNull Cursor cursor) {
        ChecklistItem item = new ChecklistItem();
        item.id = ItemsDbHelper.getItemId(cursor);
        item.description = ItemsDbHelper.getItemDesc(cursor);
        item.checked = ItemsDbHelper.isItemChecked(cursor);
        return item;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
