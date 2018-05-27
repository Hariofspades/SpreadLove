/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hariofspades.spreadlove

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator

import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.BaseTransformationController
import com.google.ar.sceneform.ux.RotationController
import com.google.ar.sceneform.ux.ScaleController
import com.google.ar.sceneform.ux.TransformationGestureDetector
import com.google.ar.sceneform.ux.TransformationSystem
import com.google.ar.sceneform.ux.TranslationController

import java.util.ArrayList

/** Node demonstrating rotation and transformations.  */
class RotatingNode : Node(), Node.OnTapListener {
    // We'll use Property Animation to make this node rotate.

    private var rotationAnimation: ObjectAnimator? = null
    private var degreesPerSecond = 90.0f

    private var lastSpeedMultiplier = 1.0f

    private val animationDuration: Long
        get() = (1000 * 360 / (degreesPerSecond * speedMultiplier)).toLong()

    private val speedMultiplier: Float
        get() = 1.0f

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        // Animation hasn't been set up.
        if (rotationAnimation == null) {
            return
        }

        // Check if we need to change the speed of rotation.
        val speedMultiplier = speedMultiplier

        // Nothing has changed. Continue rotating at the same speed.
        if (lastSpeedMultiplier == speedMultiplier) {
            return
        }

        if (speedMultiplier == 0.0f) {
            rotationAnimation!!.pause()
        } else {
            rotationAnimation!!.resume()

            val animatedFraction = rotationAnimation!!.animatedFraction
            rotationAnimation!!.duration = animationDuration
            rotationAnimation!!.setCurrentFraction(animatedFraction)
        }
        lastSpeedMultiplier = speedMultiplier
    }

    /** Sets rotation speed  */
    fun setDegreesPerSecond(degreesPerSecond: Float) {
        this.degreesPerSecond = degreesPerSecond
    }

    override fun onActivate() {
        startAnimation()
    }

    override fun onDeactivate() {
        stopAnimation()
    }

    private fun startAnimation() {
        if (rotationAnimation != null) {
            return
        }
        rotationAnimation = createAnimator()
        rotationAnimation!!.target = this
        rotationAnimation!!.duration = animationDuration
        rotationAnimation!!.start()
    }

    private fun stopAnimation() {
        if (rotationAnimation == null) {
            return
        }
        rotationAnimation!!.cancel()
        rotationAnimation = null
    }

    /** Returns an ObjectAnimator that makes this node rotate.  */
    private fun createAnimator(): ObjectAnimator {
        // Node's setLocalRotation method accepts Quaternions as parameters.
        // First, set up orientations that will animate a circle.
        val orientation1 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
        val orientation2 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 120f)
        val orientation3 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 240f)
        val orientation4 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 360f)

        val rotationAnimation = ObjectAnimator()
        rotationAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4)

        // Next, give it the localRotation property.
        rotationAnimation.propertyName = "localRotation"

        // Use Sceneform's QuaternionEvaluator.
        rotationAnimation.setEvaluator(QuaternionEvaluator())

        //  Allow rotationAnimation to repeat forever
        rotationAnimation.repeatCount = ObjectAnimator.INFINITE
        rotationAnimation.repeatMode = ObjectAnimator.RESTART
        rotationAnimation.interpolator = LinearInterpolator()
        rotationAnimation.setAutoCancel(true)

        return rotationAnimation
    }


}
