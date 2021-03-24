package com.example.noted

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.noted.Fragment.HomeFragment
import com.example.noted.databinding.ActivityMainBinding
import com.example.noted.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var binding1: AppBarMainBinding
    private lateinit var includedView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //binding1 = AppBarMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        //includedView = binding.appbar

        setSupportActionBar(binding.appbar.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appbar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        displayScreen(-1)
    }

    private fun displayScreen(i: Int) {
        val fragment = when (i) {

            R.id.nav_home -> {
                HomeFragment()
            }
            R.id.nav_favourites -> {
                HomeFragment()

            }
            R.id.nav_settings -> {
                HomeFragment()

            }
            else -> {
                HomeFragment()
            }
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.relativeLayout, fragment)
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displayScreen(item.itemId)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
