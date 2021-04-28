package e.momo.pedometer

import android.app.Activity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedometer_configuration)

        val btShikokuRoute = findViewById<Button>(R.id.btShikokuRoute)
        val btChitashikokuroute = findViewById<Button>(R.id.btChitashikokuRoute)
        val btSaigokuroute = findViewById<Button>(R.id.btSaigokuRoute)
        val btReset = findViewById<Button>(R.id.btReset)
        val listener = RouteListener()
        btShikokuRoute.setOnClickListener(listener)
        btChitashikokuroute.setOnClickListener(listener)
        btSaigokuroute.setOnClickListener(listener)
        btReset.setOnClickListener(listener)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private inner class RouteListener : View.OnClickListener{
        override fun onClick(view: View){
            val intent = Intent(applicationContext, MainActivity::class.java)
            var route = ""
            var resetflag = false
            var routesetting = false
            when(view.id){
                R.id.btShikokuRoute -> {
                    route = "四国八十八箇所"
                    routesetting = true
                }
                R.id.btChitashikokuRoute -> {
                    route = "知多四国ルート"
                    routesetting = true
                }
                R.id.btSaigokuRoute-> {
                    route ="西国ルート"
                    routesetting = true
                }
                R.id.btReset-> {
                    resetflag = true
                    Toast.makeText(applicationContext, "リセット⓪", Toast.LENGTH_SHORT).show()
                }
            }
            if (routesetting){
                intent.putExtra("route", route)
            }
            Toast.makeText(applicationContext, "リセット①", Toast.LENGTH_SHORT).show()
            if (resetflag){
                intent.putExtra("reset", "reset")
                Toast.makeText(applicationContext, "リセット②", Toast.LENGTH_SHORT).show()
            }
            //Toast.makeText(applicationContext, "戻るよ", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }
}
