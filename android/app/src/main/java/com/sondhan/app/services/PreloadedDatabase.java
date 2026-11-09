package com.sondhan.app.services;

import android.content.Context;
import android.util.Log;

import com.sondhan.app.models.FactCheckResult;
import com.sondhan.app.models.PreloadedEntry;
import com.sondhan.app.models.Source;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreloadedDatabase {
    private static final String TAG = "PreloadedDatabase";
    private static final Map<String, PreloadedEntry> hashToEntry = new HashMap<>();
    private static final List<PreloadedEntry> allEntries = new ArrayList<>();
    private static boolean isInitialized = false;

    static {
        // Entry 1: Saiyed Abdullah
        allEntries.add(new PreloadedEntry(
                "718014225_1419002733593201_4348281707205913180_n.jpg",
                "718014225_1419002733593201_4348281707205913180_n.jpg",
                "সাইয়েদ আবদুল্লাহকে জড়িয়ে ভুয়া মন্তব্য প্রচার",
                "FALSE",
                82,
                "অনুসন্ধানে দেখা গেছে, বিদ্যুৎ ও তেলের দাম বৃদ্ধি নিয়ে জনগণকে ‘অকৃতজ্ঞ’ বলে সাইয়েদ আবদুল্লাহ কোনো মন্তব্য করেননি। দাবিটির পক্ষে কোনো নির্ভরযোগ্য সংবাদমাধ্যম, ফেসবুক পোস্ট বা প্রমাণ পাওয়া যায়নি।",
                Arrays.asList(new Source("Rumor Scanner BD", "https://rumorscanner.com/fact-check/saiyed-abdullah-fake-comment-claim/208583")),
                Arrays.asList(
                        "অনুসন্ধানে সাইয়েদ আবদুল্লাহর নামে প্রচারিত বক্তব্যটির সত্যতা যাচাই করা হয়।",
                        "বিদ্যুৎ ও তেলের দাম বৃদ্ধি নিয়ে জনগণকে ‘অকৃতজ্ঞ’ বলার দাবির কোনো প্রমাণ পাওয়া যায়নি।",
                        "দাবিটির সমর্থনে কোনো নির্ভরযোগ্য সংবাদমাধ্যমে প্রতিবেদন প্রকাশিত হয়নি।",
                        "সাইয়েদ আবদুল্লাহর অফিসিয়াল ফেসবুক পেজেও এমন কোনো মন্তব্যের তথ্য মেলেনি।",
                        "অনুসন্ধানে দেখা যায়, ৫ জুন একটি ফেসবুক প্রোফাইল থেকে প্রথম ফটোকার্ডটি প্রকাশ করা হয়।",
                        "পোস্টটিতে উল্লিখিত বক্তব্যের পক্ষে কোনো সূত্র বা প্রমাণ উপস্থাপন করা হয়নি।",
                        "উক্ত প্রোফাইলের বায়োতে নিজেকে 'Writer and Satirist' হিসেবে পরিচয় দেওয়া হয়েছে।",
                        "প্রোফাইলটিতে পূর্বেও একই ধরনের ভুয়া ও ভিত্তিহীন দাবি প্রচারের নজির পাওয়া গেছে।",
                        "এসব দাবির বিরুদ্ধে রিউমর স্ক্যানার অতীতে একাধিক ফ্যাক্টচেক প্রতিবেদন প্রকাশ করেছে।",
                        "তাই সাইয়েদ আবদুল্লাহ জনগণকে ‘অকৃতজ্ঞ’ বলেছেন—এ দাবিটি সম্পূর্ণ মিথ্যা ও ভিত্তিহীন।"
                )
        ));

        // Entry 2: Dr. Yunus
        allEntries.add(new PreloadedEntry(
                "718953356_1465989142222974_2371143075906418461_n.jpg",
                "718953356_1465989142222974_2371143075906418461_n.jpg",
                "ড. ইউনূস-নুরজাহান বেগমের বিরুদ্ধে মামলা হয়নি",
                "MISLEADING",
                72,
                "ড. মুহাম্মদ ইউনূস ও নুরজাহান বেগমসহ পাঁচজনের বিরুদ্ধে মামলা হয়েছে—এ দাবি বিভ্রান্তিকর। প্রকৃতপক্ষে, তাদের বিরুদ্ধে একটি মামলার আবেদন করা হলেও শুনানি শেষে আদালত ‘মামলা গ্রহণের উপাদান না থাকার’ কারণ দেখিয়ে আবেদনটি খারিজ করে দেন।",
                Arrays.asList(new Source("BDNEWS24.com", "https://bangla.bdnews24.com/politics/politics/976b12683a00")),
                Arrays.asList(
                        "ড. মুহাম্মদ ইউনূস ও নুরজাহান বেগমসহ পাঁচজনের বিরুদ্ধে মামলা হয়েছে—এ দাবি বিভিন্ন মাধ্যমে প্রচার করা হয়।",
                        "দাবিতে বলা হয়, হাম ও রুবেলার টিকা সময়মতো আমদানি না করায় শিশুদের জীবন ঝুঁকিতে পড়েছে।",
                        "এ অভিযোগে তাদের বিরুদ্ধে ঢাকার সিএমএম আদালতে মামলা হয়েছে বলে প্রচার করা হয়।",
                        "অনুসন্ধানে দেখা যায়, প্রচারিত দাবিটি সম্পূর্ণ সঠিক নয়।",
                        "কিশোরগঞ্জ-৫ আসনের সাবেক সংসদ সদস্য মামলার আবেদন করেছিলেন।",
                        "আবেদনে ড. ইউনূস ও নুরজাহান বেগমসহ পাঁচজনকে অভিযুক্ত করা হয়।",
                        "আদালত আবেদনটি শুনানি শেষে পর্যালোচনা করেন।",
                        "বিচারক ‘মামলা গ্রহণ করার উপাদান না থাকার’ কারণ দেখিয়ে আবেদনটি খারিজ করে দেন।",
                        "ফলে আদালতে কোনো মামলা গৃহীত বা রুজু হয়নি।",
                        "অতএব, ‘ড. ইউনূস-নুরজাহান বেগমের বিরুদ্ধে মামলা হয়েছে’ শীর্ষক দাবিটি বিভ্রান্তিকর।"
                )
        ));

        // Entry 3: Dhaka Capital
        allEntries.add(new PreloadedEntry(
                "preloaded-003",
                "claim_dhaka_capital.png",
                "Dhaka has been the capital of Bangladesh since independence in 1971",
                "TRUE",
                99,
                "Dhaka is indeed the capital of Bangladesh. It was declared the capital upon Bangladesh's independence on December 16, 1971. Article 5 of the Constitution of Bangladesh designates Dhaka as the national capital.",
                Arrays.asList(
                        new Source("Bangladesh Government Portal", "https://bangladesh.gov.bd"),
                        new Source("Encyclopedia Britannica - Dhaka", "https://www.britannica.com/place/Dhaka"),
                        new Source("Constitution of Bangladesh", "http://bdlaws.minlaw.gov.bd/act-367.html")
                ),
                Arrays.asList(
                        "The claim states that Dhaka has been the capital of Bangladesh since independence in 1971.",
                        "This claim has been verified as TRUE.",
                        "Bangladesh declared independence on March 26, 1971, and achieved victory on December 16, 1971.",
                        "Article 5 of the Constitution of Bangladesh officially designates Dhaka as the capital city.",
                        "Dhaka served as the capital of East Pakistan from 1947 to 1971 before independence.",
                        "The city has a rich history dating back to the Mughal era when it was known as Jahangirnagar.",
                        "Today, Dhaka is one of the most densely populated cities in the world with over 22 million residents.",
                        "Official government sources and international encyclopedias confirm this information.",
                        "The claim accurately reflects well-documented historical and constitutional facts.",
                        "In conclusion, this claim is verified as true according to official government and historical records."
                )
        ));

        // Entry 4: 5G Towers
        allEntries.add(new PreloadedEntry(
                "preloaded-004",
                "claim_5g_health.png",
                "5G towers cause cancer and radiation sickness",
                "FALSE",
                97,
                "There is no scientific evidence that 5G technology causes cancer or radiation sickness. 5G uses non-ionizing radio frequencies that do not have enough energy to damage DNA. The WHO, FDA, and peer-reviewed studies confirm safe operation.",
                Arrays.asList(
                        new Source("WHO - 5G and Health", "https://www.who.int/news-room/questions-and-answers/item/radiation-5g-mobile-networks-and-health"),
                        new Source("FDA - Cell Phone Safety", "https://www.fda.gov/radiation-emitting-products/cell-phones/scientific-evidence-cell-phone-safety")
                ),
                Arrays.asList(
                        "The claim asserts that 5G cell towers cause cancer and radiation sickness in humans.",
                        "This claim has been verified as FALSE.",
                        "5G technology uses non-ionizing radio waves, which do not carry enough energy to break chemical bonds or damage DNA.",
                        "The World Health Organization has stated that no adverse health effects have been causally linked to 5G exposure.",
                        "The US FDA conducted a comprehensive review and found no consistent evidence linking RF radiation to cancer.",
                        "5G frequencies operate within internationally established safety guidelines (ICNIRP).",
                        "Peer-reviewed studies published in scientific journals have found no causal link between 5G and health issues.",
                        "The conspiracy theory gained traction during the COVID-19 pandemic but has been widely debunked.",
                        "Spreading health misinformation about 5G has led to real-world harm, including vandalism of cell towers.",
                        "In conclusion, the claim that 5G causes cancer is false according to all major health and scientific organizations."
                )
        ));

        // Entry 5: Climate Change / Shishir Monir
        allEntries.add(new PreloadedEntry(
                "preloaded-005",
                "716690521_122361326474003647_5230761717797101281_n.jpg",
                "সাবেক আইজিপি মামুন রাজসাক্ষী হওয়ার ঘটনা ও আইনগত ব্যাখ্যা",
                "MISLEADING",
                96,
                "ফটো কার্ডটির প্রথম ৪টি দাবি সত্য হলেও শেষ দাবিটি অসঙ্গতিপূর্ণ। মোহাম্মদ শিশির মনির ব্যক্তিগতভাবে ট্রাইব্যুনালে মামলাটি পরিচালনা করেননি; বরং তাঁর সরাসরি আইনি দলের একজন আইনজীবী আসামিপক্ষের হয়ে সওয়াল করেছেন।",
                Arrays.asList(
                        new Source("BSS News", "https://www.bssnews.net/news-flash/290934"),
                        new Source("The Business Standard", "https://www.tbsnews.net")
                ),
                Arrays.asList(
                        "এটি সত্য যে সাবেক আইজিপি চৌধুরী আবদুল্লাহ আল-মামুনের পক্ষে একটি আইনজীবী দল কাজ করেছে।",
                        "অ্যাডভোকেট জায়েদ বিন আমজাদ ওই মামলায় প্রতিরক্ষা আইনজীবী হিসেবে দায়িত্ব পালন করেন।",
                        "তিনি অ্যাডভোকেট মোহাম্মদ শিশির মনিরের আইন প্রতিষ্ঠান “ল’ ল্যাব”-এর একজন সিনিয়র অ্যাসোসিয়েট।",
                        "মামলাটি আন্তর্জাতিক অপরাধ ট্রাইব্যুনাল-১ (আইসিটি-১)-এ বিচারাধীন ছিল।",
                        "মামলাটি জুলাই-আগস্ট ২০২৪ সালের গণঅভ্যুত্থান-সংক্রান্ত ঘটনাবলীর সঙ্গে সম্পর্কিত।",
                        "ট্রাইব্যুনালের কার্যক্রমে জায়েদ বিন আমজাদ আনুষ্ঠানিকভাবে সাবেক আইজিপির পক্ষে প্রতিনিধিত্ব করেন।",
                        "২০২৫ সালের জুলাই মাসে চৌধুরী আবদুল্লাহ আল-মামুন নিজের সম্পৃক্ততার কথা স্বীকার করেন।",
                        "স্বীকারোক্তির পর তিনি “রাজসাক্ষী” হিসেবে মর্যাদা লাভ করেন।",
                        "২০২৫ সালের নভেম্বর মাসে তাঁকে পাঁচ বছরের কারাদণ্ড দেওয়া হয়।",
                        "মোহাম্মদ শিশির মনির ব্যক্তিগতভাবে ট্রাইব্যুনালে মামলাটি পরিচালনা না করলেও তাঁর আইনি দলের সদস্য প্রতিরক্ষা পরিচালনা করেন।"
                )
        ));
    }

    public static synchronized void initialize(Context context) {
        if (isInitialized) return;
        try {
            for (PreloadedEntry entry : allEntries) {
                try (InputStream is = context.getAssets().open("preloaded/" + entry.getImageFile())) {
                    byte[] buffer = new byte[is.available()];
                    int read = is.read(buffer);
                    if (read > 0) {
                        String hash = computeSha256(buffer);
                        hashToEntry.put(hash, entry);
                        Log.d(TAG, "Registered preloaded image: " + entry.getImageFile() + " -> " + hash.substring(0, 10));
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Could not load asset preloaded/" + entry.getImageFile() + ": " + e.getMessage());
                }
            }
            isInitialized = true;
        } catch (Exception e) {
            Log.e(TAG, "Initialization failed", e);
        }
    }

    public static FactCheckResult matchImage(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) return null;
        String hash = computeSha256(imageBytes);
        Log.d(TAG, "Uploaded image hash: " + (hash.length() > 10 ? hash.substring(0, 10) : hash));
        PreloadedEntry entry = hashToEntry.get(hash);
        if (entry != null) {
            Log.d(TAG, "Preloaded match found: " + entry.getClaim());
            return entry.toFactCheckResult();
        }
        return null;
    }

    public static String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "SHA-256 error", e);
            return "";
        }
    }

    public static List<PreloadedEntry> getAllEntries() {
        return allEntries;
    }
}

