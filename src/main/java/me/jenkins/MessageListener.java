package me.jenkins;

import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener implements EventListener {


    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof MessageReceivedEvent event) {
            // this is done so that the bot doesn't respond to itself
            if (event.getAuthor().isBot()) {
                return;
            }

            String[] parsedUrl = parseYoutubeUrl(event.getMessage().getContentRaw());
            String siParameter = parsedUrl[0];
            String trackerlessUrl = parsedUrl[1];

            // only resend if there is a tracking parameter in the URL, no need to resend if there isn't
            if (siParameter != null) {
                TextChannel channel = event.getChannel().asTextChannel();
                WebhookAction webhookAction = channel.createWebhook("uwuifier");
                webhookAction.setName(event.getAuthor().getName());

                try (InputStream avatarStream = new URL(Objects.requireNonNull(event.getAuthor().getAvatarUrl())).openStream()) {
                    webhookAction.setAvatar(Icon.from(avatarStream));
                } catch (IOException e) {
                    System.out.println("Failed to set avatar, message: " + e.getMessage());
                }

                webhookAction.queue(webhook -> {
                    event.getMessage().delete().queue();
                    webhook.sendMessage(trackerlessUrl).queue();
                    // delete the webhook on success because we don't want to have tons of webhooks laying around
                    webhook.delete().queue();
                });
            }
        }
    }


    private static String[] parseYoutubeUrl(String url) {
        String siParameter = null;
        String trackerlessUrl = url;

        Pattern pattern = Pattern.compile("[?&]si=([^&]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            siParameter = matcher.group(1);
            trackerlessUrl = url.replaceAll("[?&]si=[^&]+", "");
        }

        return new String[]{siParameter, trackerlessUrl};
    }
}
