public open class Identifier<T>(_myName: T?, _myHasDollar: Boolean) {
    private val myName: T?
    private var myHasDollar: Boolean = false
    private var myNullable: Boolean = true

    public open fun getName(): T? {
        return myName
    }

    {
        myName = _myName
        myHasDollar = _myHasDollar
    }

    class object {

        public open fun <T> init(name: T?): Identifier<T> {
            val __ = Identifier(name, false)
            return __
        }

        public open fun <T> init(name: T?, isNullable: Boolean): Identifier<T> {
            val __ = Identifier(name, false)
            __.myNullable = isNullable
            return __
        }

        public open fun <T> init(name: T?, hasDollar: Boolean, isNullable: Boolean): Identifier<T> {
            val __ = Identifier(name, hasDollar)
            __.myNullable = isNullable
            return __
        }
    }
}

public open class User() {
    class object {
        public open fun main(args: Array<String?>?) {
            var i1: Identifier<*>? = Identifier.init<String?>("name", false, true)
            var i2: Identifier<*>? = Identifier.init<String?>("name", false)
            var i3: Identifier<*>? = Identifier.init<String?>("name")
        }
    }
}
fun main(args: Array<String>) = User.main(args as Array<String?>?)