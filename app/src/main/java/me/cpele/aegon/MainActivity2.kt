package me.cpele.aegon

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import me.cpele.countdown.TimerFragment

class MainActivity2 : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main2)

        val countdownFragment: TimerFragment? = supportFragmentManager.findFragmentById(R.id.main2_fr_countdown) as TimerFragment

        countdownFragment?.setOnEndListener {
            AlertDialog.Builder(this).setMessage("Done!").show()
        }
    }
}