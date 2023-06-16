package org.licenta.model

class Led(var ledLabel: String, var id: String, var value: Int, var normal: Boolean, var loc: String) {
    constructor(): this("", "", 0, false, LedLocations.HALLWAY.displayName)
}