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
package com.avmoga.dpixel.plants;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Barkskin;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroSubClass;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.particles.LeafParticle;
import com.avmoga.dpixel.items.Dewdrop;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.artifacts.SandalsOfNature;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.sprites.PlantSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
//import com.avmoga.dpixel.effects.SpellSprite;
//import com.avmoga.dpixel.items.food.Food;

public class Plant implements Bundlable {

	public String plantName;

	public int image;
	public int pos;

	public PlantSprite sprite;
	
	//private static final float TIME_TO_EAT = 3f;

	public void activate(Char ch) {

		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
			Buff.affect(ch, Barkskin.class).level(ch.HT / 3);
		}

		wither();
	}

	public static boolean checkPhase(int cell) {
	Plant plant = Dungeon.level.plants.get(cell);
	if (plant instanceof Phaseshift || plant instanceof Flytrap) {
		return true; 
		}	else {
			return false;
			}
	}
	
	public void wither() {
		Dungeon.level.uproot(pos);

		sprite.kill();
		if (Dungeon.visible[pos]) {
			CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
		}

		if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {

			int naturalismLevel = 0;
			SandalsOfNature.Naturalism naturalism = Dungeon.hero
					.buff(SandalsOfNature.Naturalism.class);
			if (naturalism != null) {
				naturalismLevel = naturalism.level() + 1;
			}

			if (Random.Int(5 - (naturalismLevel / 2)) == 0) {
				Item seed = Generator.random(Generator.Category.SEED);

				if (seed instanceof BlandfruitBush.Seed) {
					if (Random.Int(15)
							- Dungeon.limitedDrops.blandfruitSeed.count >= 0) {
						Dungeon.level.drop(seed, pos).sprite.drop();
						Dungeon.limitedDrops.blandfruitSeed.count++;
					}
				} else
					Dungeon.level.drop(seed, pos).sprite.drop();
			}
			if (Random.Int(5 - naturalismLevel) == 0) {
				Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
			}
		}
	}

	private static final String POS = "pos";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		pos = bundle.getInt(POS);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(POS, pos);
	}

	public String desc() {
		return null;
	}

	public static class Seed extends Item {

		public static final String AC_PLANT = Messages.get(Plant.class, "ac_plant");

		private static final String TXT_INFO = Messages.get(Plant.class, "info");

		private static final float TIME_TO_PLANT = 1f;

		{
			stackable = true;
			defaultAction = AC_THROW;
		}

		protected Class<? extends Plant> plantClass;
		protected String plantName;

		public Class<? extends Item> alchemyClass;

		@Override
		public ArrayList<String> actions(Hero hero) {
			ArrayList<String> actions = super.actions(hero);
			actions.add(AC_PLANT);
		//	actions.add(Food.AC_EAT);
			return actions;
		}

		@Override
		protected void onThrow(int cell) {
			if (this instanceof Phaseshift.Seed && Phaseshift.checkWater()){
				GLog.w(Messages.get(Plant.class, "prevent1"));
				super.onThrow(cell);
			}	else if (this instanceof Flytrap.Seed && Flytrap.checkWater()){
				GLog.w(Messages.get(Plant.class, "prevent2"));
				 super.onThrow(cell);
			}	else if (Dungeon.level.map[cell] == Terrain.ALCHEMY || Level.pit[cell]) {
				super.onThrow(cell);
			} else {
				Dungeon.level.plant(this, cell);
			}
		}

		@Override
		public void execute(Hero hero, String action) {
			if (action.equals(AC_PLANT)) {

				hero.spend(TIME_TO_PLANT);
				hero.busy();
				((Seed) detach(hero.belongings.backpack)).onThrow(hero.pos);

				hero.sprite.operate(hero.pos);
				
		//	} else if (action.equals(Food.AC_EAT)) {
			//	detach(hero.belongings.backpack);
//
	//			hero.sprite.operate(hero.pos);
		//		hero.busy();
			//	SpellSprite.show(hero, SpellSprite.FOOD);
			//	Sample.INSTANCE.play(Assets.SND_EAT);

			//	hero.spend(TIME_TO_EAT);

			
			} else {

				super.execute(hero, action);

			}
		}

		public Plant couch(int pos) {
			try {
				if (Dungeon.visible[pos]) {
					Sample.INSTANCE.play(Assets.SND_PLANT);
				}
				Plant plant = plantClass.newInstance();
				plant.pos = pos;
				return plant;
			} catch (Exception e) {
				return null;
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
		public int price() {
			return 10 * quantity;
		}

		@Override
		public String info() {
			return String.format(TXT_INFO, Utils.indefinite(plantName), desc());
		}
	}
}
