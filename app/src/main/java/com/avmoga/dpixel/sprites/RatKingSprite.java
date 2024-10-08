/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.avmoga.dpixel.sprites;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.items.weapon.missiles.ChestMimic;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.scenes.GameScene;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

import java.util.Calendar;

public class RatKingSprite extends MobSprite {
	@SuppressWarnings("unused")
	private static final float DURATION = 2f;
	private Animation cast;
	public boolean festive;

	public RatKingSprite() {
		super();

		final Calendar calendar = Calendar.getInstance();
		// once a year the rat king feels a bit festive!
		festive = (calendar.get(Calendar.MONTH) == 11 && calendar
				.get(Calendar.WEEK_OF_MONTH) > 2);

		final int c = festive ? 8 : 0;

		texture(Assets.RATKING);

		TextureFilm frames = new TextureFilm(texture, 16, 17);

		idle = new Animation(2, true);
		idle.frames(frames, c + 0, c + 0, c + 0, c + 1);

		run = new Animation(10, true);
		run.frames(frames, c + 2, c + 3, c + 4, c + 5, c + 6);

		attack = new Animation(15, false);
		attack.frames(frames, c + 0);

		die = new Animation(10, false);
		die.frames(frames, c + 0);

		play(idle);
	}

	@Override
	public void move(int from, int to) {

		place(to);

		play(run);
		turnTo(from, to);

		isMoving = true;

		if (Level.water[to]) {
			GameScene.ripple(to);
		}

		ch.onMotionComplete();
	}

	@Override
	public void attack(int cell) {
		if (!Level.adjacent(cell, ch.pos)) {
			//Char enemy = Actor.findChar(cell);
			((MissileSprite) parent.recycle(MissileSprite.class)).reset(ch.pos,
					cell, new ChestMimic(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
						}
					});


			play(cast);
			turnTo(ch.pos, cell);

		} else {

			super.attack(cell);

		}
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == run) {
			isMoving = false;
			idle();
		} else {
			super.onComplete(anim);
		}
	}
}
