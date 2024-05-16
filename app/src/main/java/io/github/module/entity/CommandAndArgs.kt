package io.github.module.entity

data class CommandAndArgs(
    val command: String,
    val args: List<String>,
) {
    companion object {
        fun parse(line: String): CommandAndArgs {
            val split = line.trim().split(Regex("\\s+"))

            if (split.isEmpty()) {
                return CommandAndArgs(
                    command = "",
                    args = emptyList()
                )
            }

            if (split.size == 1) {
                return CommandAndArgs(
                    command = split.first(),
                    args = emptyList(),
                )
            }

            return CommandAndArgs(
                command = split.first(),
                args = split.subList(1, split.size),
            )
        }
    }

    fun isEmpty(): Boolean {
        return this.command.isEmpty()
                && this.args.isEmpty()
    }
}
