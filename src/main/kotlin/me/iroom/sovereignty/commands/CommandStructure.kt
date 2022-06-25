package me.iroom.sovereignty.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


class CommandStructure: CommandExecutor, TabCompleter {
    private val baseSubCommand = SubCommand("")
    fun register(command: SubCommand): CommandStructure {
        baseSubCommand.register(command)
        return this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return baseSubCommand.onCommand(sender, args.toList())
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return baseSubCommand.getAutoCompleteString(sender, args.toList())
    }

    fun register(name: String) {
        val c = Bukkit.getPluginCommand(name)!!
        c.tabCompleter = this
        c.setExecutor(this)
    }
}
