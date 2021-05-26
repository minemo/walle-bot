package com.minemo.wallebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fusesource.jansi.Ansi.ansi;

public class WalleBotApplication extends ListenerAdapter {

    Hashtable<String, String> accounts = new Hashtable<>(20);
    Hashtable<String, String> accountsinv = new Hashtable<>(20);
    Spotifyactions spac = new Spotifyactions();
    Redeemprices rp = new Redeemprices();
    Hashtable<String, Long> jointime = new Hashtable<>(10);
    Hashtable<String, Long> leavetime = new Hashtable<>(10);

    public static void main(String[] args) throws Exception {

        //init Colors
        AnsiConsole.systemInstall();


        String token = "ODI2MjA5MTgyMjMwMzgwNTQ0.YGJJQw.lk-NNkGE0UK5clAWSJl2gVI9bio";//ADD KEY HERE

        JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(new WalleBotApplication()).setActivity(Activity.watching("Minemo beim dummes Zeug machen zu"))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(ansi().fgBrightBlue().a("----------------------------------------------------------------------------------------------------").reset());
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Member joinedmember = event.getMember();
        jointime.put(joinedmember.getUser().getAsTag(), System.currentTimeMillis() / 60000);
        System.out.println(ansi().fgBrightDefault().a("[" + LocalTime.now().truncatedTo(ChronoUnit.MINUTES) + "]:").fgBrightGreen().a(joinedmember.getUser().getAsTag() + " joined a voice-channel!").reset());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        Guild guild = event.getGuild();
        MessageChannel channel = guild.getTextChannelById("845722929043472424");
        Member leftmember = event.getMember();
        leavetime.put(leftmember.getUser().getAsTag(), System.currentTimeMillis() / 60000);

        //Calculate time in VC
        //TODO make these values persistent as well
        long ontime;
        if(jointime.get(leftmember.getUser().getAsTag()) == null) {
            ontime = leavetime.get(leftmember.getUser().getAsTag()) - leavetime.get(leftmember.getUser().getAsTag());
        } else {
            ontime = leavetime.get(leftmember.getUser().getAsTag()) - jointime.get(leftmember.getUser().getAsTag());
        }

        System.out.println(ansi().fgBrightDefault().a("[" + LocalTime.now().truncatedTo(ChronoUnit.MINUTES) + "]:").fgBrightRed().a(leftmember.getUser().getAsTag() + " left a voice-channel!").reset());

        //if more than 0.1 Weebcoin has been earned, send the amount to the corresponding account
        if (!(ontime / 10f <= 0.1)) {
            try {
                cryptointeract crypto = new cryptointeract("HTTP://127.0.0.1:7545");
                EmbedBuilder eb = new EmbedBuilder();
                if (accounts.containsKey(leftmember.getUser().getAsTag())) {
                    crypto.transact(accounts.get("Minemo#8392"), accounts.get(leftmember.getUser().getAsTag()), (ontime / 10f)*1000000000000000000f, "ontimereward");
                    eb.setTitle("Weebcoin Rewards");
                    eb.setColor(Color.GREEN);
                    eb.setThumbnail(leftmember.getUser().getAvatarUrl());
                    eb.setDescription(leftmember.getEffectiveName() + " hat gerade **" + ontime / 10f + " Weebcoin** verdient!");
                } else {
                    eb.setTitle("Weebcoin Rewards");
                    eb.setColor(Color.RED);
                    eb.setThumbnail(leftmember.getUser().getAvatarUrl());
                    eb.setDescription(leftmember.getEffectiveName() + " hat gerade **" + ontime / 10f + " Weebcoin** verdient!\nAber da Er/Sie keinen Account geclaimed hatte\n sind Die jetzt weg.");
                }
                Objects.requireNonNull(channel).sendMessage(eb.build()).queue();
            /*TODO maybe nur senden, wenn Menge an Weebcoin signifikant ist ( >= 0,5)
              TODO evtl bei neuem "Streak" spezielle benachichtigung*/
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        MessageAction.setDefaultMentions(EnumSet.of(Message.MentionType.USER));
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        //Add Commands here
        if (!msg.getAuthor().isBot()) {

            //Log Messages for Debug purposes
            System.out.println(ansi().fgBrightDefault().a("[" + LocalTime.now().truncatedTo(ChronoUnit.MINUTES) + "]:" + "Nachicht von ").fgBrightCyan().a(msg.getAuthor().getName()).fgBrightDefault().a(": ").fgBrightBlue().a(msg.getContentRaw()).reset());

            //Look for Author change (as Name#Tagnum)
            Pattern pattern = Pattern.compile("as ([a-z]|[0-9])+?#[0-9]{4}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(msg.getContentRaw());
            User reqauthor = msg.getAuthor();

            //TODO fix Author changing
            /*if (matcher.find()) {
                String tag = matcher.group(0);
                reqauthor = Objects.requireNonNull(guild.getMemberByTag(tag.substring(3))).getUser();
                System.out.println(ansi().render("Wird als @|cyan " + tag.substring(3, tag.length()-5) + " ausgeführt!@|"));
            }*/

            //Watch for Crypto related messages (-c ****)
            if (channel.getId().equals("826207624625127505")) {
                if (msg.getContentRaw().startsWith("-c")) {
                    //Read Accounts from file
                    try {
                        this.accounts = Filewriter.readlines("accounts.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    EmbedBuilder eb = new EmbedBuilder();
                    cryptointeract crypto = new cryptointeract("HTTP://127.0.0.1:7545");
                    if (msg.getContentRaw().equals("-c debug") && Objects.requireNonNull(msg.getMember()).getRoles().contains(guild.getRoleById("826211225955467274"))) {
                        eb.setTitle("Weebcoin interface - Debug");
                        eb.setColor(Color.RED);
                        try {
                            eb.addField("Version", crypto.getVersion(), false);
                            eb.addField("Number of blocks", crypto.getlastblock(), false);
                            eb.addField("Number of accounts", String.valueOf(crypto.getAccount().size()), false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        channel.sendMessage(eb.build()).queue();
                    }
                    //DEBUG show accounts
                    else if (msg.getContentRaw().equals("-c accounts") && Objects.requireNonNull(msg.getMember()).getRoles().contains(guild.getRoleById("826211225955467274"))) {
                        eb.setTitle("Weebcoin interface - Accounts");
                        eb.setColor(Color.CYAN);
                        try {
                            for (String acc : crypto.getAccount()
                            ) {
                                eb.addField(acc, "Balance: " + crypto.getbalance(acc).floatValue() / 1000000000000000000f, false);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        channel.sendMessage(eb.build()).queue();
                    }
                    //Claim account
                    else if (msg.getContentRaw().equals("-c claim")) {
                        eb.setTitle("Weebcoin interface - Claim");
                        eb.setColor(Color.GREEN);
                        Random n = new Random();
                        try {
                            String newrandomacc = crypto.getAccount().get((int) (n.nextFloat() * crypto.getAccount().size()));
                            String newownertag = reqauthor.getAsTag();
                            String newownerid = reqauthor.getId();
                            if (!accounts.containsValue(newrandomacc) && !accounts.containsKey(newownertag)) {
                                accounts.put(newownertag, newrandomacc);
                                accountsinv.put(newrandomacc, newownertag);
                                Filewriter.writefile("accounts.txt", newownertag, newrandomacc);
                                eb.setDescription("Account: ```" + newrandomacc + "``` wurde dem Nutzer: <@" + newownerid + ">" + " zugewiesen!");
                            } else if (accounts.containsValue(newrandomacc) && !accounts.containsKey(newownertag)) {
                                eb.setDescription("Account: " + newrandomacc + " schon genutzt, bitte neu versuchen.");
                            } else if (accounts.containsKey(newownertag)) {
                                eb.setDescription("Nutzer <@" + newownerid + "> besitzt schon einen Account.");
                            }
                            //TODO self-repeat function, if not successfully assigned
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        channel.sendMessage(eb.build()).queue();
                    }
                    //Show Account Info
                    //TODO add Identicon to message
                    else if (msg.getContentRaw().equals("-c account")) {
                        eb.setTitle("Weebcoin interface - Account");
                        eb.setColor(Color.YELLOW);
                        if (accounts.containsKey(reqauthor.getAsTag())) {
                            try {
                                eb.setDescription("Adresse: ```" + accounts.get(reqauthor.getAsTag()) + "```\n" + "Balance: " + crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() / 1000000000000000000f + " Weebcoin");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            eb.setDescription("Benutzer besitzt keinen Account.\n\nUm einen Account zu beanspruchen benutze ```-c claim```");
                        }
                        channel.sendMessage(eb.build()).queue();
                    }
                    //send Weebcoin
                    else if (msg.getContentRaw().startsWith("-c send")) {
                        Pattern address = Pattern.compile("0x([0-9]|[a-z])+?$");
                        Pattern am = Pattern.compile("[0-9]+?\\.[0-9]");
                        Matcher admatch = address.matcher(msg.getContentRaw());
                        Matcher ammatch = am.matcher(msg.getContentRaw());
                        eb.setTitle("Weebcoin interface - Transaction");
                        eb.setColor(Color.YELLOW);
                        if (accounts.containsKey(reqauthor.getAsTag()) && admatch.find() && ammatch.find() && accounts.containsValue(admatch.group(0))) {
                            try {
                                String to = admatch.group(0);
                                float amount = Float.parseFloat(ammatch.group(0));
                                if (crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() / 1000000000000000000f >= amount) {
                                    crypto.transact(accounts.get(reqauthor.getAsTag()), to, amount * 1000000000000000000f, "send");
                                    eb.setDescription("Erfolgreicher Transfer von " + amount + " Weebcoin zu <@" + Objects.requireNonNull(guild.getMemberByTag(accountsinv.get(to))).getId() + ">");
                                } else {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Du versuchst mehr Weebcoin zu senden, als sich auf deinem Account befinden!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!accounts.containsKey(reqauthor.getAsTag())) {
                            eb.setColor(Color.RED);
                            eb.setDescription("Benutzer besitzt keinen Account.\n\nUm einen Account zu beanspruchen benutze ```-c claim```");
                        } else if (!admatch.find() || !accounts.containsValue(admatch.group(0))) {
                            eb.setColor(Color.RED);
                            eb.setDescription("Keinen gültigen Account angegeben.\nBitte stelle sicher, dass alles richtig geschrieben ist!");
                        } else if (!ammatch.find()) {
                            eb.setColor(Color.RED);
                            eb.setDescription("Keine Menge angegeben!");
                        }
                        channel.sendMessage(eb.build()).queue();
                    }
                    //Redeem Weebcoin
                    else if (msg.getContentRaw().startsWith("-c redeem")) {
                        Pattern p = Pattern.compile("[1-9]");
                        Matcher m = p.matcher(msg.getContentRaw());
                        Pattern p2 = Pattern.compile("http[s]?://[a-z]+?\\.[a-z]+?\\.[a-z]+?/([a-z])+?\\.(jpg)|(png)|(jpeg)", Pattern.CASE_INSENSITIVE);
                        Matcher m2 = p2.matcher(msg.getContentRaw());
                        if (msg.getContentRaw().contains("spause")) {
                            eb.setTitle("Weebcoin interface - Redeem");
                            eb.setColor(Color.GREEN);
                            try {
                                if (accounts.containsKey(reqauthor.getAsTag()) && crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("spause") * 1000000000000000000f) {
                                    spac.pause();
                                    eb.setDescription("Account: ```" + accounts.get(reqauthor.getAsTag()) + "```\nDie Spotify-Wiedergabe von <@301022277187796992> wurde zum Preis von: **" + rp.prices.get("spause") + " Weebcoins** pausiert.");
                                    crypto.transact(accounts.get(reqauthor.getAsTag()), accounts.get("Minemo#8392"), (rp.prices.get("spause") * 1000000000000000000f), "spause");
                                } else if (!accounts.containsKey(reqauthor.getAsTag())) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Benutzer besitzt keinen Account.\n\nUm einen Account zu beanspruchen benutze ```-c claim```");
                                } else if (!enoughbalance(reqauthor, crypto)) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Zu wenig Weebcoin zum einlösen!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            channel.sendMessage(eb.build()).queue();
                        } else if (msg.getContentRaw().contains("skip") && m.find()) {
                            eb.setTitle("Weebcoin interface - Redeem");
                            eb.setColor(Color.GREEN);
                            try {
                                if (accounts.containsKey(reqauthor.getAsTag()) && crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("sskip") * 1000000000000000000f) {
                                    spac.skip(Integer.parseInt(m.group(0)));
                                    eb.setDescription("Account: ```" + accounts.get(reqauthor.getAsTag()) + "```\nEs wurden **" + m.group(0) + " Songs** in <@301022277187796992>'s Spotify für: **" + (rp.prices.get("sskip") * Integer.parseInt(m.group(0))) + " Weebcoins** geskipped.");
                                    crypto.transact(accounts.get(reqauthor.getAsTag()), accounts.get("Minemo#8392"), (rp.prices.get("sskip") * 1000000000000000000f), "sskip");
                                } else if (!accounts.containsKey(reqauthor.getAsTag())) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Benutzer besitzt keinen Account.\n\nUm einen Account zu beanspruchen benutze ```-c claim```");
                                } else if (!(crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("sskip") * 1000000000000000000f)) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Zu wenig Weebcoin zum einlösen!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            channel.sendMessage(eb.build()).queue();
                        } else if (msg.getContentRaw().contains("cwall") && m2.find()) {
                            eb.setTitle("Weebcoin interface - Redeem");
                            eb.setColor(Color.GREEN);
                            try {
                                if (accounts.containsKey(reqauthor.getAsTag()) && crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("wall") * 1000000000000000000f) {
                                    spac.changewall(m2.group(0));
                                    eb.setImage(m2.group(0));
                                    eb.setDescription("Account: ```" + accounts.get(reqauthor.getAsTag()) + "```\nEs wurde <@301022277187796992>'s Hintergrund für: **" + rp.prices.get("wall") + " Weebcoins** gesetzt.");
                                    crypto.transact(accounts.get(reqauthor.getAsTag()), accounts.get("Minemo#8392"), (rp.prices.get("wall") * 1000000000000000000f), "cwall");
                                } else if (!accounts.containsKey(reqauthor.getAsTag())) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Benutzer besitzt keinen Account.\n\nUm einen Account zu beanspruchen benutze ```-c claim```");
                                } else if (!(crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("wall") * 1000000000000000000f)) {
                                    eb.setColor(Color.RED);
                                    eb.setDescription("Zu wenig Weebcoin zum einlösen!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            channel.sendMessage(eb.build()).queue();
                        }
                    }
                }
            }
        }
    }

    private boolean enoughbalance(User reqauthor, cryptointeract crypto) throws IOException {
        return crypto.getbalance(accounts.get(reqauthor.getAsTag())).floatValue() >= rp.prices.get("wall") * 1000000000000000000f;
    }
}