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
package com.avmoga.dpixel.levels.painters;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.actors.hero.Belongings;
import com.avmoga.dpixel.actors.mobs.Mob;
import com.avmoga.dpixel.actors.mobs.npcs.ImpShopkeeper;
import com.avmoga.dpixel.actors.mobs.npcs.Shopkeeper;
import com.avmoga.dpixel.items.Ankh;
import com.avmoga.dpixel.items.Bomb;
import com.avmoga.dpixel.items.BookOfDead;
import com.avmoga.dpixel.items.BookOfLife;
import com.avmoga.dpixel.items.BookOfTranscendence;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.items.Honeypot;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.Stylus;
import com.avmoga.dpixel.items.Torch;
import com.avmoga.dpixel.items.Weightstone;
import com.avmoga.dpixel.items.armor.LeatherArmor;
import com.avmoga.dpixel.items.armor.MailArmor;
import com.avmoga.dpixel.items.armor.ScaleArmor;
import com.avmoga.dpixel.items.artifacts.Artifact;
import com.avmoga.dpixel.items.artifacts.TimekeepersHourglass;
import com.avmoga.dpixel.items.bags.ArtifactBox;
import com.avmoga.dpixel.items.bags.PotionBandolier;
import com.avmoga.dpixel.items.bags.ScrollHolder;
import com.avmoga.dpixel.items.bags.WandHolster;
import com.avmoga.dpixel.items.food.Food;
import com.avmoga.dpixel.items.potions.Potion;
import com.avmoga.dpixel.items.potions.PotionOfHealing;
import com.avmoga.dpixel.items.scrolls.Scroll;
import com.avmoga.dpixel.items.scrolls.ScrollOfIdentify;
import com.avmoga.dpixel.items.scrolls.ScrollOfMagicMapping;
import com.avmoga.dpixel.items.scrolls.ScrollOfRemoveCurse;
import com.avmoga.dpixel.items.wands.Wand;
import com.avmoga.dpixel.items.weapon.melee.BattleAxe;
import com.avmoga.dpixel.items.weapon.melee.Longsword;
import com.avmoga.dpixel.items.weapon.melee.Mace;
import com.avmoga.dpixel.items.weapon.melee.Quarterstaff;
import com.avmoga.dpixel.items.weapon.melee.Spear;
import com.avmoga.dpixel.items.weapon.melee.Sword;
import com.avmoga.dpixel.items.weapon.missiles.CurareDart;
import com.avmoga.dpixel.items.weapon.missiles.IncendiaryDart;
import com.avmoga.dpixel.items.weapon.missiles.Javelin;
import com.avmoga.dpixel.items.weapon.missiles.Shuriken;
import com.avmoga.dpixel.levels.LastShopLevel;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Room;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.plants.Plant;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class ShopPainter extends Painter {

	private static int pasWidth;
	private static int pasHeight;

	private static ArrayList<Item> itemsToSpawn;

	public static void paint(Level level, Room room) {

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.EMPTY_SP);

		pasWidth = room.width() - 2;
		pasHeight = room.height() - 2;
		int per = pasWidth * 2 + pasHeight * 2;

		if (itemsToSpawn == null)
			generateItems();

		int pos = xy2p(room, room.entrance()) + (per - itemsToSpawn.size()) / 2;
		for (Item item : itemsToSpawn) {

			Point xy = p2xy(room, (pos + per) % per);
			int cell = xy.x + xy.y * Level.getWidth();

			if (level.heaps.get(cell) != null) {
				do {
					cell = room.random();
				} while (level.heaps.get(cell) != null);
			}

			level.drop(item, cell).type = Heap.Type.FOR_SALE;

			pos++;
		}

		placeShopkeeper(level, room);

		for (Room.Door door : room.connected.values()) {
			door.set(Room.Door.Type.REGULAR);
		}

		itemsToSpawn = null;
	}

	private static void generateItems() {

		itemsToSpawn = new ArrayList<Item>();

		switch (Dungeon.depth) {
			case 1:case 6:
			itemsToSpawn.add((Random.Int(2) == 0 ? new Quarterstaff()
					: new Spear()).identify());
			itemsToSpawn.add(Random.Int(2) == 0 ? new IncendiaryDart()
					.quantity(Random.NormalIntRange(2, 4)) : new CurareDart()
					.quantity(Random.NormalIntRange(1, 3)));
			itemsToSpawn.add(new LeatherArmor().identify());
			break;

		case 11:
			itemsToSpawn.add((Random.Int(2) == 0 ? new Sword() : new Mace())
					.identify());
			itemsToSpawn.add(Random.Int(2) == 0 ? new CurareDart()
					.quantity(Random.NormalIntRange(2, 5)) : new Shuriken()
					.quantity(Random.NormalIntRange(3, 6)));
			itemsToSpawn.add(new MailArmor().identify());
			break;

		case 16:
			itemsToSpawn.add((Random.Int(2) == 0 ? new Longsword()
					: new BattleAxe()).identify());
			itemsToSpawn.add(Random.Int(2) == 0 ? new Shuriken()
					.quantity(Random.NormalIntRange(4, 7)) : new Javelin()
					.quantity(Random.NormalIntRange(3, 6)));
			itemsToSpawn.add(new ScaleArmor().identify());
			break;

		case 21:
			//itemsToSpawn.add(Random.Int(2) == 0 ? new Glaive().identify()
			//	: new WarHammer().identify());
			//itemsToSpawn.add(Random.Int(2) == 0 ? new Javelin().quantity(Random
			//		.NormalIntRange(4, 7)) : new Tamahawk().quantity(Random
			//		.NormalIntRange(4, 7)));
			//itemsToSpawn.add(new PlateArmor().identify());
			itemsToSpawn.add(new Torch());
			itemsToSpawn.add(new Torch());
			itemsToSpawn.add(new Torch());
			itemsToSpawn.add(new BookOfDead());
			itemsToSpawn.add(new BookOfLife());
			itemsToSpawn.add(new BookOfTranscendence());
			break;
		}

		ChooseBag(Dungeon.hero.belongings);

		itemsToSpawn.add(new PotionOfHealing());
		for (int i = 0; i < 3; i++)
			itemsToSpawn.add(Generator.random(Generator.Category.POTION));

		itemsToSpawn.add(new ScrollOfIdentify());
		itemsToSpawn.add(new ScrollOfRemoveCurse());
		itemsToSpawn.add(new ScrollOfMagicMapping());
		itemsToSpawn.add(Generator.random(Generator.Category.SCROLL));

		for (int i = 0; i < 2; i++)
			itemsToSpawn.add(Random.Int(2) == 0 ? Generator
					.random(Generator.Category.POTION) : Generator
					.random(Generator.Category.SCROLL));
		
		itemsToSpawn.add(new Bomb().random());
		switch (Random.Int(5)) {
		case 1:
			itemsToSpawn.add(new Bomb());
			break;
		case 2:
			itemsToSpawn.add(new Bomb().random());
			break;
		case 3:
		case 4:
			itemsToSpawn.add(new Honeypot());
			break;
		}

		if (Dungeon.depth == 1 ||Dungeon.depth == 6) {
			itemsToSpawn.add(new Ankh());
			itemsToSpawn.add(new Weightstone());
		} else {
			itemsToSpawn.add(Random.Int(2) == 0 ? new Ankh()
					: new Weightstone());
		}

		TimekeepersHourglass hourglass = Dungeon.hero.belongings
				.getItem(TimekeepersHourglass.class);
		if (hourglass != null) {
			int bags = 0;
			// creates the given float percent of the remaining bags to be
			// dropped.
			// this way players who get the hourglass late can still max it,
			// usually.
			switch (Dungeon.depth) {
			case 1:case 6:
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.20f);
				break;
			case 11:
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
				break;
			case 16:
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
				break;
			case 21:
				bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);
				break;
			}

			for (int i = 1; i <= bags; i++) {
				itemsToSpawn.add(new TimekeepersHourglass.sandBag());
				hourglass.sandBags++;
			}
		}

		Item rare;
		switch (Random.Int(10)) {
		case 0:
			rare = Generator.random(Generator.Category.WAND);
			rare.level = 0;
			break;
		case 1:
			rare = Generator.random(Generator.Category.RING);
			rare.level = 1;
			break;
		case 2:
			rare = Generator.random(Generator.Category.ARTIFACT).identify();
			break;
		default:
			rare = new Stylus();
		}
		rare.cursed = rare.cursedKnown = false;
		itemsToSpawn.add(rare);

		// this is a hard limit, level gen allows for at most an 8x5 room, can't
		// fit more than 39 items + 1 shopkeeper.
		if (itemsToSpawn.size() > 39)
			throw new RuntimeException(
					"Shop attempted to carry more than 39 items!");

		Collections.shuffle(itemsToSpawn);
	}

	private static void ChooseBag(Belongings pack) {
		// FIXME: this whole method is pretty messy to accomplish a fairly
		// simple logic goal. Should be a better way.

		// there is a bias towards giving certain bags earlier, seen here
		int seeds = 2, scrolls = 1, potions = 1, artifacts = 1, wands = 0;

		// we specifically only want to look at items in the main bag, none of
		// the sub-bags.
		for (Item item : pack.backpack.items) {
			if (item instanceof Plant.Seed)
				seeds++;
			else if (item instanceof Scroll)
				scrolls++;
			else if (item instanceof Potion)
				potions++;
			else if (item instanceof Wand)
				wands++;
			else if (item instanceof Artifact)
				artifacts++;
		}
		// ...and the equipped weapon incase it's a wand
		if (pack.weapon instanceof Wand)
			wands++;
		
		// ...and the equipped slots in case they are Artifacts
		if (pack.misc1 instanceof Artifact)
			artifacts++;
		if (pack.misc2 instanceof Artifact)
			artifacts++;
		// kill our counts for bags that have already been dropped.
		if (Dungeon.limitedDrops.seedBag.dropped())
			seeds = 0;
		if (Dungeon.limitedDrops.scrollBag.dropped())
			scrolls = 0;
		if (Dungeon.limitedDrops.potionBag.dropped())
			potions = 0;
		if (Dungeon.limitedDrops.wandBag.dropped())
			wands = 0;
		if (Dungeon.limitedDrops.artifactBox.dropped())
			artifacts = 0;
		
		// then pick whichever valid bag has the most items available to put
		// into it.
		if (seeds >= scrolls && seeds >= potions && seeds >= wands && seeds >= artifacts
				&& !Dungeon.limitedDrops.seedBag.dropped()) {
			Dungeon.limitedDrops.seedBag.drop();
			itemsToSpawn.add(new Food());
		} else if (scrolls >= potions && scrolls >= wands && scrolls >= artifacts
				&& !Dungeon.limitedDrops.scrollBag.dropped()) {
			Dungeon.limitedDrops.scrollBag.drop();
			itemsToSpawn.add(new ScrollHolder());
		} else if (potions >= wands && potions >= artifacts
				&& !Dungeon.limitedDrops.potionBag.dropped()) {
			Dungeon.limitedDrops.potionBag.drop();
			itemsToSpawn.add(new PotionBandolier());
		} else if (wands >= artifacts && !Dungeon.limitedDrops.wandBag.dropped()) {
			Dungeon.limitedDrops.wandBag.drop();
			itemsToSpawn.add(new WandHolster());
		} else if (!Dungeon.limitedDrops.artifactBox.dropped()) {
			Dungeon.limitedDrops.artifactBox.drop();
			itemsToSpawn.add(new ArtifactBox());
		}
	}

	public static int spaceNeeded() {
		if (itemsToSpawn == null)
			generateItems();

		// plus one for the shopkeeper
		return itemsToSpawn.size() + 1;
	}

	private static void placeShopkeeper(Level level, Room room) {

		int pos;
		do {
			pos = room.random();
		} while (level.heaps.get(pos) != null);

		Mob shopkeeper = level instanceof LastShopLevel ? new ImpShopkeeper()
				: new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add(shopkeeper);

		if (level instanceof LastShopLevel) {
			for (int i = 0; i < Level.NEIGHBOURS9.length; i++) {
				int p = shopkeeper.pos + Level.NEIGHBOURS9[i];
				if (level.map[p] == Terrain.EMPTY_SP) {
					level.map[p] = Terrain.WATER;
				}
			}
		}
	}

	private static int xy2p(Room room, Point xy) {
		if (xy.y == room.top) {

			return (xy.x - room.left - 1);

		} else if (xy.x == room.right) {

			return (xy.y - room.top - 1) + pasWidth;

		} else if (xy.y == room.bottom) {

			return (room.right - xy.x - 1) + pasWidth + pasHeight;

		} else {

			if (xy.y == room.top + 1) {
				return 0;
			} else {
				return (room.bottom - xy.y - 1) + pasWidth * 2 + pasHeight;
			}

		}
	}

	private static Point p2xy(Room room, int p) {
		if (p < pasWidth) {

			return new Point(room.left + 1 + p, room.top + 1);

		} else if (p < pasWidth + pasHeight) {

			return new Point(room.right - 1, room.top + 1 + (p - pasWidth));

		} else if (p < pasWidth * 2 + pasHeight) {

			return new Point(room.right - 1 - (p - (pasWidth + pasHeight)),
					room.bottom - 1);

		} else {

			return new Point(room.left + 1, room.bottom - 1
					- (p - (pasWidth * 2 + pasHeight)));

		}
	}
}
