package com.example.nuber

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class NuberPagerAdapter (fm: FragmentManager): FragmentPagerAdapter(fm){
    override fun getItem(p0: Int): Fragment {
        return when(p0){
            0 -> NUberMapsActivity()
            1 -> NuberShoppingFragment()
            else -> NuberProductsFragment()
        }
    }

    override fun getPageTitle(p0: Int): CharSequence? {
        return when(p0){
            0 -> "MAPA Nuber"
            1 -> "Shopping"
            else -> "My Shopping Cart"
        }
    }

    override fun getCount(): Int {
       return 3
    }
}