package com.runtimexml.utils;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DimensionConvertor {

    // -- Initialize dimension string to constant lookup.
    public static final Map<String, Integer> dimensionConstantLookup = initDimensionConstantLookup();
    // -- Initialize pattern for dimension string.
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$");
    public static Map<String, Float> cached = new HashMap<>();

    @NonNull
    private static @Unmodifiable Map<String, Integer> initDimensionConstantLookup() {
        return Map.of("px", TypedValue.COMPLEX_UNIT_PX, "dip", TypedValue.COMPLEX_UNIT_DIP, "dp", TypedValue.COMPLEX_UNIT_DIP, "sp", TypedValue.COMPLEX_UNIT_SP, "pt", TypedValue.COMPLEX_UNIT_PT, "in", TypedValue.COMPLEX_UNIT_IN, "mm", TypedValue.COMPLEX_UNIT_MM);
    }

    public static int stringToDimensionPixelSize(@NonNull String dimension, DisplayMetrics metrics, ViewGroup parent, boolean horizontal) {
        if (dimension.endsWith("%")) {
            float pct = Float.parseFloat(dimension.substring(0, dimension.length() - 1)) / 100.0f;
            return (int) (pct * (horizontal ? parent.getMeasuredWidth() : parent.getMeasuredHeight()));
        }
        return stringToDimensionPixelSize(dimension, metrics);
    }

    public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        final float f;
        if (cached.containsKey(dimension)) {
            f = cached.get(dimension);
        } else {
            InternalDimension internalDimension = stringToInternalDimension(dimension);
            final float value = internalDimension.value;
            f = TypedValue.applyDimension(internalDimension.unit, value, metrics);
            cached.put(dimension, f);
        }
        final int res = (int) (f + 0.5f);
        if (res != 0) return res;
        if (f == 0) return 0;
        if (f > 0) return 1;
        return -1;
    }

    public static float stringToDimension(String dimension, DisplayMetrics metrics) {
        if (cached.containsKey(dimension)) return cached.get(dimension);
        // -- Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        float val = TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
        cached.put(dimension, val);
        return val;
    }

    @NonNull
    @Contract("_ -> new")
    private static InternalDimension stringToInternalDimension(String dimension) {
        // -- Match target against pattern.
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            // -- Match found.
            // -- Extract value.
            float value = Float.parseFloat(Objects.requireNonNull(matcher.group(1)));
            // -- Extract dimension units.
            String unit = Objects.requireNonNull(matcher.group(3)).toLowerCase();
            // -- Get Android dimension constant.
            Integer dimensionUnit = dimensionConstantLookup.get(unit);
            if (dimensionUnit == null) {
                // -- Invalid format.
                throw new NumberFormatException();
            } else {
                // -- Return valid dimension.
                return new InternalDimension(value, dimensionUnit);
            }
        } else {
            Log.e("DimensionConverter", "Invalid number format: " + dimension);
            // -- Invalid format.
            throw new NumberFormatException();
        }
    }

    private static class InternalDimension {
        float value;
        int unit;

        public InternalDimension(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }
    }
}
