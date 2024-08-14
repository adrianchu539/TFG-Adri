package com.example.sanbotapp.modulos;

import android.util.Log;

import com.example.sanbotapp.robotControl.HandsControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.beans.LED;

import java.util.ArrayList;
import java.util.List;

public class ModuloEmocional {
    private List<Integer> codigoEmocionesRobot = new ArrayList<Integer>();
    private List<Integer> codigoEmocionesUsuario = new ArrayList<Integer>();
    private ArrayList<String> emocionesRobot = new ArrayList<String>();
    private ArrayList<String> emocionesUsuario = new ArrayList<String>();
    private HandsControl handsControl;
    private HeadControl headControl;
    private HardwareControl hardwareControl;
    private SystemControl systemControl;

    // Gestión emociones robot
    private String emociones[] = {"ÉXTASIS", "ALEGRÍA", "SERENIDAD", "ADMIRACIÓN", "CONFIANZA", "APROBACIÓN",
            "TERROR", "MIEDO", "TEMOR", "ASOMBRO", "SORPRESA", "DISTRACCIÓN", "PENA", "TRISTEZA", "MELANCOLÍA",
            "AVERSIÓN", "ASCO", "ABURRIMIENTO", "FURIA", "IRA", "ENFADO", "VIGILANCIA", "ANTICIPACIÓN", "INTERÉS", "OPTIMISMO",
            "AMOR", "SUMISIÓN", "SUSTO", "DECEPCIÓN", "REMORDIMIENTO", "DESPRECIO", "AGRESIVIDAD", "ESPERANZA", "CULPA", "CURIOSIDAD",
            "DESESPERACIÓN", "INCREDULIDAD", "ENVIDIA", "CINISMO", "ORGULLO", "ANSIEDAD", "DELEITE", "SENTIMENTALISMO", "VERGÜENZA",
            "INDIGNACIÓN", "PESIMISMO", "MORBOSIDAD", "DOMINANCIA"};

    // Constructor
    public ModuloEmocional(HandsControl handsControl, HeadControl headControl, HardwareControl hardwareControl, SystemControl systemControl){
        this.handsControl = handsControl;
        this.headControl = headControl;
        this.hardwareControl = hardwareControl;
        this.systemControl = systemControl;
    }

    // Función que separa la interpretación emocional
    // de la respuesta a la consulta y devuelve esta última
    public String separarRespuestaGPT(String respuesta){
        // Respuesta sin interpretación emocional
        String respuestaGPT = respuesta.substring(respuesta.indexOf("]")+1, respuesta.length());
        Log.d("Respuesta GPT", respuestaGPT);
        return respuestaGPT;
    }

    // Función que separa la interpretación emocional del usuario y la del robot
    public void gestionEmocional(String respuestaGPT) throws InterruptedException {
        String[] cadena = separarEmociones(respuestaGPT);

        sentimientosUsuario(cadena[0]);

        sentimientosRobot(cadena[1]);

        for (int i = 0; i < codigoEmocionesRobot.size(); i++) {
            emocionesRobot.add(" " + emociones[codigoEmocionesRobot.get(i) - 1]);
        }
        for (int i = 0; i < codigoEmocionesUsuario.size(); i++) {
            emocionesUsuario.add(emociones[codigoEmocionesUsuario.get(i) - 1]);
        }
    }

    // Función que separa la consulta de la respuesta
    // de la interpretación emocional y devuelve esta última
    private String[] separarEmociones(String respuesta){
        // Apartamos la valoración emocional para utilizarla más tarde
        String valoracionEmocional = respuesta.substring(0, respuesta.indexOf("]")+1);
        Log.d("valoracion emocional", valoracionEmocional);
        String segmentos[] = valoracionEmocional.split("/");

        return segmentos;
    }

    // Función que almacena las emociones del usuario
    // recogiéndolas de la valoración emocional
    private void sentimientosUsuario(String emocionesUsuario){
        ArrayList<Integer> codigoEmocionesUsuario = new ArrayList<>();
        String usuarioFinal = emocionesUsuario.substring(emocionesUsuario.indexOf("(") + 1, emocionesUsuario.indexOf(")"));
        Log.d("Respuesta usuario final", usuarioFinal);
        String[] sentimientosUsuario = usuarioFinal.split("-");
        for(String sentimientos : sentimientosUsuario){
            codigoEmocionesUsuario.add(Integer.valueOf(sentimientos));
        }
    };

    // Función que almacena las emociones del robot
    // recogiéndolas de la valoración emocional
    private void sentimientosRobot(String emocionesRobot) throws InterruptedException {
        ArrayList<Integer> codigoEmocionesRobot = new ArrayList<>();
        String robotFinal = emocionesRobot.substring(emocionesRobot.indexOf("(") + 1, emocionesRobot.indexOf(")"));
        Log.d("Respuesta robot final", robotFinal);
        String[] sentimientosRobot = robotFinal.split("-");
        for(String sentimientos : sentimientosRobot){
            codigoEmocionesRobot.add(Integer.valueOf(sentimientos));
        }
        expresividadGestual(codigoEmocionesRobot);
    };

    // Función que utiliza las funciones del robot como la expresión facial,
    // el movimiento de brazos y cabeza
    private void expresividadGestual(List<Integer> codigoEmocionesRobot) throws InterruptedException {
        int indexSentimiento;
        if(codigoEmocionesRobot.size()>1){
            indexSentimiento = codigoEmocionesRobot.get((int) Math.floor(Math.random() * codigoEmocionesRobot.size()));
        }
        else{
            indexSentimiento = codigoEmocionesRobot.get(0);
        }
        Log.d("indexSentimiento", String.valueOf(indexSentimiento));
        if(indexSentimiento>=0 && indexSentimiento<=2){ // ALEGRÍA
            systemControl.cambiarEmocion(EmotionsType.SMILE);
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_YELLOW);
            handsControl.controlBasicoBrazos(HandsControl.AccionesBrazos.LEVANTAR_BRAZO,  HandsControl.TipoBrazo.AMBOS);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
            handsControl.reiniciar();
        }
        else if(indexSentimiento>=3 && indexSentimiento<=5){ // CONFIANZA
            systemControl.cambiarEmocion(EmotionsType.PRISE);
        }
        else if(indexSentimiento>=6 && indexSentimiento<=8){ // MIEDO
            systemControl.cambiarEmocion(EmotionsType.GRIEVANCE);
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_PURPLE);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
        }
        else if(indexSentimiento>=9 && indexSentimiento<=10){ // SORPRESA
            systemControl.cambiarEmocion(EmotionsType.SURPRISE);
            handsControl.controlBasicoBrazos(HandsControl.AccionesBrazos.LEVANTAR_BRAZO,  HandsControl.TipoBrazo.AMBOS);
            Thread.sleep(3000);
            handsControl.reiniciar();
        }
        else if(indexSentimiento==11){ // DISTRACCIÓN
            systemControl.cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento>=12 && indexSentimiento<=13){ // TRISTEZA
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_BLUE);
            headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ABAJO);
            systemControl.cambiarEmocion(EmotionsType.GOODBYE);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
            headControl.reiniciar();
        }
        else if(indexSentimiento==14){ // MELANCOLIA
            systemControl.cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento>=15 && indexSentimiento<=16){ // ASCO
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_GREEN);
            systemControl.cambiarEmocion(EmotionsType.ARROGANCE);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
        }
        else if(indexSentimiento==17){ // ABURRIMIENTO
            systemControl.cambiarEmocion(EmotionsType.SLEEP);
        }
        else if(indexSentimiento>=18 && indexSentimiento<=19){ // IRA
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_RED);
            systemControl.cambiarEmocion(EmotionsType.ANGRY);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
        }
        else if(indexSentimiento==20){ // ENFADO
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_RED);
            systemControl.cambiarEmocion(EmotionsType.ABUSE);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
        }
        else if(indexSentimiento>=21 && indexSentimiento<=23){ // INTERÉS
            systemControl.cambiarEmocion(EmotionsType.QUESTION);
        }
        else if(indexSentimiento==24){ // OPTIMISMO
            systemControl.cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==25){ // AMOR
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_PINK);
            headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ABAJO);
            systemControl.cambiarEmocion(EmotionsType.LAUGHTER);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
            headControl.reiniciar();
        }
        else if(indexSentimiento==26){ // SUMISION
            systemControl.cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==27){ // SUSTO
            systemControl.cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==28){ // DECEPCION
            systemControl.cambiarEmocion(EmotionsType.GOODBYE);
        }
        else if(indexSentimiento==29){ // REMORDIMIENTO
            systemControl.cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==30){ // DESPRECIO
            systemControl.cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==31){ // AGRESIVIDAD
            systemControl.cambiarEmocion(EmotionsType.ANGRY);
        }
        else if(indexSentimiento==32){ // ESPERANZA
            systemControl.cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==33){ // CULPA
            systemControl.cambiarEmocion(EmotionsType.SWEAT);
        }
        else if(indexSentimiento==34){ // CURIOSIDAD
            systemControl.cambiarEmocion(EmotionsType.SNICKER);
        }
        else if(indexSentimiento==35){ // DESESPERACION
            systemControl.cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==36){ // INCREDULIDAD
            systemControl.cambiarEmocion(EmotionsType.SURPRISE);
        }
        else if(indexSentimiento==37){ // ENVIDIA
            systemControl.cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==38){ // CINISMO
            systemControl.cambiarEmocion(EmotionsType.ARROGANCE);
        }
        else if(indexSentimiento==39){ // ORGULLO
            systemControl.cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==40){ // ANSIEDAD
            systemControl.cambiarEmocion(EmotionsType.GRIEVANCE);
        }
        else if(indexSentimiento==41){ // DELEITE
            systemControl.cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==42){ // SENTIMENTALISMO
            systemControl.cambiarEmocion(EmotionsType.SMILE);
        }
        else if(indexSentimiento==43){ // VERGUENZA
            systemControl.cambiarEmocion(EmotionsType.SHY);
            hardwareControl.encenderLED(LED.PART_ALL, LED.MODE_PINK);
            headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ABAJO);
            Thread.sleep(3000);
            hardwareControl.apagarLED(LED.PART_ALL);
            headControl.reiniciar();
        }
        else if(indexSentimiento==44){ // INDIGNACIÓN
            systemControl.cambiarEmocion(EmotionsType.ABUSE);
        }
        else if(indexSentimiento==45){ // PESIMISMO
            systemControl.cambiarEmocion(EmotionsType.GOODBYE);
            headControl.controlBasicoCabeza(HeadControl.AccionesCabeza.ABAJO);
            Thread.sleep(3000);
            headControl.reiniciar();
        }
        else if(indexSentimiento==46){ // MORBOSIDAD
            systemControl.cambiarEmocion(EmotionsType.FAINT);
        }
        else if(indexSentimiento==47){ // DOMINANCIA
            systemControl.cambiarEmocion(EmotionsType.ARROGANCE);
        }
    }

}
