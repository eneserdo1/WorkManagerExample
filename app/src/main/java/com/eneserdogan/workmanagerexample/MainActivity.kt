package com.eneserdogan.workmanagerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data=Data.Builder().putInt("intKey",1).build()

        val constraints=Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED) net bağlı olmalı
            .setRequiresCharging(false)
            .build()
/*
        val myWorkRequest:WorkRequest= OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            //.setInitialDelay(5,TimeUnit.HOURS)//Gecikme için
            //.addTag() //Kendimiz tag atabiliriz
            .build()
        WorkManager.getInstance(this).enqueue(myWorkRequest)

 */
        val myWorkRequest:PeriodicWorkRequest= PeriodicWorkRequestBuilder<RefreshDatabase>(15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id).observe(this,
            Observer {
                if(it.state ==WorkInfo.State.RUNNING){
                    println("running")
                }else if(it.state==WorkInfo.State.FAILED){
                    println("Başarısız")
                }else if (it.state==WorkInfo.State.SUCCEEDED){
                    println("succeed")
                }
            })

       // WorkManager.getInstance(this).cancelAllWork() //Hepsini iptal et

        val oneTimeRequest:OneTimeWorkRequest= OneTimeWorkRequestBuilder<RefreshDatabase>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).beginWith(oneTimeRequest)
            .then(oneTimeRequest)
            .then(oneTimeRequest)
            .enqueue()
    }
}