package me.iroom.sovereignty.commands

import org.bukkit.command.CommandSender

data class SubCommand(val name: String) {
    private val subCommands = emptyList<SubCommand>().toMutableList()
    private val autoCompleteString = emptyList<String>().toMutableList()
    private lateinit var thenExecute: (CommandSender, List<String>) -> Boolean
    private var isRequiresOp = false

    fun requireOp(): SubCommand {
        isRequiresOp = true
        return this
    }

    fun register(command: SubCommand): SubCommand {
        subCommands.add(command)
        return this
    }

    fun onCommand(sender: CommandSender, args: List<String>): Boolean {
        if(args.isEmpty()) {
            if(validatePermission(sender))
                return thenExecute(sender, args)
            return false
        }
        return subCommands.firstOrNull { it.name == args[0] }?.onCommand(sender, args.subList(1, args.size))
            ?: if(validatePermission(sender)) thenExecute(sender, args) else false
    }

    private fun validatePermission(sender: CommandSender): Boolean {
        if(isRequiresOp && !sender.isOp) return false
        return true
    }

    fun getAutoCompleteString(sender: CommandSender, args: List<String>): MutableList<String> {
        if(args.isEmpty()) {
            return emptyList<String>().toMutableList()
        }
        if(args.size == 1) {
            val k = subCommands.filter { validatePermission(sender) }.map{it.name}.toMutableList()
            k.addAll(autoCompleteString)
            return k
        }
        val k = subCommands.firstOrNull {validatePermission(sender) && it.name == args[0]}
        if(k != null)
            return k.getAutoCompleteString(sender, args.subList(1, args.size))
        return emptyList<String>().toMutableList()
    }

    fun execute(f: (sender: CommandSender, args: List<String>) -> Boolean): SubCommand {
        thenExecute = f
        return this
    }

    fun addAutoCompleteString(str: String): SubCommand {
        autoCompleteString.add(str)
        return this
    }
}