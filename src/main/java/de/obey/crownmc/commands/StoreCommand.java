package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       29.06.2023 / 20:55

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class StoreCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        messageUtil.sendMessage(sender, "Besuch unseren Store unter§f§o http://store.crownmc.de");

        return false;
    }
}
