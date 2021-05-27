package com.minemo.wallebot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Class for reading and writing account associations into a persistent file

public class Filewriter {

    public static void writefile(String filename, String key, String value) throws IOException {
        Encoder enc = Base64.getEncoder();
        if (Files.exists(Path.of(filename))) {
            String newline = enc.encodeToString(key.getBytes(StandardCharsets.UTF_8)) + " " + value + "\n";
            Files.writeString(Path.of(filename),newline, StandardOpenOption.APPEND);
        } else {
            String newline = enc.encodeToString(key.getBytes(StandardCharsets.UTF_8)) + " " + value + "\n";
            Files.writeString(Path.of(filename),newline, StandardOpenOption.CREATE);
        }
    }

    public static Hashtable<String, String> readlines(String filename, Boolean inverse) throws IOException {
        Decoder dec = Base64.getDecoder();
        Hashtable<String, String> newaccs = new Hashtable<>();
        if (Files.exists(Path.of(filename))) {
            List<String> entries = Files.readAllLines(Path.of(filename));
            for (String line: entries) {
                Pattern usrpat = Pattern.compile("^([a-z]|[0-9]).+? ", Pattern.CASE_INSENSITIVE);
                Pattern addrpat = Pattern.compile("0x([a-z]|[0-9])+?$");
                Matcher addrmat = addrpat.matcher(line);
                Matcher usrmat = usrpat.matcher(line);
                if(addrmat.find() && usrmat.find()) {
                    String usr = new String(dec.decode(usrmat.group(0).stripTrailing()), StandardCharsets.UTF_8);
                    String address = addrmat.group();
                    if(!inverse) {
                        newaccs.put(usr, address);
                    }else {
                        newaccs.put(address, usr);
                    }
                }
            }
            return newaccs;
        } else {
            Files.createFile(Path.of(filename));
            return new Hashtable<String, String>();
        }
    }
}
