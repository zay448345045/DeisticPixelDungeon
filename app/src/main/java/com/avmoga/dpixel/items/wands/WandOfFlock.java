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
package com.avmoga.dpixel.items.wands;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.mobs.npcs.NPC;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.MagicMissile;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.SheepSprite;
import com.avmoga.dpixel.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfFlock extends Wand {

	{
		name = Messages.get(this, "name");
	}

	@Override
	protected void onZap(int cell) {

		int level = level();

		int n = level + 2;

		if (Actor.findChar(cell) != null && Ballistica.distance > 2) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}

		boolean[] passable = BArray.or(Level.passable, Level.avoid, null);
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char) actor).pos] = false;
			}
		}

		PathFinder.buildDistanceMap(cell, passable, n);
		int dist = 0;

		if (Actor.findChar(cell) != null) {
			PathFinder.distance[cell] = Integer.MAX_VALUE;
			dist = 1;
		}

		float lifespan = level + 3;

		sheepLabel: for (int i = 0; i < n; i++) {
			do {
				for (int j = 0; j < Level.getLength(); j++) {
					if (PathFinder.distance[j] == dist) {

						Sheep sheep = new Sheep();
						sheep.lifespan = lifespan;
						sheep.pos = j;
						GameScene.add(sheep);
						Dungeon.level.mobPress(sheep);

						CellEmitter.get(j).burst(Speck.factory(Speck.WOOL), 4);

						PathFinder.distance[j] = Integer.MAX_VALUE;

						continue sheepLabel;
					}
				}
				dist++;
			} while (dist < n);
		}
	}

	@Override
	protected void fx(int cell, Callback callback) {
		MagicMissile.wool(curUser.sprite.parent, curUser.pos, cell, callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		// TODO: add special effect <sheep transmutation>
	}

	public static class Sheep extends NPC {

		private static final String[] QUOTES = {Messages.get(WandOfFlock.class, "1"), Messages.get(WandOfFlock.class, "2"), Messages.get(WandOfFlock.class, "3"),
				Messages.get(WandOfFlock.class, "4")};

		{
			name = Messages.get(WandOfFlock.class, "sname");
			spriteClass = SheepSprite.class;
		}

		public float lifespan;

		private boolean initialized = false;

		@Override
		protected boolean act() {
			if (initialized) {
				HP = 0;

				destroy();
				sprite.die();

			} else {
				initialized = true;
				spend(lifespan + Random.Float(2));
			}
			return true;
		}

		@Override
		public void damage(int dmg, Object src) {
		}

		@Override
		public void add(Buff buff) {
		}

		@Override
		public String description() {
			return Messages.get(WandOfFlock.class, "sdesc");
		}

		@Override
		public void interact() {
			yell(Random.element(QUOTES));
		}
	}
}
