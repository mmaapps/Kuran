package com.atasoyyazilim.kurankerim.vakit

import android.content.Context
import android.hardware.*
import android.view.Surface
import android.view.WindowManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlin.math.abs

data class PusulaOlcumu(val manyetikYon:Double?,val guvenilir:Boolean,val duz:Boolean,val sensorVar:Boolean)

object KibleSensoru {
    @Suppress("DEPRECATION")
    fun izle(c:Context)=callbackFlow<PusulaOlcumu>{
        val sm=c.getSystemService(SensorManager::class.java)
        val rv=sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val acc=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mag=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if(rv==null&&(acc==null||mag==null)){trySend(PusulaOlcumu(null,false,false,false));close();return@callbackFlow}
        var a:FloatArray?=null
        var m:FloatArray?=null
        var accuracy=SensorManager.SENSOR_STATUS_UNRELIABLE
        val matrix=FloatArray(9)
        val displayMatrix=FloatArray(9)
        val angles=FloatArray(3)
        val listener=object:SensorEventListener{
            override fun onAccuracyChanged(s:Sensor?,status:Int){
                if(s?.type==Sensor.TYPE_ROTATION_VECTOR||s?.type==Sensor.TYPE_MAGNETIC_FIELD){
                    accuracy=status
                    if(status<SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)trySend(PusulaOlcumu(null,false,false,true))
                }
            }
            override fun onSensorChanged(e:SensorEvent){
                when(e.sensor.type){
                    Sensor.TYPE_ROTATION_VECTOR->{SensorManager.getRotationMatrixFromVector(matrix,e.values);accuracy=e.accuracy}
                    Sensor.TYPE_ACCELEROMETER->{a=e.values.copyOf()}
                    Sensor.TYPE_MAGNETIC_FIELD->{m=e.values.copyOf();accuracy=e.accuracy}
                    else->return
                }
                if(rv==null){
                    val x=a?:return;val y=m?:return
                    if(!SensorManager.getRotationMatrix(matrix,null,x,y))return
                }
                val rotation=c.getSystemService(WindowManager::class.java).defaultDisplay.rotation
                val axes=when(rotation){
                    Surface.ROTATION_90->SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180->SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270->SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else->SensorManager.AXIS_X to SensorManager.AXIS_Y
                }
                if(!SensorManager.remapCoordinateSystem(matrix,axes.first,axes.second,displayMatrix))return
                SensorManager.getOrientation(displayMatrix,angles)
                val heading=Math.toDegrees(angles[0].toDouble())
                val pitch=abs(Math.toDegrees(angles[1].toDouble()))
                val roll=abs(Math.toDegrees(angles[2].toDouble()))
                if(heading.isFinite())trySend(PusulaOlcumu(KibleYon.normalize(heading),accuracy>=SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM,pitch<=25&&roll<=25,true))
            }
        }
        val registered=if(rv!=null)sm.registerListener(listener,rv,SensorManager.SENSOR_DELAY_UI)
            else sm.registerListener(listener,acc!!,SensorManager.SENSOR_DELAY_UI)&&sm.registerListener(listener,mag!!,SensorManager.SENSOR_DELAY_UI)
        if(!registered){sm.unregisterListener(listener);trySend(PusulaOlcumu(null,false,false,false));close();return@callbackFlow}
        awaitClose{sm.unregisterListener(listener)}
    }.conflate()
}
