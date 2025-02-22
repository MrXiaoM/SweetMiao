package top.mrxiaom.miao.func.filters;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerFilter {
    default int priority() {
        return 1000;
    }

    /**
     * 检查玩家是否可以触发聊天替换
     * @param player 玩家
     * @return 是否触发聊天替换
     */
    boolean check(Player player);
}
