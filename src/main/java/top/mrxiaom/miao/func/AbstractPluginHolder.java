package top.mrxiaom.miao.func;
        
import top.mrxiaom.miao.SweetMiao;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetMiao> {
    public AbstractPluginHolder(SweetMiao plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetMiao plugin, boolean register) {
        super(plugin, register);
    }
}
