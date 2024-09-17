package com.example.labcronometro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime, tvLapTimes;
    private Button btnStartStop, btnLap, btnReset;

    private long startTime = 0; // Tiempo de inicio
    private long elapsedTime = 0; // Tiempo transcurrido
    private Handler handler = new Handler();
    private boolean running = false; // Estado del cronómetro
    private ArrayList<String> lapTimes = new ArrayList<>(); // Lista de los tiempos
    private int lapCount = 0; // Número de vueltas
    private long totalTime = 0; // Tiempo total acumulado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias lay
        tvTime = findViewById(R.id.tvTime);
        tvLapTimes = findViewById(R.id.tvLapTimes);
        btnStartStop = findViewById(R.id.btnStartStop);
        btnLap = findViewById(R.id.btnLap);
        btnReset = findViewById(R.id.btnReset);

        // botón "Iniciar/Detener"
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    stopTimer();
                } else {
                    startTimer();
                }
            }
        });

        //  botón "Marcar vuelta"
        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lapCount < 5) {
                    markLap(); // Marca vuelta
                }
                if (lapCount == 5) {
                    stopTimer(); // Detiene el cronómetro tras 5 vueltas
                    showLapTimes(); // Muestra los tiempos de las vueltas
                }
            }
        });

        //  botón "Reiniciar"
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    // Iniciar cronómetro
    private void startTimer() {
        startTime = System.currentTimeMillis() - elapsedTime; // Para continuar desde el tiempo anterior si se pausó
        handler.postDelayed(runnable, 0);
        running = true;
        btnStartStop.setText("Detener");
        btnLap.setEnabled(true); // Habilitar "Marcar vuelta"
        btnReset.setEnabled(false); // Deshabilitar "Reiniciar" mientras corre
        tvLapTimes.setVisibility(View.GONE);
        lapTimes.clear();
        lapCount = 0;
        totalTime = 0;
    }

    // Detener cronómetro
    private void stopTimer() {
        handler.removeCallbacks(runnable); // Detener los callbacks
        elapsedTime = System.currentTimeMillis() - startTime; // Guardar el tiempo transcurrido
        running = false;
        btnStartStop.setText("Iniciar");
        btnLap.setEnabled(false); // Deshabilitar "Marcar vuelta" cuando está detenido
        btnReset.setEnabled(true); // Habilitar "Reiniciar" cuando se detiene
    }

    // Reiniciar cronómetro y vueltas
    private void resetTimer() {
        tvTime.setText("00:00:00");
        tvLapTimes.setVisibility(View.GONE);
        lapTimes.clear();
        lapCount = 0;
        totalTime = 0;
        elapsedTime = 0; // Restablecer el tiempo transcurrido
        btnReset.setEnabled(false); // Deshabilitar "Reiniciar" tras el reseteo
        btnLap.setEnabled(false); // Asegurar que "Marcar vuelta" esté deshabilitado
    }

    // Marcar vuelta
    private void markLap() {
        lapCount++;
        long lapTime = System.currentTimeMillis() - startTime; // Tiempo de la vuelta actual
        totalTime += lapTime; // Agregar al total
        lapTimes.add(String.format("Vuelta %d - Parcial: %s, Total: %s", lapCount, formatTime(lapTime), formatTime(totalTime)));
        startTime = System.currentTimeMillis();  // Reiniciar el tiempo para la próxima vuelta
    }

    // Mostrar los tiempos de las vueltas
    private void showLapTimes() {
        StringBuilder lapText = new StringBuilder();
        for (String lap : lapTimes) {
            lapText.append(lap).append("\n");
        }
        tvLapTimes.setText(lapText.toString());
        tvLapTimes.setVisibility(View.VISIBLE);
    }

    // Actualizar el tiempo del cronómetro
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long currentElapsedTime = System.currentTimeMillis() - startTime;
            tvTime.setText(formatTime(currentElapsedTime));
            handler.postDelayed(this, 1000);
        }
    };

    // Formatear el tiempo en hh:mm:ss
    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60)) % 60;
        int hours = (int) (millis / (1000 * 60 * 60));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}

