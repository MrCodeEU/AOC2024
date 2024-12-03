package Day3

import java.io.File

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day3/$fileName").readLines()
    var memory = ""
    input.forEach{
        memory += it
    }
    var muls = mutableListOf<Int>()
    // first go over every char
    memory.forEachIndexed { i, c ->
        // check if we find m
        if(c == 'm'){
            checkMul(memory, i, muls, true)
        }
    }

    println("Part 1:")
    println("Sum: ${muls.sum()}")


    muls = mutableListOf()
    var enabled = true
    // first go over every char
    memory.forEachIndexed { i, c ->
        // check if we find m
        if(c == 'm'){
            checkMul(memory, i, muls, enabled)
        } else if (c == 'd') { // check if we find d for do or don't
            //check for o
            if (memory[i+1] == 'o'){
                //check for '(' if it is a do() else check for n for don't()
                if(memory[i+2] == '('){
                    //check closing ')'
                    if (memory[i+3] == ')'){
                        // found valid do = enable
                        enabled = true
                    }
                } else if (memory[i+2] == 'n'){ //potential don't()
                    if (memory[i+3] == '\''){
                        if(memory[i+4] == 't'){
                            if(memory[i+5] == '('){
                                if (memory[i+6] == ')'){
                                    // found valid don't => disable
                                    enabled = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    println("Part 2:")
    println("Sum: ${muls.sum()}")
}

fun checkMul(memory: String, i: Int, muls: MutableList<Int>, enabled: Boolean) {
    //check if next char is u
    if(memory[i+1] == 'u'){
        //check if next char is l
        if(memory[i+2] == 'l'){
            //check if next char is "("
            if(memory[i+3] == '('){
                //loop all chars while next char is number
                var i1 = 4
                var X = ""
                while (memory[i+i1].isDigit()){
                    X += memory[i+i1]
                    i1++
                }
                //check if non digit char is ',' else invalid input => continue
                if(memory[i+i1] == ','){
                    // loop again but here we want to find ')'
                    var i2 = i1+1
                    var Y = ""
                    while (memory[i+i2].isDigit()){
                        Y += memory[i+i2]
                        i2++
                    }
                    // check if non digit number is ')' else invalid input
                    if (memory[i+i2] == ')' && enabled){ //enabled could be placed further up but I don't care right now
                        muls.add(X.toInt() * Y.toInt())
                    }
                }
            }
        }
    }
}