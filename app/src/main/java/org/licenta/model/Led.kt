package org.licenta.model

class Led(var ledLabel: String, var id: String, var value: Int, var normal: Boolean) {
    constructor(): this("", "", 0, false)
}