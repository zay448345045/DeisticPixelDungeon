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
package com.avmoga.dpixel.items.potions;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.Fire;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Burning;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.effects.Splash;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.ItemStatusHandler;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.levels.Terrain;
import com.avmoga.dpixel.plants.Plant;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSprite;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashSet;

public class Potion extends Item {

	public static final String AC_DRINK = Messages.get(Potion.class, "ac_drink");

	private static final String TXT_HARMFUL = Messages.get(Potion.class, "harmful");
	private static final String TXT_BENEFICIAL = Messages.get(Potion.class, "beneficial");
	private static final String TXT_YES = Messages.get(Potion.class, "yes");
	private static final String TXT_NO = Messages.get(Potion.class, "no");
	private static final String TXT_R_U_SURE_DRINK = Messages.get(Potion.class, "sure_drink");
	private static final String TXT_R_U_SURE_THROW = Messages.get(Potion.class, "sure_throw");


	private static final float TIME_TO_DRINK = 1f;

	private static final Class<?>[] potions = { PotionOfHealing.class,
			PotionOfExperience.class, PotionOfToxicGas.class,
			PotionOfLiquidFlame.class, PotionOfStrength.class,
			PotionOfParalyticGas.class, PotionOfLevitation.class,
			PotionOfMindVision.class, PotionOfPurity.class,
			PotionOfInvisibility.class, PotionOfMight.class,
			PotionOfFrost.class, PotionOfMending.class,
			PotionOfOverHealing.class};
	
	private static final String[] colors = { Messages.get(Potion.class, "turquoise"),
			Messages.get(Potion.class, "crimson"),
			Messages.get(Potion.class, "azure"),
			Messages.get(Potion.class, "jade"),
			Messages.get(Potion.class, "golden"),
			Messages.get(Potion.class, "magenta"),
			Messages.get(Potion.class, "charcoal"),
			Messages.get(Potion.class, "ivory"),
			Messages.get(Potion.class, "amber"),
			Messages.get(Potion.class, "bistre"),
			Messages.get(Potion.class, "indigo"),
			Messages.get(Potion.class, "silver"),
			Messages.get(Potion.class, "aqua"),
			Messages.get(Potion.class, "violet")};
	private static final Integer[] images = { ItemSpriteSheet.POTION_TURQUOISE,
			ItemSpriteSheet.POTION_CRIMSON, ItemSpriteSheet.POTION_AZURE,
			ItemSpriteSheet.POTION_JADE, ItemSpriteSheet.POTION_GOLDEN,
			ItemSpriteSheet.POTION_MAGENTA, ItemSpriteSheet.POTION_CHARCOAL,
			ItemSpriteSheet.POTION_IVORY, ItemSpriteSheet.POTION_AMBER,
			ItemSpriteSheet.POTION_BISTRE, ItemSpriteSheet.POTION_INDIGO,
			ItemSpriteSheet.POTION_SILVER, ItemSpriteSheet.POTION_AQUA,
		    ItemSpriteSheet.POTION_VIOLET};

	private static ItemStatusHandler<Potion> handler;

	private String color;

	public boolean ownedByFruit = false;

	{
		stackable = true;
		defaultAction = AC_DRINK;
	}

	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<Potion>(
				(Class<? extends Potion>[]) potions, colors, images);
	}

	public static void save(Bundle bundle) {
		handler.save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		handler = new ItemStatusHandler<Potion>(
				(Class<? extends Potion>[]) potions, colors, images, bundle);
	}

	public Potion() {
		super();
		syncVisuals();
	}

	@Override
	public void syncVisuals() {
		image = handler.image(this);
		color = handler.label(this);
	};

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_DRINK);
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		if (action.equals(AC_DRINK)) {

			if (isKnown()
					&& (this instanceof PotionOfLiquidFlame
							|| this instanceof PotionOfToxicGas || this instanceof PotionOfParalyticGas)) {

				GameScene.show(new WndOptions(TXT_HARMFUL, TXT_R_U_SURE_DRINK,
						TXT_YES, TXT_NO) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							drink(hero);
						}
					};
				});

			} else {
				drink(hero);
			}

		} else {

			super.execute(hero, action);

		}
	}

	@Override
	public void doThrow(final Hero hero) {

		if (isKnown()
				&& (this instanceof PotionOfExperience
						|| this instanceof PotionOfHealing
						|| this instanceof PotionOfMindVision
						|| this instanceof PotionOfStrength
						|| this instanceof PotionOfInvisibility 
						|| this instanceof PotionOfMight
						|| this instanceof PotionOfOverHealing
						|| this instanceof PotionOfMending)) {

			GameScene.show(new WndOptions(TXT_BENEFICIAL, TXT_R_U_SURE_THROW,
					TXT_YES, TXT_NO) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						Potion.super.doThrow(hero);
					}
				};
			});

		} else {
			super.doThrow(hero);
		}
	}

	protected void drink(Hero hero) {

		detach(hero.belongings.backpack);

		hero.spend(TIME_TO_DRINK);
		hero.busy();
		apply(hero);

		Sample.INSTANCE.play(Assets.SND_DRINK);

		hero.sprite.operate(hero.pos);
	}

	@Override
	protected void onThrow(int cell) {
		if (Dungeon.level.map[cell] == Terrain.WELL || Level.pit[cell] || Plant.checkPhase(cell)) {

			super.onThrow(cell);

		} else {

			shatter(cell);

		}
	}

	public void apply(Hero hero) {
		shatter(hero.pos);
	}

	public void shatter(int cell) {
		if (Dungeon.visible[cell]) {
			GLog.i(Messages.get(this, "shatter", color()));
			Sample.INSTANCE.play(Assets.SND_SHATTER);
			splash(cell);
		}
	}

	@Override
	public void cast(final Hero user, int dst) {
		super.cast(user, dst);
	}

	public boolean isKnown() {
		return handler.isKnown(this);
	}

	public void setKnown() {
		if (!ownedByFruit) {
			if (!isKnown()) {
				handler.know(this);
			}

			Badges.validateAllPotionsIdentified();
		}
	}

	@Override
	public Item identify() {

		setKnown();
		return this;
	}

	protected String color() {
		return color;
	}

	@Override
	public String name() {
		return isKnown() ? name : Messages.get(this, "unknown_name", color);
	}

	@Override
	public String info() {
		return isKnown() ? desc() : Messages.get(this, "unknown_desc", color);
	}

	@Override
	public boolean isIdentified() {
		return isKnown();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	public static HashSet<Class<? extends Potion>> getKnown() {
		return handler.known();
	}

	public static HashSet<Class<? extends Potion>> getUnknown() {
		return handler.unknown();
	}

	public static boolean allKnown() {
		return handler.known().size() == potions.length;
	}

	protected void splash(int cell) {
		final int color = ItemSprite.pick(image, 8, 10);
		Splash.at(cell, color, 5);

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		if (fire != null)
			fire.clear(cell);

		Char ch = Actor.findChar(cell);
		if (ch != null)
			Buff.detach(ch, Burning.class);
	}

	@Override
	public int price() {
		return 20 * quantity;
	}
}
