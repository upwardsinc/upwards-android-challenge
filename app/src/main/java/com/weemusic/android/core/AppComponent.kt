package com.weemusic.android.core

import com.weemusic.android.ui.MainActivity
import dagger.Component

@AppScope
@Component(dependencies = [DomainComponent::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}