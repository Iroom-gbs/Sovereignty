package me.iroom.sovereignty.util

class Option(bkey: String, bvalue: String) {
    private var _key = ""
    var key
        get() = _key.decodeOption
        set(value) {
            _key = value.encodeOption
        }

    init { key = bkey }

    private var _value = ""
    var value
        get() = _value.decodeOption
        set(value) {
            _value = value.encodeOption
        }

    init {value = bvalue}

    val subOptions = emptyList<Option>().toMutableList()

    constructor(key: String, value: Any): this(key, value.toString())

    public fun append(opt: Option) = this.apply{subOptions.add(opt)}
    public fun append(key: String, value: String) = append(Option(key, value))
    public fun append(key: String, value: Any) = append(Option(key, value))

    companion object {
        private fun CountStartRArrow(s: String): Pair<Int, String>
        {
            for(i in s.indices)
                if (s[i] != '>')
                    return Pair(i, s.substring(i until s.length))
            return Pair(s.length, "")
        }

        fun createRootOption() = Option("", "")

        fun readOption(str: String): Option {
            val root = createRootOption()
            var prv = -1
            var line = 0
            for(c in str.split("\n").map{it.trimStart()}) {
                line++
                if (c == "") continue
                if (c[0] == '#') continue
                val (d, cmd) = CountStartRArrow(c)
                if(cmd == "") continue
                if (d > prv + 1) throw Exception("Compile error on line $line")

                var o = root
                for (i in 0 until d)
                    o = o.subOptions.last()

                val splitPos = cmd.indexOf('=')
                o.append(cmd.substring(0, splitPos), cmd.substring((splitPos + 1) until cmd.length))
                prv = d
            }
            return root
        }
    }

    private val String.encodeOption
        get() = this.replace("\\", "\\\\").replace("\n", "\\n").replace(">", "\\>")

    private val String.decodeOption
        get() = this.replace("\\>", ">").replace("\\n", "\n").replace("\\\\", "\\")

    operator fun get(x: String) = subOptions.filter{ it.key == x }

    fun str(dim: Int = 0): String = ">".repeat(dim) + "$key=$value\n" + subOptions.joinToString("\n") { it.str(dim + 1) }
}