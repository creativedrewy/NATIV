package com.creativedrewy.nativ.viewstate

import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewStateCache @Inject constructor() {

    val refItem = "I $this"
    var setThis = ""
}
