package com.minemo.wallebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

@SpringBootApplication
public class WalleBotApplication extends ListenerAdapter {

	public static void main(String[] args) throws LoginException {

		String token = "";//ADD KEY HERE

		JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES)
				.addEventListeners(new WalleBotApplication()).setActivity(Activity.watching("Minemo beim dummes Zeug machen zu"))
				.build();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		MessageAction.setDefaultMentions(EnumSet.of(Message.MentionType.USER));
		JDA api = event.getJDA();
		Message msg = event.getMessage();
		MessageChannel channel = event.getChannel();
		//Add Commands here
		//TODO surround if statements with Channel-Check
		if (!msg.getAuthor().isBot()) {

			//Log Messages for Debug purposes
			System.out.println("Nachicht von " + msg.getAuthor().getName() + ": " + msg.getContentRaw());

			//this Command can be ignored
			if (msg.getContentRaw().equals("-startup")) {
				if (channel.getId().equals("826207624625127505")) {
					channel.sendMessage("- - - STARTUP - - -").queue();
					channel.sendMessage("YO IM BACK BOIS!!!").queue();
					channel.sendMessage("Danke an Mo, dass es so lang gedauert hat...").queue();
				}
			} else if (msg.getContentRaw().equals("-surprise")) {
				if (channel.getId().equals("826207624625127505")) {
					channel.sendMessage("Sry.. noch nicht implementiert, aber Ich ping mal die Devs XD <@301022277187796992>").queue();

				}
			}else if (msg.getContentRaw().startsWith("-whois ")) {
				if (channel.getId().equals("826207624625127505")) {
					channel.sendMessage("WAAAAAALLEE").queue();

				}
			}
		}
	}
}
