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
package com.avmoga.dpixel.windows;

import static com.avmoga.dpixel.Dungeon.hero;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.Statistics;
import com.avmoga.dpixel.actors.hero.Belongings;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.sprites.HeroSprite;
import com.avmoga.dpixel.ui.BadgesList;
import com.avmoga.dpixel.ui.Icons;
import com.avmoga.dpixel.ui.ItemSlot;
import com.avmoga.dpixel.ui.RedButton;
import com.avmoga.dpixel.ui.ScrollPane;
import com.avmoga.dpixel.ui.Window;
import com.avmoga.dpixel.utils.Utils;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Locale;

public class WndRanking extends WndTabbed {

	private static final String TXT_ERROR = Messages.get(WndRanking.class, "error");

	private static final String TXT_STATS = Messages.get(WndRanking.class, "stats");
	private static final String TXT_ITEMS = Messages.get(WndRanking.class, "items");

	private static final String TXT_BADGES = "徽章";

	private static final int WIDTH = 115;
	private static final int HEIGHT = 180;

	private static final int TAB_WIDTH = 40;

	private Thread thread;
	private String error = null;

	private Image busy;

	public WndRanking(final String gameFile) {

		super();
		resize(WIDTH, HEIGHT);

		thread = new Thread() {
			@Override
			public void run() {
				try {
					Badges.loadGlobal();
					Dungeon.loadGame(gameFile);
				} catch (Exception e) {
					error = TXT_ERROR;
				}
			}
		};
		thread.start();

		busy = Icons.BUSY.get();
		busy.origin.set(busy.width / 2, busy.height / 2);
		busy.angularSpeed = 720;
		busy.x = (WIDTH - busy.width) / 2;
		busy.y = (HEIGHT - busy.height) / 2;
		add(busy);
	}

	@Override
	public void update() {
		super.update();

		if (thread != null && !thread.isAlive()) {
			thread = null;
			if (error == null) {
				remove(busy);
				createControls();
			} else {
				hide();
				Game.scene().add(new WndError(TXT_ERROR));
			}
		}
	}

	private void createControls() {

		String[] labels = { TXT_STATS, TXT_ITEMS, TXT_BADGES };
		Group[] pages = { new StatsTab(), new ItemsTab(), new BadgesTab() };

		for (int i = 0; i < pages.length; i++) {

			add(pages[i]);

			Tab tab = new RankingTab(labels[i], pages[i]);
			add(tab);
		}

		layoutTabs();

		select(0);
	}

	private class RankingTab extends LabeledTab {

		private Group page;

		public RankingTab(String label, Group page) {
			super(label);
			this.page = page;
		}

		@Override
		protected void select(boolean value) {
			super.select(value);
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}

	private class StatsTab extends Group {

		private static final int GAP = 4;
		private final String TXT_TITLE = Messages.get(WndRanking.class, "title");

		private final String TXT_CHALLENGES = Messages.get(WndRanking.class, "challenges");

		private final String TXT_HEALTH = Messages.get(WndRanking.class, "health");
		private final String TXT_STR = Messages.get(WndRanking.class, "str");
		private final String TXT_TEST = Messages.get(WndRanking.class, "test");

		private final String TXT_DURATION = Messages.get(WndRanking.class, "duration");

		private final String TXT_DEPTH = Messages.get(WndRanking.class, "depth");
		private final String TXT_ENEMIES = Messages.get(WndRanking.class, "enemies");
		private final String TXT_ANKHS = Messages.get(WndRanking.class, "ankhs");
		private final String TXT_WATERS = Messages.get(WndRanking.class, "waters");
		private final String TXT_SHADOW = Messages.get(WndRanking.class, "shadow");

		private static final String TXT_GOLD = "收集金币总量";

		private static final String TXT_FOOD = "食用食物总数";
		private static final String TXT_ALCHEMY = "使用药水总量";


		public StatsTab() {
			super();

			String heroClass = hero.className()+hero.heroRace.title();

			IconTitle title = new IconTitle();
			title.icon(HeroSprite.avatar(hero.heroClass,
					hero.tier()));
			title.label(Utils.format(TXT_TITLE, hero.lvl, heroClass)
					.toUpperCase(Locale.ENGLISH));
			title.color(Window.SHPX_COLOR);
			title.setRect(0, 0, WIDTH, 0);
			add(title);

			float pos = title.bottom();

			if (Dungeon.challenges > 0) {
				RedButton btnCatalogus = new RedButton(TXT_CHALLENGES) {
					@Override
					protected void onClick() {
						Game.scene().add(
								new WndChallenges(Dungeon.challenges, false));
					}
				};
				btnCatalogus.setRect(0, pos + GAP, btnCatalogus.reqWidth() + 2,
						btnCatalogus.reqHeight() + 2);
				add(btnCatalogus);

				pos = btnCatalogus.bottom();
			}

			pos += GAP + GAP;

			pos = statSlot(this, TXT_STR, Integer.toString(hero.STR),
					pos);
			pos = statSlot(this, TXT_HEALTH, Integer.toString(hero.HT),
					pos);

			pos += GAP;

			pos = statSlot(this, (Dungeon.playtest ? TXT_TEST:TXT_DURATION),
					Integer.toString((int) Statistics.duration), pos);

			pos += GAP;

			pos = statSlot(this, TXT_DEPTH,
					Integer.toString(Statistics.deepestFloor), pos);
			pos = statSlot(this, TXT_ENEMIES,
					Integer.toString(Statistics.enemiesSlain), pos);
			pos = statSlot(this, TXT_GOLD,
					Integer.toString(Statistics.goldCollected), pos);

			pos += GAP;

			pos = statSlot(this, TXT_FOOD,
					Integer.toString(Statistics.foodEaten), pos);
			pos = statSlot(this, TXT_ALCHEMY,
					Integer.toString(Statistics.potionsCooked), pos);
			pos = statSlot(this, TXT_ANKHS,
					Integer.toString(Statistics.ankhsUsed), pos);

			pos = statSlot(this, TXT_WATERS,
					Integer.toString(Statistics.waters), pos);
			pos = statSlot(this, TXT_SHADOW,
					Integer.toString(Statistics.shadowYogsKilled), pos);
		}

		private float statSlot(Group parent, String label, String value,
				float pos) {

			RenderedText txt = PixelScene.renderText(label, 7);
			txt.y = pos;
			parent.add(txt);

			txt = PixelScene.renderText(value, 7);
			txt.x = PixelScene.align(WIDTH * 0.65f);
			txt.y = pos;
			parent.add(txt);

			return pos + GAP + txt.baseLine();
		}
	}

	private class ItemsTab extends Group {

		private float pos;

		public ItemsTab() {
			super();

			Belongings stuff = hero.belongings;
			if (stuff.weapon != null) {
				addItem(stuff.weapon);
			}
			if (stuff.armor != null) {
				addItem(stuff.armor);
			}
			if (stuff.misc1 != null) {
				addItem(stuff.misc1);
			}
			if (stuff.misc2 != null) {
				addItem(stuff.misc2);
			}

			pos = 29;
			for (int i = 0; i < 2; i++) {
				if (Dungeon.quickslot.getItem(i) != null) {
					QuickSlotButton slot = new QuickSlotButton(
							Dungeon.quickslot.getItem(i));

					slot.setRect(pos, 116, 28, 28);

					add(slot);

				} else {
					ColorBlock bg = new ColorBlock(28, 28, 0xFF4A4D44);
					bg.x = pos;
					bg.y = 116;
					add(bg);
				}
				pos += 29;
			}
		}

		private void addItem(Item item) {
			ItemButton slot = new ItemButton(item);
			slot.setRect(0, pos, width, ItemButton.HEIGHT);
			add(slot);

			pos += slot.height() + 1;
		}
	}

	private class BadgesTab extends Group {

		public BadgesTab() {
			super();

			camera = WndRanking.this.camera;

			ScrollPane list = new BadgesList(false);
			add(list);

			list.setSize(WIDTH, HEIGHT);
		}
	}

	private class ItemButton extends Button {

		public static final int HEIGHT = 28;

		private Item item;

		private ItemSlot slot;
		private ColorBlock bg;
		private RenderedText name;

		public ItemButton(Item item) {

			super();

			this.item = item;

			slot.item(item);
			if (item.cursed && item.cursedKnown) {
				bg.ra = +0.2f;
				bg.ga = -0.1f;
			} else if (!item.isIdentified()) {
				bg.ra = 0.1f;
				bg.ba = 0.1f;
			}
		}

		@Override
		protected void createChildren() {

			bg = new ColorBlock(HEIGHT, HEIGHT, 0xFF4A4D44);
			add(bg);

			slot = new ItemSlot();
			add(slot);

			name = PixelScene.renderText("?", 7);
			add(name);

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			slot.setRect(x, y, HEIGHT, HEIGHT);

			name.x = slot.right() + 2;
			name.y = y + (height - name.baseLine()) / 2;

			String str = Utils.capitalize(item.name());
			name.text(str);
			if (name.width() > width - name.x) {
				do {
					str = str.substring(0, str.length() - 1);
					name.text(str + "...");
				} while (name.width() > width - name.x);
			}

			super.layout();
		}

		@Override
		protected void onTouchDown() {
			bg.brightness(1.5f);
			Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
		};

		@Override
		protected void onTouchUp() {
			bg.brightness(1.0f);
		};

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}

	private class QuickSlotButton extends ItemSlot {

		public static final int HEIGHT = 28;

		private Item item;
		private ColorBlock bg;

		QuickSlotButton(Item item) {
			super(item);
			this.item = item;
		}

		@Override
		protected void createChildren() {
			bg = new ColorBlock(HEIGHT, HEIGHT, 0xFF4A4D44);
			add(bg);

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			super.layout();
		}

		@Override
		protected void onTouchDown() {
			bg.brightness(1.5f);
			Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
		};

		@Override
		protected void onTouchUp() {
			bg.brightness(1.0f);
		};

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}
}
