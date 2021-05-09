package e.momo.pedometer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

var click = 0
class PedometerConfiguration : AppCompatActivity() {

    val routename = arrayOf("サンプルルート動作中","四国八十八ヶ所霊場巡礼中","知多四国八十八ヶ所霊場巡礼中")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer_configuration)

        val btShikokuRoute = findViewById<Button>(R.id.btShikokuRoute)
        val btChitashikokuroute = findViewById<Button>(R.id.btChitashikokuRoute)
        //val btSaigokuroute = findViewById<Button>(R.id.btSaigokuRoute)
        val btReset = findViewById<Button>(R.id.btReset)
        val listener = ClickListener()
        btShikokuRoute.setOnClickListener(listener)
        btChitashikokuroute.setOnClickListener(listener)
        //btSaigokuroute.setOnClickListener(listener)
        btReset.setOnClickListener(listener)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private inner class ClickListener : View.OnClickListener{
        override fun onClick(view: View){
            val intent = Intent(applicationContext, MainActivity::class.java)
            var route = ""
            var resetflag = false
            var routesetting = false
            var returnflag = false

            when(view.id){
                R.id.btShikokuRoute -> {
                    route = routename[1]
                    routesetting = true
                }
                R.id.btChitashikokuRoute -> {
                    route = routename[2]
                    routesetting = true
                }
                R.id.btReset-> {
                    resetflag = true
                }
            }

            if (routesetting){
                AlertDialog.Builder(this@PedometerConfiguration) // FragmentではActivityを取得して生成
                    .setTitle("ルートの変更")
                    .setMessage("これまでの歩数を記録しルートを変更しますか？")
                    .setPositiveButton("OK") { dialog, which ->
                        intent.putExtra("route", route)
                        finish()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        // Noが押された場合は何も行わない
                    }
                    .show()
                //intent.putExtra("route", route)
                //returnflag = true
            }
            if (resetflag) {
                AlertDialog.Builder(this@PedometerConfiguration) // FragmentではActivityを取得して生成
                    .setTitle("記録のリセット")
                    .setMessage("本当に全ての記録を消去しますか？")
                    .setPositiveButton("OK") { dialog, which ->
                        intent.putExtra("reset", "reset")
                        returnflag = true
                        finish()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        // Noが押された場合は何も行わない
                        Toast.makeText(applicationContext, "No", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
            setResult(Activity.RESULT_OK, intent)
            if (returnflag){
                finish()
            }
        }
    }
}
