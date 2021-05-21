package co.bugg.quickplay.client.command.premium.glyph;

import co.bugg.quickplay.Quickplay;
import co.bugg.quickplay.actions.serverbound.AlterGlyphAction;
import co.bugg.quickplay.client.command.ACommand;
import co.bugg.quickplay.client.render.PlayerGlyph;
import co.bugg.quickplay.util.Message;
import co.bugg.quickplay.util.QuickplayChatComponentTranslation;
import co.bugg.quickplay.util.ServerUnavailableException;
import co.bugg.quickplay.wrappers.chat.ChatStyleWrapper;
import co.bugg.quickplay.wrappers.chat.Formatting;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

public class GlyphCommandSet extends GlyphCommand {

    public GlyphCommandSet(ACommand parent) {
        super(
                parent,
                Collections.singletonList("set"),
                Quickplay.INSTANCE.elementController.translate("quickplay.commands.quickplay.premium.glyph.set.help"),
                "<imageURL>",
                true,
                true,
                90,
                true,
                parent == null ? 0 : parent.getDepth() + 1
        );
    }

    @Override
    public void run(String[] args) {
        try {
            if(args.length < 4) {
                Quickplay.INSTANCE.minecraft.sendLocalMessage(new Message(
                        new QuickplayChatComponentTranslation("quickplay.commands.quickplay.premium.glyph.set.illegal")
                                .setStyle(new ChatStyleWrapper().apply(Formatting.RED))));
                return;
            }

            final URL url = new URL(args[3]);
            Quickplay.INSTANCE.threadPool.submit(() -> {
                try {
                    final PlayerGlyph glyph = new PlayerGlyph(Quickplay.INSTANCE.minecraft.getUuid(),
                            url, null, null, null);
                    Quickplay.INSTANCE.socket.sendAction(new AlterGlyphAction(glyph));
                } catch (ServerUnavailableException e) {
                    e.printStackTrace();
                    Quickplay.INSTANCE.minecraft.sendLocalMessage(new Message(
                            new QuickplayChatComponentTranslation("quickplay.failedToConnect")
                                    .setStyle(new ChatStyleWrapper().apply(Formatting.RED))));
                }
            });
        } catch(MalformedURLException e) {
            e.printStackTrace();
            Quickplay.INSTANCE.sendExceptionRequest(e);
            Quickplay.INSTANCE.minecraft.sendLocalMessage(new Message(
                    new QuickplayChatComponentTranslation("quickplay.commands.quickplay.premium.glyph.set.illegal")
                            .setStyle(new ChatStyleWrapper().apply(Formatting.RED))));
        }
    }
}