package com.movie_explorer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.movie_explorer.R
import com.movie_explorer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var vBinding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vBinding.root)
        navController = findNavController(R.id.navHostFragment)

        vBinding.bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.movieFragment, R.id.favoriteFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}