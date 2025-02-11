package com.flipkart.android.proteus.view.custom

import android.content.Context
import android.widget.ProgressBar

/**
 * A simple {@link ProgressBar} subclass that is pre-configured for horizontal style.
 *
 * `HorizontalProgressBar` is a convenience class that creates a standard Android {@link ProgressBar}
 * but with the style already set to be horizontal. This eliminates the need to explicitly specify the
 * horizontal progress bar style every time you want to use one in your layouts or code.
 *
 * It's essentially a shortcut for quickly obtaining a horizontal-style progress bar.
 *
 * **Key Features:**
 *
 * *   **Horizontal Style by Default:** Instantiates a {@link ProgressBar} with the style `android.R.attr.progressBarStyleHorizontal`
 *     automatically applied in its constructor.
 * *   **Simplified Usage:**  Reduces boilerplate code when you need a horizontal progress bar. You don't have to remember or
 *     set the style attribute programmatically or in XML every time.
 * *   **Standard ProgressBar Functionality:**  Retains all the standard capabilities of a regular Android {@link ProgressBar},
 *     allowing you to control progress, visibility, and other properties in the same way.
 * *   **Constructor for Context:** Provides a simple constructor that takes a {@link Context} to create an instance.
 *
 * **Usage Scenario:**
 *
 * Use `HorizontalProgressBar` whenever you need a horizontal progress bar in your Android application. It simplifies
 * the creation process, especially if you frequently use horizontal progress bars and want to avoid repeating style declarations.
 *
 * This class is a very straightforward example of how you can create specialized UI components by extending
 * standard Android widgets and pre-configuring them with specific default settings or styles for convenience.
 */
open class HorizontalProgressBar : ProgressBar {

    /**
     * Constructor for {@link HorizontalProgressBar}.
     *
     * Initializes the progress bar with a horizontal style.
     *
     * @param context The Android {@link Context}.
     */
    constructor(context: Context) : super(context, null, android.R.attr.progressBarStyleHorizontal)
}