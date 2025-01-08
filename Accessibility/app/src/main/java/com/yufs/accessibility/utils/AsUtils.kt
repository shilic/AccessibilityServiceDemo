package com.yufs.accessibility.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi


/**
 *Author: YFS(893145181@qq.com)
 *Time:2022/8/16 11:01
 */
object AsUtils {
    /**
     * 跳转开启服务
     */
    fun jumpToAccessibilitySetting(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    /**
     * 坐标模拟点击:最低api24，即要求Android7.0以上
     */
    fun click(accessibilityService: AccessibilityService, x: Float, y: Float) {
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x, y)
        builder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        val gesture = builder.build()
        accessibilityService.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                }

                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                }
            },
            null
        )
    }

    fun isServiceON(context: Context, className: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(100)
        if (runningServices.size < 0) {
            return false
        }
        for (i in runningServices.indices) {
            val service = runningServices[i].service
            if (service.className.contains(className)) {
                return true
            }
        }
        return false
    }

    /**
     * 唤醒屏幕
     */

    private fun wakeUpScreed(context: Context, tag: String) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
                tag
            )

            wl.acquire(60*1000L)
            wl.release()
        }
    }



    /**
     * 滑动操作
     *
     * @param service   无障碍服务实例
     * @param path      移动路径
     * @param startTime 延时启动时间
     * @param duration  执行持续时间
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun move(
        service: AccessibilityService,
        path: Path,
        @IntRange(from = 0) startTime: Long,
        @IntRange(from = 0) duration: Long,
        callback: Callback?
    ) {
        if (callback == null) {
            service.dispatchGesture(
                GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, startTime, duration)).build(),
                null,
                null
            )
        } else {
            service.dispatchGesture(
                GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, startTime, duration)).build(),
                object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription) {
                        super.onCompleted(gestureDescription)
                        callback.onSuccess()
                    }

                    override fun onCancelled(gestureDescription: GestureDescription) {
                        super.onCancelled(gestureDescription)
                        callback.onError()
                    }
                },
                null
            )
        }
    }

    /**
     * 查找节点信息
     *
     * @param id
     * @param text
     * @param contentDescription
     * @return null表示未找到
     */
    fun findNodeInfo(
        service: AccessibilityService,
        id: String,
        text: String,
        contentDescription: String
    ): AccessibilityNodeInfo? {
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(contentDescription)) {
            return null
        }
        val nodeInfo = service.rootInActiveWindow
        if (nodeInfo != null) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(id)
            for (n in list) {
                val nodeInfoText =
                    if (TextUtils.isEmpty(n.text)) "" else n.text
                        .toString()
                val nodeContentDescription =
                    if (TextUtils.isEmpty(n.contentDescription)) "" else n.contentDescription
                        .toString()
                if (TextUtils.isEmpty(text)) {
                    if (contentDescription == nodeContentDescription) {
                        return n
                    }
                } else {
                    if (text == nodeInfoText) {
                        return n
                    }
                }
            }
        }
        return null
    }

    /**
     * 点击节点
     * @return true表示点击成功
     */
    fun performClickNodeInfo(nodeInfo: AccessibilityNodeInfo?): Boolean {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            } else {
                val parent = nodeInfo.parent
                if (parent != null) {
                    val isParentClickSuccess = performClickNodeInfo(parent)
                    parent.recycle()
                    return isParentClickSuccess
                }
            }
        }
        return false
    }

    /**
     * 查找并点击节点
     */
    fun findAndPerformClickNodeInfo(
        service: AccessibilityService,
        id: String,
        text: String,
        contentDescription: String
    ): Boolean {
        return performClickNodeInfo(findNodeInfo(service, id, text, contentDescription))
    }

    /**
     * 通过文本查找节点（节点无id值时使用）
     */
    fun findNodeInfoByText(
        service: AccessibilityService,
        text: String?,
        desc: String?,
        className: String
    ): AccessibilityNodeInfo? {
        val nodeInfo = service.rootInActiveWindow
        val list = nodeInfo.findAccessibilityNodeInfosByText(text ?: desc)
        for (n in list) {
            if (n.className.equals(className)) {
                if (desc != null) {
                    if (desc == n.contentDescription.toString()) {
                        val bounds = Rect()
                        n.getBoundsInScreen(bounds)
                        return n
                    }
                } else {
                    val bounds = Rect()
                    n.getBoundsInScreen(bounds)
                    return n
                }
            }
        }
        return null
    }

    interface Callback {
        fun onSuccess()
        fun onError()
    }

}