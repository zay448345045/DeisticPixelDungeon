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
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Poison;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.AdamantArmor;
import com.avmoga.dpixel.items.Gold;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.CrabKingSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class CrabKing extends Mob {

	private static final int JUMP_DELAY = 5;

	{
		name = Messages.get(this, "name");
		spriteClass = CrabKingSprite.class;
		baseSpeed = 2f;

		HP = HT = 300*adj(2);
		EXP = 20;
		defenseSkill = 60;
	}

	private int timeToJump = JUMP_DELAY;
	
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange(30, 80);
	}

	@Override
	public int attackSkill(Char target) {
		return 35;
	}

	@Override
	public int dr() {
		return 10;
	}

	@Override
	protected boolean act() {
		boolean result = super.act();

		int regen=Math.round(Dungeon.shellCharge/10);
		
		if (HP < HT && Dungeon.shellCharge>10) {
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			HP = HP + regen;
			Dungeon.shellCharge-=regen;
			GLog.n(Messages.get(CrabKing.class, "heal"));
		}
		return result;
	}
	
	@Override
	public void die(Object cause) {

		
		
		GameScene.bossSlain();
		
		Dungeon.level.drop(new Gold(Random.Int(100, 2000)), pos).sprite.drop();
		Dungeon.level.drop(new AdamantArmor(), pos).sprite.drop();
		Dungeon.crabkingkilled=true;
	
		
		super.die(cause);

		yell(Messages.get(CrabKing.class, "die"));
					
	}

	@Override
	protected boolean getCloser(int target) {
		if (Level.fieldOfView[target]) {
			jump();
			super.notice();
			yell("没用的，你打不到我！！！"+Dungeon.hero.givenName());
			return true;
		} else {
			return super.getCloser(target);
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		timeToJump--;
		if (timeToJump <= 0 && Level.adjacent(pos, enemy.pos)) {
			jump();
			return true;
		} else {
			return super.doAttack(enemy);
		}
	}

	private void jump() {
		timeToJump = JUMP_DELAY;
		
		int newPos;
		do {
			newPos = Random.Int(Level.getLength());
		} while (!Level.fieldOfView[newPos] || !Level.passable[newPos]
				|| Level.adjacent(newPos, enemy.pos)
				|| Actor.findChar(newPos) != null);

		sprite.move(pos, newPos);
		move(newPos);

		if (Dungeon.visible[newPos]) {
			CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
			Sample.INSTANCE.play(Assets.SND_PUFF);
		}

		spend(1 / speed());
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(CrabKing.class, "notice", Dungeon.hero.givenName()));
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(ToxicGas.class);
		RESISTANCES.add(Poison.class);
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
