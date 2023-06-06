package org.licenta.model

class Led(val ledLabel: String, val id: String, val value: Int, val normal: Boolean) {
    constructor(): this("", "", 0, false)
}