package com.ihdyo.postit.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.ihdyo.postit.widget.StackRemoteViewsFactory

class StackWidgetService: RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext)
}