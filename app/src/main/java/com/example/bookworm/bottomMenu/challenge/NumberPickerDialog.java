package com.example.bookworm.bottomMenu.challenge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

public class NumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    String title;    //dialog 제목
    String subtitle;    //dialog 부제목
    int minvalue;    //입력가능 최소값
    int maxvalue;    //입력가능 최대값
    int step;    //선택가능 값들의 간격
    int defvalue;    //dialog 시작 숫자 (현재값)

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        //Dialog 시작 시 bundle로 전달된 값을 받아온다

        title = getArguments().getString("title");
        subtitle = getArguments().getString("subtitle");
        minvalue = getArguments().getInt("minvalue");
        maxvalue = getArguments().getInt("maxvalue");
        step = getArguments().getInt("step");
        defvalue = getArguments().getInt("defvalue");

        //최소값과 최대값 사이의 값들 중에서 일정한 step사이즈에 맞는 값들을 배열로 만든다.
        String[] myValues = getArrayWithSteps(minvalue, maxvalue, step);

        //displayedvalues를 사용하지 않고 min/max 값을 설정해서 선택을 받을 때는 선택한 보여지는 값이 바로 리턴되는 값이다.
        //하지만 이런경우에는 보여지는 값은 최소값과 최대값사이에 값이 1씩 증가되는 값들이 모두 보여지게 된다. (별도의 step을 줄 수 없다)
        //일정한 간격의 숫자만을 선택할 수 있게 하려면, String 배열에 display가 되는 값들을 입력해서 setDisplayedValues함수로 입력해줘야 하며
        //이런경우 그 숫자가 리턴값이 아닌 배열의 인덱스가 리턴값이 된다. 따라서 minValue와 maxvalue는 이에 맞게 설정해 주어야 한다.

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue((maxvalue - minvalue) / step);
        numberPicker.setDisplayedValues(myValues);

        //현재값 설정 (dialog를 실행했을 때 시작지점)
        numberPicker.setValue((defvalue - minvalue) / step);
        //키보드 입력을 방지
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //숫자가 끝나고 0으로 순환하지 않게 함
        numberPicker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //제목 설정
        builder.setTitle(title);
        //부제목 설정
        builder.setMessage(subtitle);

        //Ok button을 눌렀을 때 동작 설정
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //dialog를 종료하면서 값이 변했다는 함수는 onValuechange함수를 실행시킨다.
                //실제 구현에서는 이 클래스의 함수를 재 정의해서 동작을 수행한다.

                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        //취소 버튼을 눌렀을 때 동작 설정
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(numberPicker);
        //number picker 실행
        return builder.create();
    }

    //Listener의 getter
    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    //Listener의 setter
    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    //최소값부터 최대값가지 일정 간격의 값을 String 배열로 출력
    public String[] getArrayWithSteps(int min, int max, int step) {

        int number_of_array = (max - min) / step + 1;

        String[] result = new String[number_of_array];

        for (int i = 0; i < number_of_array; i++) {
            result[i] = String.valueOf(min + step * i);
        }
        return result;
    }
}
