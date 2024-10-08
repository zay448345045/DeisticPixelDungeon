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
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Paralysis;
import com.avmoga.dpixel.actors.buffs.Vertigo;
import com.avmoga.dpixel.effects.Flare;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.effects.particles.ElmoParticle;
import com.avmoga.dpixel.items.scrolls.ScrollOfPsionicBlast;
import com.avmoga.dpixel.items.wands.WandOfBlink;
import com.avmoga.dpixel.items.wands.WandOfDisintegration;
import com.avmoga.dpixel.items.weapon.enchantments.Death;
import com.avmoga.dpixel.levels.CityBossLevel;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.KingSprite;
import com.avmoga.dpixel.sprites.UndeadSprite;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class King extends Mob {

	private static final int MAX_ARMY_SIZE = 8;
	private static final int REGEN = 3;


	{
		name = Messages.get(this, "name");
		spriteClass = KingSprite.class;

		HP = HT = 500;
		EXP = 40;
		defenseSkill = 25; //25

		Undead.count = 0;
	}

	private boolean nextPedestal = true;

	private static final String PEDESTAL = "pedestal";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PEDESTAL, nextPedestal);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		nextPedestal = bundle.getBoolean(PEDESTAL);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 38);
	}

	@Override
	public int attackSkill(Char target) {
		return 32;
	}

	@Override
	public int dr() {
		return 14; //14
	}

	@Override
	public String defenseVerb() {
		return Messages.get(this, "def");
	}

	@Override
	protected boolean getCloser(int target) {
		return canTryToSummon() ? super.getCloser(CityBossLevel
				.pedestal(nextPedestal)) : super.getCloser(target);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return canTryToSummon() ? pos == CityBossLevel.pedestal(nextPedestal)
				: Level.adjacent(pos, enemy.pos);
	}

	private boolean canTryToSummon() {
		if (Undead.count < maxArmySize()) {
			Char ch = Actor.findChar(CityBossLevel.pedestal(nextPedestal));
			return ch == this || ch == null;
		} else {
			return false;
		}
	}

	@Override
	public boolean attack(Char enemy) {
		if (canTryToSummon() && pos == CityBossLevel.pedestal(nextPedestal)) {
			summon();
			return true;
		} else {
			if (Actor.findChar(CityBossLevel.pedestal(nextPedestal)) == enemy) {
				nextPedestal = !nextPedestal;
			}
			return super.attack(enemy);
		}
	}


	@Override
	protected boolean act() {
		boolean result = super.act();

		if (HP < HT) {
			sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			HP = HP + REGEN;
		}
		return result;
	}
	
	
	private void summonLiches (int pos){
		 DwarfLich.spawnAround(pos);
	}
	
	@Override
	public void die(Object cause) {
		            
		int findTomb=Dungeon.hero.pos;
		yell(Messages.get(this, "die", Dungeon.hero.givenName()));
		 for (Mob mob : Dungeon.level.mobs) {
				if (mob instanceof DwarfKingTomb){findTomb=mob.pos;}
		 }
		 
		 summonLiches(findTomb);
		GLog.n(Messages.get(this, "release"));
		 super.die(cause);
							
	}

	private int maxArmySize() {
		return 1 + MAX_ARMY_SIZE * (HT - HP) / HT;
	}

	private void summon() {

		nextPedestal = !nextPedestal;

		sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
		Sample.INSTANCE.play(Assets.SND_CHALLENGE);

		boolean[] passable = Level.passable.clone();
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char) actor).pos] = false;
			}
		}

		int undeadsToSummon = maxArmySize() - Undead.count;

		PathFinder.buildDistanceMap(pos, passable, undeadsToSummon);
		PathFinder.distance[pos] = Integer.MAX_VALUE;
		int dist = 1;

		undeadLabel: for (int i = 0; i < undeadsToSummon; i++) {
			do {
				for (int j = 0; j < Level.getLength(); j++) {
					if (PathFinder.distance[j] == dist) {

						Undead undead = new Undead();
						undead.pos = j;
						GameScene.add(undead);

						WandOfBlink.appear(undead, j);
						new Flare(3, 32).color(0x000000, false).show(
								undead.sprite, 2f);

						PathFinder.distance[j] = Integer.MAX_VALUE;

						continue undeadLabel;
					}
				}
				dist++;
			} while (dist < undeadsToSummon);
		}

		yell(Messages.get(this, "summon"));
		HP += Random.Int(1, HT - HP);
		sprite.emitter().burst(ElmoParticle.FACTORY, 5);
	}

	@Override
	public void notice() {
		super.notice();
		yell(Messages.get(this, "notice"));
	}

	@Override
	public String description() {
		return Messages.get(this, "desc");
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(ToxicGas.class);
		RESISTANCES.add(Death.class);
		RESISTANCES.add(ScrollOfPsionicBlast.class);
		RESISTANCES.add(WandOfDisintegration.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add(Paralysis.class);
		IMMUNITIES.add(Vertigo.class);
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	public static class Undead extends Mob {

		public static int count = 0;

		{
			name = Messages.get(King.class, "dname");
			spriteClass = UndeadSprite.class;

			HP = HT = 50;
			defenseSkill = 15;

			EXP = 0;

			state = WANDERING;
		}

		@Override
		protected void onAdd() {
			count++;
			super.onAdd();
		}

		@Override
		protected void onRemove() {
			count--;
			super.onRemove();
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(12, 16);
		}

		@Override
		public int attackSkill(Char target) {
			return 16;
		}

		@Override
		public int attackProc(Char enemy, int damage) {
			if (Random.Int(MAX_ARMY_SIZE) == 0) {
				Buff.prolong(enemy, Paralysis.class, 1);
			}

			return damage;
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			if (src instanceof ToxicGas) {
				((ToxicGas) src).clear(pos);
			}
		}

		@Override
		public void die(Object cause) {
			super.die(cause);

			if (Dungeon.visible[pos]) {
				Sample.INSTANCE.play(Assets.SND_BONES);
			}
		}

		@Override
		public int dr() {
			return 5;
		}

		@Override
		public String defenseVerb() {
			return Messages.get(King.class, "ddef");
		}

		@Override
		public String description() {
			return Messages.get(King.class, "ddesc");
		}

		private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
		static {
			IMMUNITIES.add(Death.class);
			IMMUNITIES.add(Paralysis.class);
		}

		@Override
		public HashSet<Class<?>> immunities() {
			return IMMUNITIES;
		}
	}
}
