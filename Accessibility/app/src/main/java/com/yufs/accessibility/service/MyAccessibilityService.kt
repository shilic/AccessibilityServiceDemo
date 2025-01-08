package com.yufs.accessibility.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.yufs.accessibility.utils.AsUtils
import com.yufs.accessibility.utils.LogUtils
import kotlin.concurrent.thread

/**
 *Author: YFS(893145181@qq.com)
 *Time:2022/8/16 10:42
 */
class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        //获取指定包名应用
        val packageName = event.packageName
        //只使用界面变化的监听，避免点击事件监听进入死循环
        if(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED != event.eventType){
            return
        }
        if ("com.yufs.accessibility" == packageName) {
            LogUtils.e("Thread:${Thread.currentThread().name},event:${event}")
            //找到对应node，开始点击
            val nodeInfo = AsUtils.findNodeInfo(
                this,
                "com.yufs.accessibility:id/btn_click_node",
                "节点模拟点击",
                ""
            )
            nodeInfo?.let {
                thread {
                    LogUtils.e("找到节点，三秒后执行点击事件")
                    Thread.sleep(3000)
                    AsUtils.performClickNodeInfo(it)
                }
            }


        }
    }

    override fun onInterrupt() {
        LogUtils.e("onInterrupt==>")
    }

    /**
     * 服务连接成功
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        thread {
            //便于设置完成后返回来看到显示效果
            Thread.sleep(5000)
            LogUtils.e("坐标点击：500,515")
            AsUtils.click(this, 507f, 512f)
        }
    }
}