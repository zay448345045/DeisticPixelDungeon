package com.avmoga.dpixel.windows;

import com.avmoga.dpixel.Messages.Languages;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.ShatteredPixelDungeon;
import com.avmoga.dpixel.scenes.PixelScene;
import com.avmoga.dpixel.scenes.TitleScene;
import com.avmoga.dpixel.ui.RedButton;
import com.avmoga.dpixel.ui.RenderedTextMultiline;
import com.avmoga.dpixel.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.RenderedText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class WndLangs extends Window {

    private int WIDTH_P = 120;
    private int WIDTH_L = 171;

    private int MIN_HEIGHT = 110;

    private int BTN_WIDTH = 50;
    private int BTN_HEIGHT = 14;

    public WndLangs() {
        super();

        final ArrayList<Languages> langs = new ArrayList<>(Arrays.asList(Languages.values()));

        Languages nativeLang = Languages.matchLocale(Locale.getDefault());
        langs.remove(nativeLang);
        //move the native language to the top.
        langs.add(0, nativeLang);

        final Languages currLang = Messages.lang();

        //language buttons layout
        int y = 0;
        for (int i = 0; i < langs.size(); i++) {
            final int langIndex = i;
            RedButton btn = new RedButton(Messages.titleCase(langs.get(i).nativeName())) {
                @Override
                protected void onClick() {
                    super.onClick();
                    Messages.setup(langs.get(langIndex));
                    ShatteredPixelDungeon.language(langs.get(langIndex));
                    RenderedText.clearCache();
                    ShatteredPixelDungeon.switchScene(TitleScene.class);
                }
            };
            if (currLang == langs.get(i)) {
                btn.textColor(TITLE_COLOR);
            } else {
                switch (langs.get(i).status()) {
                    case INCOMPLETE:
                        btn.textColor(0x990000);
                        break;
                    case UNREVIEWED:
                        btn.textColor(0xCCCCCC);
                        break;
                }
            }
            btn.setSize(BTN_WIDTH, BTN_HEIGHT);
            if (ShatteredPixelDungeon.landscape() && i % 2 == 1) {
                btn.setPos(BTN_WIDTH + 1, y - 15);
            } else {
                btn.setPos(0, y);
                y += 15;
            }

            add(btn);
        }
        y = Math.max(MIN_HEIGHT, y + 1);
        resize(ShatteredPixelDungeon.landscape() ? WIDTH_L : WIDTH_P, y);

        int textLeft = width - 65;
        int textWidth = width - textLeft;

        ColorBlock separator = new ColorBlock(1, y, 0xFF000000);
        separator.x = textLeft - 2.5f;
        add(separator);

        //language info layout.
        RenderedText title = PixelScene.renderText(Messages.titleCase(currLang.nativeName()), 9);
        title.x = textLeft + (textWidth - title.width()) / 2f;
        title.y = 0;
        title.hardlight(TITLE_COLOR);
        PixelScene.align(title);
        add(title);

        if (currLang == Languages.ENGLISH) {

            RenderedTextMultiline info = PixelScene.renderMultiline(6);
            info.text("This is the source language, written by the developer.", width - textLeft);
            info.setPos(textLeft, title.height() + 2);
            add(info);

        } else {

            RenderedTextMultiline info = PixelScene.renderMultiline(6);
            switch (currLang.status()) {
                case REVIEWED:
                    info.text(Messages.get(this, "completed"), width - textLeft);
                    break;
                case UNREVIEWED:
                    info.text(Messages.get(this, "unreviewed"), width - textLeft);
                    break;
                case INCOMPLETE:
                    info.text(Messages.get(this, "unfinished"), width - textLeft);
                    break;
            }
            info.setPos(textLeft, title.height() + 2);
            add(info);

            RedButton creditsBtn = new RedButton(Messages.titleCase(Messages.get(this, "credits"))) {
                @Override
                protected void onClick() {
                    super.onClick();
                    String creds = "";
                    String[] reviewers = currLang.reviewers();
                    String[] translators = currLang.translators();
                    if (reviewers.length > 0) {
                        creds += "_" + Messages.titleCase(Messages.get(WndLangs.class, "reviewers")) + "_\n";
                        for (String reviewer : reviewers) {
                            creds += "-" + reviewer + "\n";
                        }
                        creds += "\n";
                    }

                    if (reviewers.length > 0 || translators.length > 0) {
                        creds += "_" + Messages.titleCase(Messages.get(WndLangs.class, "translators")) + "_";
                        //reviewers are also translators
                        for (String reviewer : reviewers) {
                            if (ShatteredPixelDungeon.language() != Languages.CHINESE) {
                                creds += "\n-" + reviewer;
                            }
                        }
                        for (String translator : translators) {
                            creds += "\n-" + translator;
                        }
                    }

                    Window credits = new Window();

                    RenderedTextMultiline title = PixelScene.renderMultiline(9);
                    title.text(Messages.titleCase(Messages.get(WndLangs.class, "credits")), 65);
                    title.hardlight(SHPX_COLOR);
                    title.setPos((65 - title.width()) / 2, 0);
                    credits.add(title);

                    RenderedTextMultiline text = PixelScene.renderMultiline(6);
                    text.text(creds, 80);
                    text.setPos(0, title.bottom() + 2);
                    credits.add(text);

                    credits.resize(80, (int) text.bottom());
                    parent.add(credits);
                }
            };
            creditsBtn.setSize(creditsBtn.reqWidth() + 2, 16);
            creditsBtn.setPos(textLeft + (textWidth - creditsBtn.width()) / 2f, y - 18);
            add(creditsBtn);

            RenderedTextMultiline transifex_text = PixelScene.renderMultiline(6);
            transifex_text.text(Messages.get(this, "transifex"), width - textLeft);
            transifex_text.setPos(textLeft, creditsBtn.top() - 2 - transifex_text.height());
            add(transifex_text);

        }

    }

}

