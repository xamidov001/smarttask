package uz.pdp.smarttask

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uz.pdp.smarttask.customView.FloodFill
import uz.pdp.smarttask.databinding.ActivityMainBinding
import uz.pdp.smarttask.databinding.DialogAppBinding
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val presenter = FloodFill()
    private  val TAG = "MainActivity"

    @SuppressLint("ClickableViewAccessibility", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            seekBar.max = 100

            size.setOnClickListener {
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                val bindingD = DialogAppBinding.inflate(layoutInflater)
                val create = alertDialog.create()
                create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                create.setView(bindingD.root)
                bindingD.apply {
                    cancle.setOnClickListener {
                        create.dismiss()
                    }

                    ok.setOnClickListener {
                        if (height.text.toString().isNotEmpty() && width.text.toString().isNotEmpty()) {
                            val newW = width.text.toString().toInt()
                            val newH = height.text.toString().toInt()
                            val params1 = myCanva1.layoutParams
                            val params2 = myCanva2.layoutParams
                            params1.width = newW
                            params1.height = newH
                            params2.width = newW
                            params2.height = newH
                            myCanva1.layoutParams = params1; myCanva1.requestLayout(); myCanva1.forceLayout()
                            myCanva2.layoutParams = params2; myCanva2.requestLayout(); myCanva2.forceLayout()
                            create.dismiss()
                        } else {
                            create.dismiss()
                        }
                    }

                }

                create.show()
            }

            clear.setOnClickListener {
                myCanva1.setImageBitmap(null)
                myCanva2.setImageBitmap(null)
            }

            generate.setOnClickListener {
                generateBitmaps()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { showProgress() }
                    .observeOn(Schedulers.computation())
                    .map {
                        presenter.generateRandomBitmap(it.x, it.y)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        hideProgress()
                        showResults(it)
                    }
            }

            touchUser()
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it.action == MotionEvent.ACTION_DOWN }
                .observeOn(Schedulers.computation())
                .map {
                    presenter.executeFloodFilling1(
                        algorithm1.selectedItemPosition,
                        myCanva1,
                        it,
                        100-seekBar.progress
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showResult(it) }, { it.printStackTrace() })

            touchUser2()
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it.action == MotionEvent.ACTION_DOWN }
                .observeOn(Schedulers.computation())
                .map {
                    presenter.executeFloodFilling2(
                        algorithm2.selectedItemPosition,
                        myCanva2,
                        it,
                        100-seekBar.progress
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showResult2(it) }, { it.printStackTrace() })

        }
    }

    private fun showResult2(it: Bitmap) {
        binding.apply {
            myCanva2.setImageBitmap(it)
        }
    }

    private fun showResult(it: Bitmap) {
        binding.apply {
            myCanva1.setImageBitmap(it)
        }
    }

    private fun showResults(it: Bitmap) {
        binding.apply {
            myCanva1.setImageBitmap(it)
            myCanva2.setImageBitmap(it)
        }
    }

    private fun hideProgress() {
        binding.apply {
            progress1.visibility = View.GONE
            progress2.visibility = View.GONE
        }
    }

    private fun showProgress() {
        binding.apply {
            progress1.visibility = View.VISIBLE
            progress2.visibility = View.VISIBLE
        }

    }

    private fun generateBitmaps(): Observable<Point> = Observable.create() { emmit ->
        binding.apply {
//            emmit.onNext(Point(myCanva1.width, myCanva1.height))
            emmit.onNext(Point(64, 64))
        }
    }

    private fun touchUser(): Observable<MotionEvent> = Observable.create { emmit ->
        binding.myCanva1.setOnTouchListener { v, event ->
            if ((v as ImageView).drawable != null) emmit.onNext(event)
            true
        }

    }

    private fun touchUser2(): Observable<MotionEvent> = Observable.create { emmit ->
        binding.myCanva2.setOnTouchListener { v, event ->
            if ((v as ImageView).drawable != null) emmit.onNext(event)
            true
        }
    }
}