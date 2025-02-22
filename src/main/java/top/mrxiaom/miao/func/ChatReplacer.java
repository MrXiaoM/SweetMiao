package top.mrxiaom.miao.func;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoRegister
public class ChatReplacer extends AbstractModule implements Listener {
    boolean enable = false;
    EventPriority priority;
    EventExecutor onChat = (ignored, event) -> {
        if (!enable) return;
        AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        if (player.hasPermission("sweet.miao.chat")
                && !player.hasPermission("sweet.miao.bypass")) {
            onChat(player, e);
        }
    };
    private String meow;
    private final List<String>
            moodWordsNormal = new ArrayList<>(),
            moodWordsQuestion = new ArrayList<>(),
            moodWordsSpecialQuestion = new ArrayList<>();
    private final List<Pattern> ignorePattern = new ArrayList<>();
    private final Map<UUID, Integer> disableCountMap = new HashMap<>();
    public ChatReplacer(SweetMiao plugin) {
        super(plugin);
        registerEvents();
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        config.addDefault("mood-word", null);
        this.enable = config.getBoolean("enable", false);
        this.priority = Util.valueOr(EventPriority.class, config.getString("priority"), EventPriority.LOW);
        if (priority == EventPriority.MONITOR) {
            priority = EventPriority.LOW;
        }

        ignorePattern.clear();
        for (String s : config.getStringList("ignore-pattern")) {
            try {
                ignorePattern.add(Pattern.compile(s));
            } catch (Exception e) {
                warn("无法编译正则表达式 " + s);
            }
        }

        meow = config.getString("meow", "喵");

        moodWordsNormal.clear();
        List<String> normal = strList(config, "mood-word.normal");
        if (normal == null) {
            Collections.addAll(moodWordsNormal, "啊", "嘛");
        } else {
            moodWordsNormal.addAll(normal);
        }

        moodWordsQuestion.clear();
        List<String> question = strList(config, "mood-word.question");
        if (question == null) {
            Collections.addAll(moodWordsQuestion, "吗");
        } else {
            moodWordsQuestion.addAll(question);
        }

        moodWordsSpecialQuestion.clear();
        List<String> specialQuestion = strList(config, "mood-word.special-question");
        if (specialQuestion == null) {
            Collections.addAll(moodWordsSpecialQuestion, "什么");
        } else {
            moodWordsSpecialQuestion.addAll(specialQuestion);
        }

        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, priority, onChat, plugin);
    }

    public void putDisableCount(UUID player, Integer count) {
        if (count == null || count <= 0) {
            disableCountMap.remove(player);
        } else {
            disableCountMap.put(player, count);
        }
    }

    private void onChat(Player player, AsyncPlayerChatEvent e) {
        UUID uuid = player.getUniqueId();
        int count = disableCountMap.getOrDefault(uuid, 0);
        if (count > 0) {
            putDisableCount(uuid, count - 1);
            return;
        }
        if (matchAnyIgnorePattern(e.getMessage())) {
            return;
        }
        e.setMessage(processChat(e.getMessage()));
    }

    public boolean matchAnyIgnorePattern(String s) {
        for (Pattern pattern : ignorePattern) {
            if (matchPattern(pattern, s)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static List<String> strList(MemoryConfiguration config, String key) {
        if (config.contains(key) && config.isList(key)) {
            return config.getStringList(key);
        }
        return null;
    }

    static Pattern patternEnd = Pattern.compile("[。！？；.!?;]+$");
    public String processChat(String s) {
        String[] message = splitPunctuation(s.trim());
        if (message[0].endsWith(meow)) return s;
        if (endsWithMood(message[0])) {
            if (endsWithQuestionMood(message[0]) && hasNotQuestionMark(message[1])) {
                message[0] = removeLast(message[0]);
                message[1] = "？" + message[1];
            } else {
                message[0] = removeLast(message[0]);
            }
        }
        if (specialQuestionWords(message[0])) {
            message[0] = removeLast(message[0]);
            if (hasNotQuestionMark(message[1])) {
                message[1] = "？" + message[1];
            }
        }
        return message[0] + meow + message[1];
    }
    public static String removeLast(String s) {
        return s.substring(0, s.length() - 1);
    }
    public boolean endsWithMood(String s) {
        for (String str : moodWordsNormal) {
            if (s.endsWith(str)) return true;
        }
        return endsWithQuestionMood(s);
    }
    public boolean endsWithQuestionMood(String s) {
        for (String str : moodWordsQuestion) {
            if (s.endsWith(str)) return true;
        }
        return false;
    }
    public boolean specialQuestionWords(String s) {
        for (String str : moodWordsSpecialQuestion) {
            if (s.endsWith(str)) return true;
        }
        return false;
    }
    public static boolean hasNotQuestionMark(String s) {
        return !s.contains("?") && !s.contains("？");
    }
    public static String[] splitPunctuation(String str) {
        Matcher m = patternEnd.matcher(str);
        if (!m.find()) return new String[] { str, "" };
        String group = m.group();
        return new String[] { str.substring(0, str.length() - group.length()), group};
    }

    public static boolean matchPattern(Pattern pattern, String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches() && matcher.start() == 0 && matcher.end() == s.length();
    }

    public static ChatReplacer inst() {
        return instanceOf(ChatReplacer.class);
    }
}
