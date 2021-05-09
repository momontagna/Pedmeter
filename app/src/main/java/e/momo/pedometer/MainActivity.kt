package e.momo.pedometer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
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
    val coff_lowpass = 0.5
    //val coff_lowpass = 0.5
    var xn = 0.0
    var y_pre = 0.0
    var yn = 0.0
    var flag_first = true
    var up = false

    // TODO: 21/04/18  次の目的地の距離までのあつかいかたは一元管理できれば修正する．
    var nstep_tonext = 0
    var ntemple = 0


    var id_route = 0
    val routename = arrayOf("サンプルルート動作中","四国八十八ヶ所霊場巡礼中","知多四国八十八ヶ所霊場巡礼中")
    // TODO: 21/04/17 読み込む札所のデータはは外部に保存する．
    //札所番号，山号, 寺院名，前の札所からの距離[歩数],
    val number_temple = 0
    val sangou_temple = 1
    val name_temple = 2
    val distance_temple = 3

    var temples = (arrayOf(
        arrayOf(1,"竺和山一乗院","霊山寺",1),arrayOf(2,"日照山無量寿院","極楽寺",2),arrayOf(3,"亀光山釈迦院","金泉寺",5),arrayOf(4,"黒巖山遍照院","大日寺",5),
        arrayOf(5,"無尽山荘厳院","地蔵寺",2),arrayOf(6,"温泉山瑠璃光院","安楽寺",2),arrayOf(7,"光明山蓮華院","十楽寺",4),arrayOf(8,"普明山真光院","熊谷寺",2),
        arrayOf(9,"正覚山菩提院","法輪寺",22),arrayOf(10,"得度山灌頂院","切幡寺",12),arrayOf(11,"","金剛山藤井寺",21),arrayOf(12,"摩盧山正寿院","焼山寺",2),
        arrayOf(13,"大栗山花蔵院","大日寺",2),arrayOf(14,"盛寿山延命院","常楽寺",2),arrayOf(15,"薬王山金色院","国分寺",22),arrayOf(16,"光耀山千手院","観音寺",2),
        arrayOf(17,"瑠璃山真福院","井戸寺",2),arrayOf(18,"母養山宝樹院","恩山寺",2),arrayOf(19,"橋池山摩尼院","立江寺",2),arrayOf(20,"霊鷲山宝珠院","鶴林寺",2),
        arrayOf(21,"舎心山常住院","太龍寺",2),arrayOf(22,"白水山医王院","平等寺",2),arrayOf(23,"医王山無量寿院","薬王寺",2),arrayOf(24,"室戸山明星院","最御崎寺",2),
        arrayOf(25,"宝珠山真言院","津照寺",2),arrayOf(26,"龍頭山光明院","金剛頂寺",2),arrayOf(27,"竹林山地蔵院","神峯寺",2),arrayOf(28,"法界山高照院","大日寺",2),
        arrayOf(29,"摩尼山宝蔵院","国分寺",2),arrayOf(30,"百々山東明院","善楽寺",2),arrayOf(31,"五台山金色院","竹林寺",2),arrayOf(32,"八葉山求聞持院","禅師峰寺",2),
        arrayOf(33,"高福山高福院","雪蹊寺",2),arrayOf(34,"本尾山朱雀院","種間寺",2),arrayOf(35,"醫王山鏡池院","清瀧寺",2),arrayOf(36,"独鈷山伊舎那院","青龍寺",2),
        arrayOf(37,"藤井山五智院","岩本寺",2),arrayOf(38,"蹉跎山補陀洛院","金剛福寺",2),arrayOf(39,"赤亀山寺山院","延光寺",2),arrayOf(40,"平城山薬師院","観自在寺",2),
        arrayOf(41,"稲荷山護国院","龍光寺",2),arrayOf(42,"一か山毘盧舎那院","佛木寺",2),arrayOf(43,"源光山円手院","明石寺",2),arrayOf(44,"菅生山大覚院","大寶寺",2),
        arrayOf(45,"海岸山","岩屋寺",2),arrayOf(46,"医王山養珠院","浄瑠璃寺",2),arrayOf(47,"熊野山妙見院","八坂寺",2),arrayOf(48,"清滝山安養院","西林寺",2),
        arrayOf(49,"西林山三蔵院","浄土寺",2),arrayOf(50,"東山瑠璃光院","繁多寺",2),arrayOf(51,"熊野山虚空蔵院","石手寺",2),arrayOf(52,"瀧雲山護持院","太山寺",2),
        arrayOf(53,"須賀山正智院","圓明寺",2),arrayOf(54,"近見山宝鐘院","延命寺",2),arrayOf(55,"別宮山金剛院","南光坊",2),arrayOf(56,"金輪山勅王院","泰山寺",2),
        arrayOf(57,"府頭山無量寿院","栄福寺",2),arrayOf(58,"作礼山千光院","仙遊寺",2),arrayOf(59,"金光山最勝院","国分寺",2),arrayOf(60,"石鈇山福智院","横峰寺",2),
        arrayOf(61,"栴檀山教王院","香園寺",2),arrayOf(62,"天養山観音院","宝寿寺",2),arrayOf(63,"密教山胎蔵院","吉祥寺",2),arrayOf(64,"石鈇山金色院","前神寺",2),
        arrayOf(65,"由霊山慈尊院","三角寺",2),arrayOf(66,"巨鼇山千手院","雲辺寺",2),arrayOf(67,"小松尾山不動光院","大興寺",2),arrayOf(68,"七宝山","神恵院",2),
        arrayOf(69,"七宝山","観音寺",2),arrayOf(70,"七宝山持宝院","本山寺",2),arrayOf(71,"剣五山千手院","弥谷寺",2),arrayOf(72,"我拝師山延命院","曼荼羅寺",2),
        arrayOf(73,"我拝師山求聞持院","出釈迦寺",2),arrayOf(74,"医王山多宝院","甲山寺",2),arrayOf(75,"五岳山誕生院","善通寺",2),arrayOf(76,"鶏足山宝幢院","金倉寺",2),
        arrayOf(77,"桑多山明王院","道隆寺",2),arrayOf(78,"仏光山広徳院","郷照寺",2),arrayOf(79,"金華山高照院","天皇寺",2),arrayOf(80,"白牛山千手院","國分寺",2),
        arrayOf(81,"綾松山洞林院","白峯寺",2),arrayOf(82,"青峰山千手院","根香寺",2),arrayOf(83,"神毫山大宝院","一宮寺",2),arrayOf(84,"南面山千光院","屋島寺",2),
        arrayOf(85,"五剣山観自在院","八栗寺",2),arrayOf(86,"補陀洛清浄光院","志度寺",2),arrayOf(87,"補陀洛観音院","長尾寺",2),arrayOf(88,"医王山遍照光院","大窪寺",2),
        arrayOf(89, "おわり",4 ))
    )

    //距離は（一社）四国八十八ヶ所霊場会[https://88shikokuhenro.jp/]による．また1.0kmあたり2000歩として換算している．
    var tempples_shikoku = (arrayOf(
        arrayOf(1,"竺和山一乗院","霊山寺",77000),arrayOf(2,"日照山無量寿院","極楽寺",2400),arrayOf(3,"亀光山釈迦院","金泉寺",5000),arrayOf(4,"黒巖山遍照院","大日寺",10000),
        arrayOf(5,"無尽山荘厳院","地蔵寺",4000),arrayOf(6,"温泉山瑠璃光院","安楽寺",10600),arrayOf(7,"光明山蓮華院","十楽寺",2000),arrayOf(8,"普明山真光院","熊谷寺",8400),
        arrayOf(9,"正覚山菩提院","法輪寺",5000),arrayOf(10,"得度山灌頂院","切幡寺",7600),arrayOf(11,"","金剛山藤井寺",19600),arrayOf(12,"摩盧山正寿院","焼山寺",25000),
        arrayOf(13,"大栗山花蔵院","大日寺",43000),arrayOf(14,"盛寿山延命院","常楽寺",5000),arrayOf(15,"薬王山金色院","国分寺",2000),arrayOf(16,"光耀山千手院","観音寺",3400),
        arrayOf(17,"瑠璃山真福院","井戸寺",6000),arrayOf(18,"母養山宝樹院","恩山寺",38000),arrayOf(19,"橋池山摩尼院","立江寺",8000),arrayOf(20,"霊鷲山宝珠院","鶴林寺",28000),
        arrayOf(21,"舎心山常住院","太龍寺",13000),arrayOf(22,"白水山医王院","平等寺",24000),arrayOf(23,"医王山無量寿院","薬王寺",42000),arrayOf(24,"室戸山明星院","最御崎寺",17000),
        arrayOf(25,"宝珠山真言院","津照寺",14000),arrayOf(26,"龍頭山光明院","金剛頂寺",8000),arrayOf(27,"竹林山地蔵院","神峯寺",61000),arrayOf(28,"法界山高照院","大日寺",77000),
        arrayOf(29,"摩尼山宝蔵院","国分寺",18000),arrayOf(30,"百々山東明院","善楽寺",14000),arrayOf(31,"五台山金色院","竹林寺",15000),arrayOf(32,"八葉山求聞持院","禅師峰寺",12000),
        arrayOf(33,"高福山高福院","雪蹊寺",15000),arrayOf(34,"本尾山朱雀院","種間寺",13000),arrayOf(35,"醫王山鏡池院","清瀧寺",19000),arrayOf(36,"独鈷山伊舎那院","青龍寺",30000),
        arrayOf(37,"藤井山五智院","岩本寺",111000),arrayOf(38,"蹉跎山補陀洛院","金剛福寺",173000),arrayOf(39,"赤亀山寺山院","延光寺",112000),arrayOf(40,"平城山薬師院","観自在寺",60000),
        arrayOf(41,"稲荷山護国院","龍光寺",96000),arrayOf(42,"一か山毘盧舎那院","佛木寺",6000),arrayOf(43,"源光山円手院","明石寺",22000),arrayOf(44,"菅生山大覚院","大寶寺",140000),
        arrayOf(45,"海岸山","岩屋寺",18000),arrayOf(46,"医王山養珠院","浄瑠璃寺",35000),arrayOf(47,"熊野山妙見院","八坂寺",2000),arrayOf(48,"清滝山安養院","西林寺",9000),
        arrayOf(49,"西林山三蔵院","浄土寺",6000),arrayOf(50,"東山瑠璃光院","繁多寺",3000),arrayOf(51,"熊野山虚空蔵院","石手寺",5000),arrayOf(52,"瀧雲山護持院","太山寺",21000),
        arrayOf(53,"須賀山正智院","圓明寺",4000),arrayOf(54,"近見山宝鐘院","延命寺",69000),arrayOf(55,"別宮山金剛院","南光坊",7200),arrayOf(56,"金輪山勅王院","泰山寺",6200),
        arrayOf(57,"府頭山無量寿院","栄福寺",6000),arrayOf(58,"作礼山千光院","仙遊寺",5000),arrayOf(59,"金光山最勝院","国分寺",12400),arrayOf(60,"石鈇山福智院","横峰寺",66000),
        arrayOf(61,"栴檀山教王院","香園寺",20000),arrayOf(62,"天養山観音院","宝寿寺",3000),arrayOf(63,"密教山胎蔵院","吉祥寺",2800),arrayOf(64,"石鈇山金色院","前神寺",7000),
        arrayOf(65,"由霊山慈尊院","三角寺",90000),arrayOf(66,"巨鼇山千手院","雲辺寺",41000),arrayOf(67,"小松尾山不動光院","大興寺",27000),arrayOf(68,"七宝山","神恵院",18000),
        arrayOf(69,"七宝山","観音寺",0),arrayOf(70,"七宝山持宝院","本山寺",9400),arrayOf(71,"剣五山千手院","弥谷寺",24400),arrayOf(72,"我拝師山延命院","曼荼羅寺",8000),
        arrayOf(73,"我拝師山求聞持院","出釈迦寺",800),arrayOf(74,"医王山多宝院","甲山寺",5000),arrayOf(75,"五岳山誕生院","善通寺",3000),arrayOf(76,"鶏足山宝幢院","金倉寺",4600),
        arrayOf(77,"桑多山明王院","道隆寺",7800),arrayOf(78,"仏光山広徳院","郷照寺",14200),arrayOf(79,"金華山高照院","天皇寺",12600),arrayOf(80,"白牛山千手院","國分寺",12600),
        arrayOf(81,"綾松山洞林院","白峯寺",13400),arrayOf(82,"青峰山千手院","根香寺",9200),arrayOf(83,"神毫山大宝院","一宮寺",26600),arrayOf(84,"南面山千光院","屋島寺",27400),
        arrayOf(85,"五剣山観自在院","八栗寺",14400),arrayOf(86,"補陀洛清浄光院","志度寺",13000),arrayOf(87,"補陀洛観音院","長尾寺",14000),arrayOf(88,"医王山遍照光院","大窪寺",31200)
    ))

    //札所番号, (院号), ()
    var tempples_chita = (arrayOf(
        arrayOf(1," ","曹源寺",12200),arrayOf(2," ","極楽寺",6400),arrayOf(3," ","普門寺",800),arrayOf(4," ","延命寺",4200),
        arrayOf(5," ","地蔵寺",9600),arrayOf(6," ","常福寺",10400),arrayOf(7," ","極楽寺",7000),arrayOf(8," ","傳宗院",1700),
        arrayOf(9," ","明徳寺",4200),arrayOf(10," ","観音寺",2800),arrayOf(11," ","安徳寺",3800),arrayOf(12," ","福住寺",3600),
        arrayOf(13," ","安楽寺",5600),arrayOf(14," ","興昌寺",1200),arrayOf(15," ","洞雲院",3200),arrayOf(16," ","平泉寺",4800),
        arrayOf(17," ","観音寺",1200),arrayOf(18," ","光照寺",7400),arrayOf(19," ","光照院",3200),arrayOf(20," ","龍台院",1600),
        arrayOf(21," ","常楽寺",4000),arrayOf(22," ","大日寺",9600),arrayOf(23," ","蓮花院",1300),arrayOf(24," ","徳正寺",3000),
        arrayOf(25," ","円観寺",4400),arrayOf(26," ","弥勒寺",12600),arrayOf(27," ","誓海寺",7600),arrayOf(28," ","永寿寺",11200),
        arrayOf(29," ","正法寺",5400),arrayOf(30," ","医王寺",5600),arrayOf(31," ","利生院",280),arrayOf(32," ","宝乗院",100),
        arrayOf(33," ","北室院",160),arrayOf(34," ","性慶院",520),arrayOf(35," ","成願寺",3200),arrayOf(36," ","遍照寺",3600),
        arrayOf(37," ","大光院",4400),arrayOf(38," ","正法禅寺",2000),arrayOf(39," ","医徳院",800),arrayOf(40," ","影向寺",15000),
        arrayOf(41," ","西方寺",5200),arrayOf(42," ","天龍寺",2600),arrayOf(43," ","岩屋寺",2200),arrayOf(44," ","大宝寺",10000),
        arrayOf(45," ","泉蔵院",2600),arrayOf(46," ","如意輪寺",1500),arrayOf(47," ","持宝院",2000),arrayOf(48," ","良参寺",7600),
        arrayOf(49," ","吉祥寺",4200),arrayOf(50," ","大御堂寺",2800),arrayOf(51," ","野間大坊",340),arrayOf(52," ","密蔵院",580),
        arrayOf(53," ","安養院",440),arrayOf(54," ","海潮院",45400),arrayOf(55," ","法山寺",45800),arrayOf(56," ","瑞境寺",3200),
        arrayOf(57," ","報恩寺",5200),arrayOf(58," ","来応寺",14400),arrayOf(59," ","玉泉寺",520),arrayOf(60," ","安楽寺",3000),
        arrayOf(61," ","高讃寺",4400),arrayOf(62," ","洞雲寺",2800),arrayOf(63," ","大善院",3400),arrayOf(64," ","宝全寺",1700),
        arrayOf(65," ","相持院",2200),arrayOf(66," ","中之坊寺",10800),arrayOf(67," ","三光院",2200),arrayOf(68," ","宝蔵寺",1700),
        arrayOf(69," ","慈光寺",1200),arrayOf(70," ","地蔵寺",1300),arrayOf(71," ","大智院",2600),arrayOf(72," ","慈雲寺",9400),
        arrayOf(73," ","正法院",4200),arrayOf(74," ","密厳寺",280),arrayOf(75," ","誕生堂",20),arrayOf(76," ","如意寺",240),
        arrayOf(77," ","浄蓮寺",400),arrayOf(78," ","福生寺",4600),arrayOf(79," ","妙楽寺",2200),arrayOf(80," ","栖光院",3600),
        arrayOf(81," ","龍蔵寺",1600),arrayOf(82," ","観福寺",6800),arrayOf(83," ","弥勒寺",2800),arrayOf(84," ","玄猷寺",3200),
        arrayOf(85," ","清水寺",2800),arrayOf(86," ","観音寺",3000),arrayOf(87," ","長寿寺",12400),arrayOf(88," ","円通寺",6200)
    ))

    //初期設定では四国ルートとする
    var selectedtemples = temples

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btClear = findViewById<Button>(R.id.btSetting)
        val btSave  = findViewById<Button>(R.id.btSave)
        val listener = ClickListener()
        btClear.setOnClickListener(listener)
        btSave.setOnClickListener(listener)

        id_route = CallIntData("routename")
        if (id_route == 0) id_route = 1
        if (id_route == 1){
            selectedtemples = tempples_shikoku
        }else if (id_route == 2){
            selectedtemples = tempples_chita
        }
        nstep = CallIntData(routename[id_route])

        //初期表示
        //nstepToNowLocation(nstep)
        Log.i("Initial", "hogehoge")
        Log.i("Initial", id_route.toString())
        Redraw()
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
                if (route == routename[1]){
                    RestoreIntData(routename[id_route], nstep)
                    id_route = 1
                    selectedtemples = tempples_shikoku
                    Toast.makeText(applicationContext, "四国八十八箇所ルートに切り替えました。", Toast.LENGTH_SHORT).show()
                    nstep = CallIntData(routename[id_route])
                }else if (route == routename[2]){
                    RestoreIntData(routename[id_route], nstep)
                    id_route = 2
                    selectedtemples = tempples_chita
                    Toast.makeText(applicationContext, "知多四国八十八箇所ルートに切り替えました。", Toast.LENGTH_SHORT).show()
                    nstep = CallIntData(routename[id_route])
                }
                Redraw()
            }
            if (reset == "reset"){
                AllClear()
            }
        }
    }

    private inner class ClickListener : View.OnClickListener{
        override fun onClick(view: View){
            when(view.id){
                //設定画面へ遷移
                R.id.btSetting-> {
                    val intent = Intent(applicationContext, PedometerConfiguration::class.java)
                    startActivityForResult(intent,CodeForSetting)
                }
                R.id.btSave -> {
                    RestoreIntData("nstep", nstep)
                    RestoreIntData("routename", id_route)
                    Log.i("ExternalOutputSave", nstep.toString())
                    Toast.makeText(applicationContext, "保存しました。", Toast.LENGTH_SHORT).show()
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
                //札所到着89
                if (ArriveTemple()){
                    Redraw()
                    Toast.makeText(applicationContext, "札所に到着しました。", Toast.LENGTH_SHORT).show()
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

    fun ArriveTemple():Boolean{
        return nstep_tonext < 0
    }

    //現在歩数 -> 現在札所数，残り歩数
    fun nstepToNowLocation(n:Int):Pair<Int,Int> {
        var nt = 0 //札所カウント
        var nrem = n //残り歩数
        var tmpdistance = 0

        do {
            tmpdistance = selectedtemples[nt][distance_temple] as Int
            Log.i("Initial", tmpdistance.toString())
            if (nrem > tmpdistance){
                nrem -= tmpdistance
                nt++
            }
        }while (nrem > tmpdistance)

        return Pair(nt, tmpdistance -nrem)
    }


    fun Redraw(){
        //現在の歩数表示
        ntemple = nstepToNowLocation(nstep).first
        nstep_tonext = nstepToNowLocation(nstep).second
        //現在のルート，目的地，残り歩数表示
        txRoute.text = "${routename[id_route]}"
        Log.i("Redraw", selectedtemples[0][2].toString())
        txTempleto.text= "第${selectedtemples[ntemple+1][number_temple]}番札所　${selectedtemples[ntemple+1][name_temple]}まで"
        txNextTempleStep.text = "あと${nstep_tonext}歩"

    }

    fun AllClear(){
        nstep_tonext = 0
        nstep = 0
        val dataStore:SharedPreferences = getSharedPreferences("DaraStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()
        editor.putInt("nstep", 0)
        editor.apply()
        Redraw()
        Toast.makeText(applicationContext, "データをクリアしました。", Toast.LENGTH_SHORT).show()
    }

    fun RestoreIntData(name:String, input:Int){
        val dataStore:SharedPreferences = getSharedPreferences("DaraStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()
        editor.putInt(name, input)
        editor.apply()
    }

    fun CallIntData(name:String) :Int{
        val dataStore:SharedPreferences = getSharedPreferences("DaraStore", Context.MODE_PRIVATE)
        val editor = dataStore.edit()
        //初期値を読み出す。なければ0とする。
        return dataStore.getInt(name, 0)
    }

}



