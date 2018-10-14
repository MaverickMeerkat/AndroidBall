package com.example.drefaeli.myredball;

import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity {
    private int _xDelta;
    private int _yDelta;
    View ball;
    View main;
    GestureDetector gestureDetectorBall;
    GestureDetector gestureDetectorMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = findViewById(R.id.main);
        ball = findViewById(R.id.ball);

        gestureDetectorMain = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            /**
             * Double tap on the main screen outside the ball, brings the ball to tap location
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                ball.setX(e.getX() - ball.getWidth() / 2);
                ball.setY(e.getY() - ball.getHeight() / 2);

                return true;
            }
        });

        gestureDetectorBall = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            /**
             * Creates a bounce animation if ball is tapped twice
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
                ball.startAnimation(animation);
                return true;
            }

            /**
             * Creates a fling effect in boundary of the main screen
             * IllegalArgumentExceptions are suppressed
             * @param e1
             * @param e2
             * @param velocityX
             * @param velocityY
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   final float velocityX, final float velocityY) {
                int maxX = main.getWidth() - ball.getWidth();
                int maxY = main.getHeight() - ball.getHeight();

                try {
                    // avoid limit exception (IllegalArgumentException: Starting value need to be in between min value and max value)
                    FlingAnimation flingX = new FlingAnimation(ball, DynamicAnimation.X);
                    flingX.setStartVelocity(velocityX)
                            .setMinValue(0)
                            .setMaxValue(maxX)
                            .setFriction(1.0f)
                            .start();

                    FlingAnimation flingY = new FlingAnimation(ball, DynamicAnimation.Y);
                    flingY.setStartVelocity(velocityY)
                            .setMinValue(0)
                            .setMaxValue(maxY)
                            .setFriction(1.0f)
                            .start();
                } catch (IllegalArgumentException ex) {
                    // ignore
                }

                return true;
            }
        });

        // touch listener on the main view
        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetectorMain.onTouchEvent(event);
            }
        });

        // touch listener on the ball view
        ball.setOnTouchListener(new View.OnTouchListener() {
            /**
             * Handles both gesture detection, and regular movement
             * @param v
             * @param event
             * @return
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // for double tap on the ball, and fling
                boolean ret = gestureDetectorBall.onTouchEvent(event);

                // for regular movement
                if (!ret) {
                    moveBall(event);
                }
                return true;
            }

            private void moveBall(MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _xDelta = X - (int) ball.getX();
                        _yDelta = Y - (int) ball.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (X - _xDelta >= 0
                                && Y - _yDelta >= 0
                                && X - _xDelta <= main.getWidth() - ball.getWidth()
                                && Y - _yDelta <= main.getHeight() - ball.getHeight()) {
                            ball.setX(X - _xDelta);
                            ball.setY(Y - _yDelta);
                        }
                        break;
                }
            }

        });
    }
}