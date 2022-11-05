package com.example.ejerciciogps

import androidx.viewbinding.ViewBinding

object Utills {
var binding :ViewBinding?= null
    fun dp(pixeles:Int):Int{
        if (binding == null) return 0
        val scale = binding!!.root.resources.displayMetrics.density
        return (scale * pixeles * 0.5f).toInt()
    }
}