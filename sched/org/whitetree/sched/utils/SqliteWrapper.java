/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.whitetree.sched.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;


public final class SqliteWrapper {
    private static final String TAG = "SqliteWrapper";
    private static final String SQLITE_EXCEPTION_DETAIL_MESSAGE
                = "unable to open database file";

    private SqliteWrapper() {
        // Forbidden being instantiated.
    }

    // FIXME: need to optimize this method.
    private static boolean isLowMemory(SQLiteException e) {
        return e.getMessage().equals(SQLITE_EXCEPTION_DETAIL_MESSAGE);
    }

    public static void checkSQLiteException(Context context, SQLiteException e) {
        if (isLowMemory(e)) {
//            Toast.makeText(context, com.android.internal.R.string.low_memory,
//                    Toast.LENGTH_SHORT).show();
        } else {
//            throw e;
        }
    }

    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        try {
            return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (SQLiteException e) {
            Log.e("TAG", "Catch a SQLiteException when query: ");
            checkSQLiteException(context, e);
            return null;
        }
    }

    public static boolean requery(Context context, Cursor cursor) {
        try {
            return cursor.requery();
        } catch (SQLiteException e) {
            Log.e("TAG", "Catch a SQLiteException when requery: ");
            checkSQLiteException(context, e);
            return false;
        }
    }
    public static int update(Context context, Uri uri, ContentValues values,
            String where, String[] selectionArgs) {
        try {
            return context.getContentResolver().update(uri, values, where, selectionArgs);
        } catch (SQLiteException e) {
            Log.e("TAG","Catch a SQLiteException when update: ");
            checkSQLiteException(context, e);
            return -1;
        }
    }

    public static int delete(Context context, Uri uri, String where,
            String[] selectionArgs) {
        try {
            return context.getContentResolver().delete(uri, where, selectionArgs);
        } catch (SQLiteException e) {
            Log.e("TAG", "Catch a SQLiteException when delete: ");
            checkSQLiteException(context, e);
            return -1;
        }
    }

    public static Uri insert(Context context, Uri uri,
            ContentValues values) {
        try {
            return context.getContentResolver().insert(uri, values);
        } catch (SQLiteException e) {
            Log.e("TAG", "Catch a SQLiteException when insert: ");
            checkSQLiteException(context, e);
            return null;
        }
    }
}
