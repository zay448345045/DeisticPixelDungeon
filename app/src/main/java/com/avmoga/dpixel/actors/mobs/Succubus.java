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
package com.avmoga.dpixel.actors.mobs;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Charm;
import com.avmoga.dpixel.actors.buffs.Light;
import com.avmoga.dpixel.actors.buffs.Sleep;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.food.Meat;
import com.avmoga.dpixel.items.scrolls.ScrollOfLullaby;
import com.avmoga.dpixel.items.wands.WandOfBlink;
import com.avmoga.dpixel.items.weapon.enchantments.Leech;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Succubus extends Mob {

	private static final int BLINK_DELAY = 5;

	private int delay = 0;

	{
		name = Messages.get(this, "name");
		spriteClass = SuccubusSprite.class;

		HP = HT = 80+(adj(0)*Random.NormalIntRange(5, 7));
		defenseSkill = 25+adj(1);
		viewDistance = Light.DISTANCE;

		EXP = 12;
		maxLvl = 25;

		loot = new ScrollOfLullaby();
		lootChance = 0.05f;
		
		lootOther = new Meat();
		lootChanceOther = 0.1f; // by default, see die()
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(15, 25+adj(0));
	}

	@Override
	public int attackProc(Char enemy, int damage) {

		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Charm.class, Charm.durationFactor(enemy)
					* Random.IntRange(3, 7)).object = id();
			enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART),
					0.2f, 5);
			Sample.INSTANCE.play(Assets.SND_CHARMS);
		}

		return damage;
	}

	@Override
	protected boolean getCloser(int target) {
		if (Level.fieldOfView[target] && Level.distance(pos, target) > 2
				&& delay <= 0) {

			blink(target);
			spend(-1 / speed());
			return true;

		} else {

			delay--;
			return super.getCloser(target);

		}
	}

	private void blink(int target) {

		int cell = Ballistica.cast(pos, target, true, true);
		
		if (Actor.findChar(cell) != null && Ballistica.distance > 1) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}
		
       if (!Level.pit[cell]){
		WandOfBlink.appear(this, cell);
       }

		delay = BLINK_DELAY;
	}

	@Override
	public int attackSkill(Char target) {
		return 40+adj(1);
	}

	@Override
	public int dr() {
		return 10+adj(1);
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(Leech.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Sleep.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
