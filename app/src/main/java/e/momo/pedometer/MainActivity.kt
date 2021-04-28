package e.momo.pedometer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(),SensorEventListener {


    var abs_acceleration = 0.0
    var nstep = 0

    //遷移画面
    val CodeForSetting = 1000

    //フィルタリング係数 0< a < 1
    val coff_lowpass = 0.6
    var xn = 0.0
    var y_pre = 0.0
    var yn = 0.0
    var flag_first = true
    var up = false

    // TODO: 21/04/18  次の目的地の距離までのあつかいかたは一元管理できれば修正する．
    var nstep_tonext = 0
    var ntemple = 0

    // TODO: 21/04/17 データの処理方法は外部に保存する．
    //Array 札所番号，寺院名，前の札所からの距離[km],(wikipediaリンク)
    var temples = (arrayOf(
        arrayOf(1,"竺和山",2),arrayOf(2,"日照山",2),arrayOf(3,"亀光山",2),arrayOf(4,"黒巖山",2),
        arrayOf(5,"無尽山",2),arrayOf(6,"温泉山",2),arrayOf(7,"光明山",2),arrayOf(8,"普明山",2),
        arrayOf(9,"正覚山",2),arrayOf(10,"得度山",2),arrayOf(11,"金剛山",2),arrayOf(12,"摩盧山",2),
        arrayOf(13,"大栗山",2),arrayOf(14,"盛寿山",2),arrayOf(15,"薬王山",2),arrayOf(16,"光耀山",2),
        arrayOf(17,"瑠璃山",2),arrayOf(18,"母養山",2),arrayOf(19,"橋池山",2),arrayOf(20,"霊鷲山",2),
        arrayOf(21,"舎心山",2),arrayOf(22,"白水山",2),arrayOf(23,"医王山",2),arrayOf(24,"室戸山",2),
        arrayOf(25,"宝珠山",2),arrayOf(26,"龍頭山",2),arrayOf(27,"竹林山",2),arrayOf(28,"法界山",2),
        arrayOf(29,"摩尼山",2),arrayOf(30,"百々山",2),arrayOf(31,"五台山",2),arrayOf(32,"八葉山",2),
        arrayOf(33,"高福山",2),arrayOf(34,"本尾山",2),arrayOf(35,"醫王山",2),arrayOf(36,"独鈷山",2),
        arrayOf(37,"藤井山",2),arrayOf(38,"蹉跎山",2),arrayOf(39,"赤亀山",2),arrayOf(40,"平城山",2),
        arrayOf(41,"稲荷山",2),arrayOf(42,"一か山",2),arrayOf(43,"源光山",2),arrayOf(44,"菅生山",2),
        arrayOf(45,"海岸山",2),arrayOf(46,"医王山",2),arrayOf(47,"熊野山",2),arrayOf(48,"清滝山",2),
        arrayOf(49,"西林山",2),arrayOf(50,"東山",2),arrayOf(51,"熊野山",2),arrayOf(52,"瀧雲山",2),
        arrayOf(53,"須賀山",2),arrayOf(54,"近見山",2),arrayOf(55,"別宮山",2),arrayOf(56,"金輪山",2),
        arrayOf(57,"府頭山",2),arrayOf(58,"作礼山",2),arrayOf(59,"金光山",2),arrayOf(60,"石鈇山",2),
        arrayOf(61,"栴檀山",2),arrayOf(62,"天養山",2),arrayOf(63,"密教山",2),arrayOf(64,"石鈇山",2),
        arrayOf(65,"由霊山",2),arrayOf(66,"巨鼇山",2),arrayOf(67,"小松尾山",2),arrayOf(68,"七宝山",2),
        arrayOf(69,"七宝山",2),arrayOf(70,"七宝山",2),arrayOf(71,"剣五山",2),arrayOf(72,"我拝師山",2),
        arrayOf(73,"我拝師山",2),arrayOf(74,"医王山",2),arrayOf(75,"五岳山",2),arrayOf(76,"鶏足山",2),
        arrayOf(77,"桑多山",2),arrayOf(78,"仏光山",2),arrayOf(79,"金華山",2),arrayOf(80,"白牛山",2),
        arrayOf(81,"綾松山",2),arrayOf(82,"青峰山",2),arrayOf(83,"神毫山",2),arrayOf(84,"南面山",2),
        arrayOf(85,"五剣山",2),arrayOf(86,"補陀洛",2),arrayOf(87,"補陀洛",2),arrayOf(88,"医王山",2),
        arrayOf(89, "おわり",4 ))
    )

    //距離は（一社）四国八十八ヶ所霊場会[https://88shikokuhenro.jp/]による．また1.0kmあたり2000歩として換算している．
    var tempples_shikoku = (arrayOf(
        arrayOf(1,"霊山寺",77000),arrayOf(2,"極楽寺",2400),arrayOf(3,"金泉寺",5000),arrayOf(4,"大日寺",10000),
        arrayOf(5,"地蔵寺",4000),arrayOf(6,"安楽寺",10600),arrayOf(7,"十楽寺",2000),arrayOf(8,"熊谷寺",8400),
        arrayOf(9,"法輪寺",5000),arrayOf(10,"切幡寺",7600),arrayOf(11,"藤井寺",19600),arrayOf(12,"焼山寺",25000),
        arrayOf(13,"大日寺",43000),arrayOf(14,"常楽寺",5000),arrayOf(15,"国分寺",2000),arrayOf(16,"観音寺",3400),
        arrayOf(17,"井戸寺",6000),arrayOf(18,"恩山寺",38000),arrayOf(19,"立江寺",8000),arrayOf(20,"鶴林寺",28000),
        arrayOf(21,"太龍寺",13000),arrayOf(22,"平等寺",24000),arrayOf(23,"薬王寺",42000),arrayOf(24,"最御崎寺",17000),
        arrayOf(25,"津照寺",14000),arrayOf(26,"金剛頂寺",8000),arrayOf(27,"神峯寺",61000),arrayOf(28,"大日寺",77000),
        arrayOf(29,"国分寺",18000),arrayOf(30,"善楽寺",14000),arrayOf(31,"竹林寺",15000),arrayOf(32,"禅師峰寺",12000),
        arrayOf(33,"雪蹊寺",15000),arrayOf(34,"種間寺",13000),arrayOf(35,"清瀧寺",19000),arrayOf(36,"青龍寺",30000),
        arrayOf(37,"岩本寺",111000),arrayOf(38,"金剛福寺",173000),arrayOf(39,"延光寺",112000),arrayOf(40,"観自在寺",60000),
        arrayOf(41,"龍光寺",96000),arrayOf(42,"佛木寺",6000),arrayOf(43,"明石寺",22000),arrayOf(44,"大寶寺",140000),
        arrayOf(45,"岩屋寺",18000),arrayOf(46,"浄瑠璃寺",35000),arrayOf(47,"八坂寺",2000),arrayOf(48,"西林寺",9000),
        arrayOf(49,"浄土寺",6000),arrayOf(50,"繁多寺",3000),arrayOf(51,"石手寺",5000),arrayOf(52,"太山寺",21000),
        arrayOf(53,"圓明寺",4000),arrayOf(54,"延命寺",69000),arrayOf(55,"南光坊",7200),arrayOf(56,"泰山寺",6200),
        arrayOf(57,"栄福寺",6000),arrayOf(58,"仙遊寺",5000),arrayOf(59,"国分寺",12400),arrayOf(60,"横峰寺",66000),
        arrayOf(61,"香園寺",20000),arrayOf(62,"宝寿寺",3000),arrayOf(63,"吉祥寺",2800),arrayOf(64,"前神寺",7000),
        arrayOf(65,"三角寺",90000),arrayOf(66,"雲辺寺",41000),arrayOf(67,"大興寺",27000),arrayOf(68,"神恵院",18000),
        arrayOf(69,"観音寺",0),arrayOf(70,"本山寺",9400),arrayOf(71,"弥谷寺",24400),arrayOf(72,"曼荼羅寺",8000),
        arrayOf(73,"出釈迦寺",800),arrayOf(74,"甲山寺",5000),arrayOf(75,"善通寺",3000),arrayOf(76,"金倉寺",4600),
        arrayOf(77,"道隆寺",7800),arrayOf(78,"郷照寺",14200),arrayOf(79,"天皇寺",12600),arrayOf(80,"國分寺",12600),
        arrayOf(81,"白峯寺",13400),arrayOf(82,"根香寺",9200),arrayOf(83,"一宮寺",26600),arrayOf(84,"屋島寺",27400),
        arrayOf(85,"八栗寺",14400),arrayOf(86,"志度寺",13000),arrayOf(87,"長尾寺",14000),arrayOf(88,"大窪寺",31200)
    ))



    //初期設定では四国ルートとする
    var selectedtemples = temples
    var distance = temples[ntemple][2] as Int



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btClear = findViewById<Button>(R.id.btSetting)
        val btSave  = findViewById<Button>(R.id.btSave)
        val listener = ClearListener()
        btClear.setOnClickListener(listener)
        btSave.setOnClickListener(listener)

        //初期表示
        //txTemplefrom.text = "第${temples[ntemple][0]}札所　${temples[ntemple][1]}"
        txTempleto.text= "第${selectedtemples[ntemple+1][0]}番札所　${selectedtemples[ntemple+1][1]}まで"
        nstep_tonext = selectedtemples[ntemple + 1][2] as Int
        txNextTempleStep.text = "あと${nstep_tonext}歩"

    }


    override fun onResume() {
        super.onResume()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelermeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(this, accelermeter, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK&&
            requestCode == CodeForSetting && intent != null) {
            val route = intent.extras?.getString("route")?:""
            val reset = intent.extras?.getString("reset")?:""
            if(route != ""){
                txRoute.text = route
                if (route == "四国八十八箇所"){
                    selectedtemples = tempples_shikoku
                    Redeaw()
                    Toast.makeText(applicationContext, "四国八十八箇所ルートに切り替えました．", Toast.LENGTH_SHORT).show()
                }
            }
            if (reset == "reset"){
                AllClear()
            }
        }
    }

    private inner class ClearListener : View.OnClickListener{
        override fun onClick(view: View){
            when(view.id){
                //設定画面へ遷移
                R.id.btSetting-> {
                    val intent = Intent(applicationContext, PedometerConfiguration::class.java)
                    startActivityForResult(intent,CodeForSetting)
                }
                R.id.btSave -> {
                    // TODO: 21/04/25 保存ボタンをクリックした時は現状を外部データに書き出す
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val acc_x = event.values[0]
            val acc_y = event.values[1]
            val acc_z = event.values[2]
            abs_acceleration = sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z).toDouble()

            if (walkCheck(event)){
                nstep++
                nstep_tonext--
                txNextTempleStep.text = "あと${nstep_tonext}歩"

                //札所到着
                if (nstep > distance && ntemple < temples.size -1){
                    ntemple++
                    distance = distance + temples[ntemple][2] as Int
                    txTempleto.text= "第${temples[ntemple+1][0]}番札所　${temples[ntemple+1][1]}まで"
                    nstep_tonext = temples[ntemple + 1][2] as Int
                }
            }
            txNstep.text = "${nstep}歩"
        }

    }

    //歩数計算方法
    fun walkCheck(event: SensorEvent): Boolean {
        val acc_x = event.values[0]
        val acc_y = event.values[1]
        val acc_z = event.values[2]
        abs_acceleration = sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z).toDouble()

        if(flag_first){
            y_pre = coff_lowpass * abs_acceleration
            flag_first = false
            up = true
        }else{
            yn = coff_lowpass * abs_acceleration + (1.0 - coff_lowpass) * y_pre
            //波形が上昇中かつ最新出力が1ステップ前より低い（=ピークを超えた）場合1ステップとカウント
            if (up && yn < y_pre){
                up = false
                return true
            }else if (!up && yn > y_pre ){
                up = true
                y_pre = yn
            }
        }
        return false
    }

    fun Redeaw(){
        txTempleto.text= "第${temples[ntemple+1][0]}番札所　${temples[ntemple+1][1]}まで"
        txNstep.text = "${nstep}歩"
    }

    fun AllClear(){
        nstep_tonext = 0
        nstep = 0
    }
}



