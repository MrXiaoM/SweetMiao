package top.mrxiaom.miao.func;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.pluginbase.func.AutoRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoRegister
public class ChatReplacer extends AbstractModule implements Listener {
    public ChatReplacer(SweetMiao plugin) {
        super(plugin);
        registerEvents();
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        super.reloadConfig(config);
    }

    Pattern patternEnd = Pattern.compile("[。！？；.!?;]+$");
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("sweet.miao.chat")
                && !player.hasPermission("sweet.miao.bypass")) {
            e.setMessage(processChat(e.getMessage()));
        }
    }
    public String processChat(String s) {
        String[] message = splitPunctuation(s.trim());
        if (endsWithMood(message[0])) {
            if (endsWithQuestionMood(message[0]) && !hasQuestionMark(message[1])) {
                message[1] = "？" + message[1];
            }
            int length = message[0].length();
            message[0] = message[0].substring(0, length - 1);
        }
        return message[0] + "喵" + message[1];
    }
    public static boolean endsWithMood(String s) {
        return s.endsWith("啊") || s.endsWith("嘛") || endsWithQuestionMood(s);
    }
    public static boolean endsWithQuestionMood(String s) {
        return s.endsWith("吗") || s.endsWith("什么");
    }
    public static boolean hasQuestionMark(String s) {
        return s.contains("?") || s.contains("？");
    }
    public String[] splitPunctuation(String str) {
        Matcher m = patternEnd.matcher(str);
        if (!m.find()) return new String[] { str, "" };
        String group = m.group();
        return new String[] { str.substring(0, str.length() - group.length()), group};
    }
}
