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
package com.avmoga.dpixel.items.quest;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Hunger;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.Bat;
import com.avmoga.dpixel.actors.mobs.DwarfKingTomb;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.weapon.Weapon;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSprite.Glowing;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.ui.BuffIndicator;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Pickaxe extends Weapon {

	public static final String AC_MINE = Messages.get(Pickaxe.class, "ac_mine");
	public static final float TIME_TO_MINE = 2;
	private static final String TXT_NO_VEIN = Messages.get(Pickaxe.class, "no_vein");

	private static final Glowing BLOODY = new Glowing(0x550000);

	{
		name = "pickaxe";
		image = ItemSpriteSheet.PICKAXE;

		unique = true;

		defaultAction = AC_MINE;

		STR = 14;
		MIN = 10;
		MAX = 22;
	}

	public boolean bloodStained = false;
	
	
		
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_MINE);
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {

		if (action == AC_MINE) {

			if (Dungeon.depth < 11 || Dungeon.depth > 15) {
				GLog.w(TXT_NO_VEIN);
				return;
			}

			for (int i = 0; i < Level.NEIGHBOURS8.length; i++) {

				final int pos = hero.pos + Level.NEIGHBOURS8[i];
				if (Dungeon.level.map[pos] == Terrain.WALL_DECO) {

					hero.spend(TIME_TO_MINE);
					hero.busy();

					hero.sprite.attack(pos, new Callback() {

						@Override
						public void call() {

							CellEmitter.center(pos).burst(
									Speck.factory(Speck.STAR), 7);
							Sample.INSTANCE.play(Assets.SND_EVOKE);

							Level.set(pos, Terrain.WALL);
							GameScene.updateMap(pos);

							DarkGold gold = new DarkGold();
							if (gold.doPickUp(Dungeon.hero)) {
								GLog.i(Hero.TXT_YOU_NOW_HAVE, gold.name());
							} else {
								Dungeon.level.drop(gold, hero.pos).sprite
										.drop();
							}

							Hunger hunger = hero.buff(Hunger.class);
							if (hunger != null && !hunger.isStarving()) {
								hunger.satisfy(-Hunger.STARVING / 10);
								BuffIndicator.refreshHero();
							}

							hero.onOperateComplete();
						}
					});

					return;
				}
			}

			GLog.w(TXT_NO_VEIN);

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		if (!bloodStained && defender instanceof Bat && (defender.HP <= damage)) {
			bloodStained = true;
			updateQuickslot();
		}
		if (defender instanceof DwarfKingTomb){
			
			defender.damage(Random.Int(100,200), this);
		}
	}

	private static final String BLOODSTAINED = "bloodStained";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);

		bundle.put(BLOODSTAINED, bloodStained);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		bloodStained = bundle.getBoolean(BLOODSTAINED);
	}

	@Override
	public Glowing glowing() {
		return bloodStained ? BLOODY : null;
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}
}
