package com.example.bookworm.bottomMenu.profile.Album.view

import android.content.Context
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.selection.SelectionTracker

open class ActionModeController(val context: Context, val selectionTracker: SelectionTracker<Long>) :
    ActionMode.Callback {

    override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return false
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
        return false
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        selectionTracker.clearSelection()
    }
}