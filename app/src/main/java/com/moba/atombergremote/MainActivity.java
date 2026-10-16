package com.moba.atombergremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private ConsumerIrManager irManager;
    private TextView status;
    private boolean irReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View root = findViewById(R.id.remoteRoot);
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            } else {
                view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            }
            return insets;
        });
        root.requestApplyInsets();
        if (Build.VERSION.SDK_INT >= 28) {
            findViewById(R.id.title).setAccessibilityHeading(true);
        }
        findViewById(R.id.aboutButton).setOnClickListener(view -> showAbout());
        status = findViewById(R.id.status);
        irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
        irReady = detectIr();
        bind(R.id.powerButton, NecProtocol.Command.POWER);
        bind(R.id.speed1Button, NecProtocol.Command.SPEED1);
        bind(R.id.speed2Button, NecProtocol.Command.SPEED2);
        bind(R.id.speed3Button, NecProtocol.Command.SPEED3);
        bind(R.id.speed4Button, NecProtocol.Command.SPEED4);
        bind(R.id.speed5Button, NecProtocol.Command.SPEED5);
        bind(R.id.boostButton, NecProtocol.Command.BOOST);
        bind(R.id.timerButton, NecProtocol.Command.TIMER);
        bind(R.id.sleepButton, NecProtocol.Command.SLEEP);
        bind(R.id.ledButton, NecProtocol.Command.LED);
    }

    private boolean detectIr() {
        try {
            if (irManager == null || !irManager.hasIrEmitter()) {
                status.setText(R.string.no_ir);
                return false;
            }
            ConsumerIrManager.CarrierFrequencyRange[] ranges = irManager.getCarrierFrequencies();
            if (ranges != null) {
                boolean supported = false;
                for (ConsumerIrManager.CarrierFrequencyRange range : ranges) {
                    if (range.getMinFrequency() <= NecProtocol.FREQUENCY
                            && range.getMaxFrequency() >= NecProtocol.FREQUENCY) {
                        supported = true;
                        break;
                    }
                }
                if (!supported) {
                    status.setText(R.string.unsupported_frequency);
                    return false;
                }
            }
            status.setText(R.string.ir_ready);
            return true;
        } catch (RuntimeException exception) {
            status.setText(R.string.ir_unavailable);
            return false;
        }
    }

    private void bind(int id, NecProtocol.Command command) {
        TextView button = findViewById(id);
        button.setEnabled(irReady);
        button.setAlpha(irReady ? 1f : 0.4f);
        button.setOnClickListener(view -> {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            CharSequence label = button.getContentDescription();
            sendIr(command, (label == null ? button.getText() : label).toString());
        });
    }

    private void showAbout() {
        StringBuilder message = new StringBuilder(getString(R.string.credits));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getAssets().open("LICENSE"), StandardCharsets.UTF_8))) {
            message.append("\n\n");
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line).append('\n');
            }
        } catch (IOException exception) {
            message.append("\n\n").append(getString(R.string.license_unavailable));
        }
        new AlertDialog.Builder(this).setTitle(R.string.about_title)
                .setMessage(message.toString())
                .setPositiveButton(android.R.string.ok, null).show();
    }

    private void sendIr(NecProtocol.Command command, String label) {
        if (!irReady) return;
        try {
            irManager.transmit(NecProtocol.FREQUENCY, NecProtocol.encode(command.code));
            status.setText(getString(R.string.sent, label));
        } catch (RuntimeException exception) {
            status.setText(R.string.transmission_failed);
            Toast.makeText(this, R.string.transmission_failed, Toast.LENGTH_LONG).show();
        }
    }
}
