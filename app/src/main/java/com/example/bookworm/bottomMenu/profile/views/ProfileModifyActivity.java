package com.example.bookworm.bottomMenu.profile.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.bottomMenu.profile.ChallengeViewModel;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.dataprocessing.image.ImageProcessing;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ActivityProfileModifyBinding;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo NowUser;
    private ImageProcessing imageProcess;
    ActivityProfileModifyBinding binding;
    private boolean uploadCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = this;


        imageProcess = new ImageProcessing(this);
        UserInfoViewModel pv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);
        ChallengeViewModel cv = new ViewModelProvider(this, new ChallengeViewModel.Factory(context)).get(ChallengeViewModel.class);
        LiveData<Boolean> bool = pv.isDuplicated();
        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            NowUser = userInfo;
            binding.tvNickname.setText(NowUser.getUsername());
            setMedal(NowUser);
            Glide.with(this)
                    .load(userInfo.getProfileimg())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.ivProfileImage);

            binding.btnFavGenre.setOnClickListener(it -> {
                cv.getChallengeList(userInfo.getToken());
            });
            //프로필 이미지 변경
            imageProcess.getBitmapUri().observe(this, observer -> {
                Glide.with(binding.getRoot()).load(observer).circleCrop().into(binding.ivProfileImage);
            });

            //프로필이미지를 업로드 하는 경우
            imageProcess.getBitmap().observe(this, bitmap -> {
                //완료버튼을 누르면 이미지 업데이트
                binding.btnFinish.setOnClickListener(view -> {
                    String imgName = "profile_" + NowUser.getToken() + ".jpg";
                    imageProcess.uploadImage(bitmap, imgName); // 이미지 업로드
                });
                imageProcess.getImgData().observe(this, imgurl -> {
                    if (checkIdChanged()) {
                        NowUser.setProfileimg(imgurl);
                        pv.updateUser(NowUser);
                        finish();
                    } else {
                        Toast.makeText(this, "아이디 중복 체크 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            //이미지 없이 업로드
            binding.btnFinish.setOnClickListener(view -> {
                if (uploadCheck) {
                    pv.updateUser(NowUser);
                    finish();
                } else {
                    Toast.makeText(this, "아이디 중복 체크 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
            binding.edtNewNickname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    uploadCheck = false;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    uploadCheck = checkIdChanged();
                }
            });


            //값이 변경 되면 알려줌
            bool.observe(this, value -> {
                if (value == false) {
                    NowUser.setUsername(binding.edtNewNickname.getText().toString());
                    uploadCheck = true;
                    Toast.makeText(context, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "사용할 수 없는 닉네임입니다.", Toast.LENGTH_SHORT).show();
            });

            binding.edtIntroduce.setText(NowUser.getIntroduce()); //자기소개 세팅

            binding.btnIntroModify.setOnClickListener(new View.OnClickListener() { //자기소개 수정
                @Override
                public void onClick(View view) {
                    if (binding.btnIntroModify.getText().toString().equals("수정")) { //수정하기 버튼을 눌렀을 때
                        binding.btnIntroModify.setText("완료");
                        binding.edtIntroduce.setBackgroundColor(Color.parseColor("#2200ff00")); //배경색 변경
                        binding.edtIntroduce.setEnabled(true); //EditText를 수정 가능한 상태로 만듦
                        binding.btnModifyCancle.setVisibility(View.VISIBLE); //수정 취소 버튼 노출

                        //키보드 띄우기
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        binding.edtIntroduce.requestFocus(); //초점 이동
                        binding.edtIntroduce.setSelection(binding.edtIntroduce.length()); //가장 마지막으로 커서 이동
                    } else { //완료 버튼을 눌렀을 때
                        new AlertDialog.Builder(context) //변경하
                                .setMessage("변경하시겠습니까?")
                                .setPositiveButton("네", (dialog, which) -> {
                                    NowUser.setIntroduce(binding.edtIntroduce.getText().toString()); //userinfo의 자기소개를 변경
                                    pv.updateUser(NowUser); // 사용자 정보 업데이트
                                    binding.btnIntroModify.setText("수정"); //완료 버튼에서 다시 수정 버튼으로 변경
                                    binding.edtIntroduce.setBackgroundColor(Color.WHITE); //배경색 변경
                                    binding.edtIntroduce.setEnabled(false); //수정 불가능하게 변경
                                    binding.btnModifyCancle.setVisibility(View.INVISIBLE); //수정 취소 버튼 숨김

                                    //키보드 내리기
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(binding.edtIntroduce.getWindowToken(), 0);
                                    binding.edtIntroduce.clearFocus(); //초점 제거
                                    dialog.dismiss();

                                })
                                .setNegativeButton("아니요", (dialog, which)
                                        -> dialog.dismiss())
                                .show();
                    }
                }

            });

            binding.btnModifyCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnIntroModify.setText("수정"); //완료 버튼에서 다시 수정 버튼으로 변경
                    binding.edtIntroduce.setBackgroundColor(Color.WHITE); //배경색 변경
                    binding.edtIntroduce.setEnabled(false); //수정 불가능하게 변경
                    binding.edtIntroduce.setText(NowUser.getIntroduce()); //기존의 자기소개로 변경
                    binding.btnModifyCancle.setVisibility(View.INVISIBLE); //수정 취소 버튼 숨김

                    //키보드 내리기
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.edtIntroduce.getWindowToken(), 0);
                    binding.edtIntroduce.clearFocus(); //초점 제거
                }
            });

            if (NowUser.getMedalAppear()) { //메달 표시가 true일때
                binding.setMedalInNickname.setSelected(true);
                binding.setMedalInNickname.setText("표시");
            } else { //메달 표시가 false일때
                binding.setMedalInNickname.setSelected(false);
                binding.setMedalInNickname.setText("숨김");
            }

            binding.setMedalInNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.setMedalInNickname.isSelected()) {
                        NowUser.setMedalAppear(false);
                        binding.setMedalInNickname.setSelected(false);
                        binding.setMedalInNickname.setText("숨김");
                        pv.updateUser(NowUser);
                        setMedal(NowUser);
                    } else {
                        NowUser.setMedalAppear(true);
                        binding.setMedalInNickname.setSelected(true);
                        binding.setMedalInNickname.setText("표시");
                        pv.updateUser(NowUser);
                        setMedal(NowUser);
                    }
                }
            });

        });

        cv.getChallengeList().observe(this, it -> {
            for (Challenge i : it) {
                Log.d("챌린지 데이터: ", i.getMaster());
            }
        });
        binding.tvProfileImageModify.setOnClickListener(it -> {
            imageProcess.initProcess();
        });

        binding.checkDuplicate.setOnClickListener(it -> {
            String name = binding.edtNewNickname.getText().toString();
            if (!name.contains(" ") && !name.equals("")) pv.checkDuplicate(name);
            else Toast.makeText(context, "닉네임에는 공백을 넣을 수 없습니다.", Toast.LENGTH_SHORT).show();
        });


        //뒤로가기 버튼
        binding.btnBack.setOnClickListener(view -> finish());

    }

    private boolean checkIdChanged() {
        String name = binding.edtNewNickname.getText().toString();
        if (name.equals("")) return true;
        else if (!name.equals("") && !uploadCheck) return false;
        else return true;
    }

    //메달 표시 유무에 따른 세팅
    private void setMedal(UserInfo userInfo) {
        if (userInfo.getMedalAppear()) { //메달을 표시한다면
            binding.ivMedal.setVisibility(View.VISIBLE);
            switch (Integer.parseInt(String.valueOf(userInfo.getTier()))) { //티어 0 ~ 5에 따라 다른 메달이 나오게
                case 1:
                    binding.ivMedal.setImageResource(R.drawable.medal_bronze);
                    break;
                case 2:
                    binding.ivMedal.setImageResource(R.drawable.medal_silver);
                    break;
                case 3:
                    binding.ivMedal.setImageResource(R.drawable.medal_gold);
                    break;
                case 4:
//                    binding.ivMedal.setImageResource(R.drawable.medal_platinum);
                    break;
                case 5:
//                    binding.ivMedal.setImageResource(R.drawable.medal_diamond);
                    break;
                default: //티어가 없을때
                    binding.ivMedal.setImageResource(0);
            }
        } else { //메달을 표시하지 않을거라면
            binding.ivMedal.setVisibility(View.GONE);
            binding.ivMedal.setImageResource(0);
        }
    }

}