package com.yufs.accessibility

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yufs.accessibility.app.viewBinding
import com.yufs.accessibility.databinding.ActivityMainBinding
import com.yufs.accessibility.service.MyAccessibilityService
import com.yufs.accessibility.utils.AsUtils
import com.yufs.accessibility.utils.AsUtils.jumpToAccessibilitySetting

class MainActivity : AppCompatActivity() {
    private val mVb by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mVb.btnStart.setOnClickListener {
            //开启辅助权限
            jumpToAccessibilitySetting(this)
        }
        //手动开发者选项显示屏幕坐标：指针位置

        mVb.btnClick.setOnClickListener {
            Toast.makeText(this, "坐标按钮：点击了", Toast.LENGTH_SHORT).show()
        }

        mVb.btnClickNode.setOnClickListener {
            Toast.makeText(this, "节点按钮：点击了", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        mVb.tvStatus.text = "服务是否开启：${
            AsUtils.isServiceON(
                this,
                MyAccessibilityService::class.java.name
            )
        }"
    }
}