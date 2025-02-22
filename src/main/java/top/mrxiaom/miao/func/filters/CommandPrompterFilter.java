package top.mrxiaom.miao.func.filters;

import com.cyr1en.commandprompter.CommandPrompter;
import com.cyr1en.commandprompter.prompt.PromptManager;
import com.cyr1en.commandprompter.prompt.PromptRegistry;
import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.miao.func.AbstractModule;
import top.mrxiaom.miao.func.ChatReplacer;
import top.mrxiaom.pluginbase.func.AutoRegister;

@AutoRegister(requirePlugins = "CommandPrompter")
public class CommandPrompterFilter extends AbstractModule {
    boolean loaded = false;
    public CommandPrompterFilter(SweetMiao plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        if (loaded) return;
        loaded = true;
        ChatReplacer inst = ChatReplacer.inst();
        inst.registerPlayerFilter(player -> {
            CommandPrompter plugin = CommandPrompter.getInstance();
            PromptManager promptManager = plugin.getPromptManager();
            PromptRegistry promptRegistry = promptManager.getPromptRegistry();
            return !promptRegistry.inCommandProcess(player);
        });
    }
}
