package com.example.bookworm.bottomMenu.challenge.subactivity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.challenge.ChallengeViewModel
import com.example.bookworm.bottomMenu.challenge.NumberPickerDialog
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.bookitems.Book
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.SubactivityChallengeCreatechallengeBinding
import java.text.SimpleDateFormat
import java.util.*

class SubActivityCreateChallenge : AppCompatActivity(), NumberPicker.OnValueChangeListener {
    val binding by lazy {
        SubactivityChallengeCreatechallengeBinding.inflate(layoutInflater)
    }
    private val challengeViewModel by lazy {
        ViewModelProvider(this, ChallengeViewModel.Factory(this))[ChallengeViewModel::class.java]
    }
    private val userViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this))[UserInfoViewModel::class.java]
    }
    lateinit var imm: InputMethodManager

    private var challengeData = Challenge()

    /** 액티비티간 데이터 전달 핸들러 검색한 데이터의 값을 전달받는 매개체
     * */
    private val bookResult: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            challengeData.book = result.data!!
                    .getParcelableExtra("bookData")!! //책데이터가 넘어오면 세팅한다.
            binding.tvCreatechallengeBookname.text = challengeData.book.title
            Glide.with(binding.root).load(challengeData.book.imgUrl).into(binding.ivThumbnail)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val liveData = MutableLiveData<UserInfo>()
        imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        userViewModel.getUser(null, liveData = liveData)
        liveData.observe(this) { userInfo ->
            challengeData.apply {
                masterToken = userInfo.token
                startDate = SimpleDateFormat("yyyy-MM-dd",
                        Locale.getDefault()).format(Calendar.getInstance().time) //오늘 날짜를 시작일자로 세팅
                currentPart.add(masterToken) //방장을 현재 참가자로 추가
            }
            setUI()
        }

    }

    //다른 영역 선택시, 키보드 내림.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val rect = Rect()
            currentFocus!!.getGlobalVisibleRect(rect)
            if (!rect.contains(ev!!.x.toInt(), ev.y.toInt())) {
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                currentFocus!!.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun setUI() {
        binding.apply {
            tvCreatechallengeBookname.apply {
                //관련 세팅
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.MARQUEE
                isSelected = true
                //책 선택 화면으로
                setOnClickListener {
                    //책가져오는 메소드 작성
                    val intent = Intent(this@SubActivityCreateChallenge, SearchMainActivity::class.java)
                    if (challengeData.book != Book()) intent.putExtra("prevBook", challengeData.book)
                    bookResult.launch(intent)
                }
            }
            ivThumbnail.setOnClickListener {
                //책가져오는 메소드 작성
                val intent = Intent(this@SubActivityCreateChallenge, SearchMainActivity::class.java)
                if (challengeData.book != Book()) intent.putExtra("prevBook", challengeData.book)
                bookResult.launch(intent)
            }

            /**
             * 날짜를 선택할 수 있는 데이터 피커를 보여준다,
             **/
            datePicker.apply {
                val myCalendar = Calendar.getInstance()
                setOnClickListener {
                    val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        myCalendar[Calendar.YEAR] = year
                        myCalendar[Calendar.MONTH] = month
                        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                        tvEndDate.text = SimpleDateFormat("yyyy-MM-dd",
                                Locale.getDefault()).format(myCalendar.time)
                    }
                    DatePickerDialog(this@SubActivityCreateChallenge, datePicker,
                            myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar.get(Calendar.DAY_OF_MONTH))
                            .apply {
                                getDatePicker().minDate = System.currentTimeMillis() - 1000
                                getDatePicker().maxDate = System.currentTimeMillis() - 1000 + 1000L * 60 * 60 * 24 * 30
                                show()
                            }
                }
            }

            //인원수 고르기
            numberPicker.setOnClickListener {
                showNumberPicker(view = it, "인원 선택", "", 30, 2, 1, 10)
            }

            //뒤로가기
            btnBack.setOnClickListener {
                backPressed()
            }
            //챌린지 생성
            btnStartChallenge.setOnClickListener {
                val liveData = MutableLiveData<LoadState>()
                challengeData.apply {
                    if (book == Book() || etCreatechallengeChallengeinfo.text.toString() == "" ||
                            etCreatechallengeChallengename.text.toString() == "" || tvMax.text.toString() == "" ||
                            tvEndDate.text.toString() == "00-00-00"
                    )
                        Toast.makeText(this@SubActivityCreateChallenge, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show()
                    else {
                        id = "${masterToken}_${startDate.hashCode() + title.hashCode() + masterToken.hashCode()}"
                        description = etCreatechallengeChallengeinfo.text.toString()
                        title = etCreatechallengeChallengename.text.toString()
                        maxPart = tvMax.text.toString().toLong()
                        endDate = tvEndDate.text.toString()
                        //파이어베이스에 데이터를 업로드
                        challengeViewModel.createChallenge(this, liveData)
                        liveData.observe(this@SubActivityCreateChallenge) {
                            if (it == LoadState.Done) {
                                Toast.makeText(this@SubActivityCreateChallenge, "챌린지를 정상적으로 생성하였습니다.", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK, intent.apply {
                                    putExtra("challengeData", challengeData)
                                })
                                finish()
                            } else Toast.makeText(this@SubActivityCreateChallenge, "챌린지를 생성하는 도중 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }
        }
    }

    private fun showNumberPicker(view: View, title: String, subtitle: String, maxvalue: Int, minvalue: Int, step: Int, defvalue: Int) {
        NumberPickerDialog().apply {
            arguments = Bundle(6).apply {
                putString("title", title) // key , value
                putString("subtitle", subtitle) // key , value
                putInt("maxvalue", maxvalue) // key , value
                putInt("minvalue", minvalue) // key , value
                putInt("step", step) // key , value
                putInt("defValue", defvalue) // key , value
            }
            valueChangeListener = this@SubActivityCreateChallenge
            show(this@SubActivityCreateChallenge.supportFragmentManager, "number Picker")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        binding.tvMax.text = (picker!!.value + 2).toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backPressed()
    }

    //뒤로가기 시에 보여주는 메뉴
    private fun backPressed() {
        //만약 작성된 내용이 있는 경우
        if (challengeData.book != Book() || binding.etCreatechallengeChallengename.text.isNotEmpty() || binding.etCreatechallengeChallengeinfo.text.isNotEmpty()) {
            AlertDialog.Builder(this)
                    .setTitle("현재까지 작성된 내용이 삭제됩니다. 그래도 계속하시겠습니까?")
                    .setPositiveButton("네") { dialog, v ->
                        finish()
                        dialog.dismiss()
                    }.setNegativeButton("아니오") { dialog, v ->
                        dialog.dismiss()
                    }.show()
        } else finish()
    }
}