package com.getevent.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.getevent.mobile.app.navigation.AppNavGraph
import com.getevent.mobile.app.ui.theme.GetEventTheme
import com.getevent.mobile.app.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        setContent {
            GetEventTheme {
                Surface {
                    AppNavGraph()
                }
            }
        }

    }
}
