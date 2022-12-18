package com.example.eatingtimes;

import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

import java.time.LocalTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeObject {
    private TextView textView;
    private SwitchCompat switchCompat;
    private LocalTime localTime = LocalTime.of(0,0);
    private int index;
    private boolean enabled = true;

    public TextView getTextView() {
        return textView;
    }

    public SwitchCompat getSwitchCompat() {
        return switchCompat;
    }

    public int getIndex() {
        return index;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        textView.setAlpha(this.enabled ? 1f : .5f);
    }

    public TimeObject(int index, TextView textView, SwitchCompat switchCompat) {
        this.index = index;
        this.textView = textView;
        this.switchCompat = switchCompat;
    }

}
