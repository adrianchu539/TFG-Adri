package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.beans.OperationResult;
import com.sanbot.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion;
import com.sanbot.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.PIRListener;

import java.util.Arrays;
import java.util.List;


/**
 * className: WheelControlActivity
 * function: 轮子控制
 * <p/>
 * create at 2017/5/24 15:37
 *
 * @author gangpeng
 */

public class WheelControlActivity extends TopBaseActivity {



    Spinner svWheelNoAngleAction;
    EditText etWheelNoAngleSpeed;
    EditText etWheelNoAngleDuration;
    Button tvWheelNoAngleStart;
    Button tvWheelNoAngleEndTurn;
    Button tvWheelNoAngleEndRun;
    Spinner svWheelRelativeAction;
    EditText etWheelRelativeSpeed;
    EditText etWheelRelativeAngle;
    Button tvWheelRelativeStart;
    Button tvWheelRelativeEnd;
    EditText etWheelDistanceSpeed;
    EditText etWheelDistance;
    Button tvWheelDistanceStart;
    Button tvWheelDistanceEnd;

    Button movimiento;

    public WheelMotionManager wheelMotionManager;
    //private HardWareManager hardWareManager;

    // VARIABLE QUE CONTROLA SI DETECTA ALGUNA PRESENCIA HUMANA
    // private boolean pirChecked = false;

    /**
     * 无角度运动action
     */
    private byte[] noAngleAction = {NoAngleWheelMotion.ACTION_FORWARD_RUN, NoAngleWheelMotion.ACTION_LEFT_CIRCLE
            , NoAngleWheelMotion.ACTION_RIGHT_CIRCLE, NoAngleWheelMotion.ACTION_TURN_LEFT, NoAngleWheelMotion.ACTION_TURN_RIGHT};
    private byte curNoAngleAction;

    /**
     * 相对运动action
     */
    private byte[] relativeAction = {RelativeAngleWheelMotion.TURN_LEFT, RelativeAngleWheelMotion.TURN_RIGHT};
    private byte curRelativeAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        register(WheelControlActivity.class);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_control);
        //初始化变量
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        // hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);


        svWheelNoAngleAction = findViewById(R.id.sv_wheel_no_angle_action);
        etWheelNoAngleSpeed = findViewById(R.id.et_wheel_no_angle_speed);
        etWheelNoAngleDuration = findViewById(R.id.et_wheel_no_angle_duration);
        tvWheelNoAngleStart = findViewById(R.id.tv_wheel_no_angle_start);
        tvWheelNoAngleEndTurn = findViewById(R.id.tv_wheel_no_angle_end_turn);
        tvWheelNoAngleEndRun = findViewById(R.id.tv_wheel_no_angle_end_run);
        svWheelRelativeAction = findViewById(R.id.sv_wheel_relative_action);
        etWheelRelativeSpeed = findViewById(R.id.et_wheel_relative_speed);
        etWheelRelativeAngle = findViewById(R.id.et_wheel_relative_angle);
        tvWheelRelativeStart = findViewById(R.id.tv_wheel_relative_start);
        tvWheelRelativeEnd = findViewById(R.id.tv_wheel_relative_end);
        etWheelDistanceSpeed = findViewById(R.id.et_wheel_distance_speed);
        etWheelDistance = findViewById(R.id.et_wheel_distance);
        tvWheelDistanceStart = findViewById(R.id.tv_wheel_distance_start);
        tvWheelDistanceEnd = findViewById(R.id.tv_wheel_distance_end);

        movimiento = findViewById(R.id.movimiento);
        initListener();

        //ACTUALIZAR VALOR DEL PIR
        /*
        hardWareManager.setOnHareWareListener(new PIRListener() {
            @Override
            public void onPIRCheckResult(boolean isChecked, int part) {
                pirChecked = isChecked;
                System.out.print((part == 1 ? "Front of the body" : "Backof the body") + "PIR has been triggered");
            }
        });*/

        movimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> indicaciones = Arrays.asList("derecha", "avanza", "izquierda");
                ejecutarIndicaciones(indicaciones);
                //girarDerecha(5, 90);

            }
        });
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        //无角度运动action
        svWheelNoAngleAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curNoAngleAction = noAngleAction[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curNoAngleAction = noAngleAction[0];
            }
        });
        //相对运动action
        svWheelRelativeAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curRelativeAction = relativeAction[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curRelativeAction = relativeAction[0];
            }
        });
    }

    // Método para avanzar
    public void avanzar(int velocidad, int distancia) {
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, velocidad, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

    }


    // Método para girar a la izquierda
    public void  girarIzquierda(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
    }


    // Método para girar a la derecha
    public void girarDerecha(int velocidad, int angulo) {
        RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, velocidad, angulo);
        wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
    }




    private void ejecutarIndicaciones(List<String> indicaciones)  {
        for  (String indicacion : indicaciones) {
            System.out.println(indicacion);
            switch (indicacion) {
                case "avanza":
                    avanzar(5, 100);
                    break;
                case "izquierda":
                    girarIzquierda(5, 90);
                    break;
                case "derecha":
                    girarDerecha(5, 90);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onMainServiceConnected() {

    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            //无角度运动
            case R.id.tv_wheel_no_angle_start:
                int speed;
                int duration;
                try {
                    speed = Integer.parseInt(etWheelNoAngleSpeed.getText().toString());
                    duration = Integer.parseInt(etWheelNoAngleDuration.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                    duration = 10;
                }
                NoAngleWheelMotion noAngleWheelMotion = new NoAngleWheelMotion(curNoAngleAction, speed, duration);
                wheelMotionManager.doNoAngleMotion(noAngleWheelMotion);
                break;
            //无角度停止转圈
            case R.id.tv_wheel_no_angle_end_turn:
                noAngleWheelMotion = new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_STOP_TURN, 1, 0);
                wheelMotionManager.doNoAngleMotion(noAngleWheelMotion);
                break;
            //无角度停止行走
            case R.id.tv_wheel_no_angle_end_run:
                noAngleWheelMotion = new NoAngleWheelMotion(NoAngleWheelMotion.ACTION_STOP_RUN, 1, 1);
                wheelMotionManager.doNoAngleMotion(noAngleWheelMotion);
                break;
            //相对角度运动
            case R.id.tv_wheel_relative_start:
                int angle;
                try {
                    speed = Integer.parseInt(etWheelRelativeSpeed.getText().toString());
                    angle = Integer.parseInt(etWheelRelativeAngle.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                    angle = 90;
                }
                RelativeAngleWheelMotion relativeAngleWheelMotion = new RelativeAngleWheelMotion(curRelativeAction, speed, angle);
                wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                break;
            //停止相对角度运动
            case R.id.tv_wheel_relative_end:
                relativeAngleWheelMotion = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_STOP, 1, 1);
                wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                break;
            //距离运动
            case R.id.tv_wheel_distance_start:
                int distance;
                try {
                    speed = Integer.parseInt(etWheelDistanceSpeed.getText().toString());
                    distance = Integer.parseInt(etWheelDistance.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    speed = 5;
                    distance = 10;
                }
                DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, speed, distance);
                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                break;
            //结束距离运动
            case R.id.tv_wheel_distance_end:
                distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_STOP_RUN, 1, 1);
                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                break;
        }
    }
}
