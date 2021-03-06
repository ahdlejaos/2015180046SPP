package kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.game.BaseGame;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.interfaces.GameObject;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.objects.AnimSprite;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.res.Metrics;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.R;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.objects.Sprite;
import kr.ac.tukorea.ge.sgp02.s12345678.dragonflight.framework.res.BitmapPool;

public class Fighter extends Sprite implements GameObject {
    private static final String TAG = Fighter.class.getSimpleName();
    private RectF targetRect = new RectF();


//    private float angle;
    private float dx, dy;
    private float tx, ty;
    private float elapsedTimeForFire;
    private float fireInterval = 1.0f / 10;

    private static Bitmap targetBitmap;

    private int bulletLevel;
    private int life;
    private int bulletNum;
    private int coin;
    private boolean onSkill;

    public Fighter(float x, float y) {
        super(x, y, R.dimen.fighter_radius, R.mipmap.player);
        setTargetPosition(x, y);

        targetBitmap = BitmapPool.get(R.mipmap.target);

        fireInterval = Metrics.floatValue(R.dimen.fighter_fire_interval);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, dstRect, null);
        if (dx != 0 || dy != 0) {
            canvas.drawBitmap(targetBitmap, null, targetRect, null);
        }
    }

    public void update() {
        float frameTime = BaseGame.getInstance().frameTime;
        elapsedTimeForFire += frameTime;
        if (elapsedTimeForFire >= fireInterval) {
            fire();
            elapsedTimeForFire -= fireInterval;
        }

        if (dx == 0)
            return;

        float dx = this.dx * frameTime;
        boolean arrived = false;
        if ((dx > 0 && x + dx > tx) || (dx < 0 && x + dx < tx)) {
            dx = tx - x;
            x = tx;
            arrived = true;
        } else {
            x += dx;
        }
        dstRect.offset(dx, 0);
        if (arrived) {
            this.dx = 0;
        }
    }

    public void setTargetPosition(float tx, float ty) {
        this.tx = tx;
        this.ty = y;
        targetRect.set(tx - radius/2, y - radius/2,
                tx + radius/2, y + radius/2);
//        angle = (float) Math.atan2(ty - y, tx - x);
        dx = Metrics.size(R.dimen.fighter_speed);
        if (tx < x) {
            dx = -dx;
        }
//        dy = (float) (dist * Math.sin(angle));
    }

    public void fire() {
        MainGame game = MainGame.get();
        int score = game.score.get();
        if (score > 100000) score = 100000;
        float power = 10 + score / 1000;
        Bullet bullet = Bullet.get(x, y, power);
        game.add(MainGame.Layer.bullet, bullet);
    }
}
