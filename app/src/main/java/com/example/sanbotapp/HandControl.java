package com.example.sanbotapp;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.qihancloud.opensdk.function.beans.handmotion.RelativeAngleHandMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;



public class HandControl extends TopBaseActivity {

    private HandMotionManager handMotionManager;

    /**
     * Movimiento sin control del angulo
     speed: 1-10(max)
     action: the motion of hands
     NoAngleHandMotion.ACTION_UP //hands up
     NoAngleHandMotion.ACTION_DOWN //hands down
     NoAngleHandMotion.ACTION_STOP //hands stop
     NoAngleHandMotion.ACTION_RESET//hands reset to default position
     part: the right/left hand
     NoAngleHandMotion.PART_LEFT //only left
     NoAngleHandMotion.PART_RIGHT 2 //only right
     NoAngleHandMotion.PART_BOTH 3 //both
     */
    private Spinner svHandNoAngleAction;
    private Spinner svHandNoAnglePart;

    private EditText etHandNoAngleSpeed;
    private Button tvHandNoAngleStart;
    private Button tvHandNoAngleEnd;



    private byte[] noAngleAction = {NoAngleHandMotion.ACTION_UP, NoAngleHandMotion.ACTION_DOWN, NoAngleHandMotion.ACTION_RESET, NoAngleHandMotion.ACTION_STOP};
    private byte curNoAngleAction;

    private byte[] noAnglePart = {NoAngleHandMotion.PART_LEFT, NoAngleHandMotion.PART_RIGHT, NoAngleHandMotion.PART_BOTH};
    private byte curNoAnglePart;

    /**
     Movimiento relativo, respecto a la posicion de la mano
     speed: 1-8(max)
     angle: 0-270degree(counterclockwise)
     action: the motion of hands
     RelativeAngleHandMotion.ACTION_UP //hands up
     RelativeAngleHandMotion.ACTION_DOWN //hands down
     part: the right/left hand
     RelativeAngleHandMotion.PART_LEFT //control left hand
     RelativeAngleHandMotion.PART_RIGHT //control right hand
     RelativeAngleHandMotion.PART_BOTH //control both
     */

    private Spinner svHandRelativeAction;
    private Spinner svHandRelativePart;

    private EditText etHandRelativeSpeed;
    private EditText etHandRelativeAngle;
    private Button tvHandRelativeStart;

    private byte[] relativeAction = {RelativeAngleHandMotion.ACTION_UP, RelativeAngleHandMotion.ACTION_DOWN};
    private byte curRelativeAction;

    private byte[] relativePart = {RelativeAngleHandMotion.PART_LEFT, RelativeAngleHandMotion.PART_RIGHT, RelativeAngleHandMotion.PART_BOTH};
    private byte curRelativePart;

    /**
     Movimiento absoluto, respecto a una posición fija
     speed:1-8
     angle:0-270degree(counterclockwise)
     action: the motion of hands
     AbsoluteAngleHandMotion.ACTION_UP //hands up
     AbsoluteAngleHandMotion.ACTION_DOWN //hands down
     part: the right/left hand
     AbsoluteAngleHandMotion.PART_LEFT //only left
     AbsoluteAngleHandMotion.PART_RIGHT 2 //only right
     AbsoluteAngleHandMotion.PART_BOTH 3 //both
     */
    private Spinner svHandAbsolutePart;

    private EditText etHandAbsoluteSpeed;
    private EditText etHandAbsoluteAngle;
    private Button tvHandAbsoluteStart;

    private byte[] absolutePart = {AbsoluteAngleHandMotion.PART_LEFT, AbsoluteAngleHandMotion.PART_RIGHT, AbsoluteAngleHandMotion.PART_BOTH};
    private byte curAbsolutePart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(HandControl.class);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_control);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);

        // Inicializar Spinners
        svHandNoAngleAction = findViewById(R.id.sv_hand_no_angle_action);
        svHandNoAnglePart = findViewById(R.id.sv_hand_no_angle_part);
        svHandRelativeAction = findViewById(R.id.sv_hand_relative_action);
        svHandRelativePart = findViewById(R.id.sv_hand_relative_part);
        svHandAbsolutePart = findViewById(R.id.sv_hand_absolute_part);

        // Inicializar EditTexts
        etHandNoAngleSpeed = findViewById(R.id.et_hand_no_angle_speed);
        etHandRelativeSpeed = findViewById(R.id.et_hand_relative_speed);
        etHandRelativeAngle = findViewById(R.id.et_hand_relative_angle);
        etHandAbsoluteSpeed = findViewById(R.id.et_hand_absolute_speed);
        etHandAbsoluteAngle = findViewById(R.id.et_hand_absolute_angle);

        // Inicializar Buttons
        tvHandNoAngleStart = findViewById(R.id.tv_hand_no_angle_start);
        tvHandNoAngleEnd = findViewById(R.id.tv_hand_no_angle_end);
        tvHandRelativeStart = findViewById(R.id.tv_hand_relative_start);
        tvHandAbsoluteStart = findViewById(R.id.tv_hand_absolute_start);

        // Inicializar listeners
        initListener();


    }

    @Override
    protected void onMainServiceConnected() {

    }

    private void initListener() {
        //无角度action
        svHandNoAngleAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curNoAngleAction = noAngleAction[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curNoAngleAction = noAngleAction[0];
            }
        });
        //无角度part
        svHandNoAnglePart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curNoAnglePart = noAnglePart[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curNoAnglePart = noAnglePart[0];
            }
        });
        //相对角度action
        svHandRelativeAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curRelativeAction = relativeAction[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curRelativeAction = relativeAction[0];
            }
        });
        //相对角度part
        svHandRelativePart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curRelativePart = relativePart[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curRelativePart = relativePart[0];
            }
        });
        //绝对角度part
        svHandAbsolutePart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curAbsolutePart = absolutePart[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curAbsolutePart = absolutePart[0];
            }
        });
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            //无角度运动
            case R.id.tv_hand_no_angle_start:
                int speed;
                try {
                    speed = Integer.parseInt(etHandNoAngleSpeed.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                }
                NoAngleHandMotion noAngleHandMotion = new NoAngleHandMotion(curNoAnglePart, speed, curNoAngleAction);
                handMotionManager.doNoAngleMotion(noAngleHandMotion);
                break;
            //停止无角度运动
            case R.id.tv_hand_no_angle_end:
                try {
                    speed = Integer.parseInt(etHandNoAngleSpeed.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                }
                noAngleHandMotion = new NoAngleHandMotion(curNoAnglePart, speed, NoAngleHandMotion.ACTION_STOP);
                handMotionManager.doNoAngleMotion(noAngleHandMotion);
                break;
            //相对运动
            case R.id.tv_hand_relative_start:
                int angle;
                try {
                    speed = Integer.parseInt(etHandRelativeSpeed.getText().toString());
                    angle = Integer.parseInt(etHandRelativeAngle.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                    angle = 0;
                }
                RelativeAngleHandMotion relativeAngleHandMotion = new RelativeAngleHandMotion(curRelativePart, speed, curRelativeAction, angle);
                handMotionManager.doRelativeAngleMotion(relativeAngleHandMotion);
                break;
            //绝对运动
            case R.id.tv_hand_absolute_start:
                try {
                    speed = Integer.parseInt(etHandAbsoluteSpeed.getText().toString());
                    angle = Integer.parseInt(etHandAbsoluteAngle.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                    angle = 180;
                }
                AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(curAbsolutePart, speed, angle);
                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                break;
        }
    }

}
