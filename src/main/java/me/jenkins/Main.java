package me.jenkins;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(Constants.TOKEN)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new MessageListener())
                    .build();
            jda.awaitReady();

        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}