package test

import dependency.*

data A("a") B(1) C class Annotations {

    inline A("f") B(2) C fun f(A("i") B(3) C deprecated("1") i: Int) {
    }

    inline A("p") B(3) C val p: Int = 2
}
