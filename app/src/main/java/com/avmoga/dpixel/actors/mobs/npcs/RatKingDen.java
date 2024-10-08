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


import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.sprites.RatKingSprite;

public class RatKingDen extends NPC {

	{
		name = Messages.get(RatKing.class, "name");
		spriteClass = RatKingSprite.class;

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
		
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (state == SLEEPING) {
			notice();
			yell(Messages.get(RatKing.class, "yone"));
			state = WANDERING;

		} else {
			yell(Messages.get(RatKing.class, "yell"));
		}
	}

	@Override
	public String description() {
		return ((RatKingSprite) sprite).festive ? Messages.get(RatKing.class, "descone")
				: Messages.get(RatKing.class, "desctwo");
	}
}
