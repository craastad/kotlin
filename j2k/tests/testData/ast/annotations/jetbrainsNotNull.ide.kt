package test

public class Test(str: String) {
    var myStr: String = "String2"

    public fun sout(str: String) {
        System.out.println(str)
    }

    public fun dummy(str: String): String {
        return str
    }

    public fun test() {
        sout("String")
        val test = "String2"
        sout(test)
        sout(dummy(test))

        Test(test)
    }

    {
        myStr = str
    }
}