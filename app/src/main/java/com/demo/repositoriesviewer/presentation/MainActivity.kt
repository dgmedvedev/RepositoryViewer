package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.demo.repositoriesviewer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.startMainActivity()
        bindViewModel()
    }

    private fun bindViewModel() {
        mainViewModel.isToken.observe(this) { isToken ->
            if (isToken) {
                launchFragment(
                    fragmentId = R.id.repositoriesListFragment,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.authFragment, true)
                        .build()
                )
            } else {
                launchFragment(fragmentId = R.id.authFragment)
            }
        }
    }

    private fun launchFragment(fragmentId: Int, navOptions: NavOptions? = null) {
        findNavController(R.id.container).apply {
            navigateUp()
            navigate(resId = fragmentId, navOptions = navOptions, args = null)
        }
    }
}