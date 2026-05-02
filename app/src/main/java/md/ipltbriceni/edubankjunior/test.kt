package md.ipltbriceni.edubankjunior

fun patrat(x: Int): Int {
    return x*x
}

fun aplica(x: Int, func: (Int) -> Int): Int {
    return func(x)
}


fun main() {
    aplica(2, ::patrat)
}
