package com.example.anomalbrowser.funny_birds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.anomalbrowser.R;


public class GameView extends View{

    public Sprite playerBird;
    public Sprite enemyBird, enemyBird2, enemyBird3;
    private Sprite bonus;

    private int viewWidth;
    private int viewHeight;

    private int points = -78;
    private int level = 0;
    private int nextLevel = 100;
    private int bacPoints;

    private int timerInterval = 30;
    private int enemyTimer = 30;
    private int bactimerInterval;
    private int bacenemyTimer;


    private Boolean isPause = false;
    private String pauseText = "";

    private String resultText = "";

    private Boolean isGO = false;

    private String startText = "";

    public GameView(Context context) {
        super(context);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth()/5;
        int h = b.getHeight()/3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i ==0 && j == 0) {
                    continue;
                }
                if (i ==2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird = new Sprite(-300, 1000, -300, 0, firstFrame, b);
        enemyBird2 = new Sprite(-300, 1000, -300, 0, firstFrame, b);
        enemyBird3 = new Sprite(-300, 1000, -300, 0, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue;
                }

                if (i ==2 && j == 0) {
                    continue;
                }

                enemyBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
                enemyBird2.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
                enemyBird3.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }



        b = BitmapFactory.decodeResource(getResources(), R.drawable.bonus);
        w = b.getWidth();
        h = b.getHeight();
        firstFrame = new Rect(0, 0, 5*w, h);

        bonus = new Sprite(0, 50, -300, 0, firstFrame, b);



        Timer t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);
        enemyBird2.draw(canvas);
        enemyBird3.draw(canvas);
        bonus.draw(canvas);
        bonus.update(timerInterval);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 200, 70, p);
        canvas.drawText(level + "", viewWidth - 300, 70, p);
        canvas.drawText("PAUSE", 20, 70, p);

        Paint pPause = new Paint();
        pPause.setAntiAlias(true);
        pPause.setTextSize(200.0f);
        pPause.setColor(Color.WHITE);
        pPause.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(pauseText, canvas.getWidth() / 2, (int) ((canvas.getHeight() / 2) - ((pPause.descent() + pPause.ascent()) / 2)) - 150, pPause);

        Paint pStart = new Paint();
        pStart.setAntiAlias(true);
        pStart.setTextSize(200.0f);
        pStart.setColor(Color.GREEN);
        pStart.setStyle(Paint.Style.FILL);
        pStart.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(startText, canvas.getWidth() / 2, canvas.getHeight() / 2 + 275, pStart);

        Paint pRes = new Paint();
        pRes.setAntiAlias(true);
        pRes.setTextSize(100.0f);
        pRes.setColor(Color.WHITE);
        pRes.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(resultText, canvas.getWidth() / 2, (int) ((canvas.getHeight() / 2) - ((pPause.descent() + pPause.ascent()) / 2)) - 25, pRes);
    }

    protected void update () {
        playerBird.update(timerInterval);
        enemyBird.update(enemyTimer);
        enemyBird2.update(enemyTimer);
        enemyBird3.update(enemyTimer);


        if (isPause) {
            this.pauseText = "PAUSE";
            startText = "CLICK TO START";

        }
        else if (isGO) {
            this.pauseText = "GAME OVER";
            resultText = "YOUR RESULT: " + level + "  " + points;
            startText = "CLICK TO START";

        }
        else {
            this.pauseText = "";
            startText = "";
            resultText = "";

        }


        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }


        if (enemyBird3.getX() < - enemyBird3.getFrameWidth()) {
            teleportEnemy3();
            points +=10;
        }

        if (enemyBird3.intersect(playerBird)) {
            teleportEnemy3();
            points -= 40;
        }
        if (enemyBird2.getX() < - enemyBird2.getFrameWidth()) {
            teleportEnemy2();
            points +=10;
        }

        if (enemyBird2.intersect(playerBird)) {
            teleportEnemy2();
            points -= 40;
        }
        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy();
            points +=10;
        }

        if (enemyBird.intersect(playerBird)) {
            teleportEnemy ();
            points -= 40;
        }
        if (bonus.getX() < - bonus.getFrameWidth())  {
            teleportBonus ();
        }
        if (bonus.intersect(playerBird)) {
            teleportBonus ();
            points += nextLevel / 2;
        }
        if (points >= nextLevel){
            nextLevel += 50;
            points = 0;
            level +=1;
            enemyTimer *= 1.2;
        }
        else if (points < -100){
            timerInterval = 0;
            enemyTimer = 0;
            isGO = true;
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*


        int x = (int)event.getX();
        int y = (int)event.getY();
        if (isPause) {
            if (x < viewWidth && y < viewHeight) {
                isPause = false;
                timerInterval = bactimerInterval;
                enemyTimer = bacenemyTimer;
                points = bacPoints;
            }
        }
        if (x < viewWidth / 8 && y < viewHeight / 18) {
            bactimerInterval = timerInterval;
            bacenemyTimer = enemyTimer;
            bacPoints = points;
            timerInterval = 0;
            enemyTimer = 0;
            isPause = true;
        }
         */

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)  {
            if (isPause) {
                if (event.getX() < viewWidth && event.getY() < viewHeight) {
                    isPause = false;
                    timerInterval = bactimerInterval;
                    enemyTimer = bacenemyTimer;
                    points = bacPoints;
                }
            } else if (isGO) {
                if (event.getX() < viewWidth && event.getY() < viewHeight) {
                    isGO = false;
                    teleportEnemy();
                    teleportEnemy2();
                    teleportEnemy3();
                    teleportBonus();
                    timerInterval = 30;
                    enemyTimer = 30;
                    points = 0;
                    level = 0;
                    nextLevel = 0;
                    nextLevel = 100;
                }
            }
            if ((event.getY() <= viewHeight / 9 && event.getX() <= viewWidth / 12)) {
                bactimerInterval = timerInterval;
                bacenemyTimer = enemyTimer;
                bacPoints = points;
                timerInterval = 0;
                enemyTimer = 0;
                isPause = true;
//                points += 1;
            }

            if ((event.getY() >= enemyBird.getBoundingBoxRect().top && event.getY() <= enemyBird.getBoundingBoxRect().bottom ) && ((event.getX() >= enemyBird.getBoundingBoxRect().left && event.getX() <= enemyBird.getBoundingBoxRect().right))) {
                teleportEnemy();
                points += 20;
            }
            else if ((event.getY() >= enemyBird2.getBoundingBoxRect().top && event.getY() <= enemyBird2.getBoundingBoxRect().bottom) && ((event.getX() >= enemyBird2.getBoundingBoxRect().left  && event.getX() <= enemyBird2.getBoundingBoxRect().right ))) {
                teleportEnemy2();
                points += 20;
            }
            else if ((event.getY() >= enemyBird3.getBoundingBoxRect().top && event.getY() <= enemyBird3.getBoundingBoxRect().bottom) && ((event.getX() >= enemyBird3.getBoundingBoxRect().left  && event.getX() <= enemyBird3.getBoundingBoxRect().right ))) {
                teleportEnemy3();
                points += 20;
            }
            else if (event.getY() <= playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
//                points--;
            }
            else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
//                points--;
            }
        }

        return true;
    }


    private void teleportEnemy () {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }
    private void teleportEnemy2 () {
        enemyBird2.setX(viewWidth + Math.random() * 500);
        enemyBird2.setY(Math.random() * (viewHeight - enemyBird2.getFrameHeight()));
    }
    private void teleportEnemy3 () {
        enemyBird3.setX(viewWidth + Math.random() * 500);
        enemyBird3.setY(Math.random() * (viewHeight - enemyBird3.getFrameHeight()));
    }
    private void teleportBonus () {
        bonus.setX(viewWidth + Math.random() * 2000);
        bonus.setY(Math.random() * (viewHeight - bonus.getFrameHeight()));
    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            update ();
        }

        @Override
        public void onFinish() {

        }
    }
}
