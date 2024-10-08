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
import com.avmoga.dpixel.Journal;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.Blob;
import com.avmoga.dpixel.actors.blobs.ToxicGas;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Roots;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.effects.CellEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.potions.PotionOfStrength;
import com.avmoga.dpixel.items.quest.CorpseDust;
import com.avmoga.dpixel.items.wands.Wand;
import com.avmoga.dpixel.items.wands.WandOfAmok;
import com.avmoga.dpixel.items.wands.WandOfAvalanche;
import com.avmoga.dpixel.items.wands.WandOfBlink;
import com.avmoga.dpixel.items.wands.WandOfDisintegration;
import com.avmoga.dpixel.items.wands.WandOfFirebolt;
import com.avmoga.dpixel.items.wands.WandOfLightning;
import com.avmoga.dpixel.items.wands.WandOfPoison;
import com.avmoga.dpixel.items.wands.WandOfRegrowth;
import com.avmoga.dpixel.items.wands.WandOfSlowness;
import com.avmoga.dpixel.items.wands.WandOfTelekinesis;
import com.avmoga.dpixel.levels.PrisonLevel;
import com.avmoga.dpixel.levels.Room;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.plants.Plant;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.sprites.WandmakerSprite;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.utils.Utils;
import com.avmoga.dpixel.windows.WndQuest;
import com.avmoga.dpixel.windows.WndWandmaker;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Wandmaker extends NPC {

	{
		name = Messages.get(Wandmaker.class, "name");
		spriteClass = WandmakerSprite.class;
	}

	private static final String TXT_BERRY1 = Messages.get(Wandmaker.class, "berry1");

	private static final String TXT_DUST1 = Messages.get(Wandmaker.class, "dust1");

	private static final String TXT_BERRY2 = Messages.get(Wandmaker.class, "berry2", Dungeon.hero.givenName());

	private static final String TXT_DUST2 = Messages.get(Wandmaker.class, "dust2", Dungeon.hero.givenName());

	@Override
	protected boolean act() {
		throwItem();
		return super.act();
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public String defenseVerb() {
		return Messages.get(Tinkerer1.class, "def");
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
		if (Quest.given) {

			Item item = Quest.alternative ? Dungeon.hero.belongings
					.getItem(CorpseDust.class) : Dungeon.hero.belongings
					.getItem(Rotberry.Seed.class);
			if (item != null) {
				GameScene.show(new WndWandmaker(this, item));
			} else {
				tell(Quest.alternative ? TXT_DUST2 : TXT_BERRY2,
						Dungeon.hero.givenName());
			}

		} else {

			Quest.placeItem();

			if (Quest.given)
				tell(Quest.alternative ? TXT_DUST1 : TXT_BERRY1);

			Journal.add(Journal.Feature.WANDMAKER);
		}
	}

	private void tell(String format, Object... args) {
		GameScene.show(new WndQuest(this, Utils.format(format, args)));
	}

	@Override
	public String description() {
		return Messages.get(Wandmaker.class, "desc");
	}

	public static class Quest {

		private static boolean spawned;

		private static boolean alternative;

		private static boolean given;

		public static Wand wand1;
		public static Wand wand2;

		public static void reset() {
			spawned = false;

			wand1 = null;
			wand2 = null;
		}

		private static final String NODE = "wandmaker";

		private static final String SPAWNED = "spawned";
		private static final String ALTERNATIVE = "alternative";
		private static final String GIVEN = "given";
		private static final String WAND1 = "wand1";
		private static final String WAND2 = "wand2";

		public static void storeInBundle(Bundle bundle) {

			Bundle node = new Bundle();

			node.put(SPAWNED, spawned);

			if (spawned) {

				node.put(ALTERNATIVE, alternative);

				node.put(GIVEN, given);

				node.put(WAND1, wand1);
				node.put(WAND2, wand2);
			}

			bundle.put(NODE, node);
		}

		public static void restoreFromBundle(Bundle bundle) {

			Bundle node = bundle.getBundle(NODE);

			if (!node.isNull() && (spawned = node.getBoolean(SPAWNED))) {

				alternative = node.getBoolean(ALTERNATIVE);

				given = node.getBoolean(GIVEN);

				wand1 = (Wand) node.get(WAND1);
				wand2 = (Wand) node.get(WAND2);
			} else {
				reset();
			}
		}

		public static void spawn(PrisonLevel level, Room room) {
			if (!spawned && Dungeon.depth > 6
					&& Random.Int(10 - Dungeon.depth) == 0) {

				Wandmaker npc = new Wandmaker();
				do {
					npc.pos = room.random();
				} while (level.map[npc.pos] == Terrain.ENTRANCE
						|| level.map[npc.pos] == Terrain.SIGN);
				level.mobs.add(npc);
				Actor.occupyCell(npc);

				spawned = true;
				alternative = Random.Int(2) == 0;

				given = false;

				switch (Random.Int(5)) {
				case 0:
					wand1 = new WandOfAvalanche();
					break;
				case 1:
					wand1 = new WandOfDisintegration();
					break;
				case 2:
					wand1 = new WandOfFirebolt();
					break;
				case 3:
					wand1 = new WandOfLightning();
					break;
				case 4:
					wand1 = new WandOfPoison();
					break;
				}
				wand1.random().upgrade();

				switch (Random.Int(5)) {
				case 0:
					wand2 = new WandOfAmok();
					break;
				case 1:
					wand2 = new WandOfBlink();
					break;
				case 2:
					wand2 = new WandOfRegrowth();
					break;
				case 3:
					wand2 = new WandOfSlowness();
					break;
				case 4:
					wand2 = new WandOfTelekinesis();
					break;
				}
				wand2.random().upgrade();
			}
		}

		public static void placeItem() {
			if (alternative) {

				ArrayList<Heap> candidates = new ArrayList<Heap>();
				for (Heap heap : Dungeon.level.heaps.values()) {
					if (heap.type == Heap.Type.SKELETON
							&& !Dungeon.visible[heap.pos]) {
						candidates.add(heap);
					}
				}

				if (candidates.size() > 0) {
					Random.element(candidates).drop(new CorpseDust());
					given = true;
				} else {
					int pos = Dungeon.level.randomRespawnCell();
					while (Dungeon.level.heaps.get(pos) != null) {
						pos = Dungeon.level.randomRespawnCell();
					}

					if (pos != -1) {
						Heap heap = Dungeon.level.drop(new CorpseDust(), pos);
						heap.type = Heap.Type.SKELETON;
						heap.sprite.link();
						given = true;
					}
				}

			} else {

				int shrubPos = Dungeon.level.randomRespawnCell();
				while (Dungeon.level.heaps.get(shrubPos) != null) {
					shrubPos = Dungeon.level.randomRespawnCell();
				}

				if (shrubPos != -1) {
					Dungeon.level.plant(new Rotberry.Seed(), shrubPos);
					given = true;
				}

			}
		}

		public static void complete() {
			wand1 = null;
			wand2 = null;

			Journal.remove(Journal.Feature.WANDMAKER);
		}
	}

	public static class Rotberry extends Plant {

		private static final String TXT_DESC = Messages.get(Wandmaker.class, "berrydesc");

		{
			image = 7;
			plantName = Messages.get(Wandmaker.class, "berryname");
		}

		@Override
		public void activate(Char ch) {
			super.activate(ch);

			GameScene.add(Blob.seed(pos, 100, ToxicGas.class));

			Dungeon.level.drop(new Seed(), pos).sprite.drop();

			if (ch != null) {
				Buff.prolong(ch, Roots.class, TICK * 3);
			}
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}

		public static class Seed extends Plant.Seed {
			{
				plantName = Messages.get(Wandmaker.class, "berryname");

				name = Messages.get(Wandmaker.class, "seedname", plantName);
				image = ItemSpriteSheet.SEED_ROTBERRY;

				plantClass = Rotberry.class;
				alchemyClass = PotionOfStrength.class;
			}

			@Override
			public boolean doPickUp(Hero hero) {
				if (super.doPickUp(hero)) {

					if (Dungeon.level != null) {
						for (Mob mob : Dungeon.level.mobs) {
							mob.beckon(Dungeon.hero.pos);
						}

						GLog.w(Messages.get(Wandmaker.class, "pickup"));
						CellEmitter.center(Dungeon.hero.pos).start(
								Speck.factory(Speck.SCREAM), 0.3f, 3);
						Sample.INSTANCE.play(Assets.SND_CHALLENGE);
					}

					return true;
				} else {
					return false;
				}
			}

			@Override
			public String desc() {
				return TXT_DESC;
			}
		}
	}
}
