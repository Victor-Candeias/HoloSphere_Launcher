package com.holosphere.launcher

import android.app.Application

class HoloSphereApplication : Application() {
    val container by lazy { AppContainer(this) }
}
