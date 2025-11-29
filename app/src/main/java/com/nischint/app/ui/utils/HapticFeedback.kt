package com.nischint.app.ui.utils

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Haptic feedback types
 */
enum class HapticType {
    LIGHT_CLICK,      // Light tap feedback
    MEDIUM_CLICK,     // Medium tap feedback
    HEAVY_CLICK,      // Heavy tap feedback
    SUCCESS,          // Success action feedback
    ERROR,            // Error action feedback
    SELECTION         // Selection feedback
}

/**
 * Perform haptic feedback
 */
@Composable
fun performHapticFeedback(type: HapticType = HapticType.MEDIUM_CLICK) {
    val view = LocalView.current
    val context = LocalContext.current
    
    val hapticConstant = when (type) {
        HapticType.LIGHT_CLICK -> HapticFeedbackConstants.VIRTUAL_KEY
        HapticType.MEDIUM_CLICK -> HapticFeedbackConstants.KEYBOARD_TAP
        HapticType.HEAVY_CLICK -> HapticFeedbackConstants.LONG_PRESS
        HapticType.SUCCESS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.KEYBOARD_TAP
        }
        HapticType.ERROR -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.REJECT
        } else {
            HapticFeedbackConstants.KEYBOARD_TAP
        }
        HapticType.SELECTION -> HapticFeedbackConstants.TEXT_HANDLE_MOVE
    }
    
    view.performHapticFeedback(hapticConstant)
}

/**
 * Perform haptic feedback with vibration (for stronger feedback)
 */
@Composable
fun performHapticVibration(duration: Long = 50) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(duration)
    }
}

/**
 * Composable hook for haptic feedback
 */
@Composable
fun rememberHapticFeedback(): (HapticType) -> Unit {
    val view = LocalView.current
    val context = LocalContext.current
    
    return { type: HapticType ->
        val hapticConstant = when (type) {
            HapticType.LIGHT_CLICK -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticType.MEDIUM_CLICK -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticType.HEAVY_CLICK -> HapticFeedbackConstants.LONG_PRESS
            HapticType.SUCCESS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
            HapticType.ERROR -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.REJECT
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
            HapticType.SELECTION -> HapticFeedbackConstants.TEXT_HANDLE_MOVE
        }
        view.performHapticFeedback(hapticConstant)
    }
}

