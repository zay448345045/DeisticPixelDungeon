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
package com.avmoga.dpixel.actors.mobs.npcs;


import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.particles.ElmoParticle;
import com.avmoga.dpixel.items.DzewaRatKing;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.items.weapon.melee.Spork;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.sprites.RatKingSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class RatKing extends NPC {

	{
		name = Messages.get(RatKing.class, "name");
		spriteClass = RatKingSprite.class;
		HP=HT=100;
		state = SLEEPING;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public float speed() {
		return 2f;
	}

	@Override
	protected Char chooseEnemy() {
		return null;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public void add(Buff buff) {
	}

	@Override
	public boolean reset() {
		return true;
	}


	@Override
	public void interact() {

		int checkChests = 0;
		int length = Level.getLength();
		for (int i = 0; i < length; i++) {
			Heap chest = Dungeon.level.heaps.get(i);
			if (chest != null && chest.chestCheck()) {
				checkChests++;
			}
		}

		Spork spork = Dungeon.hero.belongings.getItem(Spork.class);
		//RoyalSpork royalspork = Dungeon.hero.belongings.getItem(RoyalSpork.class);

		sprite.turnTo(pos, Dungeon.hero.pos);
		if (state == SLEEPING) {
			//notice();
			yell(Messages.get(RatKing.class, "yone"));
			yell(Messages.get(RatKing.class, "ytwo"));
			state = WANDERING;
		} else if (Statistics.deepestFloor > 10 && checkChests >= Dungeon.ratChests && spork == null) {
			yell(Messages.get(RatKing.class, "notsteal"));
			Dungeon.sporkAvail = true;
		} else if (checkChests < Dungeon.ratChests) {
			Dungeon.sporkAvail = false;
			yell(Messages.get(RatKing.class, "steal"));
			destroy();
			sprite.killAndErase();
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
			Sample.INSTANCE.play(Assets.SND_CURSED);
			GLog.n("你偷了鼠王的东西，鼠王被你活生生气死了");
			Dungeon.level.drop(new DzewaRatKing(), pos).sprite.drop();
		} else if (spork != null) {
			yell(Messages.get(RatKing.class, "found"));

		} else {
			yell(Messages.get(RatKing.class, "yell"));
		}
	}

	@Override
	public String description() {
		return ((RatKingSprite) sprite).festive ? Messages.get(RatKing.class, "descone") : Messages.get(RatKing.class, "desctwo");
	}
}
