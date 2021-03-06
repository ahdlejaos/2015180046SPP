package kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.game;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.R;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.objects.AnimSprite;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.game.BaseGame;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.res.BitmapPool;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.interfaces.BoxCollidable;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.util.Gauge;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.res.Metrics;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.interfaces.Recyclable;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.game.RecycleBin;

public class Enemy extends AnimSprite implements BoxCollidable, Recyclable {
    public static final float FRAMES_PER_SECOND = 10.0f;
    private static final String TAG = Enemy.class.getSimpleName();
    public static float size;
    protected int level;
    protected float life, maxLife;
    protected Gauge gauge;
    protected float dy;
    protected RectF boundingBox = new RectF();
    protected static int[] bitmapIds = {
            R.mipmap.enemy01,R.mipmap.enemy02,R.mipmap.enemy03,
            R.mipmap.boss01,R.mipmap.boss02,R.mipmap.boss03,

    };
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = bitmapIds.length;


    public static Enemy get(int level, float x, float speed) {
        Enemy enemy = (Enemy) RecycleBin.get(Enemy.class);
        if (enemy != null) {

            enemy.set(level, x, speed);
            return enemy;
        }
        return new Enemy(level, x, speed);
    }

    private void set(int level, float x, float speed) {
        bitmap = BitmapPool.get(bitmapIds[level - 1]);
        this.x = x;
        this.y = -size;
        this.dy = speed;
        this.level = level;
        life = maxLife = level * 10;
        gauge.setValue(1.0f);
    }
    private Enemy(int level, float x, float speed) {
        super(x, -size, size, size, bitmapIds[level - 1], FRAMES_PER_SECOND, 4);
        this.level = level;

        dy = speed;
        life = maxLife = level * 10;
        gauge = new Gauge(
                Metrics.size(R.dimen.enemy_gauge_fg_width), R.color.enemy_gauge_fg,
                Metrics.size(R.dimen.enemy_gauge_bg_width), R.color.enemy_gauge_bg,
                size * 0.9f
        );
        gauge.setValue(1.0f);
        Log.d(TAG, "Created: " + this);
    }

    @Override
    public void update() {
//        super.update();

        float frameTime = BaseGame.getInstance().frameTime;
        y += dy * frameTime;
        setDstRectWithRadius();
        boundingBox.set(dstRect);
        boundingBox.inset(size/16, size/16);
        if (dstRect.top > Metrics.height) {
            BaseGame.getInstance().remove(this);

        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        gauge.draw(canvas, x, y + size * 0.5f);
    }

    @Override
    public RectF getBoundingRect() {
        return boundingBox;
    }

    @Override
    public void finish() {

    }

    public int getScore() {
        return level * level * 100;
    }

    public boolean decreaseLife(float power) {
        life -= power;
        if (life <= 0) return true;

        gauge.setValue((float)life / maxLife);
        return false;
    }
}
