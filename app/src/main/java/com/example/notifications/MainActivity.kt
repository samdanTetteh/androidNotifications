package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationCompatBuilder: NotificationCompat.Builder

    val ACTION_UPDATE_NOTIFICATION = "com.example.notifications.ACTION_UPDATE_NOTIFICATION"
    val ACTION_DELETE_NOTIFICATION = "com.example.notifications.ACTION_DELETE_NOTIFICATION"
    val notification_Id = 1
    val notificationReciever = NotificationReceiver()
    val notificationDeleteReciever = NotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(notificationReciever, IntentFilter(ACTION_UPDATE_NOTIFICATION))
        registerReceiver(notificationDeleteReciever, IntentFilter(ACTION_DELETE_NOTIFICATION))
        setNotificationsState(true, false, false)
    }

    override fun onDestroy() {
        unregisterReceiver(notificationReciever)
        unregisterReceiver(notificationDeleteReciever)
        super.onDestroy()
    }


    fun getNotificationBuilder(): NotificationCompat.Builder {
        return with(NotificationCompat.Builder(this, "101")) {
            setSmallIcon(android.R.drawable.ic_notification_clear_all)
            setContentTitle(getString(R.string.notification_title))
            setContentText(getString(R.string.notification_content_txt))
            setStyle(
                NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_big_txt))
            )
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setDefaults(NotificationCompat.DEFAULT_ALL)
        }

    }


        fun showNotification(view: View) {
            val updateIntent  = Intent(ACTION_UPDATE_NOTIFICATION)
            val updatePendingIntent = PendingIntent.getBroadcast(this, notification_Id, updateIntent, PendingIntent.FLAG_ONE_SHOT)


            val deleteIntent = Intent(ACTION_DELETE_NOTIFICATION)
            val pendingDeleteIntent = PendingIntent.getBroadcast(this, notification_Id, deleteIntent, PendingIntent.FLAG_ONE_SHOT)

            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.notification_channel_name)
                val descriptionTxt = getString(R.string.notifiation_description_txt)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("101", name, importance).apply {
                    description = descriptionTxt
                }

                notificationManager.createNotificationChannel(channel)
            }



                val intent = Intent(this, AnotherActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, notification_Id, intent, PendingIntent.FLAG_UPDATE_CURRENT)


            notificationCompatBuilder = getNotificationBuilder()
            with(notificationCompatBuilder){
                addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
                notificationCompatBuilder.setDeleteIntent(pendingDeleteIntent)
                setContentIntent(pendingIntent)
                setAutoCancel(true)
            }



                with(NotificationManagerCompat.from(this)) {
                    notify(notification_Id, notificationCompatBuilder.build())
                }


            setNotificationsState(false, true, true);


            }





        fun updateNotification(view: View) {
            val notificationImage  = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
            notificationCompatBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(notificationImage).setBigContentTitle(getString(
                            R.string.notificaton_updated_txt)))


            notificationManager.notify(notification_Id, notificationCompatBuilder.build())

            setNotificationsState(false, false, true);

        }

        fun cancelNotification(view: View) {
            notificationManager.cancel(notification_Id)

            setNotificationsState(true, false, false)
        }



    fun setNotificationsState(isNotifyEnabled : Boolean, isUpdateEnabled : Boolean, isCancelEnabled : Boolean ){
        notify_btn.isEnabled = isNotifyEnabled
        update_btn.isEnabled = isUpdateEnabled
        cancel_btn.isEnabled = isCancelEnabled

    }



    inner class NotificationReceiver: BroadcastReceiver() {


        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals(ACTION_UPDATE_NOTIFICATION)){
                updateNotification(update_btn as View)
            }else{
                setNotificationsState(true, false, false)
            }

        }

    }

}
