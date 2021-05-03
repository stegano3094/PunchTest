package com.stegano.punch

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    var maxPower = 0.0  // 측정된 최대 펀치력
    var isStart = false  // 측정 시작
    var startTime = 0L  // 측정 시작 시간
    val sensorManager: SensorManager by lazy {  // 센서 매니저
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val eventListener: SensorEventListener = object: SensorEventListener {  // 센서 이벤트 리스너
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) {
                    return@let
                }

                val power = event.values[0].toDouble().pow(2.0) +
                        event.values[1].toDouble().pow(2.0) +
                        event.values[2].toDouble().pow(2.0)

                if(power > 500 && !isStart) {
                    startTime = System.currentTimeMillis()
                    isStart = true
                }
                if(isStart) {
                    imageView.clearAnimation()  // 측정이 시작되면 애니메이션 제거
                    stateLabel.text = "3초간 측정 중입니다."
                    if(maxPower < power) {
                        maxPower = power
                    }
                    if(System.currentTimeMillis() - startTime > 3000) {  // 3초간 측정
                        isStart = false
                        punchPowerTestComplete(maxPower)
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initGame()
    }

    fun initGame() {
        maxPower = 0.0
        isStart = false
        startTime = 0L
        stateLabel.text = "핸드폰을 손에 쥐고 주먹을 내지르세요."
        sensorManager.registerListener(
                eventListener,
                // 중력을 제외한 x,y,z축의 가속도를 측정, Sensor.TYPE_AMBIENT_TEMPERATURE 등 다양하게 바꿈으로써 센서타입 변경 가능
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL
        )

        val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.tran)
        imageView.startAnimation(animation)
        animation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
                // 애니메이션이 시작될 때
            }

            override fun onAnimationEnd(animation: Animation?) {
                // 애니메이션이 끝날 때
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션이 반복 중일 때
            }
        })
    }

    fun punchPowerTestComplete(power: Double) {
        Log.e("MainActivity", "punchPowerTestComplete: power : " + power.toString())
        sensorManager.unregisterListener(eventListener)
        val intent = Intent(this@MainActivity, ResultActivity::class.java)
        intent.putExtra("power", power)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        try {
            sensorManager.unregisterListener(eventListener)
        } catch (e: Exception) {
        }
    }
}